package com.py.demataccount.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.py.common.enums.BrokerType;
import com.py.entity.PyUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "py_broker_master_accounts",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_client_id", columnNames = "client_id")
        },
        indexes = {
            @Index(name = "idx_username", columnList = "username")
        }
)
@Data
public class PyBrokerAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "py_broker_id")
    private Long id;

    @Column(name = "client_id", nullable = false, length = 50)
    private String clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "py_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PyUser pyUser;

    @Column(name = "username", nullable = false, length = 100)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "broker_type", nullable = false, length = 20)
    private BrokerType brokerType;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_status", length = 20)
    private String lastLoginStatus;   // SUCCESS / FAILED

    @Column(name = "last_login_error", columnDefinition = "TEXT")
    private String lastLoginError;    // error reason if failed

    @Column(name = "last_login_attempt")
    private LocalDateTime lastLoginAttempt;

    @OneToOne(mappedBy = "brokerAccount",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PyBrokerSecret secret;

    @OneToOne(mappedBy = "brokerAccount",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PyBrokerToken token;

    @Column(name = "REGISTERED_IP", nullable = true)
    private String brokerRegisteredIp;


    @Column(name = "is_own", nullable = false)
    private Boolean isOwn = true;

    @PrePersist
    void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
    }

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
     * @return the pyUser
     */
    public PyUser getPyUser() {
        return pyUser;
    }

    /**
     * @param pyUser the pyUser to set
     */
    public void setPyUser(PyUser pyUser) {
        this.pyUser = pyUser;
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
     * @return the brokerType
     */
    public BrokerType getBrokerType() {
        return brokerType;
    }

    /**
     * @param brokerType the brokerType to set
     */
    public void setBrokerType(BrokerType brokerType) {
        this.brokerType = brokerType;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
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
     * @return the lastLoginStatus
     */
    public String getLastLoginStatus() {
        return lastLoginStatus;
    }

    /**
     * @param lastLoginStatus the lastLoginStatus to set
     */
    public void setLastLoginStatus(String lastLoginStatus) {
        this.lastLoginStatus = lastLoginStatus;
    }

    /**
     * @return the lastLoginError
     */
    public String getLastLoginError() {
        return lastLoginError;
    }

    /**
     * @param lastLoginError the lastLoginError to set
     */
    public void setLastLoginError(String lastLoginError) {
        this.lastLoginError = lastLoginError;
    }

    /**
     * @return the lastLoginAttempt
     */
    public LocalDateTime getLastLoginAttempt() {
        return lastLoginAttempt;
    }

    /**
     * @param lastLoginAttempt the lastLoginAttempt to set
     */
    public void setLastLoginAttempt(LocalDateTime lastLoginAttempt) {
        this.lastLoginAttempt = lastLoginAttempt;
    }

    /**
     * @return the secret
     */
    public PyBrokerSecret getSecret() {
        return secret;
    }

    /**
     * @param secret the secret to set
     */
    public void setSecret(PyBrokerSecret secret) {
        this.secret = secret;
    }

    /**
     * @return the token
     */
    public PyBrokerToken getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(PyBrokerToken token) {
        this.token = token;
    }

    /**
     * @return the brokerRegisteredIp
     */
    public String getBrokerRegisteredIp() {
        return brokerRegisteredIp;
    }

    /**
     * @param brokerRegisteredIp the brokerRegisteredIp to set
     */
    public void setBrokerRegisteredIp(String brokerRegisteredIp) {
        this.brokerRegisteredIp = brokerRegisteredIp;
    }

    /**
     * @return the isOwn
     */
    public Boolean getIsOwn() {
        return isOwn;
    }

    /**
     * @param isOwn the isOwn to set
     */
    public void setIsOwn(Boolean isOwn) {
        this.isOwn = isOwn;
    }
}
