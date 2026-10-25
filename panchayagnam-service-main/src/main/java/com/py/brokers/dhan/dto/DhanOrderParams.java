package com.py.brokers.dhan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DhanOrderParams {

    @JsonProperty("dhanClientId")
    private String dhanClientId;

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("transactionType")
    private String transactionType;       // BUY / SELL

    @JsonProperty("exchangeSegment")
    private String exchangeSegment;       // NSE_EQ, BSE_EQ, NSE_FNO, BSE_FNO, NSE_CURRENCY, MCX_COMM

    @JsonProperty("productType")
    private String productType;           // CNC, INTRADAY, MARGIN, MTF

    @JsonProperty("orderType")
    private String orderType;             // LIMIT, MARKET, STOP_LOSS, STOP_LOSS_MARKET

    @JsonProperty("validity")
    private String validity;              // DAY, IOC

    @JsonProperty("securityId")
    private String securityId;            // Exchange standard scrip ID

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("disclosedQuantity")
    private int disclosedQuantity;

    @JsonProperty("price")
    private double price;

    @JsonProperty("triggerPrice")
    private double triggerPrice;

    @JsonProperty("afterMarketOrder")
    private boolean afterMarketOrder;

    @JsonProperty("amoTime")
    private String amoTime;               // PRE_OPEN, OPEN, OPEN_30, OPEN_60

    // ── Getters & Setters ────────────────────────────────────────────────────
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDisclosedQuantity() {
        return disclosedQuantity;
    }

    public void setDisclosedQuantity(int disclosedQuantity) {
        this.disclosedQuantity = disclosedQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(double triggerPrice) {
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
