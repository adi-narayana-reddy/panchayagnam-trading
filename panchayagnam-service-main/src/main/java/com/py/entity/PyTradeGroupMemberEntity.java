package com.py.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "py_trade_group_members_table",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_group_broker",
                    columnNames = {"group_id", "broker_account_id"}
            )
        }
)
public class PyTradeGroupMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PyTradeGroupEntity tradeGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_account_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PyBrokerAccountEntity brokerAccount;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @Column(name = "is_linked", nullable = false)
    private Boolean isLinked = true;

    public Boolean getIsLinked() {
        return isLinked;
    }

    public void setIsLinked(Boolean isLinked) {
        this.isLinked = isLinked;
    }

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PyTradeGroupEntity getTradeGroup() {
        return tradeGroup;
    }

    public void setTradeGroup(PyTradeGroupEntity tradeGroup) {
        this.tradeGroup = tradeGroup;
    }

    public PyBrokerAccountEntity getBrokerAccount() {
        return brokerAccount;
    }

    public void setBrokerAccount(PyBrokerAccountEntity brokerAccount) {
        this.brokerAccount = brokerAccount;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
