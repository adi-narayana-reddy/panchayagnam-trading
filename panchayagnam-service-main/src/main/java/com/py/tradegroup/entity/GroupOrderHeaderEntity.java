package com.py.tradegroup.entity;

// PY_GROUP_ORDER_HEADER

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "PY_GROUP_ORDER_HEADER")
public class GroupOrderHeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;   // BUY / SELL

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "variety", nullable = false)
    private String variety;

    @Column(name = "status")
    private String status = "OPEN";   // OPEN / CLOSED

    @Column(name = "triggered_by")
    private Long triggeredBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupOrderStockEntity> stocks = new ArrayList<>();

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the groupId
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the transactionType
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType the transactionType to set
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * @return the orderType
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * @param orderType the orderType to set
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     * @return the productType
     */
    public String getProductType() {
        return productType;
    }

    /**
     * @param productType the productType to set
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * @return the variety
     */
    public String getVariety() {
        return variety;
    }

    /**
     * @param variety the variety to set
     */
    public void setVariety(String variety) {
        this.variety = variety;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the triggeredBy
     */
    public Long getTriggeredBy() {
        return triggeredBy;
    }

    /**
     * @param triggeredBy the triggeredBy to set
     */
    public void setTriggeredBy(Long triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    /**
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the closedAt
     */
    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    /**
     * @param closedAt the closedAt to set
     */
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    /**
     * @return the stocks
     */
    public List<GroupOrderStockEntity> getStocks() {
        return stocks;
    }

    /**
     * @param stocks the stocks to set
     */
    public void setStocks(List<GroupOrderStockEntity> stocks) {
        this.stocks = stocks;
    }
}
