package com.py.angel.dto;

public class AngelOneLtpRequest {

    private String apiKey;
    private String accessToken;
    private String exchange;
    private String tradingSymbol;
    private String symbolToken;

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken the accessToken to set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * @param exchange the exchange to set
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the tradingSymbol
     */
    public String getTradingSymbol() {
        return tradingSymbol;
    }

    /**
     * @param tradingSymbol the tradingSymbol to set
     */
    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
    }

    /**
     * @return the symbolToken
     */
    public String getSymbolToken() {
        return symbolToken;
    }

    /**
     * @param symbolToken the symbolToken to set
     */
    public void setSymbolToken(String symbolToken) {
        this.symbolToken = symbolToken;
    }
}
