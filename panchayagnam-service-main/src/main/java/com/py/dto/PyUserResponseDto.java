package com.py.dto;

import java.time.LocalDateTime;

public class PyUserResponseDto {

    private Long pyId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private long accountsCount=0;
    private long activeAccounts=0;

    public PyUserResponseDto() {
    }

    public Long getPyId() {
        return pyId;
    }

    public void setPyId(Long pyId) {
        this.pyId = pyId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the accountsCount
     */
    public long getAccountsCount() {
        return accountsCount;
    }

    /**
     * @param accountsCount the accountsCount to set
     */
    public void setAccountsCount(long accountsCount) {
        this.accountsCount = accountsCount;
    }

    /**
     * @return the activeAccounts
     */
    public long getActiveAccounts() {
        return activeAccounts;
    }

    /**
     * @param activeAccounts the activeAccounts to set
     */
    public void setActiveAccounts(long activeAccounts) {
        this.activeAccounts = activeAccounts;
    }
}
