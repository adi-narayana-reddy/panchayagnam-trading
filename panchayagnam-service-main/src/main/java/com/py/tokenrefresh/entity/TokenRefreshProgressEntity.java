package com.py.tokenrefresh.entity;


import com.py.common.enums.BrokerType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "py_token_refresh_history")
@Data
public class TokenRefreshProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "broker_account_id")
    private Long brokerAccountId;

    @Column(name = "client_id")
    private String clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "broker_type")
    private BrokerType brokerType;

    @Column(name = "status")
    private String status;          // SUCCESS / FAILED / SKIPPED

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "refresh_date")
    private LocalDateTime refreshDate;

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
     * @return the brokerAccountId
     */
    public Long getBrokerAccountId() {
        return brokerAccountId;
    }

    /**
     * @param brokerAccountId the brokerAccountId to set
     */
    public void setBrokerAccountId(Long brokerAccountId) {
        this.brokerAccountId = brokerAccountId;
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
     * @return the refreshDate
     */
    public LocalDateTime getRefreshDate() {
        return refreshDate;
    }

    /**
     * @param refreshDate the refreshDate to set
     */
    public void setRefreshDate(LocalDateTime refreshDate) {
        this.refreshDate = refreshDate;
    }
}