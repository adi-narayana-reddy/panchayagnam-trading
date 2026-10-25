package com.py.tradegroup.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PY_GROUP_TRADE_LOG
 *
 * One row = one position lifecycle per (group + broker + stock).
 *
 * Lifecycle:
 *   BUY placed  → status = BUY_SUCCESS or BUY_FAILED
 *   SELL placed → same row updated → status = SELL_SUCCESS or SELL_FAILED
 */
@Entity
@Table(name = "PY_GROUP_TRADE_LOG")
public class PyGroupTradeLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Group ────────────────────────────────────────────────────────────────
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    // ── Stock ────────────────────────────────────────────────────────────────
    @Column(name = "trading_symbol", nullable = false)
    private String tradingSymbol;

    @Column(name = "symbol_token")
    private String symbolToken;

    @Column(name = "exchange")
    private String exchange;

    // ── Broker / Account ─────────────────────────────────────────────────────
    @Column(name = "broker_account_id", nullable = false)
    private Long brokerAccountId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "broker_type")
    private String brokerType;

    @Column(name = "product_type")
    private String productType;

    // ── BUY Details ──────────────────────────────────────────────────────────
    @Column(name = "buy_quantity")
    private Integer buyQuantity;

    @Column(name = "buy_price")
    private Double buyPrice;

    @Column(name = "buy_order_id")
    private String buyOrderId;

    @Column(name = "buy_status")
    private String buyStatus;        // SUCCESS / FAILED

    @Column(name = "buy_remarks", length = 1000)
    private String buyRemarks;       // failure reason for BUY

    @Column(name = "bought_at")
    private LocalDateTime boughtAt;

    // ── SELL Details ─────────────────────────────────────────────────────────
    @Column(name = "sell_quantity")
    private Integer sellQuantity;

    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "sell_order_id")
    private String sellOrderId;

    @Column(name = "sell_status")
    private String sellStatus;       // SUCCESS / FAILED / PENDING

    @Column(name = "sell_remarks", length = 1000)
    private String sellRemarks;      // failure reason for SELL

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    // ── Overall Status ───────────────────────────────────────────────────────
    // BUY_SUCCESS → SELL_SUCCESS (fully closed)
    // BUY_SUCCESS → SELL_FAILED  (sell failed)
    // BUY_SUCCESS → SELL_PENDING (waiting for sell)
    // BUY_FAILED  (never reached sell)
    @Column(name = "status", nullable = false)
    private String status = "BUY_PENDING";

    // ── Timestamps ───────────────────────────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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
