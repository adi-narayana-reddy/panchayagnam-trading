package com.py.brokers.profitmart.dto;

// ── LOGIN REQUEST ─────────────────────────────────────────────────────────────
public class ProfitmartLoginRequest {

    private String userId;
    private String password;   // plain text — SHA-256 done internally
    private String twoFa;      // DOB in DD-MM-YYYY
    private String vendorCode;
    private String apiSecret;
    private String imei;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTwoFa() {
        return twoFa;
    }

    public void setTwoFa(String twoFa) {
        this.twoFa = twoFa;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
