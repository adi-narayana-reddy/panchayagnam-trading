package com.py.brokers.profitmart.service;

import com.py.brokers.profitmart.dto.ProfitMartAccessTokenGenerateDto;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerSecret;
import com.py.demataccount.entity.PyBrokerToken;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.demataccount.repository.PyBrokerSecretRepository;
import com.py.utils.ProfitmartMicroserviceClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfitmartOAuthService {

    private static final Logger log = LoggerFactory.getLogger(ProfitmartOAuthService.class);

    private static final String LOGIN_URL = "https://profitmax.profitmart.in/OAuthlogin/authorize/oauth";

    @Autowired
    private ProfitmartMicroserviceClient profitmartMicroserviceClient;

    @Autowired
    private PyBrokerAccountRepository brokerAccountRepository;

    @Autowired
    private PyBrokerSecretRepository brokerSecretRepository;

    /**
     * Builds the Profitmart OAuth login URL for the given client ID.
     *
     * Login URL format:
     * https://profitmax.profitmart.in/OAuthlogin/authorize/oauth
     * ?vendor_code=<apiKey>
     * &response_type=code &redirect_uri=<redirectUrl>
     * &state=<brokerAccountId>
     *
     * PyBrokerSecret field mapping: apiKey → vendor_code (registered with
     * Profitmart) redirectUrl → your callback URL e.g.
     * https://dev.anchayam.com/api/callback/profitmart
     *
     * @param profitMarClientId clientId stored in py_broker_master_accounts
     * @return JSONObject with loginUrl field
     */
    public JSONObject returnLoginUrl(String profitMarClientId) {

        // 1. Find broker account by clientId
        PyBrokerAccountEntity brokerAccount = brokerAccountRepository
                .findByClientId(profitMarClientId)
                .orElseThrow(() -> new RuntimeException(
                "Broker account not found for clientId: " + profitMarClientId));

        // 2. Get secret — holds vendor_code and redirect URL
        PyBrokerSecret secret = brokerAccount.getSecret();
        if (secret == null) {
            throw new RuntimeException(
                    "No secret configured for clientId: " + profitMarClientId);
        }

        String vendorCode = secret.getApiKey();       // vendor_code registered with Profitmart
        String redirectUrl = secret.getRedirectUrl();  // https://dev.anchayam.com/api/callback/profitmart
        String brokerAccId = brokerAccount.getClientId();    // used as state param

        if (vendorCode == null || vendorCode.isBlank()) {
            throw new RuntimeException("apiKey (vendor_code) not set for clientId: " + profitMarClientId);
        }
        if (redirectUrl == null || redirectUrl.isBlank()) {
            throw new RuntimeException("redirectUrl not set for clientId: " + profitMarClientId);
        }

        // 3. Build login URL
        String loginUrl = LOGIN_URL
                + "?client_id=" + brokerAccId;
        log.info("🔗 Profitmart login URL built | clientId={} accId={}", profitMarClientId, brokerAccId);

        // 4. Return response
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", "Profitmart login URL generated");
        response.put("loginUrl", loginUrl);
        response.put("brokerAccountId", brokerAccId);
        response.put("clientId", profitMarClientId);

        return response;
    }

    @Transactional
    public JSONObject getAccessToken(ProfitMartAccessTokenGenerateDto dto) {

        log.info("Profitmart getAccessToken | userId={}", dto.getUserId());

        // 1. Fetch broker account
        PyBrokerAccountEntity brokerAccount = brokerAccountRepository
                .findByClientId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                "Broker account not found for userId: " + dto.getUserId()));

        // 2. Build baseUrl and checksum
        String baseUrl = "http://" + brokerAccount.getBrokerRegisteredIp();
        String checkSum = sha256(dto.getUserId() + dto.getSeceretKey() + dto.getCode());

        log.info("Profitmart login call | userId={} baseUrl={}", dto.getUserId(), baseUrl);

        // 3. Call login via microservice client
        JSONObject loginResponse = profitmartMicroserviceClient.login(baseUrl, dto.getUserId(), dto.getCode(), checkSum);

        String stat = loginResponse.optString("stat", "");
        if (!"Ok".equals(stat)) {
            String emsg = loginResponse.optString("emsg", "Login failed");
            log.error("Profitmart login failed | userId={} emsg={}", dto.getUserId(), emsg);

            // update last login status on broker account
            brokerAccount.setLastLoginStatus("FAILED");
            brokerAccount.setLastLoginError(emsg);
            brokerAccount.setLastLoginAttempt(LocalDateTime.now());
            brokerAccountRepository.save(brokerAccount);

            throw new RuntimeException("Profitmart login failed: " + emsg);
        }

        // 4. Extract tokens from response
        String susertoken = loginResponse.optString("susertoken", "");
        String accessToken = loginResponse.optString("access_token", susertoken); // same value
        String refreshToken = loginResponse.optString("refresh_token", "");
        String expiresIn = loginResponse.optString("expires_in", "");
        String uname = loginResponse.optString("uname", "");

        // 5. Parse expiresAt — expires_in is epoch seconds
        LocalDateTime expiresAt = null;
        if (!expiresIn.isBlank()) {
            try {
                long epochSeconds = Long.parseLong(expiresIn);
                expiresAt = LocalDateTime.ofEpochSecond(epochSeconds, 0,
                        java.time.ZoneOffset.ofHoursMinutes(5, 30)); // IST
            } catch (Exception ex) {
                log.warn("Could not parse expires_in | value={}", expiresIn);
            }
        }

        // 6. Save to PyBrokerToken — upsert pattern
        PyBrokerToken token = brokerAccount.getToken();
        if (token == null) {
            token = new PyBrokerToken();
            token.setBrokerAccount(brokerAccount);
            log.info("Profitmart creating new token record | userId={}", dto.getUserId());
        } else {
            log.info("Profitmart updating existing token record | userId={}", dto.getUserId());
        }

        token.setAccessToken("Bearer "+accessToken);
        token.setRefreshToken(refreshToken);
        token.setTokenType("Bearer");
        token.setExpiresAt(expiresAt);
        token.setIsActive(true);
        brokerAccount.setUserName(uname);
        brokerAccount.setToken(token);

        // 7. Update last login status on broker account
        brokerAccount.setLastLoginStatus("SUCCESS");
        brokerAccount.setLastLoginError(null);
        brokerAccount.setLastLoginAttempt(LocalDateTime.now());
        brokerAccountRepository.save(brokerAccount);

        log.info("Profitmart token saved | userId={} uname={} expiresAt={}", dto.getUserId(), uname, expiresAt);

        // 8. Return success response
        JSONObject result = new JSONObject();
        result.put("status", "success");
        result.put("message", "Login successful, token saved");
        result.put("userId", loginResponse.optString("USERID", dto.getUserId()));
        result.put("uname", uname);
        result.put("susertoken", susertoken);
        result.put("access_token", accessToken);
        result.put("expires_at", expiresAt != null ? expiresAt.toString() : "");
        result.put("brkname", loginResponse.optString("brkname", ""));
        result.put("lastaccesstime", loginResponse.optString("lastaccesstime", ""));

        return result;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed: " + e.getMessage());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
