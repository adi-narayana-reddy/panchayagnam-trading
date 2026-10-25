package com.py.service;

import com.py.common.dto.ApiResponse;
import com.py.dto.PyDashboardStatsDto;
import com.py.entity.PyUser;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.demataccount.repository.PyTradeGroupRepository;
import com.py.dto.BrokerTypeCountProjection;
import com.py.dto.RequestCountProjection;
import com.py.dto.RoleCountProjection;
import com.py.dto.SubscriptionCountProjection;
import com.py.repo.PyRoleUpgradeRequestRepository;
import com.py.repo.PySubscriptionRepository;
import com.py.repo.PyUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * PyDashboardService — optimised: one query per table regardless of role.
 *
 * Query count per role:
 *   SUPER_ADMIN — 6 queries  (was 15+)
 *     1. PyUser              GROUP BY role
 *     2. PyBrokerAccount     GROUP BY brokerType WHERE active
 *     3. PyTradeGroup        COUNT + SUM active/inactive
 *     4. PyRoleUpgradeRequest GROUP BY status, requestType
 *     5. PySubscription      GROUP BY status
 *     6. PyUser              findById (auth lookup, unavoidable)
 *
 *   ADMIN / USER — 5 queries  (was 10+)
 *     1. PyUser              findById
 *     2. PyBrokerAccount     GROUP BY brokerType WHERE active AND userId
 *     3. PyTradeGroup        COUNT + SUM WHERE createdBy  (ADMIN only)
 *     4. PyRoleUpgradeRequest GROUP BY status, requestType WHERE userId
 *     5. PySubscription      GROUP BY status WHERE userId
 */
@Service
public class PyDashboardService {

    private static final Logger log = LoggerFactory.getLogger(PyDashboardService.class);

    @Autowired private PyUserRepository              pyUserRepository;
    @Autowired private PyBrokerAccountRepository     pyBrokerAccountRepository;
    @Autowired private PyRoleUpgradeRequestRepository pyRoleUpgradeRequestRepository;
    @Autowired private PyTradeGroupRepository        pyTradeGroupRepository;
    @Autowired private PySubscriptionRepository      pySubscriptionRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Entry point
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ApiResponse<PyDashboardStatsDto> getDashboardStats(Long pyId) {
        try {
            PyUser user = pyUserRepository.findById(pyId)
                    .orElseThrow(() -> new RuntimeException("User not found with pyId: " + pyId));

            PyDashboardStatsDto stats = switch (user.getRole()) {
                case SUPER_ADMIN -> getSuperAdminStats();
                case ADMIN       -> getAdminStats(user);
                case USER        -> getUserStats(user);
            };

            stats.setTotalRegistredAccounts(
                    stats.getTotalSuperAdmins() + stats.getTotalAdmins() + stats.getTotalUsers());

            return ApiResponse.success("Dashboard stats fetched successfully", stats);

        } catch (Exception e) {
            log.error("Failed to fetch dashboard stats: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to fetch dashboard stats: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUPER ADMIN — 5 aggregated queries
    // ─────────────────────────────────────────────────────────────────────────

    private PyDashboardStatsDto getSuperAdminStats() {
        PyDashboardStatsDto stats = new PyDashboardStatsDto();

        // ── 1. User counts — one query, GROUP BY role ─────────────────────────
        applyRoleCounts(stats, pyUserRepository.countAllByRole());

        // ── 2. Broker accounts — one query, GROUP BY brokerType ───────────────
        applyBrokerCounts(stats, pyBrokerAccountRepository.countAllActivByBrokerType());

        // ── 3. Trade groups — one query, total + active + inactive ────────────
        applyGroupCounts(stats, pyTradeGroupRepository.countTotalActiveInactive());

        // ── 4. Role upgrade requests — one query, GROUP BY status + requestType
        applyRequestCounts(stats, pyRoleUpgradeRequestRepository.countAllGroupedByStatusAndType());

        // ── 5. Subscriptions — one query, GROUP BY status ─────────────────────
        applySubscriptionCounts(stats, pySubscriptionRepository.countAllByStatus());

        log.info("Super Admin dashboard stats fetched");
        return stats;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN — 4 aggregated queries (no global user counts)
    // ─────────────────────────────────────────────────────────────────────────

    private PyDashboardStatsDto getAdminStats(PyUser user) {
        PyDashboardStatsDto stats = new PyDashboardStatsDto();

        // ── 1. Broker accounts — one query, GROUP BY brokerType for this user ─
        applyBrokerCounts(stats, pyBrokerAccountRepository.countActiveByBrokerTypeForUser(user.getId()));

        // ── 2. Trade groups — one query for this user ─────────────────────────
        applyGroupCounts(stats, pyTradeGroupRepository.countTotalActiveInactiveForUser(user));

        // ── 3. Role upgrade requests — one query for this user ────────────────
        applyRequestCounts(stats, pyRoleUpgradeRequestRepository.countGroupedByStatusAndTypeForUser(user));

        // ── 4. Subscriptions — one query for this user ────────────────────────
        applySubscriptionCounts(stats, pySubscriptionRepository.countByStatusForUser(user));

        log.info("Admin dashboard stats fetched for pyId={}", user.getId());
        return stats;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USER — 3 aggregated queries (no groups, no global counts)
    // ─────────────────────────────────────────────────────────────────────────

    private PyDashboardStatsDto getUserStats(PyUser user) {
        PyDashboardStatsDto stats = new PyDashboardStatsDto();

        // ── 1. Broker accounts — one query, GROUP BY brokerType for this user ─
        applyBrokerCounts(stats, pyBrokerAccountRepository.countActiveByBrokerTypeForUser(user.getId()));

        // Groups — users have none
        stats.setGroupCount(0);
        stats.setActiveGroupCount(0);
        stats.setInactiveGroupCount(0);

        // ── 2. Role upgrade requests — one query for this user ────────────────
        //    (CLIENT type only for USERs; GROUP requests blocked at service level)
        applyRequestCounts(stats, pyRoleUpgradeRequestRepository.countGroupedByStatusAndTypeForUser(user));
        // Override group request counts — USERs cannot raise GROUP requests
        stats.setPendingGroupRequests(0);
        stats.setApprovedGroupRequests(0);
        stats.setRejectedGroupRequests(0);
        stats.setTotalGroupRequests(0);

        // ── 3. Subscriptions — one query for this user ────────────────────────
        applySubscriptionCounts(stats, pySubscriptionRepository.countByStatusForUser(user));

        log.info("User dashboard stats fetched for pyId={}", user.getId());
        return stats;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Aggregation helpers — map projection rows → DTO fields
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps RoleCountProjection list → totalUsers / totalAdmins / totalSuperAdmins.
     * One row per role; missing roles default to 0.
     */
    private void applyRoleCounts(PyDashboardStatsDto stats, List<RoleCountProjection> rows) {
        long users = 0, admins = 0, superAdmins = 0;
        for (RoleCountProjection row : rows) {
            switch (row.getRole()) {
                case "USER"        -> users      = row.getCount();
                case "ADMIN"       -> admins     = row.getCount();
                case "SUPER_ADMIN" -> superAdmins = row.getCount();
            }
        }
        stats.setTotalUsers(users);
        stats.setTotalAdmins(admins);
        stats.setTotalSuperAdmins(superAdmins);
    }

    /**
     * Maps BrokerTypeCountProjection list → totalBrokerAccounts + per-broker counts.
     * Accumulates total from the returned rows so no extra COUNT(*) query needed.
     */
    private void applyBrokerCounts(PyDashboardStatsDto stats, List<BrokerTypeCountProjection> rows) {
        long angelOne = 0, motilal = 0, dhan = 0, profitmart = 0, goodwill = 0, total = 0;
        for (BrokerTypeCountProjection row : rows) {
            long c = row.getCount();
            total += c;
            switch (row.getBrokerType()) {
                case "ANGELONE"   -> angelOne   = c;
                case "MOTILAL"    -> motilal    = c;
                case "DHAN"       -> dhan       = c;
                case "PROFITMART" -> profitmart = c;
                case "GOODWILL"   -> goodwill   = c;
            }
        }
        stats.setTotalBrokerAccounts(total);
        stats.setAngelOneAccounts(angelOne);
        stats.setMotilalAccounts(motilal);
        stats.setDhanAccounts(dhan);
        stats.setProfitmartAccounts(profitmart);
        stats.setGoodwillAccounts(goodwill);
    }

    /**
     * Maps Object[] { total, active, inactive } from a single JPQL SUM query.
     * SUM can return null if table is empty — handled with null-safe cast.
     */
    private void applyGroupCounts(PyDashboardStatsDto stats, Object[] row) {
        // Safely drill down to the inner array using Optional
        Object[] metrics = Optional.ofNullable(row)
                .filter(r -> r.length > 0 && r[0] instanceof Object[])
                .map(r -> (Object[]) r[0])
                .orElse(new Object[0]); // Fallback to an empty array if invalid

        // Map the indices to long values safely using our helper method
        stats.setGroupCount(getLongValue(metrics, 0));
        stats.setActiveGroupCount(getLongValue(metrics, 1));
        stats.setInactiveGroupCount(getLongValue(metrics, 2));
    }

    // Clean helper to extract, null-check, and safely cast numeric elements
    private long getLongValue(Object[] array, int index) {
        return Optional.ofNullable(array)
                .filter(a -> index < a.length && a[index] instanceof Number)
                .map(a -> ((Number) a[index]).longValue())
                .orElse(0L);
    }

    /**
     * Maps RequestCountProjection list → all request count fields.
     * One row per (status × requestType) combination; missing combos default to 0.
     * Also computes combined totals (pendingRequests = CLIENT_PENDING + GROUP_PENDING, etc.)
     */
    private void applyRequestCounts(PyDashboardStatsDto stats, List<RequestCountProjection> rows) {
        long pC = 0, aC = 0, rC = 0; // CLIENT
        long pG = 0, aG = 0, rG = 0; // GROUP

        for (RequestCountProjection row : rows) {
            long c    = row.getCount();
            String st = row.getStatus();
            String rt = row.getRequestType();

            if ("CLIENT".equals(rt)) {
                switch (st) {
                    case "PENDING"  -> pC = c;
                    case "APPROVED" -> aC = c;
                    case "REJECTED" -> rC = c;
                }
            } else if ("ADMIN".equals(rt)) {
                switch (st) {
                    case "PENDING"  -> pG = c;
                    case "APPROVED" -> aG = c;
                    case "REJECTED" -> rG = c;
                }
            }
        }

        // CLIENT
        stats.setPendingClientRequests(pC);
        stats.setApprovedClientRequests(aC);
        stats.setRejectedClientRequests(rC);
        stats.setTotalClientRequests(pC + aC + rC);

        // GROUP
        stats.setPendingGroupRequests(pG);
        stats.setApprovedGroupRequests(aG);
        stats.setRejectedGroupRequests(rG);
        stats.setTotalGroupRequests(pG + aG + rG);

        // Combined (all types)
        stats.setPendingRequests(pC + pG);
        stats.setApprovedRequests(aC + aG);
        stats.setRejectedRequests(rC + rG);
        stats.setTotalRequests(pC + aC + rC + pG + aG + rG);
    }

    /**
     * Maps SubscriptionCountProjection list → subscriber count fields.
     * One row per status; missing statuses default to 0.
     */
    private void applySubscriptionCounts(PyDashboardStatsDto stats, List<SubscriptionCountProjection> rows) {
        long active = 0, trial = 0, expired = 0;
        for (SubscriptionCountProjection row : rows) {
            switch (row.getStatus()) {
                case "ACTIVE"  -> active  = row.getCount();
                case "TRIAL"   -> trial   = row.getCount();
                case "EXPIRED" -> expired = row.getCount();
            }
        }
        stats.setActiveSubscribers(active);
        stats.setTrialSubscribers(trial);
        stats.setExpiredSubscribers(expired);
        stats.setTotalSubscribers(active + trial + expired);
    }
}