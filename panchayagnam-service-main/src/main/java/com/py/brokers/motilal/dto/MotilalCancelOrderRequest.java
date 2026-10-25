package com.py.brokers.motilal.dto;

// MotilalCancelOrderRequest.java
public class MotilalCancelOrderRequest extends MotilalBaseRequest {

    private String uniqueOrderId;
    private String clientCode;

    public String getUniqueOrderId() {
        return uniqueOrderId;
    }

    public void setUniqueOrderId(String uniqueOrderId) {
        this.uniqueOrderId = uniqueOrderId;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
}
