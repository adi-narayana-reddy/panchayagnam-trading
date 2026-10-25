package com.py.profitmart.dto;

// ── BASE REQUEST (carries session token for all post-login calls) ─────────────
public class ProfitmartBaseRequest {

    private String userId;
    private String susertoken;   // session token from login response
    private String vendorCode;
    private String apiSecret;
    private String imei;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSusertoken() {
        return susertoken;
    }

    public void setSusertoken(String susertoken) {
        this.susertoken = susertoken;
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
