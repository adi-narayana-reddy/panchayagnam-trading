package com.py.common.util;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TwilioOtpService {

    private static final Logger logger = LogManager.getLogger(TwilioOtpService.class);

    @Value("${twilio.service-sid:}")
    private String serviceSid;

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @Value("${twilio.from-number:}")
    private String fromNumber; // E.164 or Messaging Service SID

    // In-memory OTP store: phone -> (code, expiry)
    private static final class OtpEntry {
        final String code;
        final Instant expiresAt;
        final String codeReqFor;
        OtpEntry(String code, Instant expiresAt, String codeReqFor) { this.code = code; this.expiresAt = expiresAt; this.codeReqFor = codeReqFor; }
    }

    private final ConcurrentHashMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private static final int DEFAULT_OTP_EXPIRY_SECONDS = 5 * 60; // 5 minutes

    // -------------------- Messaging API + custom message methods --------------------
    /**
     * Send custom SMS with generated OTP using Twilio Messaging API.
     * Stores OTP in in-memory store for verification.
     *
     * @param phoneNumber phone in any format (10-digit or E.164)
     * @return true if SMS sent (message accepted), false otherwise
     */
    public boolean sendOtpV2(String phoneNumber, String codeReqFor) {
        if (!twilioEnabled) {
            logger.warn("Twilio OTP service is not enabled. Configure twilio.enabled=true in application.yml");
            return false;
        }
        if (fromNumber == null || fromNumber.isEmpty()) {
            logger.error("Twilio from-number is not configured (twilio.from-number)");
            return false;
        }

        try {
            String formatted = formatPhoneNumber(phoneNumber);
            String otp = generateOtp();
            Instant expiresAt = Instant.now().plusSeconds(DEFAULT_OTP_EXPIRY_SECONDS);
            otpStore.put(formatted, new OtpEntry(otp, expiresAt, codeReqFor));

            String messageBody = String.format("Panchayagnam: Your OTP is %s. Do not share it with anyone. Expires in %d minutes.",
                    otp, DEFAULT_OTP_EXPIRY_SECONDS / 60);

            Message message = Message.creator(
                    new PhoneNumber(formatted),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();

            logger.info("Custom OTP SMS sent to {} sid={}", maskPhoneNumber(formatted), message.getSid());
            return true;
        } catch (Exception e) {
            logger.error("Failed to send custom OTP SMS to {}", maskPhoneNumber(phoneNumber), e);
            return false;
        }
    }

    /**
     * Verify OTP against in-memory store created by sendCustomOtp
     *
     * @param phoneNumber phone used to receive OTP (any format)
     * @param otp user-provided OTP
     * @return true if OTP matches and not expired, false otherwise
     */
    public boolean verifyOtpV2(String phoneNumber, String otp) {
        try {
            String formatted = formatPhoneNumber(phoneNumber);
            OtpEntry entry = otpStore.get(formatted);
            if (entry == null) return false;
            if (Instant.now().isAfter(entry.expiresAt)) {
                otpStore.remove(formatted);
                return false;
            }
            boolean ok = entry.code.equals(otp);
            if (ok) otpStore.remove(formatted);
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

    // -------------------- helpers --------------------
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        if (phoneNumber.startsWith("91") && phoneNumber.length() == 12) {
            return "+" + phoneNumber;
        }
        if (phoneNumber.length() == 10) {
            return "+91" + phoneNumber;
        }
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        return "+91" + phoneNumber;
    }

    private static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
