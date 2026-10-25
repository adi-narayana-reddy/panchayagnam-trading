package com.py.tokenrefresh.service;

import com.py.tokenrefresh.entity.TokenRefreshAuditEntity;
import com.py.brokers.angel.client.SmartConnect;
import com.py.common.util.TotpUtil;
import com.py.brokers.angel.dto.User;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerSecret;
import com.py.demataccount.entity.PyBrokerToken;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.demataccount.repository.PyBrokerTokenRepository;
import com.py.tokenrefresh.repository.TokenRefreshAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TokenRefreshService {

    private static final Logger log = LoggerFactory.getLogger(TokenRefreshService.class);

    @Autowired
    private PyBrokerAccountRepository dematAccountRepository;

    @Autowired
    private PyBrokerTokenRepository tokenRepository;

    @Autowired
    private TokenRefreshAuditRepository auditRepository;

    @Autowired
    private TotpUtil totpUtility;

    /**
     * Refresh tokens for ALL active accounts
     */
    public void refreshAllTokens(String triggeredBy) {
        List<PyBrokerAccountEntity> accounts = dematAccountRepository.findAll();
        log.info("Starting token refresh for {} active accounts. Triggered by: {}", accounts.size(), triggeredBy);

        int success = 0;
        int failed = 0;

        for (PyBrokerAccountEntity account : accounts) {
            boolean result = refreshTokenForAccount(account, triggeredBy);
            if (result) {
                success++;
            } else {
                failed++;
            }
        }

        log.info("Token refresh done. âœ… Success: {} âŒ Failed: {}", success, failed);
    }

    /**
     * Refresh token for a single account + save audit
     */
    public boolean refreshTokenForAccount(PyBrokerAccountEntity account, String triggeredBy) {

        // Start audit
        TokenRefreshAuditEntity audit = new TokenRefreshAuditEntity();
        audit.setDematAccountId(account.getId());
        audit.setClientId(account.getClientId());
        audit.setUserName(account.getUserName());
        audit.setTriggeredBy(triggeredBy);
        audit.setAttemptedAt(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        // Get or create token entity
        Optional<PyBrokerToken> optionalToken = tokenRepository.findById(account.getId());
        PyBrokerToken tokenEntity;
        if (optionalToken.isPresent()) {
            tokenEntity = optionalToken.get();
        } else {
            tokenEntity = new PyBrokerToken();
            tokenEntity.setBrokerAccount(account);
        }

        tokenEntity.setUpdatedAt(LocalDateTime.now());

        try {
            // Step 1: Get secrets
            PyBrokerSecret secret = account.getSecret();
            if (secret == null) {
                throw new RuntimeException("No secret found for account: " + account.getClientId());
            }

            String apiKey = secret.getApiKey();
            String mpin = secret.getPassword();
            String totpReferenceKey = secret.getTotpSecret();

            // Step 2: Generate TOTP
            String totp = totpUtility.generate(totpReferenceKey);
            log.info("Generated TOTP for clientId: {}", account.getClientId());

            // Step 3: Login
            SmartConnect smartConnect = new SmartConnect(apiKey);
            User user = smartConnect.generateSession(account.getClientId(), mpin, totp,apiKey);

            // Step 4: Validate
            if (user == null || user.getAccessToken() == null) {
                throw new RuntimeException("Login returned null â€” check credentials");
            }

            // Step 5: Save tokens
            tokenEntity.setAccessToken(user.getAccessToken());
            tokenEntity.setRefreshToken(user.getRefreshToken());
            tokenEntity.setFeedToken(user.getFeedToken());
            tokenEntity.setExpiresAt(LocalDateTime.now().plusHours(24));
            tokenEntity.setIsActive(true);
//            tokenEntity.setLastLoginRemarks(null);
            tokenRepository.save(tokenEntity);

            // âœ… Save SUCCESS audit
            audit.setStatus("SUCCESS");
            audit.setRemarks(null);
            audit.setCompletedAt(LocalDateTime.now());
            audit.setDurationMs(System.currentTimeMillis() - startTime);
            auditRepository.save(audit);

            log.info("âœ… Token saved for clientId: {}", account.getClientId());
            return true;

        } catch (Exception e) {
            // Save failed token status
            tokenEntity.setIsActive(false);
//            tokenEntity.setLastLoginRemarks(e.getMessage());
            tokenEntity.setExpiresAt(null);
            tokenRepository.save(tokenEntity);

            // âœ… Save FAILED audit
            audit.setStatus("FAILED");
            audit.setRemarks(e.getMessage());
            audit.setCompletedAt(LocalDateTime.now());
            audit.setDurationMs(System.currentTimeMillis() - startTime);
            auditRepository.save(audit);

            log.error("âŒ Token refresh failed for {}: {}", account.getClientId(), e.getMessage());
            return false;
        }
    }

//    /**
//     * Get latest valid JWT â€” used by all APIs
//     */
//    public String getLatestJwtToken() {
//        DematAccountTokenEntity tokenEntity = tokenRepository.findFirstByOrderByUpdatedAtDesc();
//
//        if (tokenEntity == null) {
//            throw new RuntimeException("No token found. Please wait for scheduler.");
//        }
//
//        if (!"SUCCESS".equals(tokenEntity.getLastLoginStatus())) {
//            throw new RuntimeException("Last login failed: " + tokenEntity.getLastLoginRemarks());
//        }
//
//        boolean isExpired = tokenEntity.getJwtExpiry() == null
//                || tokenEntity.getJwtExpiry().isBefore(LocalDateTime.now().plusMinutes(5));
//
//        if (isExpired) {
//            log.info("Token expired, refreshing now...");
//            refreshTokenForAccount(tokenEntity.getDematAccount(), "AUTO_REFRESH");
//            tokenEntity = tokenRepository.findFirstByOrderByUpdatedAtDesc();
//        }
//
//        return tokenEntity.getJwtToken();
//    }
}

