package com.py.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "py_role_upgrade_request_table")
public class PyRoleUpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "py_id", nullable = false)
    private PyUser pyUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", nullable = true)
    private PyUser reviewedBy;

    @Column(name = "requested_role", nullable = false, length = 20)
    private String requestedRole = "ADMIN";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "plan_months")
    private Integer planMonths;

        @Column(name = "plan_amount_per_client")
    private Double amount;

    @Column(name = "no_of_clients")
    private Integer noOfClients;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getNoOfClients() {
        return noOfClients;
    }

    public void setNoOfClients(Integer noOfClients) {
        this.noOfClients = noOfClients;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", length = 20)
    private RequestType requestType = RequestType.ADMIN; // default ADMIN for existing flow

    public enum RequestType {
        ADMIN, CLIENT, GROUP
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        PENDING, APPROVED, REJECTED, REVOKED
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PyUser getPyUser() {
        return pyUser;
    }

    public void setPyUser(PyUser pyUser) {
        this.pyUser = pyUser;
    }

    public PyUser getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(PyUser reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getRequestedRole() {
        return requestedRole;
    }

    public void setRequestedRole(String requestedRole) {
        this.requestedRole = requestedRole;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
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
     * @return the planMonths
     */
    public Integer getPlanMonths() {
        return planMonths;
    }

    /**
     * @param planMonths the planMonths to set
     */
    public void setPlanMonths(Integer planMonths) {
        this.planMonths = planMonths;
    }

    /**
     * @return the requestType
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * @param requestType the requestType to set
     */
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
