package com.py.brokers.angel.dto;

public class WebSocketConnectRequest {

    private String jwtToken;
    private String apiKey;
    private String clientCode;
    private String feedToken;
    private String token;   // e.g. 3045 (SBIN)
    private String exchangeType;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getFeedToken() {
        return feedToken;
    }

    public void setFeedToken(String feedToken) {
        this.feedToken = feedToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the exchangeType
     */
    public String getExchangeType() {
        return exchangeType;
    }

    /**
     * @param exchangeType the exchangeType to set
     */
    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }
}

