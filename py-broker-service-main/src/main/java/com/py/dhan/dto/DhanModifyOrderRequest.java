// DhanModifyOrderRequest.java
package com.py.dhan.dto;

public class DhanModifyOrderRequest extends DhanBaseRequest {

    private String dhanClientId;
    private String orderId;
    private String orderType;
    private Integer quantity;
    private Integer disclosedQuantity;
    private String validity;
    private Double price;
    private Double triggerPrice;

    public String getDhanClientId() {
        return dhanClientId;
    }

    public void setDhanClientId(String dhanClientId) {
        this.dhanClientId = dhanClientId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getDisclosedQuantity() {
        return disclosedQuantity;
    }

    public void setDisclosedQuantity(Integer disclosedQuantity) {
        this.disclosedQuantity = disclosedQuantity;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(Double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }
}
