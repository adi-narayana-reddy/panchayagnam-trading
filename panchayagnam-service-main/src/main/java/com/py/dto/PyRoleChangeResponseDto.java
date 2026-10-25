package com.py.dto;

public class PyRoleChangeResponseDto {

    private Long pyId;
    private String fullName;
    private String previousRole;
    private String newRole;
    private long groupsAffected;
    private String message;

    public Long getPyId() { return pyId; }
    public void setPyId(Long pyId) { this.pyId = pyId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPreviousRole() { return previousRole; }
    public void setPreviousRole(String previousRole) { this.previousRole = previousRole; }

    public String getNewRole() { return newRole; }
    public void setNewRole(String newRole) { this.newRole = newRole; }

    public long getGroupsAffected() { return groupsAffected; }
    public void setGroupsAffected(long groupsAffected) { this.groupsAffected = groupsAffected; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}