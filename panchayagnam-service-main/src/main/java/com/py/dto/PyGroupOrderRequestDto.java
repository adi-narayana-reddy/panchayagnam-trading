package com.py.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PyGroupOrderRequestDto {

    @NotNull(message = "groupId is required")
    private Long groupId;

    @NotNull(message = "transactionType is required")
    private String transactionType;   // BUY | SELL

    @NotNull(message = "orderType is required")
    private String orderType;         // MARKET | LIMIT | STOPLOSS_LIMIT …

    @NotNull(message = "productType is required")
    private String productType;       // INTRADAY | DELIVERY | CARRYFORWARD …

    private String variety;           // NORMAL | AMO …

    @Valid
    @NotEmpty(message = "stocks list must not be empty")
    private List<PyStockOrderDto> stocks;  // ← multiple stocks

    // Getters & Setters
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public List<PyStockOrderDto> getStocks() {
        return stocks;
    }

    public void setStocks(List<PyStockOrderDto> stocks) {
        this.stocks = stocks;
    }
}
