package com.py.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PyTradeGroupMemberRequestDto {

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Broker account IDs are required")
    private List<Long> brokerAccountIds;

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public List<Long> getBrokerAccountIds() { return brokerAccountIds; }
    public void setBrokerAccountIds(List<Long> brokerAccountIds) { this.brokerAccountIds = brokerAccountIds; }
}