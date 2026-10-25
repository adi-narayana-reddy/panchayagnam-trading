package com.py.brokers.angel.dto;

/**
 *
 * @author abcom
 */
public class AngelLoginRequest {
    private String clientCode;
    private String mPin;
    private String totp;
    private String privateKey;
    private String jwtToken;
    private String refreshToken;
    private String totpSecretKey;
    

    /**
     * @return the clientCode
     */
    public String getClientCode() {
        return clientCode;
    }

    /**
     * @param clientCode the clientCode to set
     */
    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    /**
     * @return the mPin
     */
    public String getmPin() {
        return mPin;
    }

    /**
     * @param mPin the mPin to set
     */
    public void setmPin(String mPin) {
        this.mPin = mPin;
    }

    /**
     * @return the totp
     */
    public String getTotp() {
        return totp;
    }

    /**
     * @param totp the totp to set
     */
    public void setTotp(String totp) {
        this.totp = totp;
    }

    /**
     * @return the privateKey
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * @param privateKey the privateKey to set
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

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
     * @return the totpSecretKey
     */
    public String getTotpSecretKey() {
        return totpSecretKey;
    }

    /**
     * @param totpSecretKey the totpSecretKey to set
     */
    public void setTotpSecretKey(String totpSecretKey) {
        this.totpSecretKey = totpSecretKey;
    }
    
}

