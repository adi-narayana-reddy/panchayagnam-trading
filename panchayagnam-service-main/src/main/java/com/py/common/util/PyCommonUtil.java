package com.py.common.util;

import com.py.common.dto.ApiResponse;
import com.py.common.enums.BrokerType;
import com.py.demataccount.dto.DematAccountAddRequest;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.ipconfig.entity.PyBrokerIpConfigEntity;
import org.springframework.stereotype.Component;

@Component
public class PyCommonUtil {

    /**
     * Builds base URL from IP config.
     *
     * @param ipConfig the IP config entity
     * @return base URL for microservice
     */
    public static String getBaseUrl(PyBrokerIpConfigEntity ipConfig) {
        return "http://" + ipConfig.getIpAddress() + ":" + ipConfig.getPortNo();
    }

    public static String getBaseUrl(PyBrokerAccountEntity account) {
        String ipPort = account.getBrokerRegisteredIp();
        if (ipPort == null || ipPort.isBlank()) {
            throw new RuntimeException("No instance URL for clientId: " + account.getClientId());
        }
        String ipWithPort = account.getBrokerRegisteredIp();
        return "http://" + ipWithPort;
    }

    public ApiResponse<?> validateAccountRequest(DematAccountAddRequest request) {
        // Validate broker type
        if (request.getBrokerType() == null) {
            return ApiResponse.error(400, "Broker type is required");
        }

        // Validate required fields
        if (request.getClientId() == null || request.getClientId().isEmpty()) {
            return ApiResponse.error(400, "Client ID is required");
        }

        Long pyUserId = request.getPyUserId();
        if (pyUserId == null || pyUserId <= 0) {
            return ApiResponse.error(400, "Valid PyUser ID is required");
        }

        if (request.getApiKey() == null || request.getApiKey().isEmpty()) {
            return ApiResponse.error(400, "API Key is required");
        }

        // Validate IP for brokers that require it
        if (request.getBrokerType() != BrokerType.UPSTOX) {
            if (request.getIp() == null || request.getIp().isEmpty()) {
                return ApiResponse.error(400, "IP address is required for " + request.getBrokerType());
            }
        }

        return ApiResponse.success("Validation passed");
    }
}

