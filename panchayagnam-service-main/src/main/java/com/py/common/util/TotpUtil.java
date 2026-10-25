package com.py.common.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TotpUtil {

    private static final Logger log = LoggerFactory.getLogger(TotpUtil.class);

    public String generate(String secretKey) {
        try {
            String cleanSecret = secretKey.replace(" ", "").toUpperCase();
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            int code = gAuth.getTotpPassword(cleanSecret);
            String totp = String.format("%06d", code);
            log.info("TOTP generated successfully");
            return totp;
        } catch (Exception e) {
            log.error("TOTP generation failed: {}", e.getMessage(), e);
            throw new RuntimeException("TOTP generation failed: " + e.getMessage(), e);
        }
    }
}

