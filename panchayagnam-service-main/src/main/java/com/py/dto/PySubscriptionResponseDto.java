package com.py.dto;

import java.time.LocalDateTime;

public class PySubscriptionResponseDto {

    private Long subscriptionId;
    private Long userId;
    private String fullName;
    private String type;
    private Integer planMonths;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Boolean isTrial;
    private Long expiresInDays;
    private Boolean expiredFlag;

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPlanMonths() {
        return planMonths;
    }

    public void setPlanMonths(Integer planMonths) {
        this.planMonths = planMonths;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsTrial() {
        return isTrial;
    }

    public void setIsTrial(Boolean isTrial) {
        this.isTrial = isTrial;
    }

    public Long getExpiresInDays() {
        return expiresInDays;
    }

    public void setExpiresInDays(Long expiresInDays) {
        this.expiresInDays = expiresInDays;
    }

    public Boolean getExpiredFlag() {
        return expiredFlag;
    }

    public void setExpiredFlag(Boolean expiredFlag) {
        this.expiredFlag = expiredFlag;
    }
}
