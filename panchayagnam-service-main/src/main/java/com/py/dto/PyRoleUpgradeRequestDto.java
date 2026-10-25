package com.py.dto;

import jakarta.validation.constraints.NotBlank;

public class PyRoleUpgradeRequestDto {

    @NotBlank(message = "Reason is required")
    private String reason;
    private Integer planMonths;
    private String requestType;
    private double amount;
    private Integer noOfClients;

    public Integer getNoOfClients() {
        return noOfClients;
    }

    public void setNoOfClients(Integer noOfClients) {
        this.noOfClients = noOfClients;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPlanMonths() {
        return planMonths;
    }

    public void setPlanMonths(Integer planMonths) {
        this.planMonths = planMonths;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public PyRoleUpgradeRequestDto() {
    }

    public PyRoleUpgradeRequestDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
