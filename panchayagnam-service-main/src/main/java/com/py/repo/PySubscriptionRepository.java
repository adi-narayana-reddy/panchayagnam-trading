package com.py.repo;

import com.py.dto.SubscriptionCountProjection;
import com.py.entity.PySubscription;
import com.py.entity.PyUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PySubscriptionRepository extends JpaRepository<PySubscription, Long> {

    Optional<PySubscription> findTopByPyUserAndTypeOrderByCreatedAtDesc(
            PyUser pyUser, PySubscription.Type type);

    boolean existsByPyUserAndTypeAndStatus(
            PyUser pyUser, PySubscription.Type type, PySubscription.Status status);

    // Global subscriber counts (SUPER_ADMIN)
    long countByStatus(PySubscription.Status status);

// Per-user subscriber counts (ADMIN / USER)
    long countByPyUserAndStatus(PyUser pyUser, PySubscription.Status status);
    
    @Query("""
            SELECT CAST(s.status AS string) AS status, COUNT(s) AS count
            FROM PySubscription s
            GROUP BY s.status
            """)
    List<SubscriptionCountProjection> countAllByStatus();
 
    // ── NEW: single query for ADMIN/USER — filtered by owner ─────────────────
    @Query("""
            SELECT CAST(s.status AS string) AS status, COUNT(s) AS count
            FROM PySubscription s
            WHERE s.pyUser = :user
            GROUP BY s.status
            """)
    List<SubscriptionCountProjection> countByStatusForUser(@Param("user") PyUser user);
}
