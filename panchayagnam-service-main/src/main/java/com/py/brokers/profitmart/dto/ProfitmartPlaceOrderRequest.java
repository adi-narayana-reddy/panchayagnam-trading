package com.py.brokers.profitmart.dto;

// ── PLACE ORDER REQUEST ───────────────────────────────────────────────────────
public class ProfitmartPlaceOrderRequest extends ProfitmartBaseRequest {

    private String exchange;       // NSE / BSE / NFO / MCX
    private String tradingSymbol;  // e.g. INFY-EQ
    private String buyOrSell;      // B / S
    private int quantity;
    private int discloseQty;
    private String priceType;      // LMT / MKT / SL-LMT / SL-MKT
    private double price;
    private Double triggerPrice;
    private String productType;    // C / M / H / B
    private String retention;      // DAY / IOC / EOS
    private String remarks;
    private String amo;            // Yes / No

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

    public String getBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        this.buyOrSell = buyOrSell;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDiscloseQty() {
        return discloseQty;
    }

    public void setDiscloseQty(int discloseQty) {
        this.discloseQty = discloseQty;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(Double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getRetention() {
        return retention;
    }

    public void setRetention(String retention) {
        this.retention = retention;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAmo() {
        return amo;
    }

    public void setAmo(String amo) {
        this.amo = amo;
    }
}
