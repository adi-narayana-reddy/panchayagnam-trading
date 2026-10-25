package com.py.dto;

import com.py.common.enums.HotStockCategory;
import com.py.common.enums.HotStockDirection;
import com.py.common.enums.HotStockSignal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PyHotStockResponseDto {

    private Long stockId;
    private String symbol;
    private String companyName;
    private BigDecimal price;
    private BigDecimal target;
    private BigDecimal changePercent;
    private HotStockDirection direction;
    private HotStockSignal signal;
    private HotStockCategory category;
    private Long recommendedByPyId;
    private String recommendedByName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String symbolToken;

    // Getters and Setters
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

    public Long getRecommendedByPyId() {
        return recommendedByPyId;
    }

    public void setRecommendedByPyId(Long recommendedByPyId) {
        this.recommendedByPyId = recommendedByPyId;
    }

    public String getRecommendedByName() {
        return recommendedByName;
    }

    public void setRecommendedByName(String recommendedByName) {
        this.recommendedByName = recommendedByName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the direction
     */
    public HotStockDirection getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(HotStockDirection direction) {
        this.direction = direction;
    }

    /**
     * @return the signal
     */
    public HotStockSignal getSignal() {
        return signal;
    }

    /**
     * @param signal the signal to set
     */
    public void setSignal(HotStockSignal signal) {
        this.signal = signal;
    }

    /**
     * @return the category
     */
    public HotStockCategory getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
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
