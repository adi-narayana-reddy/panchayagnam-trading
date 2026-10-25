package com.py.angel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** A wrapper for order params to be sent while placing an order. */
@Schema(description = "Order parameters for placing/modifying an order")
public class OrderParams {

	@Schema(description = "Order ID (used for modify)", example = "210426000000011")
	private String orderid;

	@Schema(description = "Exchange in which instrument is listed", example = "NSE", allowableValues = {"NSE", "BSE", "NFO", "BFO", "CDS", "MCX"})
	private String exchange;

	@Schema(description = "Symbol token of the instrument", example = "3045")
	private String symbolToken;

	@Schema(description = "Transaction type", example = "BUY", allowableValues = {"BUY", "SELL"})
	private String transactiontype;

	@Schema(description = "Order quantity", example = "1")
	private Integer quantity;

	@Schema(description = "Order price", example = "850.50")
	private Double price;

	@Schema(description = "Product type code", example = "CNC", allowableValues = {"NRML", "MIS", "CNC"})
	private String producttype;

	@Schema(description = "Order type", example = "LIMIT", allowableValues = {"LIMIT", "SL", "SL-M", "MARKET"})
	private String ordertype;

	@Schema(description = "Order duration", example = "DAY", allowableValues = {"DAY", "IOC"})
	private String duration;

	@Schema(description = "Order variety", example = "NORMAL")
	private String variety;

	@Schema(description = "Trading symbol", example = "SBIN-EQ")
	private String tradingsymbol;

	@Schema(description = "Trigger price for SL orders", example = "0")
	private String triggerprice;

	@Schema(description = "Square off value (for BO orders)", example = "0")
	private String squareoff;

	@Schema(description = "Stop loss value (for BO orders)", example = "0")
	private String stoploss;

	@Schema(description = "Symbol token", example = "3045")
	private String symboltoken;

    /**
     * @return the orderid
     */
    public String getOrderid() {
        return orderid;
    }

    /**
     * @param orderid the orderid to set
     */
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    /**
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * @param exchange the exchange to set
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the symbolToken
     */
    public String getSymbolToken() {
        return symbolToken;
    }

    /**
     * @param symbolToken the symbolToken to set
     */
    public void setSymbolToken(String symbolToken) {
        this.symbolToken = symbolToken;
    }

    /**
     * @return the transactiontype
     */
    public String getTransactiontype() {
        return transactiontype;
    }

    /**
     * @param transactiontype the transactiontype to set
     */
    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype;
    }

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the producttype
     */
    public String getProducttype() {
        return producttype;
    }

    /**
     * @param producttype the producttype to set
     */
    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    /**
     * @return the ordertype
     */
    public String getOrdertype() {
        return ordertype;
    }

    /**
     * @param ordertype the ordertype to set
     */
    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    /**
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * @return the variety
     */
    public String getVariety() {
        return variety;
    }

    /**
     * @param variety the variety to set
     */
    public void setVariety(String variety) {
        this.variety = variety;
    }

    /**
     * @return the tradingsymbol
     */
    public String getTradingsymbol() {
        return tradingsymbol;
    }

    /**
     * @param tradingsymbol the tradingsymbol to set
     */
    public void setTradingsymbol(String tradingsymbol) {
        this.tradingsymbol = tradingsymbol;
    }

    /**
     * @return the triggerprice
     */
    public String getTriggerprice() {
        return triggerprice;
    }

    /**
     * @param triggerprice the triggerprice to set
     */
    public void setTriggerprice(String triggerprice) {
        this.triggerprice = triggerprice;
    }

    /**
     * @return the squareoff
     */
    public String getSquareoff() {
        return squareoff;
    }

    /**
     * @param squareoff the squareoff to set
     */
    public void setSquareoff(String squareoff) {
        this.squareoff = squareoff;
    }

    /**
     * @return the stoploss
     */
    public String getStoploss() {
        return stoploss;
    }

    /**
     * @param stoploss the stoploss to set
     */
    public void setStoploss(String stoploss) {
        this.stoploss = stoploss;
    }

    /**
     * @return the symboltoken
     */
    public String getSymboltoken() {
        return symboltoken;
    }

    /**
     * @param symboltoken the symboltoken to set
     */
    public void setSymboltoken(String symboltoken) {
        this.symboltoken = symboltoken;
    }
	
	

}
