package com.py.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PyTradeGroupResponseDto {

    private Long groupId;
    private String groupName;
    private String description;
    private Long createdByPyId;
    private String createdByName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long memberCount;
    private List<PyTradeGroupMemberResponseDto> members;

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCreatedByPyId() { return createdByPyId; }
    public void setCreatedByPyId(Long createdByPyId) { this.createdByPyId = createdByPyId; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getMemberCount() { return memberCount; }
    public void setMemberCount(Long memberCount) { this.memberCount = memberCount; }

    public List<PyTradeGroupMemberResponseDto> getMembers() { return members; }
    public void setMembers(List<PyTradeGroupMemberResponseDto> members) { this.members = members; }
}