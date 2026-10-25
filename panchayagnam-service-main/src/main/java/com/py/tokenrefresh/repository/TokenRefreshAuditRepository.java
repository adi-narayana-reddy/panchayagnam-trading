package com.py.tokenrefresh.repository;

import com.py.tokenrefresh.entity.TokenRefreshAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TokenRefreshAuditRepository extends JpaRepository<TokenRefreshAuditEntity, Long> {

    // Get all audits for a specific account
    List<TokenRefreshAuditEntity> findByDematAccountIdOrderByAttemptedAtDesc(Long dematAccountId);

    // Get all failed audits for today
    List<TokenRefreshAuditEntity> findByStatusAndAttemptedAtAfterOrderByAttemptedAtDesc(
        String status, LocalDateTime after);

    // Get latest audit per run
    List<TokenRefreshAuditEntity> findByTriggeredByOrderByAttemptedAtDesc(String triggeredBy);
}
