package com.py.brokers.motilal.dto;

public class MotilalAddRequest {

    // ✅ Account details
    private String clientId;       // Motilal client/user ID
    private String userName;       // display name
    private Boolean isAdmin = false;

    // ✅ Secret details
    private String mpin;           // login password
    private String apiKey;         // from Motilal developer portal
    private String apiSecretKey;   // from Motilal developer portal
    private String vendorInfo;     // vendor short name or client code
    private String totpReferenceKey; // 32 char TOTP key from profile
    private String twoFa;          // PAN (capital) or DOB (dd/mm/yyyy)

    // Getters & Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecretKey() {
        return apiSecretKey;
    }

    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecretKey = apiSecretKey;
    }

    public String getVendorInfo() {
        return vendorInfo;
    }

    public void setVendorInfo(String vendorInfo) {
        this.vendorInfo = vendorInfo;
    }

    public String getTotpReferenceKey() {
        return totpReferenceKey;
    }

    public void setTotpReferenceKey(String totpReferenceKey) {
        this.totpReferenceKey = totpReferenceKey;
    }

    public String getTwoFa() {
        return twoFa;
    }

    public void setTwoFa(String twoFa) {
        this.twoFa = twoFa;
    }
}
