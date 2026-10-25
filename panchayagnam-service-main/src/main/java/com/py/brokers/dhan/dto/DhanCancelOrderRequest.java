// DhanCancelOrderRequest.java
package com.py.brokers.dhan.dto;

public class DhanCancelOrderRequest extends DhanBaseRequest {

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
