package com.py.tradegroup.entity;

import com.py.common.enums.BrokerType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "py_group_position_table")
public class PyGroupPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "broker_account_id", nullable = false)
    private Long brokerAccountId;

    @Column(name = "client_id", length = 50)
    private String clientId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "broker_type", length = 20)
    private BrokerType brokerType;

    @Column(name = "trading_symbol", length = 50)
    private String tradingSymbol;

    @Column(name = "symbol_token", length = 20)
    private String symbolToken;

    @Column(name = "exchange", length = 10)
    private String exchange;

    @Column(name = "order_type", length = 20)
    private String orderType;

    @Column(name = "product_type", length = 20)
    private String productType;

    @Column(name = "buy_quantity")
    private Integer buyQuantity;

    @Column(name = "buy_price")
    private Double buyPrice;

    @Column(name = "buy_order_id", length = 50)
    private String buyOrderId;

    @Column(name = "sell_quantity")
    private Integer sellQuantity;

    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "sell_order_id", length = 50)
    private String sellOrderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status = Status.OPEN;

    // ✅ Added — stores error reason for FAILED orders
    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "bought_at")
    private LocalDateTime boughtAt;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Status {
        OPEN,       // bought, not sold yet
        CLOSED,     // fully sold
        PARTIAL,    // partially sold
        FAILED      // buy failed
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getBrokerAccountId() { return brokerAccountId; }
    public void setBrokerAccountId(Long brokerAccountId) { this.brokerAccountId = brokerAccountId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public BrokerType getBrokerType() { return brokerType; }
    public void setBrokerType(BrokerType brokerType) { this.brokerType = brokerType; }

    public String getTradingSymbol() { return tradingSymbol; }
    public void setTradingSymbol(String tradingSymbol) { this.tradingSymbol = tradingSymbol; }

    public String getSymbolToken() { return symbolToken; }
    public void setSymbolToken(String symbolToken) { this.symbolToken = symbolToken; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public Integer getBuyQuantity() { return buyQuantity; }
    public void setBuyQuantity(Integer buyQuantity) { this.buyQuantity = buyQuantity; }

    public Double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(Double buyPrice) { this.buyPrice = buyPrice; }

    public String getBuyOrderId() { return buyOrderId; }
    public void setBuyOrderId(String buyOrderId) { this.buyOrderId = buyOrderId; }

    public Integer getSellQuantity() { return sellQuantity; }
    public void setSellQuantity(Integer sellQuantity) { this.sellQuantity = sellQuantity; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public String getSellOrderId() { return sellOrderId; }
    public void setSellOrderId(String sellOrderId) { this.sellOrderId = sellOrderId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getBoughtAt() { return boughtAt; }
    public void setBoughtAt(LocalDateTime boughtAt) { this.boughtAt = boughtAt; }

    public LocalDateTime getSoldAt() { return soldAt; }
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
