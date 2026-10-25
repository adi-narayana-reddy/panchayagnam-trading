package com.py.brokers.angel.dto;

public class AngelOnePlaceOrderRequest {

    private String apiKey;
    private String accessToken;
    private String variety;

    // OrderParams fields
    private String tradingsymbol;
    private String symboltoken;
    private String exchange;
    private String transactiontype;  // BUY or SELL
    private String ordertype;
    private String producttype;
    private String duration;
    private Double price;
    private Double triggerprice;
    private Integer quantity;
    private String squareoff;
    private String stoploss;
    private String trailingStopLoss;

    // Getters and Setters
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getTradingsymbol() {
        return tradingsymbol;
    }

    public void setTradingsymbol(String tradingsymbol) {
        this.tradingsymbol = tradingsymbol;
    }

    public String getSymboltoken() {
        return symboltoken;
    }

    public void setSymboltoken(String symboltoken) {
        this.symboltoken = symboltoken;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTriggerprice() {
        return triggerprice;
    }

    public void setTriggerprice(Double triggerprice) {
        this.triggerprice = triggerprice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSquareoff() {
        return squareoff;
    }

    public void setSquareoff(String squareoff) {
        this.squareoff = squareoff;
    }

    public String getStoploss() {
        return stoploss;
    }

    public void setStoploss(String stoploss) {
        this.stoploss = stoploss;
    }

    public String getTrailingStopLoss() {
        return trailingStopLoss;
    }

    public void setTrailingStopLoss(String trailingStopLoss) {
        this.trailingStopLoss = trailingStopLoss;
    }
}
