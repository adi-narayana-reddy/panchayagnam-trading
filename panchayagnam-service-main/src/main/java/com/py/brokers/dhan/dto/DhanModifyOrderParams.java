/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.py.brokers.dhan.dto;

public class DhanModifyOrderParams {

    /** Required – Dhan client ID */
    private String dhanClientId;

    /** Required – Order ID to modify */
    private String orderId;

    /**
     * Required – Order type after modification.
     * Allowed: LIMIT | MARKET | STOP_LOSS | STOP_LOSS_MARKET
     */
    private String orderType;

    /** Required (conditionally) – New quantity */
    private int quantity;

    /** Required (conditionally) – New price (for LIMIT orders) */
    private double price;

    /**
     * Optional – Number of shares to disclose.
     * Must be >= 30% of quantity if used.
     */
    private int disclosedQuantity;

    /**
     * Required (conditionally) – Trigger price for SL / SL-M orders.
     * Send 0 or omit for LIMIT / MARKET.
     */
    private double triggerPrice;

    /**
     * Required – Validity of the modified order.
     * Allowed: DAY | IOC
     */
    private String validity;

    /**
     * @return the dhanClientId
     */
    public String getDhanClientId() {
        return dhanClientId;
    }

    /**
     * @param dhanClientId the dhanClientId to set
     */
    public void setDhanClientId(String dhanClientId) {
        this.dhanClientId = dhanClientId;
    }

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the orderType
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * @param orderType the orderType to set
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the disclosedQuantity
     */
    public int getDisclosedQuantity() {
        return disclosedQuantity;
    }

    /**
     * @param disclosedQuantity the disclosedQuantity to set
     */
    public void setDisclosedQuantity(int disclosedQuantity) {
        this.disclosedQuantity = disclosedQuantity;
    }

    /**
     * @return the triggerPrice
     */
    public double getTriggerPrice() {
        return triggerPrice;
    }

    /**
     * @param triggerPrice the triggerPrice to set
     */
    public void setTriggerPrice(double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    /**
     * @return the validity
     */
    public String getValidity() {
        return validity;
    }

    /**
     * @param validity the validity to set
     */
    public void setValidity(String validity) {
        this.validity = validity;
    }
}