
package com.py.tradegroup.entity;

// PY_GROUP_ORDER_EXECUTION

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "PY_GROUP_ORDER_EXECUTION")
public class GroupOrderExecutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private GroupOrderStockEntity stock;

    @Column(name = "broker_id", nullable = false)
    private Long brokerId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "unique_order_id")
    private String uniqueOrderId;

    @Column(name = "status")
    private String status = "PENDING";  // SUCCESS / FAILED / PENDING

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "broker_type")
    private String brokerType;

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
     * @return the stock
     */
    public GroupOrderStockEntity getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(GroupOrderStockEntity stock) {
        this.stock = stock;
    }

    /**
     * @return the brokerId
     */
    public Long getBrokerId() {
        return brokerId;
    }

    /**
     * @param brokerId the brokerId to set
     */
    public void setBrokerId(Long brokerId) {
        this.brokerId = brokerId;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the uniqueOrderId
     */
    public String getUniqueOrderId() {
        return uniqueOrderId;
    }

    /**
     * @param uniqueOrderId the uniqueOrderId to set
     */
    public void setUniqueOrderId(String uniqueOrderId) {
        this.uniqueOrderId = uniqueOrderId;
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
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the executedAt
     */
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    /**
     * @param executedAt the executedAt to set
     */
    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    /**
     * @return the brokerType
     */
    public String getBrokerType() {
        return brokerType;
    }

    /**
     * @param brokerType the brokerType to set
     */
    public void setBrokerType(String brokerType) {
        this.brokerType = brokerType;
    }
}
