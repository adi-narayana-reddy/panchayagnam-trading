package com.py.brokers.motilal;

import com.py.demataccount.entity.PyBrokerSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class MotilalLoginService {

    private static final Logger log = LoggerFactory.getLogger(MotilalLoginService.class);

    public HttpHeaders buildHeaders(String apiKey, String apiSecret,
            String vendorInfo, String authToken, String accessToken) {

        log.info("Building Motilal headers | hasAuthToken={} hasAccessToken={}",
                authToken != null, accessToken != null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "MOSL/V.1.1.0");
        headers.set("apikey", apiKey);
        headers.set("apisecretkey", apiSecret);
        headers.set("SourceId", "WEB");
        headers.set("macAddress", "00:00:00:00:00:00");
        headers.set("clientlocalIp", "192.168.1.1");
        headers.set("ClientPublicIp", "103.120.51.70");
        headers.set("osname", "Windows 10");
        headers.set("osversion", "10.0");
        headers.set("devicemodel", "PC");
        headers.set("manufacturer", "DELL");
        headers.set("productname", "EasyTrade");
        headers.set("productversion", "1.0");
        headers.set("browsername", "Chrome");
        headers.set("browserversion", "120.0");
        headers.set("vendorinfo", vendorInfo != null ? vendorInfo : "");

        if (authToken != null && !authToken.isBlank()) {
            headers.set("Authorization", authToken);
        } else {
            headers.set("Authorization", "true");
        }

        if (accessToken != null && !accessToken.isBlank()) {
            headers.set("accesstoken", accessToken);
        }

        return headers;
    }

    public HttpHeaders buildHeaders(PyBrokerSecret secret,
            String authToken,
            String accessToken) {
        log.info("Building Motilal headers | hasAuthToken={}, hasAccessToken={}",
                authToken != null, accessToken != null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "MOSL/V.1.1.0");
        headers.set("apikey", secret.getApiKey());
        headers.set("apisecretkey", secret.getApiSecret());
        headers.set("SourceId", "WEB");
        headers.set("macAddress", "00:00:00:00:00:00");
        headers.set("clientlocalIp", "192.168.1.1");
        headers.set("ClientPublicIp", "103.120.51.70");
        headers.set("osname", "Windows 10");
        headers.set("osversion", "10.0");
        headers.set("devicemodel", "PC");
        headers.set("manufacturer", "DELL");
        headers.set("productname", "EasyTrade");
        headers.set("productversion", "1.0");
        headers.set("browsername", "Chrome");
        headers.set("browserversion", "120.0");
        headers.set("vendorinfo",
                secret.getVenderInfo() != null ? secret.getVenderInfo() : "");

        // Authorization = authToken (JWT from login Step 1)
        if (authToken != null && !authToken.isBlank()) {
            headers.set("Authorization", authToken);
        } else {
            headers.set("Authorization", "true");
        }

        // accesstoken = accessToken (from getAccessToken Step 2)
        if (accessToken != null && !accessToken.isBlank()) {
            headers.set("accesstoken", accessToken);
        }

        return headers;
    }
}
