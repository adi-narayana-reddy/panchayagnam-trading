package com.py.dto;

public class PyBrokerAccountSummaryDto {

    private Long brokerId;
    private String brokerClientId;
    private String userName;
    private Long pyId;
    private Boolean isActive;
    private Boolean isOwn;

    // Getters and Setters
    public Long getBrokerId() { return brokerId; }
    public void setBrokerId(Long brokerId) { this.brokerId = brokerId; }

    public String getBrokerClientId() { return brokerClientId; }
    public void setBrokerClientId(String brokerClientId) { this.brokerClientId = brokerClientId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getPyId() { return pyId; }
    public void setPyId(Long pyId) { this.pyId = pyId; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsOwn() { return isOwn; }
    public void setIsOwn(Boolean isOwn) { this.isOwn = isOwn; }
}