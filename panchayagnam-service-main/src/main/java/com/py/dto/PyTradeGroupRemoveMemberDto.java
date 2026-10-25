package com.py.dto;

import jakarta.validation.constraints.NotNull;

public class PyTradeGroupRemoveMemberDto {

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Broker account ID is required")
    private Long brokerAccountId;

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getBrokerAccountId() { return brokerAccountId; }
    public void setBrokerAccountId(Long brokerAccountId) { this.brokerAccountId = brokerAccountId; }
}