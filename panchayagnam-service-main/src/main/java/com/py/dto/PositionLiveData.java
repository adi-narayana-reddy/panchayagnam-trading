package com.py.dto;

public class PositionLiveData {

    private String tradingSymbol;
    private String symbolToken;
    private String exchange;
    private String productType;
    private int netQty;
    private double avgNetPrice;
    private double ltp;
    private double investedValue;
    private double currentValue;
    private double unrealizedPnl;
    private double pnlPercent;
    private String pnlDirection; // PROFIT / LOSS / NEUTRAL

    // Getters and Setters
    public String getTradingSymbol() {
        return tradingSymbol;
    }

    public void setTradingSymbol(String v) {
        this.tradingSymbol = v;
    }

    public String getSymbolToken() {
        return symbolToken;
    }

    public void setSymbolToken(String v) {
        this.symbolToken = v;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String v) {
        this.exchange = v;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String v) {
        this.productType = v;
    }

    public int getNetQty() {
        return netQty;
    }

    public void setNetQty(int v) {
        this.netQty = v;
    }

    public double getAvgNetPrice() {
        return avgNetPrice;
    }

    public void setAvgNetPrice(double v) {
        this.avgNetPrice = v;
    }

    public double getLtp() {
        return ltp;
    }

    public void setLtp(double v) {
        this.ltp = v;
    }

    public double getInvestedValue() {
        return investedValue;
    }

    public void setInvestedValue(double v) {
        this.investedValue = v;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double v) {
        this.currentValue = v;
    }

    public double getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public void setUnrealizedPnl(double v) {
        this.unrealizedPnl = v;
    }

    public double getPnlPercent() {
        return pnlPercent;
    }

    public void setPnlPercent(double v) {
        this.pnlPercent = v;
    }

    public String getPnlDirection() {
        return pnlDirection;
    }

    public void setPnlDirection(String v) {
        this.pnlDirection = v;
    }
}
