package com.py.tokenrefresh.scheduler;

import com.py.common.enums.BrokerType;
import static com.py.common.enums.BrokerType.ANGELONE;
import static com.py.common.enums.BrokerType.MOTILAL;
import com.py.common.util.TotpUtil;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerSecret;
import com.py.demataccount.entity.PyBrokerToken;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.mail.service.TokenRefreshMailService;
import com.py.tokenrefresh.entity.TokenRefreshProgressEntity;
import com.py.tokenrefresh.repository.TokenRefreshProgressRepository;
import com.py.utils.AngelOneMicroserviceClient;
import com.py.utils.MotilalMicroserviceClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenRefreshSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(TokenRefreshSchedulerService.class);

    @Autowired
    private PyBrokerAccountRepository pyBrokerAccountRepository;

    @Autowired
    private TokenRefreshProgressRepository tokenRefreshProgressRepository;

    @Autowired
    private AngelOneMicroserviceClient angelOneClient;

    @Autowired
    private MotilalMicroserviceClient motilalClient;

    @Autowired
    private TotpUtil totpUtil;

    @Autowired
    private TokenRefreshMailService mailService;

    // ── AngelOne Token Refresh ────────────────────────────────────────────────
    private void refreshAngelOneToken(PyBrokerAccountEntity account,
            TokenRefreshProgressEntity progress) {
        PyBrokerSecret secret = account.getSecret();
        String totp = totpUtil.generate(secret.getTotpSecret());
        String baseUrl = "http://" + account.getBrokerRegisteredIp();

        log.info("Refreshing AngelOne token | clientId={} baseUrl={}",
                account.getClientId(), baseUrl);

        JSONObject loginData = angelOneClient.login(
                baseUrl,
                account.getClientId(),
                secret.getPassword(),
                totp,
                secret.getApiKey());

        if (loginData == null || !loginData.has("jwtToken")) {
            throw new RuntimeException("AngelOne login returned null or missing jwtToken");
        }

        PyBrokerToken token = account.getToken();
        token.setAccessToken(loginData.getString("jwtToken"));
        token.setRefreshToken(loginData.optString("refreshToken", ""));
        token.setFeedToken(loginData.optString("feedToken", ""));
        token.setExpiresAt(LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());

        account.setLastLoginStatus("SUCCESS");
        account.setLastLoginAttempt(LocalDateTime.now());
        account.setLastLoginError(null);
        pyBrokerAccountRepository.save(account);

        progress.setStatus("SUCCESS");
        progress.setRemarks("AngelOne token refreshed successfully");
        log.info("AngelOne token refresh SUCCESS | clientId={}", account.getClientId());
    }

    // ── Motilal Token Refresh ─────────────────────────────────────────────────
    // ── Motilal Token Refresh via microservice ────────────────────────────────
    private void refreshMotilalToken(PyBrokerAccountEntity account,
            TokenRefreshProgressEntity progress) {

        PyBrokerSecret secret = account.getSecret();
        String baseUrl = "http://" + account.getBrokerRegisteredIp();

        String totp = totpUtil.generate(secret.getTotpSecret());
        String hashedPassword = DigestUtils.sha256Hex(
                secret.getPassword() + secret.getApiKey());

        log.info("Refreshing Motilal token via microservice | clientId={} baseUrl={}",
                account.getClientId(), baseUrl);

        // Step 1 — Login to get AuthToken
        JSONObject authJson = motilalClient.login(
                baseUrl,
                account.getClientId(),
                hashedPassword,
                secret.getTwoFa(),
                totp,
                secret.getApiKey(),
                secret.getApiSecret(),
                secret.getVenderInfo());

        if (authJson == null || !authJson.has("AuthToken")) {
            throw new RuntimeException("Motilal login returned null or missing AuthToken");
        }

        String authToken = authJson.getString("AuthToken");
        log.info("Motilal AuthToken received | clientId={}", account.getClientId());

        // Step 2 — Get AccessToken using AuthToken
        JSONObject tokenJson = motilalClient.getAccessToken(
                baseUrl,
                authToken,
                secret.getApiKey(),
                secret.getApiSecret(),
                secret.getVenderInfo());

        if (tokenJson == null || !tokenJson.has("accesstoken")) {
            throw new RuntimeException("Motilal getAccessToken returned null or missing accesstoken");
        }

        String accessToken = tokenJson.getString("accesstoken");
        log.info("Motilal AccessToken received | clientId={}", account.getClientId());

        // Step 3 — Save updated tokens
        PyBrokerToken token = account.getToken();
        token.setRefreshToken(authToken);       // AuthToken stored as refreshToken
        token.setAccessToken(accessToken);
        token.setExpiresAt(LocalDateTime.now().toLocalDate().atTime(6, 0).plusDays(1));

        account.setLastLoginStatus("SUCCESS");
        account.setLastLoginAttempt(LocalDateTime.now());
        account.setLastLoginError(null);
        pyBrokerAccountRepository.save(account);

        progress.setStatus("SUCCESS");
        progress.setRemarks("Motilal token refreshed successfully via microservice");
        log.info("Motilal token refresh SUCCESS | clientId={}", account.getClientId());
    }

    public List<TokenRefreshProgressEntity> refreshByBrokerType(BrokerType brokerType) {
        List<PyBrokerAccountEntity> accounts = pyBrokerAccountRepository
                .findByActiveAndBrokerType(Boolean.TRUE, brokerType);

        log.info("Token refresh started | brokerType={} accounts={}", brokerType, accounts.size());

        List<TokenRefreshProgressEntity> progressList = new ArrayList<>();

        for (PyBrokerAccountEntity account : accounts) {
            TokenRefreshProgressEntity progress = new TokenRefreshProgressEntity();
            progress.setBrokerAccountId(account.getId());
            progress.setClientId(account.getClientId());
            progress.setBrokerType(account.getBrokerType());
            progress.setRefreshDate(LocalDateTime.now());

            try {
                switch (brokerType) {
                    case ANGELONE ->
                        refreshAngelOneToken(account, progress);
                    case MOTILAL ->
                        refreshMotilalToken(account, progress);
                    default -> {
                        progress.setStatus("SKIPPED");
                        progress.setRemarks("Token refresh not implemented for: " + brokerType);
                    }
                }
            } catch (Exception e) {
                log.error("Token refresh failed — marking account inactive | clientId={} broker={} error={}",
                        account.getClientId(), brokerType, e.getMessage());

                progress.setStatus("FAILED");
                progress.setRemarks(e.getMessage());

                // ── Mark account inactive ─────────────────────────────────────
                account.setActive(Boolean.FALSE);
                account.setLastLoginStatus("FAILED");
                account.setLastLoginError(e.getMessage());
                account.setLastLoginAttempt(LocalDateTime.now());
                pyBrokerAccountRepository.save(account);
            }

            tokenRefreshProgressRepository.save(progress);
            progressList.add(progress);
        }

        log.info("Token refresh completed | brokerType={} total={}", brokerType, progressList.size());
        return progressList;
    }
}
