package com.py.brokers.angel.dto;

public class AngelOneRMSRequest {

    private String apiKey;
    private String accessToken;
    private String rmsUrl;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRmsUrl() {
        return rmsUrl;
    }

    public void setRmsUrl(String rmsUrl) {
        this.rmsUrl = rmsUrl;
    }
}
