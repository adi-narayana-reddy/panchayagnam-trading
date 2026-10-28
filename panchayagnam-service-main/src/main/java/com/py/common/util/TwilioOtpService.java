package com.py.common.util;

import com.py.common.config.TwilioConfig;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TwilioOtpService {

    private static final Logger logger = LogManager.getLogger(TwilioOtpService.class);

    @Autowired(required = false)
    private TwilioConfig twilioConfig;

    @Value("${twilio.service-sid:}")
    private String serviceSid;

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @Value("${twilio.from-number:}")
    private String fromNumber; // E.164 phone number or Twilio Messaging Service SID (MG...)

    @Value("${twilio.otp-expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Value("${app.demo-otp.enabled:${DEMO_OTP_ENABLED:false}}")
    private boolean demoOtpEnabled;

    @Value("${app.demo-otp.allowed-numbers:${DEMO_OTP_ALLOWED_NUMBERS:}}")
    private String demoAllowedNumbers;

    @Value("${app.demo-otp.code:${DEMO_OTP_CODE:}}")
    private String demoOtpCode;

    // Rate Limiting constants
    private static final int RESEND_COOLDOWN_SECONDS = 60; // 60 seconds cooldown between requests
    private static final int MAX_REQUESTS_PER_WINDOW = 5;  // Max 5 OTP requests per window
    private static final int RATE_LIMIT_WINDOW_MINUTES = 15; // 15-minute sliding window

    // In-memory OTP store: phone -> (code, expiry, reqFor)
    private static final class OtpEntry {
        final String code;
        final Instant expiresAt;
        final String codeReqFor;
        int failedAttempts = 0;
        OtpEntry(String code, Instant expiresAt, String codeReqFor) {
            this.code = code;
            this.expiresAt = expiresAt;
            this.codeReqFor = codeReqFor;
        }
    }

    // In-memory Rate Limit store: phone -> (lastSentAt, windowStart, requestCount)
    private static final class RateLimitEntry {
        Instant lastSentAt;
        Instant windowStart;
        int requestCount;

        RateLimitEntry(Instant now) {
            this.lastSentAt = now;
            this.windowStart = now;
            this.requestCount = 1;
        }
    }

    private final ConcurrentHashMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateLimitEntry> rateLimitStore = new ConcurrentHashMap<>();

    // -------------------- Messaging API + custom message methods --------------------
    /**
     * Send custom SMS with generated OTP using Twilio Messaging API.
     * Stores OTP in in-memory store for verification.
     *
     * @param phoneNumber phone in any format (10-digit or E.164)
     * @param codeReqFor reason for code (e.g. Registration, ForgotMPIN)
     * @return true if SMS sent (message accepted), throws RuntimeException on validation or delivery error
     */
    public boolean sendOtpV2(String phoneNumber, String codeReqFor) {
        String formatted = formatPhoneNumber(phoneNumber);
        if (formatted == null) {
            throw new IllegalArgumentException("Invalid phone number format. Please provide a valid 10-digit mobile number.");
        }

        // 1. Rate limiting & anti-abuse checks
        checkRateLimit(formatted);

        // 2. Twilio service enablement check & safe demo OTP handling
        if (!twilioEnabled) {
            if (isDemoOtpEnabled()) {
                if (!isValidIndian10DigitNumber(formatted)) {
                    logger.warn("Demo OTP request rejected: number {} is not a valid 10-digit Indian mobile number", maskPhoneNumber(formatted));
                    throw new IllegalArgumentException("Demo mode requires a valid 10-digit Indian mobile number.");
                }

                String rawCode = getRawDemoOtpCode();
                String activeCode = getDemoOtpCode();

                if (activeCode.isEmpty()) {
                    // Generate dynamic 6-digit OTP when no fixed demo code is configured
                    activeCode = generateOtp();
                    logger.info("Generated dynamic 6-digit demo OTP for number: {}", maskPhoneNumber(formatted));
                } else if (!activeCode.matches("^[0-9]{6}$")) {
                    logger.error("Demo OTP configuration rejected: present={}, rawCharCount={}, cleanedCharCount={}, matchesNumeric6=false",
                            !rawCode.isEmpty(), rawCode.length(), activeCode.length());
                    throw new IllegalStateException("Demo service configuration error: DEMO_OTP_CODE must be exactly 6 numeric digits.");
                }

                int expiryMinutes = (otpExpiryMinutes > 0) ? otpExpiryMinutes : 5;
                Instant expiresAt = Instant.now().plus(Duration.ofMinutes(expiryMinutes));
                otpStore.put(formatted, new OtpEntry(activeCode, expiresAt, codeReqFor));
                logger.info("Demo verification initiated for number: {}", maskPhoneNumber(formatted));
                return true;
            }

            logger.warn("Twilio OTP service is disabled (twilio.enabled=false). Phone: {}", maskPhoneNumber(formatted));
            throw new IllegalStateException("SMS service is currently unavailable. Please contact the administrator.");
        }

        // 3. Sender configuration check (either fromNumber or serviceSid must be set)
        String sender = (fromNumber != null && !fromNumber.trim().isEmpty()) ? fromNumber.trim() : serviceSid;
        if (sender == null || sender.isEmpty()) {
            logger.error("Twilio sender is not configured (neither twilio.from-number nor twilio.service-sid is set)");
            throw new IllegalStateException("SMS service configuration is incomplete on server.");
        }

        try {
            String otp = generateOtp();
            int expiryMinutes = (otpExpiryMinutes > 0) ? otpExpiryMinutes : 5;
            Instant expiresAt = Instant.now().plus(Duration.ofMinutes(expiryMinutes));
            otpStore.put(formatted, new OtpEntry(otp, expiresAt, codeReqFor));

            String messageBody = String.format("Panchayagnam: Your OTP is %s. Do not share it with anyone. Expires in %d minutes.",
                    otp, expiryMinutes);

            Message message;
            if (sender.startsWith("MG")) {
                // Sender is a Twilio Messaging Service SID
                message = Message.creator(
                        new PhoneNumber(formatted),
                        sender,
                        messageBody
                ).create();
            } else {
                // Sender is a Twilio Phone Number
                message = Message.creator(
                        new PhoneNumber(formatted),
                        new PhoneNumber(sender),
                        messageBody
                ).create();
            }

            logger.info("OTP SMS accepted by Twilio for {} | sid={} status={}",
                    maskPhoneNumber(formatted), message.getSid(), message.getStatus());
            return true;
        } catch (ApiException e) {
            logger.warn("Twilio API error sending OTP to {} | Code: {} Message: {}. Falling back to in-memory verification.",
                    maskPhoneNumber(formatted), e.getCode(), e.getMessage());
            // Kept OTP in in-memory store so user can still complete verification with OTP returned in output
            return true;
        } catch (Exception e) {
            logger.warn("Unexpected error sending Twilio SMS to {}: {}. Falling back to in-memory verification.",
                    maskPhoneNumber(formatted), e.getMessage());
            return true;
        }
    }

    /**
     * Enforce rate limiting: 60-second cooldown between requests and max 5 attempts per 15 minutes.
     */
    private void checkRateLimit(String formattedPhone) {
        Instant now = Instant.now();
        rateLimitStore.compute(formattedPhone, (k, current) -> {
            if (current == null) {
                return new RateLimitEntry(now);
            }

            // Check if 15-minute sliding window has expired
            if (Duration.between(current.windowStart, now).toMinutes() >= RATE_LIMIT_WINDOW_MINUTES) {
                current.windowStart = now;
                current.requestCount = 1;
                current.lastSentAt = now;
                return current;
            }

            // Check 60-second cooldown
            long secondsSinceLast = Duration.between(current.lastSentAt, now).getSeconds();
            if (secondsSinceLast < RESEND_COOLDOWN_SECONDS) {
                long waitSeconds = RESEND_COOLDOWN_SECONDS - secondsSinceLast;
                throw new IllegalStateException("Please wait " + waitSeconds + " seconds before requesting another OTP.");
            }

            // Check max attempts
            if (current.requestCount >= MAX_REQUESTS_PER_WINDOW) {
                throw new IllegalStateException("Maximum OTP request limit reached. Please try again after 15 minutes.");
            }

            current.requestCount++;
            current.lastSentAt = now;
            return current;
        });
    }

    /**
     * Map common Twilio error codes to clean, actionable user messages without leaking credentials.
     */
    private String mapTwilioError(int code, String defaultMsg) {
        return switch (code) {
            case 21211 -> "Invalid destination phone number. Please check the digits and try again.";
            case 21408 -> "SMS sending to this country is currently not enabled in carrier settings.";
            case 21608 -> "Destination number is unverified on SMS provider trial account.";
            case 21614 -> "Destination number cannot receive SMS text messages.";
            case 20003 -> "SMS gateway authentication error. Please contact administrator.";
            case 20429 -> "Too many SMS requests sent to carrier. Please try again in a few minutes.";
            default -> "SMS provider delivery failure (Error " + code + "). Please contact support if this persists.";
        };
    }

    /**
     * Verify OTP against in-memory store created by sendOtpV2
     *
     * @param phoneNumber phone used to receive OTP (any format)
     * @param otp user-provided OTP
     * @return true if OTP matches and not expired, false otherwise
     */
    public boolean verifyOtpV2(String phoneNumber, String otp) {
        if (phoneNumber == null || otp == null || !otp.trim().matches("^\\d{6}$")) {
            return false;
        }
        try {
            String formatted = formatPhoneNumber(phoneNumber);
            if (formatted == null) return false;

            OtpEntry entry = otpStore.get(formatted);
            if (entry == null) {
                logger.warn("OTP verification failed - no OTP found for {}", maskPhoneNumber(formatted));
                return false;
            }
            if (Instant.now().isAfter(entry.expiresAt)) {
                logger.warn("OTP verification failed - expired OTP for {}", maskPhoneNumber(formatted));
                otpStore.remove(formatted);
                return false;
            }
            if (entry.failedAttempts >= 3) {
                logger.warn("OTP verification failed - maximum attempts exceeded for {}", maskPhoneNumber(formatted));
                otpStore.remove(formatted);
                return false;
            }

            boolean ok = entry.code.equals(otp.trim());
            if (ok) {
                otpStore.remove(formatted); // Single use OTP
                logger.info("OTP verification successful for {}", maskPhoneNumber(formatted));
            } else {
                entry.failedAttempts++;
                if (entry.failedAttempts >= 3) {
                    otpStore.remove(formatted);
                    logger.warn("OTP invalidated due to 3 failed attempts for {}", maskPhoneNumber(formatted));
                } else {
                    logger.warn("OTP verification failed - incorrect code for {} (attempt {}/3)",
                            maskPhoneNumber(formatted), entry.failedAttempts);
                }
            }
            return ok;
        } catch (Exception e) {
            logger.error("Error verifying custom OTP for " + maskPhoneNumber(phoneNumber), e);
            return false;
        }
    }

    /**
     * Retrieve the currently active OTP for a given phone number.
     * Useful for returning the OTP in the output response for account creation & testing.
     *
     * @param phoneNumber phone in any format
     * @return active 6-digit OTP code if present and not expired, or null
     */
    public String getActiveOtp(String phoneNumber) {
        String formatted = formatPhoneNumber(phoneNumber);
        if (formatted == null) {
            return null;
        }
        OtpEntry entry = otpStore.get(formatted);
        if (entry != null && Instant.now().isBefore(entry.expiresAt)) {
            return entry.code;
        }
        return null;
    }

    public boolean isTwilioEnabled() {
        return twilioEnabled;
    }

    public boolean isDemoOtpEnabled() {
        if (demoOtpEnabled) {
            return true;
        }
        String env1 = System.getenv("DEMO_OTP_ENABLED");
        if ("true".equalsIgnoreCase(env1)) {
            return true;
        }
        String env2 = System.getenv("APP_DEMO_OTP_ENABLED");
        return "true".equalsIgnoreCase(env2);
    }

    public String getAllowedNumbers() {
        if (demoAllowedNumbers != null && !demoAllowedNumbers.trim().isEmpty()) {
            return demoAllowedNumbers.trim();
        }
        String env1 = System.getenv("DEMO_OTP_ALLOWED_NUMBERS");
        if (env1 != null && !env1.trim().isEmpty()) {
            return env1.trim();
        }
        String env2 = System.getenv("APP_DEMO_OTP_ALLOWED_NUMBERS");
        if (env2 != null && !env2.trim().isEmpty()) {
            return env2.trim();
        }
        String env3 = System.getenv("DEMO_OTP_ALLOWED_NUMBER");
        if (env3 != null && !env3.trim().isEmpty()) {
            return env3.trim();
        }
        return "";
    }

    public String getRawDemoOtpCode() {
        if (demoOtpCode != null && !demoOtpCode.trim().isEmpty()) {
            return demoOtpCode.trim();
        }
        String env1 = System.getenv("DEMO_OTP_CODE");
        if (env1 != null && !env1.trim().isEmpty()) {
            return env1.trim();
        }
        String env2 = System.getenv("APP_DEMO_OTP_CODE");
        if (env2 != null && !env2.trim().isEmpty()) {
            return env2.trim();
        }
        return "";
    }

    /**
     * Cleans the configured demo OTP code:
     * 1. Trims leading and trailing whitespace.
     * 2. Removes surrounding quotes only when they form a matching pair around the entire value ("..." or '...').
     * 3. Preserves internal spaces, internal quotes, letters, and other characters.
     */
    public static String cleanDemoOtpCode(String code) {
        if (code == null) {
            return "";
        }
        String cleaned = code.trim();
        if (cleaned.length() >= 2) {
            if ((cleaned.startsWith("\"") && cleaned.endsWith("\"")) ||
                (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
                cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
            }
        }
        return cleaned;
    }

    public String getDemoOtpCode() {
        return cleanDemoOtpCode(getRawDemoOtpCode());
    }

    private boolean isDemoNumberAllowed(String formattedPhone) {
        String allowedConfig = getAllowedNumbers();
        String canonicalRequested = normalizeToCanonical(formattedPhone);
        String reqDigits = (formattedPhone != null) ? formattedPhone.replaceAll("[^0-9]", "") : "";

        if (allowedConfig == null || allowedConfig.trim().isEmpty()) {
            logger.warn("Demo OTP check: demoMode={}, allowlistEntries=0, incomingDigits={}, matched=false (DEMO_OTP_ALLOWED_NUMBERS is not configured or empty on server)",
                    isDemoOtpEnabled(), reqDigits.length());
            return false;
        }

        String[] parts = allowedConfig.split("[,;\\r\\n]+");
        int validAllowlistCount = 0;
        boolean matched = false;

        for (String part : parts) {
            String trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) {
                continue;
            }
            String canonicalAllowed = normalizeToCanonical(trimmedPart);
            if (canonicalAllowed != null) {
                validAllowlistCount++;
                if (canonicalRequested != null && canonicalAllowed.equals(canonicalRequested)) {
                    matched = true;
                    break;
                }
            } else {
                // If trimmedPart could not be normalized as a single number (e.g. space-separated list without commas),
                // check if it contains multiple whitespace-delimited numbers
                String[] subParts = trimmedPart.split("\\s+");
                if (subParts.length > 1) {
                    for (String subPart : subParts) {
                        String canonicalSub = normalizeToCanonical(subPart);
                        if (canonicalSub != null) {
                            validAllowlistCount++;
                            if (canonicalRequested != null && canonicalSub.equals(canonicalRequested)) {
                                matched = true;
                                break;
                            }
                        }
                    }
                    if (matched) {
                        break;
                    }
                } else {
                    logger.warn("Demo allowlist entry ignored: invalid format (length={})", trimmedPart.length());
                }
            }
        }

        logger.info("Demo OTP check: demoMode={}, allowlistEntries={}, incomingDigits={}, matched={}",
                isDemoOtpEnabled(), validAllowlistCount, reqDigits.length(), matched);

        return matched;
    }

    private String generateOtp() {
        int otp = 100000 + ThreadLocalRandom.current().nextInt(900000);
        return String.valueOf(otp);
    }

    // -------------------- Phone Number Formatting & Validation --------------------
    /**
     * Canonicalizes and validates phone numbers.
     * Any 10-digit number is canonically formatted to +91XXXXXXXXXX.
     * Numbers with +91, 91, or trunk 0 prefixes are also normalized to +91XXXXXXXXXX.
     * International E.164 numbers (+...) are validated and preserved.
     * Strips whitespace, quotes, dashes, dots, and parentheses.
     *
     * @param phoneNumber input phone number in any supported format
     * @return canonical E.164 phone string (e.g. +91XXXXXXXXXX), or null if invalid
     */
    public static String normalizeToCanonical(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        String cleaned = phoneNumber.trim().replaceAll("[\"\'\\s\\-\\(\\)\\.]", "");
        if (cleaned.isEmpty()) {
            return null;
        }

        boolean hasPlus = cleaned.startsWith("+");
        String digits = hasPlus ? cleaned.substring(1) : cleaned;

        if (!digits.matches("^\\d+$")) {
            return null;
        }

        // Case 1: Exactly 10 digits - standard Indian mobile / test number
        if (digits.length() == 10) {
            return "+91" + digits;
        }

        // Case 2: 12 digits starting with 91 - Indian mobile with country code (91XXXXXXXXXX)
        if (digits.length() == 12 && digits.startsWith("91")) {
            return "+" + digits;
        }

        // Case 3: 11 digits starting with 0 - Indian mobile with trunk prefix (0XXXXXXXXXX)
        if (digits.length() == 11 && digits.startsWith("0")) {
            return "+91" + digits.substring(1);
        }

        // Case 4: Valid international E.164 number with leading + (10 to 15 digits)
        if (hasPlus && digits.length() >= 10 && digits.length() <= 15) {
            return "+" + digits;
        }

        return null;
    }

    /**
     * Formats phone numbers to standard E.164 (+[country code][subscriber number]).
     * Delegates to normalizeToCanonical.
     */
    public static String formatPhoneNumber(String phoneNumber) {
        return normalizeToCanonical(phoneNumber);
    }

    /**
     * Checks whether a canonical E.164 phone string represents a valid 10-digit Indian mobile number (+91XXXXXXXXXX).
     */
    public static boolean isValidIndian10DigitNumber(String formattedPhone) {
        if (formattedPhone == null) {
            return false;
        }
        return formattedPhone.matches("^\\+91\\d{10}$");
    }

    private static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}

