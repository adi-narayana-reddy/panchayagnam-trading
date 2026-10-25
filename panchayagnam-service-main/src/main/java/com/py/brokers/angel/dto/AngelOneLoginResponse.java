package com.py.brokers.angel.dto;

public class AngelOneLoginResponse {
    private String jwtToken;
    private String refreshToken;
    private String feedToken;
    private String clientcode;
    private String name;

    /**
     * @return the jwtToken
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * @param jwtToken the jwtToken to set
     */
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    /**
     * @return the refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param refreshToken the refreshToken to set
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * @return the feedToken
     */
    public String getFeedToken() {
        return feedToken;
    }

    /**
     * @param feedToken the feedToken to set
     */
    public void setFeedToken(String feedToken) {
        this.feedToken = feedToken;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}