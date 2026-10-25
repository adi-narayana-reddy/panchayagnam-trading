package com.py.brokers.profitmart.dto;

// ── SEARCH SCRIP REQUEST ──────────────────────────────────────────────────────
public class ProfitmartSearchRequest extends ProfitmartBaseRequest {

    private String exchange;    // NSE / BSE / NFO / MCX
    private String searchText;  // partial symbol name

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
