package com.py.tokenrefresh.entity;

import com.py.common.enums.BrokerType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "py_token_refresh_schedule")
public class PyTokenRefreshScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "broker_type", nullable = false, unique = true, length = 20)
    private BrokerType brokerType;

    @Column(name = "next_trigger_time", nullable = false)
    private LocalDateTime nextTriggerTime;

    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;

    @Column(name = "last_status", length = 20)
    private String lastStatus;

    @Column(name = "last_remarks", length = 500)
    private String lastRemarks;

    @Column(name = "notify_emails", length = 1000)
    private String notifyEmails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BrokerType getBrokerType() {
        return brokerType;
    }

    public void setBrokerType(BrokerType brokerType) {
        this.brokerType = brokerType;
    }

    public LocalDateTime getNextTriggerTime() {
        return nextTriggerTime;
    }

    public void setNextTriggerTime(LocalDateTime nextTriggerTime) {
        this.nextTriggerTime = nextTriggerTime;
    }

    public LocalDateTime getLastExecutedAt() {
        return lastExecutedAt;
    }

    public void setLastExecutedAt(LocalDateTime lastExecutedAt) {
        this.lastExecutedAt = lastExecutedAt;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getLastRemarks() {
        return lastRemarks;
    }

    public void setLastRemarks(String lastRemarks) {
        this.lastRemarks = lastRemarks;
    }

    /**
     * @return the notifyEmails
     */
    public List<String> getNotifyEmailList() {
        if (notifyEmails == null || notifyEmails.isBlank()) {
            return new ArrayList<>();
        }
        return List.of(notifyEmails.split(","));
    }

    /**
     * @param notifyEmails the notifyEmails to set
     */
    public void setNotifyEmails(String notifyEmails) {
        this.notifyEmails = notifyEmails;
    }
}
