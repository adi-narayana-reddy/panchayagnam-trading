package com.py.repo;

import com.py.dto.RequestCountProjection;
import com.py.entity.PyRoleUpgradeRequest;
import com.py.entity.PyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PyRoleUpgradeRequestRepository extends JpaRepository<PyRoleUpgradeRequest, Long> {

    // Check if user already has a pending request
    boolean existsByPyUserAndStatus(PyUser pyUser, PyRoleUpgradeRequest.Status status);

    // Get all pending requests - for Super Admin
    List<PyRoleUpgradeRequest> findByStatus(PyRoleUpgradeRequest.Status status);

    List<PyRoleUpgradeRequest> findTopByPyUserOrderByCreatedAtDesc(PyUser pyUser);

    // Count by status
    long countByStatus(PyRoleUpgradeRequest.Status status);

    // Count by user and status
    long countByPyUserAndStatus(PyUser pyUser, PyRoleUpgradeRequest.Status status);

    boolean existsByPyUserAndStatusAndRequestType(
            PyUser pyUser,
            PyRoleUpgradeRequest.Status status,
            PyRoleUpgradeRequest.RequestType requestType);

    // FIX 1: single RequestType lookup (used for pending check per type)
    List<PyRoleUpgradeRequest> findByStatusAndRequestType(
            PyRoleUpgradeRequest.Status status,
            PyRoleUpgradeRequest.RequestType requestType);

    // FIX 2: added missing IN query — used in getAllSubscriptionRequests() for approved/rejected
    List<PyRoleUpgradeRequest> findByStatusAndRequestTypeIn(
            PyRoleUpgradeRequest.Status status,
            List<PyRoleUpgradeRequest.RequestType> requestTypes);

    List<PyRoleUpgradeRequest> findByPyUserAndRequestTypeOrderByCreatedAtDesc(
            PyUser pyUser, PyRoleUpgradeRequest.RequestType requestType);

    // Count by status + requestType (for CLIENT / GROUP split)
    long countByStatusAndRequestType(
            PyRoleUpgradeRequest.Status status,
            PyRoleUpgradeRequest.RequestType requestType);

// Count by pyUser + status + requestType (for ADMIN / USER view)
    long countByPyUserAndStatusAndRequestType(
            PyUser pyUser,
            PyRoleUpgradeRequest.Status status,
            PyRoleUpgradeRequest.RequestType requestType);
    
     @Query("""
            SELECT CAST(r.status AS string)      AS status,
                   CAST(r.requestType AS string) AS requestType,
                   COUNT(r)                      AS count
            FROM PyRoleUpgradeRequest r
            GROUP BY r.status, r.requestType
            """)
    List<RequestCountProjection> countAllGroupedByStatusAndType();
 
    // ── NEW: single query for ADMIN/USER — filtered by owner ─────────────────
    @Query("""
            SELECT CAST(r.status AS string)      AS status,
                   CAST(r.requestType AS string) AS requestType,
                   COUNT(r)                      AS count
            FROM PyRoleUpgradeRequest r
            WHERE r.pyUser = :user
            GROUP BY r.status, r.requestType
            """)
    List<RequestCountProjection> countGroupedByStatusAndTypeForUser(@Param("user") PyUser user);
}
