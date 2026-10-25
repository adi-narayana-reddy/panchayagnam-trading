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

    // Rate Limiting constants
    private static final int RESEND_COOLDOWN_SECONDS = 60; // 60 seconds cooldown between requests
    private static final int MAX_REQUESTS_PER_WINDOW = 5;  // Max 5 OTP requests per window
    private static final int RATE_LIMIT_WINDOW_MINUTES = 15; // 15-minute sliding window

    // In-memory OTP store: phone -> (code, expiry, reqFor)
    private static final class OtpEntry {
        final String code;
        final Instant expiresAt;
        final String codeReqFor;
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

        // 2. Twilio service enablement check
        if (!twilioEnabled) {
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
            logger.error("Twilio API error sending OTP to {} | Code: {} Message: {}",
                    maskPhoneNumber(formatted), e.getCode(), e.getMessage(), e);
            throw new RuntimeException(mapTwilioError(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error sending custom OTP SMS to {}", maskPhoneNumber(formatted), e);
            throw new RuntimeException("Unable to deliver OTP at this time. Please try again later.");
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
        if (phoneNumber == null || otp == null || otp.trim().isEmpty()) {
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
            boolean ok = entry.code.equals(otp.trim());
            if (ok) {
                otpStore.remove(formatted); // Single use OTP
                logger.info("OTP verification successful for {}", maskPhoneNumber(formatted));
            } else {
                logger.warn("OTP verification failed - incorrect code for {}", maskPhoneNumber(formatted));
            }
            return ok;
        } catch (Exception e) {
            logger.error("Error verifying custom OTP for " + maskPhoneNumber(phoneNumber), e);
            return false;
        }
    }

    private String generateOtp() {
        int otp = 100000 + ThreadLocalRandom.current().nextInt(900000);
        return String.valueOf(otp);
    }

    // -------------------- Phone Number Formatting & Validation --------------------
    /**
     * Formats phone numbers to standard E.164 (+[country code][subscriber number]).
     * Default country code is +91 (India) for 10-digit numbers.
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        String cleaned = phoneNumber.trim().replaceAll("[\\s\\-\\(\\)]", "");
        if (cleaned.isEmpty()) {
            return null;
        }

        // Already in E.164 format with leading +
        if (cleaned.startsWith("+")) {
            String digits = cleaned.substring(1).replaceAll("[^0-9]", "");
            if (digits.length() >= 10 && digits.length() <= 15) {
                return "+" + digits;
            }
            return null; // Invalid length for E.164
        }

        // Numbers without leading +
        String digits = cleaned.replaceAll("[^0-9]", "");
        if (digits.length() == 10) {
            // Standard Indian 10-digit mobile
            return "+91" + digits;
        }
        if (digits.length() == 12 && digits.startsWith("91")) {
            // Indian number with 91 prefix but no +
            return "+" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("1")) {
            // US/Canada number with 1 prefix but no +
            return "+" + digits;
        }
        if (digits.length() > 10 && digits.length() <= 15) {
            return "+" + digits;
        }

        return null; // Too short or invalid
    }

    private static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}

