package com.py.brokers.motilal.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "motilal_token_refresh_audit")
public class MotilalTokenRefreshAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "demat_account_id")
    private Long dematAccountId;

    @Column(name = "client_id", length = 50)
    private String clientId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "status", length = 20)
    private String status; // SUCCESS / FAILED

    @Column(name = "triggered_by", length = 20)
    private String triggeredBy; // SCHEDULER / MANUAL

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @PrePersist
    void onCreate() { this.attemptedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDematAccountId() { return dematAccountId; }
    public void setDematAccountId(Long dematAccountId) { this.dematAccountId = dematAccountId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}