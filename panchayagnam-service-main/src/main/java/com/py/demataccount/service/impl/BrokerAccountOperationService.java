package com.py.demataccount.service.impl;

import com.py.brokers.angel.exception.SmartAPIException;
import com.py.common.dto.ApiResponse;
import com.py.brokers.angel.dto.User;
import com.py.common.enums.BrokerType;
import com.py.demataccount.dto.DematAccountAddRequest;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.ipconfig.entity.PyBrokerIpConfigEntity;
import com.py.demataccount.entity.PyBrokerSecret;
import com.py.demataccount.entity.PyBrokerToken;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.ipconfig.repository.PyBrokerIpConfigRepository;
import com.py.ipconfig.service.PyBrokerIpConfigService;
import com.py.demataccount.utils.BrokerProfileLoader;
import com.py.demataccount.utils.BrokerTokenSecretManager;
import com.py.entity.PyUser;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BrokerAccountOperationService {

    private static final Logger log = LoggerFactory.getLogger(BrokerAccountOperationService.class);

    @Autowired
    private PyBrokerAccountRepository pyBrokerAccountRepository;

    @Autowired
    private PyBrokerIpConfigRepository pyBrokerIpConfigRepository;

    @Autowired
    private BrokerLoginService brokerLoginService;

    @Autowired
    private BrokerTokenSecretManager tokenSecretManager;

    @Autowired
    private BrokerProfileLoader profileLoader;

    @Autowired
    private PyBrokerIpConfigService accountValidator;

    // ── ADD ───────────────────────────────────────────────────────────────────
    public ApiResponse<?> addAccount(DematAccountAddRequest request, PyUser pyUser)
            throws SmartAPIException, IOException {
        log.info("Adding broker account | clientId={}, broker={}",
                request.getClientId(), request.getBrokerType());

        // ── Duplicate check ───────────────────────────────────────────────────
        Optional<PyBrokerAccountEntity> existing
                = pyBrokerAccountRepository.findByClientId(request.getClientId());
        if (existing.isPresent()) {
            return ApiResponse.error(409,
                    "Broker account already exists for clientId: " + request.getClientId()
                    + ". Please use UPDATE to modify credentials.");
        }

        // ── IP validation ─────────────────────────────────────────────────────
        ApiResponse<PyBrokerIpConfigEntity> ipValidation
                = accountValidator.validateIpForAccount(request.getIp(), request.getBrokerType());
        if (!"SUCCESS".equals(ipValidation.getStatus())) {
            return ipValidation;
        }

        // ── Login BEFORE any DB write — invalid credentials are caught here ───
        JSONObject loginData = null;
        try {
            if (!request.getBrokerType().equals(BrokerType.PROFITMART)) {
                loginData = brokerLoginService.login(request);
            }
        } catch (RuntimeException e) {
            log.error("Broker login failed for clientId={}: {}",
                    request.getClientId(), e.getMessage());
            return ApiResponse.error(401,
                    "Login failed for " + request.getBrokerType()
                    + " [" + request.getClientId() + "]: " + e.getMessage());
        }

        // ── Persist only after login is confirmed valid ────────────────────────
        return persistNewAccount(request, pyUser, loginData, ipValidation.getData());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public ApiResponse<?> updateAccount(DematAccountAddRequest request)
            throws SmartAPIException, IOException {
        log.info("Updating broker account | clientId={}", request.getClientId());

        Optional<PyBrokerAccountEntity> accountOpt
                = pyBrokerAccountRepository.findByClientId(request.getClientId());
        if (accountOpt.isEmpty()) {
            return ApiResponse.error(404,
                    "Broker account not found for clientId: " + request.getClientId());
        }

        PyBrokerAccountEntity account = accountOpt.get();
        // ── IP validation when IP is being changed ────────────────────────────
        if (!account.getActive() && request.isActive() && StringUtils.isNotEmpty(request.getIp())) {
            ApiResponse<PyBrokerIpConfigEntity> ipValidation = accountValidator.validateIpForAccount(request.getIp(), request.getBrokerType());
            if (!"SUCCESS".equals(ipValidation.getStatus())) {
                return ipValidation;
            }
            PyBrokerIpConfigEntity ipConfig = ipValidation.getData();
            account.setBrokerRegisteredIp(ipConfig.getIpAddress() + ":" + (ipConfig.getPortNo() != null ? ipConfig.getPortNo() : 8082));
        } else if (!request.isActive()) {
            account.setBrokerRegisteredIp(null);
            account.setActive(false);
            pyBrokerAccountRepository.save(account);
            return ApiResponse.success("Account deactivated successfully", account);
        }
        // ── Login BEFORE any further DB write ─────────────────────────────────
        JSONObject loginData = null;
        try {
            if (!request.getBrokerType().equals(BrokerType.PROFITMART)) {
                loginData = brokerLoginService.login(request);
            }
        } catch (RuntimeException e) {
            log.error("Broker login failed on update for clientId={}: {}",
                    request.getClientId(), e.getMessage());
            return ApiResponse.error(401,
                    "Login failed for " + request.getBrokerType()
                    + " [" + request.getClientId() + "]: " + e.getMessage());
        }

        // ── Persist only after login is confirmed valid ────────────────────────
        return persistUpdate(account, request, loginData);
    }

    // ── DEACTIVATE ────────────────────────────────────────────────────────────
    @Transactional
    public ApiResponse<?> deActivateAccount(String clientId) {
        log.info("Deactivating broker account | clientId={}", clientId);

        Optional<PyBrokerAccountEntity> accountOpt
                = pyBrokerAccountRepository.findByClientId(clientId);
        if (accountOpt.isEmpty()) {
            return ApiResponse.error(404,
                    "Broker account not found for clientId: " + clientId);
        }

        PyBrokerAccountEntity account = accountOpt.get();
        account.setActive(false);
        account.setBrokerRegisteredIp(null);
        pyBrokerAccountRepository.save(account);

        log.info("Broker account deactivated | clientId={}", clientId);
        return ApiResponse.success("Account deactivated successfully", null);
    }

    // ── PERSIST NEW (called only after login succeeds) ─────────────────────────
    @Transactional
    private ApiResponse<?> persistNewAccount(DematAccountAddRequest request,
            PyUser pyUser, JSONObject loginData, PyBrokerIpConfigEntity ipConfig)
            throws SmartAPIException, IOException {
        try {
            PyBrokerAccountEntity account = new PyBrokerAccountEntity();
            account.setBrokerRegisteredIp(
                    ipConfig.getIpAddress() + ":" + ipConfig.getPortNo());
            account.setPyUser(pyUser);
            account.setClientId(request.getClientId());
            account.setBrokerType(request.getBrokerType());
            account.setActive(true);
            account.setLastLoginStatus("SUCCESS");
            account.setLastLoginAttempt(LocalDateTime.now());

            PyBrokerSecret secret = tokenSecretManager.createSecret(request);
            secret.setBrokerAccount(account);
            account.setSecret(secret);

            PyBrokerToken token = tokenSecretManager.createToken(loginData, request.getBrokerType());
            token.setBrokerAccount(account);
            account.setToken(token);

            User user = profileLoader.buildUserProfile(
                    request.getBrokerType(), request, loginData, secret);
            account.setUserName(profileLoader.getEffectiveUserName(request, user));
            account.setIsOwn(request.isOwn());

            pyBrokerAccountRepository.save(account);

            log.info("New broker account saved | clientId={}, broker={}",
                    request.getClientId(), request.getBrokerType());
            if (request.getBrokerType().equals(BrokerType.PROFITMART)) {
                return ApiResponse.success("Broker account details saved successfully,Please Connect Account", user);
            } else {
                return ApiResponse.success("Broker account added successfully", user);
            }

        } catch (Exception e) {
            log.error("Failed to persist broker account | clientId={}: {}",
                    request.getClientId(), e.getMessage(), e);
            throw e;
        }
    }

    // ── PERSIST UPDATE (called only after login succeeds) ─────────────────────
    @Transactional
    private ApiResponse<?> persistUpdate(PyBrokerAccountEntity account,
            DematAccountAddRequest request, JSONObject loginData)
            throws SmartAPIException, IOException {
        try {
            log.info("Persisting update | clientId={}", request.getClientId());

            PyBrokerSecret secret = account.getSecret();
            tokenSecretManager.updateSecret(secret, request);

            PyBrokerToken token = account.getToken();
            tokenSecretManager.updateToken(token, loginData, request.getBrokerType());

            account.setBrokerType(request.getBrokerType());
            account.setClientId(request.getClientId());
            account.setActive(request.isActive());
            account.setLastLoginStatus("SUCCESS");
            account.setLastLoginAttempt(LocalDateTime.now());
            account.setIsOwn(request.isOwn());
            // Build user profile for response
            User user = profileLoader.buildUserProfile(request.getBrokerType(), request, loginData, secret);
            // Update username if provided

            if (StringUtils.isNotEmpty(request.getNickName())) {
                account.setUserName(request.getNickName());
            } else {
                account.setUserName(user.userName);
            }
            pyBrokerAccountRepository.save(account);

            log.info("Broker account updated | clientId={}", request.getClientId());
            if (request.getBrokerType().equals(BrokerType.PROFITMART)) {
                return ApiResponse.success("Broker account details updated successfully,Please Connect Account", user);
            }
            return ApiResponse.success("Broker account updated successfully", user);

        } catch (RuntimeException e) {
            log.error("Failed to persist update | clientId={}: {}",
                    request.getClientId(), e.getMessage(), e);
            throw e;
        }
    }
}
