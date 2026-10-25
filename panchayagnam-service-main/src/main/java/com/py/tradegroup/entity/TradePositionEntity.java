package com.py.tradegroup.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_positions")
public class TradePositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", length = 100)
    private String groupName;

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

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "buy_price")
    private Double buyPrice;           // actual price at which bought

    @Column(name = "buy_order_id", length = 50)
    private String buyOrderId;         // Angel One order ID from buy

    @Column(name = "product_type", length = 20)
    private String productType;        // DELIVERY / INTRADAY

    @Column(name = "order_type", length = 20)
    private String orderType;          // MARKET / LIMIT

    @Column(name = "status", length = 20)
    private String status;             // OPEN / SOLD / CANCELLED

    @Column(name = "sell_order_id", length = 50)
    private String sellOrderId;        // filled when sold

    @Column(name = "sell_price")
    private Double sellPrice;          // filled when sold

    @Column(name = "bought_at")
    private LocalDateTime boughtAt;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.boughtAt  = LocalDateTime.now();
        this.status    = "OPEN";       // default status on buy
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

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

    public Double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(Double buyPrice) { this.buyPrice = buyPrice; }

    public String getBuyOrderId() { return buyOrderId; }
    public void setBuyOrderId(String buyOrderId) { this.buyOrderId = buyOrderId; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSellOrderId() { return sellOrderId; }
    public void setSellOrderId(String sellOrderId) { this.sellOrderId = sellOrderId; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public LocalDateTime getBoughtAt() { return boughtAt; }
    public void setBoughtAt(LocalDateTime boughtAt) { this.boughtAt = boughtAt; }

    public LocalDateTime getSoldAt() { return soldAt; }
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
