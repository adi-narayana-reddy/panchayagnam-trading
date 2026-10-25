package com.py.dto;

import java.util.List;

public class PyRoleUpgradeRequestSummaryDto {

    private long totalCount;
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;

    private List<PyRoleUpgradeResponseDto> pending;
    private List<PyRoleUpgradeResponseDto> approved;
    private List<PyRoleUpgradeResponseDto> rejected;

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public long getPendingCount() { return pendingCount; }
    public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }

    public long getApprovedCount() { return approvedCount; }
    public void setApprovedCount(long approvedCount) { this.approvedCount = approvedCount; }

    public long getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(long rejectedCount) { this.rejectedCount = rejectedCount; }

    public List<PyRoleUpgradeResponseDto> getPending() { return pending; }
    public void setPending(List<PyRoleUpgradeResponseDto> pending) { this.pending = pending; }

    public List<PyRoleUpgradeResponseDto> getApproved() { return approved; }
    public void setApproved(List<PyRoleUpgradeResponseDto> approved) { this.approved = approved; }

    public List<PyRoleUpgradeResponseDto> getRejected() { return rejected; }
    public void setRejected(List<PyRoleUpgradeResponseDto> rejected) { this.rejected = rejected; }
}