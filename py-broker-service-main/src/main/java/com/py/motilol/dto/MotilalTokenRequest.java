package com.py.motilol.dto;

// MotilalTokenRequest.java
public class MotilalTokenRequest {

    private String authToken;
    private String apiKey;
    private String apiSecret;
    private String vendorInfo;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getVendorInfo() {
        return vendorInfo;
    }

    public void setVendorInfo(String vendorInfo) {
        this.vendorInfo = vendorInfo;
    }
}
