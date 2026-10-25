package com.py.angel.dto;

/**
 * Request payload for modifying an existing Angel One order.
 *
 * Frontend sends: { "accId": 2, "orderId": "1234567890", "variety": "NORMAL",
 * "exchange": "NSE", "tradingSymbol":"BEL-EQ", "symbolToken": "383",
 * "orderType": "LIMIT", "productType": "DELIVERY", "quantity": 5, "price":
 * 435.00, "duration": "DAY" }
 */
public class ModifyOrderRequest {

    private Long accId;
    private String orderId;
    private String variety;        // NORMAL, STOPLOSS, AMO
    private String exchange;       // NSE, BSE, NFO
    private String tradingSymbol;
    private String symbolToken;
    private String orderType;      // LIMIT, MARKET, STOPLOSS_LIMIT, STOPLOSS_MARKET
    private String productType;    // DELIVERY, INTRADAY, CARRYFORWARD
    private Integer quantity;
    private Double price;
    private Double triggerPrice;   // optional — only for STOPLOSS orders
    private String duration;       // DAY (default)
    private String lastModifiedTime;
    private Integer qtyTradedToday;

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getAccId() {
        return accId;
    }

    public void setAccId(Long accId) {
        this.accId = accId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
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

    public String getSymbolToken() {
        return symbolToken;
    }

    public void setSymbolToken(String symbolToken) {
        this.symbolToken = symbolToken;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * @return the lastModifiedTime
     */
    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * @param lastModifiedTime the lastModifiedTime to set
     */
    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * @return the qtyTradedToday
     */
    public Integer getQtyTradedToday() {
        return qtyTradedToday;
    }

    /**
     * @param qtyTradedToday the qtyTradedToday to set
     */
    public void setQtyTradedToday(Integer qtyTradedToday) {
        this.qtyTradedToday = qtyTradedToday;
    }
}
