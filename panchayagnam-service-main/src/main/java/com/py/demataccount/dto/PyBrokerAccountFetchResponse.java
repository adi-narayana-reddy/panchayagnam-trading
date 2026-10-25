package com.py.demataccount.dto;

import com.py.common.enums.BrokerType;
import java.time.LocalDateTime;
import java.util.List;

public class PyBrokerAccountFetchResponse {

    private Long id;
    private Long pyId;
    private String clientId;
    private String clientName;
    private String nickName;
    private BrokerType brokerName;
    private Boolean isAdmin;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Double availableCash;

    // Secret fields for frontend pre-fill
    private String apiKey;
    private String mpin;
    private String totpSecret;
    private String password;

    // Child IDs for admin accounts
    private List<Long> childIds;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPyId() { return pyId; }
    public void setPyId(Long pyId) { this.pyId = pyId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public BrokerType getBrokerName() { return brokerName; }
    public void setBrokerName(BrokerType brokerName) { this.brokerName = brokerName; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Double getAvailableCash() { return availableCash; }
    public void setAvailableCash(Double availableCash) { this.availableCash = availableCash; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getMpin() { return mpin; }
    public void setMpin(String mpin) { this.mpin = mpin; }

    public String getTotpSecret() { return totpSecret; }
    public void setTotpSecret(String totpSecret) { this.totpSecret = totpSecret; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Long> getChildIds() { return childIds; }
    public void setChildIds(List<Long> childIds) { this.childIds = childIds; }
}