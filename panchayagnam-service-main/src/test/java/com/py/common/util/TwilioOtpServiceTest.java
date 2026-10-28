package com.py.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TwilioOtpServiceTest {

    @Test
    @DisplayName("Format 10-digit Indian mobile number with +91 prefix")
    void testFormatPhoneNumber_10Digits() {
        String result = TwilioOtpService.formatPhoneNumber("9876543210");
        assertEquals("+919876543210", result);
    }

    @Test
    @DisplayName("Format 12-digit Indian mobile number starting with 91")
    void testFormatPhoneNumber_12DigitsWith91() {
        String result = TwilioOtpService.formatPhoneNumber("919876543210");
        assertEquals("+919876543210", result);
    }

    @Test
    @DisplayName("Format phone number already in E.164 format")
    void testFormatPhoneNumber_AlreadyE164() {
        String result = TwilioOtpService.formatPhoneNumber("+919876543210");
        assertEquals("+919876543210", result);
    }

    @Test
    @DisplayName("Format US phone number with country code +1")
    void testFormatPhoneNumber_USNumber() {
        String result = TwilioOtpService.formatPhoneNumber("+12025550199");
        assertEquals("+12025550199", result);
    }

    @Test
    @DisplayName("Clean spaces, hyphens, and brackets from phone number")
    void testFormatPhoneNumber_WithSpecialCharacters() {
        String result = TwilioOtpService.formatPhoneNumber("(987) 654-3210");
        assertEquals("+919876543210", result);
    }

    @Test
    @DisplayName("Return null for invalid phone numbers (null, empty, too short)")
    void testFormatPhoneNumber_InvalidNumbers() {
        assertNull(TwilioOtpService.formatPhoneNumber(null));
        assertNull(TwilioOtpService.formatPhoneNumber(""));
        assertNull(TwilioOtpService.formatPhoneNumber("12345"));
    }

    @Test
    @DisplayName("Throw IllegalStateException when Twilio service is disabled")
    void testSendOtp_ServiceDisabled() {
        TwilioOtpService service = new TwilioOtpService();
        ReflectionTestUtils.setField(service, "twilioEnabled", false);
        ReflectionTestUtils.setField(service, "fromNumber", "+1234567890");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            service.sendOtpV2("9876543210", "Registration");
        });

        assertTrue(ex.getMessage().contains("SMS service is currently unavailable"));
    }

    @Test
    @DisplayName("Throw IllegalArgumentException for invalid phone number format")
    void testSendOtp_InvalidPhoneNumber() {
        TwilioOtpService service = new TwilioOtpService();
        ReflectionTestUtils.setField(service, "twilioEnabled", true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.sendOtpV2("123", "Registration");
        });

        assertTrue(ex.getMessage().contains("Invalid phone number format"));
    }

    @Test
    @DisplayName("Throw IllegalStateException when sender fromNumber is not configured")
    void testSendOtp_SenderNotConfigured() {
        TwilioOtpService service = new TwilioOtpService();
        ReflectionTestUtils.setField(service, "twilioEnabled", true);
        ReflectionTestUtils.setField(service, "fromNumber", "");
        ReflectionTestUtils.setField(service, "serviceSid", "");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            service.sendOtpV2("9876543210", "Registration");
        });

        assertTrue(ex.getMessage().contains("SMS service configuration is incomplete"));
    }

    @Test
    @DisplayName("Verify OTP returns false for non-existent or blank OTP")
    void testVerifyOtp_NonExistentOrBlank() {
        TwilioOtpService service = new TwilioOtpService();
        assertFalse(service.verifyOtpV2("9876543210", ""));
        assertFalse(service.verifyOtpV2("9876543210", null));
        assertFalse(service.verifyOtpV2(null, "123456"));
        assertFalse(service.verifyOtpV2("9876543210", "999999"));
    }

    @Test
    @DisplayName("Generate dynamic demo OTP and retrieve active OTP for 10-digit number")
    void testSendOtp_DemoMode_DynamicCode() {
        TwilioOtpService service = new TwilioOtpService();
        ReflectionTestUtils.setField(service, "twilioEnabled", false);
        ReflectionTestUtils.setField(service, "demoOtpEnabled", true);

        boolean sent = service.sendOtpV2("9876543210", "Registration");
        assertTrue(sent);

        String activeOtp = service.getActiveOtp("9876543210");
        assertNotNull(activeOtp);
        assertEquals(6, activeOtp.length());
        assertTrue(activeOtp.matches("^\\d{6}$"));

        boolean verified = service.verifyOtpV2("9876543210", activeOtp);
        assertTrue(verified);

        // Single-use OTP: second verification must fail
        assertFalse(service.verifyOtpV2("9876543210", activeOtp));
    }

    @Test
    @DisplayName("Verify configured static demo OTP code")
    void testSendOtp_DemoMode_ConfiguredCode() {
        TwilioOtpService service = new TwilioOtpService();
        ReflectionTestUtils.setField(service, "twilioEnabled", false);
        ReflectionTestUtils.setField(service, "demoOtpEnabled", true);
        ReflectionTestUtils.setField(service, "demoOtpCode", "654321");

        boolean sent = service.sendOtpV2("9123456789", "Registration");
        assertTrue(sent);

        String activeOtp = service.getActiveOtp("9123456789");
        assertEquals("654321", activeOtp);

        assertTrue(service.verifyOtpV2("9123456789", "654321"));
    }
}
