package com.py.tradegroup.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_group_sell_audit")
public class TradeGroupSellAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "demat_account_id", nullable = false)
    private Long dematAccountId;

    @Column(name = "client_id", length = 50)
    private String clientId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "exchange", length = 10)
    private String exchange;

    @Column(name = "trading_symbol", length = 50)
    private String tradingSymbol;

    @Column(name = "symbol_token", length = 20)
    private String symbolToken;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "buy_order_id", length = 50)
    private String buyOrderId;

    @Column(name = "buy_price")
    private Double buyPrice;

    @Column(name = "sell_order_id", length = 50)
    private String sellOrderId;

    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "product_type", length = 20)
    private String productType;

    @Column(name = "status", length = 20)
    private String status;            // SUCCESS / FAILED

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @PrePersist
    void onCreate() { this.executedAt = LocalDateTime.now(); }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }

    public Long getDematAccountId() { return dematAccountId; }
    public void setDematAccountId(Long dematAccountId) { this.dematAccountId = dematAccountId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getTradingSymbol() { return tradingSymbol; }
    public void setTradingSymbol(String tradingSymbol) { this.tradingSymbol = tradingSymbol; }

    public String getSymbolToken() { return symbolToken; }
    public void setSymbolToken(String symbolToken) { this.symbolToken = symbolToken; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getBuyOrderId() { return buyOrderId; }
    public void setBuyOrderId(String buyOrderId) { this.buyOrderId = buyOrderId; }

    public Double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(Double buyPrice) { this.buyPrice = buyPrice; }

    public String getSellOrderId() { return sellOrderId; }
    public void setSellOrderId(String sellOrderId) { this.sellOrderId = sellOrderId; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}
