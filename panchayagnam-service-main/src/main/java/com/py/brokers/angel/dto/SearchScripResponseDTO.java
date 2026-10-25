package com.py.brokers.angel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchScripResponseDTO {

    @JsonProperty("tradingsymbol")
    private String tradingSymbol;

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("symboltoken")
    private String symbolToken;

    /**
     * @return the tradingSymbol
     */
    public String getTradingSymbol() {
        return tradingSymbol;
    }

    /**
     * @param tradingSymbol the tradingSymbol to set
     */
    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
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

}

