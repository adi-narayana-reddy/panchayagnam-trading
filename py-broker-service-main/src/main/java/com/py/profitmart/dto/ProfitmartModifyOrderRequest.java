package com.py.profitmart.dto;

// ── MODIFY ORDER REQUEST ──────────────────────────────────────────────────────
public class ProfitmartModifyOrderRequest extends ProfitmartBaseRequest {

    private String orderNo;        // norenordno from place order response
    private String exchange;
    private String tradingSymbol;
    private int newQuantity;
    private String newPriceType;   // LMT / MKT / SL-LMT / SL-MKT
    private double newPrice;
    private Double newTriggerPrice;
    private String retention;      // DAY / IOC / EOS
    private String amo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getTradingSymbol() {
        return tradingSymbol;
    }

    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getNewPriceType() {
        return newPriceType;
    }

    public void setNewPriceType(String newPriceType) {
        this.newPriceType = newPriceType;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public Double getNewTriggerPrice() {
        return newTriggerPrice;
    }

    public void setNewTriggerPrice(Double newTriggerPrice) {
        this.newTriggerPrice = newTriggerPrice;
    }

    public String getRetention() {
        return retention;
    }

    public void setRetention(String retention) {
        this.retention = retention;
    }

    public String getAmo() {
        return amo;
    }

    public void setAmo(String amo) {
        this.amo = amo;
    }
}
