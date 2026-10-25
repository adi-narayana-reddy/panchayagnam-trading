package com.py.dto;

import java.time.LocalDateTime;

/**
 * Response DTO — full lifecycle view per broker+stock+group.
 */
public class PyGroupTradeLogResponseDto {

    private Long   id;
    private Long   groupId;
    private String groupName;

    // Stock
    private String tradingSymbol;
    private String symbolToken;
    private String exchange;

    // Broker
    private Long   brokerAccountId;
    private String clientId;
    private String userName;
    private String brokerType;
    private String productType;

    // BUY
    private Integer       buyQuantity;
    private Double        buyPrice;
    private String        buyOrderId;
    private String        buyStatus;
    private String        buyRemarks;
    private LocalDateTime boughtAt;

    // SELL
    private Integer       sellQuantity;
    private Double        sellPrice;
    private String        sellOrderId;
    private String        sellStatus;
    private String        sellRemarks;
    private LocalDateTime soldAt;

    // Overall
    private String        status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getTradingSymbol() { return tradingSymbol; }
    public void setTradingSymbol(String tradingSymbol) { this.tradingSymbol = tradingSymbol; }

    public String getSymbolToken() { return symbolToken; }
    public void setSymbolToken(String symbolToken) { this.symbolToken = symbolToken; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public Long getBrokerAccountId() { return brokerAccountId; }
    public void setBrokerAccountId(Long brokerAccountId) { this.brokerAccountId = brokerAccountId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getBrokerType() { return brokerType; }
    public void setBrokerType(String brokerType) { this.brokerType = brokerType; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public Integer getBuyQuantity() { return buyQuantity; }
    public void setBuyQuantity(Integer buyQuantity) { this.buyQuantity = buyQuantity; }

    public Double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(Double buyPrice) { this.buyPrice = buyPrice; }

    public String getBuyOrderId() { return buyOrderId; }
    public void setBuyOrderId(String buyOrderId) { this.buyOrderId = buyOrderId; }

    public String getBuyStatus() { return buyStatus; }
    public void setBuyStatus(String buyStatus) { this.buyStatus = buyStatus; }

    public String getBuyRemarks() { return buyRemarks; }
    public void setBuyRemarks(String buyRemarks) { this.buyRemarks = buyRemarks; }

    public LocalDateTime getBoughtAt() { return boughtAt; }
    public void setBoughtAt(LocalDateTime boughtAt) { this.boughtAt = boughtAt; }

    public Integer getSellQuantity() { return sellQuantity; }
    public void setSellQuantity(Integer sellQuantity) { this.sellQuantity = sellQuantity; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public String getSellOrderId() { return sellOrderId; }
    public void setSellOrderId(String sellOrderId) { this.sellOrderId = sellOrderId; }

    public String getSellStatus() { return sellStatus; }
    public void setSellStatus(String sellStatus) { this.sellStatus = sellStatus; }

    public String getSellRemarks() { return sellRemarks; }
    public void setSellRemarks(String sellRemarks) { this.sellRemarks = sellRemarks; }

    public LocalDateTime getSoldAt() { return soldAt; }
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
