package com.py.dto;

import com.py.common.enums.GroupActionType;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PyTradeGroupRequestDto {

    @NotNull(message = "Action type is required")
    private GroupActionType actionType;

    // Required for CREATE, UPDATE, DELETE
    private Long pyId;

    // Required for UPDATE, DELETE, ADD_MEMBERS, REMOVE_MEMBER
    private Long groupId;

    // Required for CREATE, UPDATE
    private String groupName;

    // Optional for CREATE, UPDATE
    private String description;

    // Required for CREATE(optional), ADD_MEMBERS
    private List<Long> brokerAccountIds;

    // Required for REMOVE_MEMBER
    private Long brokerAccountId;

    public GroupActionType getActionType() { return actionType; }
    public void setActionType(GroupActionType actionType) { this.actionType = actionType; }

    public Long getPyId() { return pyId; }
    public void setPyId(Long pyId) { this.pyId = pyId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getBrokerAccountIds() { return brokerAccountIds; }
    public void setBrokerAccountIds(List<Long> brokerAccountIds) { this.brokerAccountIds = brokerAccountIds; }

    public Long getBrokerAccountId() { return brokerAccountId; }
    public void setBrokerAccountId(Long brokerAccountId) { this.brokerAccountId = brokerAccountId; }
}