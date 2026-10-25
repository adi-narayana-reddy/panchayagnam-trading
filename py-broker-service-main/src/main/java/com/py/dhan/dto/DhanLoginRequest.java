// DhanLoginRequest.java
package com.py.dhan.dto;

public class DhanLoginRequest {

    private String clientId;
    private String mpin;
    private String totp;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public String getTotp() {
        return totp;
    }

    public void setTotp(String totp) {
        this.totp = totp;
    }
}
