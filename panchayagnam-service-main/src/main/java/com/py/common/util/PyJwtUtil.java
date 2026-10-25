package com.py.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class PyJwtUtil {

    private static final Logger log = LoggerFactory.getLogger(PyJwtUtil.class);

    @org.springframework.beans.factory.annotation.Value("${jwt.secret:panchayagnam_secret_key_2024_super_secure_key_256bit}")
    private String secretKey;

    @org.springframework.beans.factory.annotation.Value("${jwt.expiration-ms:86400000}")
    private long expirationTime;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generate token
    public String generateToken(Long userId, String phoneNumber, String role) {
        log.info("Generating JWT token | userId={}, role={}", userId, role);
        try {
            String token = Jwts.builder()
                    .subject(phoneNumber)
                    .claim("userId", userId)
                    .claim("role", role)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSigningKey())
                    .compact();
            log.info("JWT token generated successfully | userId={}", userId);
            return token;
        } catch (Exception e) {
            log.error("JWT token generation failed | userId={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    // Extract phone number from token
    public String extractPhoneNumber(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Extract userId from token
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    // Validate token
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Extract all claims
    private Claims extractClaims(String token) {
        return Jwts.parser()                                   
                .verifyWith(getSigningKey())                   
                .build()
                .parseSignedClaims(token)                      
                .getPayload();                                 
    }
}