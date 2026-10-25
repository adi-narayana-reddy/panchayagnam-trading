package com.py.motilol.dto;

// MotilalLoginRequest.java

public class MotilalLoginRequest {
    private String userId;
    private String hashedPassword;
    private String twoFa;
    private String totp;
    private String apiKey;
    private String apiSecret;
    private String vendorInfo;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public String getTwoFa() { return twoFa; }
    public void setTwoFa(String twoFa) { this.twoFa = twoFa; }
    public String getTotp() { return totp; }
    public void setTotp(String totp) { this.totp = totp; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public String getVendorInfo() { return vendorInfo; }
    public void setVendorInfo(String vendorInfo) { this.vendorInfo = vendorInfo; }
}
