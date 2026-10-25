package com.py.motilol.dto;

// MotilalLtpRequest.java
public class MotilalLtpRequest extends MotilalBaseRequest {

    private String exchange;
    private Long scripCode;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Long getScripCode() {
        return scripCode;
    }

    public void setScripCode(Long scripCode) {
        this.scripCode = scripCode;
    }
}
