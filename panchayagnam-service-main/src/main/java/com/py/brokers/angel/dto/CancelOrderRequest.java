/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.py.brokers.angel.dto;

/**
 * Request payload for cancelling an existing Angel One order.
 *
 * Frontend sends: { "accId": 2, "orderId": "1234567890", "variety": "NORMAL" }
 */
public class CancelOrderRequest {

    private Long accId;
    private String orderId;
    private String variety;   // NORMAL, STOPLOSS, AMO

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
}
