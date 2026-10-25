package com.py.common.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BCryptUtil {

    private static final Logger log = LoggerFactory.getLogger(BCryptUtil.class);
    private static final int WORK_FACTOR = 10;

    private BCryptUtil() {}

    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            log.error("BCrypt hash failed — password is empty");
            throw new IllegalArgumentException("Password cannot be empty");
        }
        log.info("Hashing password with BCrypt");
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            log.warn("BCrypt match failed — null input");
            return false;
        }
        boolean result = BCrypt.checkpw(plainPassword, hashedPassword);
        log.info("BCrypt password match result: {}", result);
        return result;
    }
}

