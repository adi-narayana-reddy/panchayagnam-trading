package com.py.brokers.profitmart.dto;

// ── SINGLE ORDER HISTORY REQUEST ──────────────────────────────────────────────
public class ProfitmartOrderHistoryRequest extends ProfitmartBaseRequest {

    private String orderNo;   // norenordno

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
