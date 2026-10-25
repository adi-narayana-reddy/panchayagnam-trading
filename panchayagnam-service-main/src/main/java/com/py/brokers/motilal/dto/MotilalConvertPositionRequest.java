package com.py.brokers.motilal.dto;

public class MotilalConvertPositionRequest {

    private String apiKey;
    private String apiSecret;
    private String vendorInfo;
    private String authToken;
    private String accessToken;

    // position conversion fields
    private String clientcode;   // optional — dealer only
    private String exchange;
    private long scripcode;
    private int quantity;
    private String oldproduct;
    private String newproduct;

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
     * @return the apiSecret
     */
    public String getApiSecret() {
        return apiSecret;
    }

    /**
     * @param apiSecret the apiSecret to set
     */
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    /**
     * @return the vendorInfo
     */
    public String getVendorInfo() {
        return vendorInfo;
    }

    /**
     * @param vendorInfo the vendorInfo to set
     */
    public void setVendorInfo(String vendorInfo) {
        this.vendorInfo = vendorInfo;
    }

    /**
     * @return the authToken
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * @param authToken the authToken to set
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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
     * @return the clientcode
     */
    public String getClientcode() {
        return clientcode;
    }

    /**
     * @param clientcode the clientcode to set
     */
    public void setClientcode(String clientcode) {
        this.clientcode = clientcode;
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
     * @return the scripcode
     */
    public long getScripcode() {
        return scripcode;
    }

    /**
     * @param scripcode the scripcode to set
     */
    public void setScripcode(long scripcode) {
        this.scripcode = scripcode;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the oldproduct
     */
    public String getOldproduct() {
        return oldproduct;
    }

    /**
     * @param oldproduct the oldproduct to set
     */
    public void setOldproduct(String oldproduct) {
        this.oldproduct = oldproduct;
    }

    /**
     * @return the newproduct
     */
    public String getNewproduct() {
        return newproduct;
    }

    /**
     * @param newproduct the newproduct to set
     */
    public void setNewproduct(String newproduct) {
        this.newproduct = newproduct;
    }
}
