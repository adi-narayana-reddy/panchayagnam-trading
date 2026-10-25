package com.py.brokers.profitmart.dto;

// ── CANCEL ORDER REQUEST ──────────────────────────────────────────────────────
public class ProfitmartCancelOrderRequest extends ProfitmartBaseRequest {
    private String orderNo;   // norenordno to cancel

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}