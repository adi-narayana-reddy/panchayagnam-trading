package com.py.brokers.angel.dto;

public class AngelOnePlaceOrderResponse {

    private String orderId;
    private String status;

    public AngelOnePlaceOrderResponse() {
    }

    public AngelOnePlaceOrderResponse(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
