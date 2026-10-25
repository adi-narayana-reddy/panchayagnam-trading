package com.py.brokers.profitmart.dto;

// ── QUOTES REQUEST ────────────────────────────────────────────────────────────
public class ProfitmartQuotesRequest extends ProfitmartBaseRequest {

    private String exchange;
    private String token;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
