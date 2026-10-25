package com.py.demataccount.repository;

import com.py.entity.PyTradeGroupEntity;
import com.py.entity.PyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PyTradeGroupRepository extends JpaRepository<PyTradeGroupEntity, Long> {

    // Get all active groups
    List<PyTradeGroupEntity> findByIsActive(Boolean isActive);

    // Get all groups created by a user
    List<PyTradeGroupEntity> findByCreatedBy(PyUser createdBy);

    // Get all active groups created by a user
    List<PyTradeGroupEntity> findByCreatedByAndIsActive(PyUser createdBy, Boolean isActive);

    // Check if group name already exists for this user
    boolean existsByGroupNameAndCreatedBy(String groupName, PyUser createdBy);

    // Find by id and active
    Optional<PyTradeGroupEntity> findByIdAndIsActive(Long id, Boolean isActive);

    Optional<PyTradeGroupEntity> findById(Long id);

    // Already exists ✅
    long countByIsActive(Boolean isActive);

    long countByCreatedByAndIsActive(PyUser createdBy, Boolean isActive);

    // Soft delete all groups by user
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PyTradeGroupEntity g SET g.isActive = false WHERE g.createdBy = :user")
    void deactivateAllGroupsByUser(@Param("user") PyUser user);

// Restore all groups by user
    @Modifying
    @Query("UPDATE PyTradeGroupEntity g SET g.isActive = true WHERE g.createdBy = :user")
    void restoreAllGroupsByUser(@Param("user") PyUser user);

// Count all groups by user regardless of active status
    long countByCreatedBy(PyUser user);

    @Query("""
            SELECT COUNT(g) as totalGroups,
                   SUM(CASE WHEN g.isActive = true  THEN 1 ELSE 0 END) as activeGroups,
                   SUM(CASE WHEN g.isActive = false THEN 1 ELSE 0 END) as inActiveGroup
            FROM PyTradeGroupEntity g
            """)
    Object[] countTotalActiveInactive();

    // ── NEW: same but for a specific owner (ADMIN) ────────────────────────────
    @Query("""
            SELECT COUNT(g),
                   SUM(CASE WHEN g.isActive = true  THEN 1 ELSE 0 END),
                   SUM(CASE WHEN g.isActive = false THEN 1 ELSE 0 END)
            FROM PyTradeGroupEntity g
            WHERE g.createdBy = :user
            """)
    Object[] countTotalActiveInactiveForUser(@Param("user") PyUser user);
}
