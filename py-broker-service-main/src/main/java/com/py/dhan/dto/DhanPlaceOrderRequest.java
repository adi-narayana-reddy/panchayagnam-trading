// DhanPlaceOrderRequest.java
package com.py.dhan.dto;

public class DhanPlaceOrderRequest extends DhanBaseRequest {

    private String dhanClientId;
    private String correlationId;
    private String transactionType;
    private String exchangeSegment;
    private String productType;
    private String orderType;
    private String validity;
    private String securityId;
    private Integer quantity;
    private Integer disclosedQuantity;
    private Double price;
    private Double triggerPrice;
    private boolean afterMarketOrder;
    private String amoTime;

    public String getDhanClientId() {
        return dhanClientId;
    }

    public void setDhanClientId(String dhanClientId) {
        this.dhanClientId = dhanClientId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getExchangeSegment() {
        return exchangeSegment;
    }

    public void setExchangeSegment(String exchangeSegment) {
        this.exchangeSegment = exchangeSegment;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
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

    public boolean isAfterMarketOrder() {
        return afterMarketOrder;
    }

    public void setAfterMarketOrder(boolean afterMarketOrder) {
        this.afterMarketOrder = afterMarketOrder;
    }

    public String getAmoTime() {
        return amoTime;
    }

    public void setAmoTime(String amoTime) {
        this.amoTime = amoTime;
    }
}
