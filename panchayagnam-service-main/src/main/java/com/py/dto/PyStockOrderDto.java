package com.py.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PyStockOrderDto {

    @NotNull(message = "tradingSymbol is required")
    private String tradingSymbol;

    @NotNull(message = "symbolToken is required")
    private String symbolToken;

    @NotNull(message = "exchange is required")
    private String exchange;

    private Double price;   // null / 0 for MARKET orders

    @NotNull(message = "brokerQuantities is required")
    private List<PyBrokerQuantityDto> brokerQuantities;

    // Getters & Setters
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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<PyBrokerQuantityDto> getBrokerQuantities() {
        return brokerQuantities;
    }

    public void setBrokerQuantities(List<PyBrokerQuantityDto> brokerQuantities) {
        this.brokerQuantities = brokerQuantities;
    }
}

