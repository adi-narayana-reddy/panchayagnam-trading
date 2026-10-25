package com.py.dto;

public class PyDashboardStatsDto {

    // ── User counts (SUPER_ADMIN only) ────────────────────────────────────────
    private long totalSuperAdmins;
    private long totalAdmins;
    private long totalUsers;
    private long totalRegistredAccounts;

    // ── Broker account counts ─────────────────────────────────────────────────
    private long totalBrokerAccounts;
    private long angelOneAccounts;
    private long motilalAccounts;
    private long dhanAccounts;
    private long profitmartAccounts;
    private long goodwillAccounts;

    // ── Trade group counts ────────────────────────────────────────────────────
    private long groupCount;
    private long activeGroupCount;
    private long inactiveGroupCount;

    // ── Role upgrade requests (all types combined) ────────────────────────────
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;

    // ── CLIENT subscription requests ──────────────────────────────────────────
    private long pendingClientRequests;
    private long approvedClientRequests;
    private long rejectedClientRequests;
    private long totalClientRequests;

    // ── GROUP subscription requests ───────────────────────────────────────────
    private long pendingGroupRequests;
    private long approvedGroupRequests;
    private long rejectedGroupRequests;
    private long totalGroupRequests;

    // ── Subscriber status (PySubscription) ───────────────────────────────────
    private long activeSubscribers;
    private long trialSubscribers;
    private long expiredSubscribers;
    private long totalSubscribers;

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public long getTotalSuperAdmins() {
        return totalSuperAdmins;
    }

    public void setTotalSuperAdmins(long totalSuperAdmins) {
        this.totalSuperAdmins = totalSuperAdmins;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalRegistredAccounts() {
        return totalRegistredAccounts;
    }

    public void setTotalRegistredAccounts(long totalRegistredAccounts) {
        this.totalRegistredAccounts = totalRegistredAccounts;
    }

    public long getTotalBrokerAccounts() {
        return totalBrokerAccounts;
    }

    public void setTotalBrokerAccounts(long totalBrokerAccounts) {
        this.totalBrokerAccounts = totalBrokerAccounts;
    }

    public long getAngelOneAccounts() {
        return angelOneAccounts;
    }

    public void setAngelOneAccounts(long angelOneAccounts) {
        this.angelOneAccounts = angelOneAccounts;
    }

    public long getMotilalAccounts() {
        return motilalAccounts;
    }

    public void setMotilalAccounts(long motilalAccounts) {
        this.motilalAccounts = motilalAccounts;
    }

    public long getDhanAccounts() {
        return dhanAccounts;
    }

    public void setDhanAccounts(long dhanAccounts) {
        this.dhanAccounts = dhanAccounts;
    }

    public long getProfitmartAccounts() {
        return profitmartAccounts;
    }

    public void setProfitmartAccounts(long profitmartAccounts) {
        this.profitmartAccounts = profitmartAccounts;
    }

    public long getGoodwillAccounts() {
        return goodwillAccounts;
    }

    public void setGoodwillAccounts(long goodwillAccounts) {
        this.goodwillAccounts = goodwillAccounts;
    }

    public long getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(long groupCount) {
        this.groupCount = groupCount;
    }

    public long getActiveGroupCount() {
        return activeGroupCount;
    }

    public void setActiveGroupCount(long activeGroupCount) {
        this.activeGroupCount = activeGroupCount;
    }

    public long getInactiveGroupCount() {
        return inactiveGroupCount;
    }

    public void setInactiveGroupCount(long inactiveGroupCount) {
        this.inactiveGroupCount = inactiveGroupCount;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public long getApprovedRequests() {
        return approvedRequests;
    }

    public void setApprovedRequests(long approvedRequests) {
        this.approvedRequests = approvedRequests;
    }

    public long getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(long rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }

    public long getPendingClientRequests() {
        return pendingClientRequests;
    }

    public void setPendingClientRequests(long pendingClientRequests) {
        this.pendingClientRequests = pendingClientRequests;
    }

    public long getApprovedClientRequests() {
        return approvedClientRequests;
    }

    public void setApprovedClientRequests(long approvedClientRequests) {
        this.approvedClientRequests = approvedClientRequests;
    }

    public long getRejectedClientRequests() {
        return rejectedClientRequests;
    }

    public void setRejectedClientRequests(long rejectedClientRequests) {
        this.rejectedClientRequests = rejectedClientRequests;
    }

    public long getTotalClientRequests() {
        return totalClientRequests;
    }

    public void setTotalClientRequests(long totalClientRequests) {
        this.totalClientRequests = totalClientRequests;
    }

    public long getPendingGroupRequests() {
        return pendingGroupRequests;
    }

    public void setPendingGroupRequests(long pendingGroupRequests) {
        this.pendingGroupRequests = pendingGroupRequests;
    }

    public long getApprovedGroupRequests() {
        return approvedGroupRequests;
    }

    public void setApprovedGroupRequests(long approvedGroupRequests) {
        this.approvedGroupRequests = approvedGroupRequests;
    }

    public long getRejectedGroupRequests() {
        return rejectedGroupRequests;
    }

    public void setRejectedGroupRequests(long rejectedGroupRequests) {
        this.rejectedGroupRequests = rejectedGroupRequests;
    }

    public long getTotalGroupRequests() {
        return totalGroupRequests;
    }

    public void setTotalGroupRequests(long totalGroupRequests) {
        this.totalGroupRequests = totalGroupRequests;
    }

    public long getActiveSubscribers() {
        return activeSubscribers;
    }

    public void setActiveSubscribers(long activeSubscribers) {
        this.activeSubscribers = activeSubscribers;
    }

    public long getTrialSubscribers() {
        return trialSubscribers;
    }

    public void setTrialSubscribers(long trialSubscribers) {
        this.trialSubscribers = trialSubscribers;
    }

    public long getExpiredSubscribers() {
        return expiredSubscribers;
    }

    public void setExpiredSubscribers(long expiredSubscribers) {
        this.expiredSubscribers = expiredSubscribers;
    }

    public long getTotalSubscribers() {
        return totalSubscribers;
    }

    public void setTotalSubscribers(long totalSubscribers) {
        this.totalSubscribers = totalSubscribers;
    }
}
