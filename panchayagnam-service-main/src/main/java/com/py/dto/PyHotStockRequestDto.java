package com.py.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import com.py.common.enums.*;

public class PyHotStockRequestDto {

    private Long pyId;

    // Required for UPDATE and DELETE
    private Long stockId;
    
    private String symbolToken;

    private String symbol;

    private String companyName;

    private BigDecimal price;

    private BigDecimal target;

    private BigDecimal changePercent;

    private HotStockDirection direction;

    private HotStockSignal signal;

    private HotStockCategory category;

    @NotNull(message = "Action type is required")
    private HotStockActionType actionType;

    public HotStockActionType getActionType() {
        return actionType;
    }

    public void setActionType(HotStockActionType actionType) {
        this.actionType = actionType;
    }

    // Getters and Setters
    public Long getPyId() {
        return pyId;
    }

    public void setPyId(Long pyId) {
        this.pyId = pyId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }

    public HotStockDirection getDirection() {
        return direction;
    }

    public void setDirection(HotStockDirection direction) {
        this.direction = direction;
    }

    public HotStockSignal getSignal() {
        return signal;
    }

    public void setSignal(HotStockSignal signal) {
        this.signal = signal;
    }

    public HotStockCategory getCategory() {
        return category;
    }

    public void setCategory(HotStockCategory category) {
        this.category = category;
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
