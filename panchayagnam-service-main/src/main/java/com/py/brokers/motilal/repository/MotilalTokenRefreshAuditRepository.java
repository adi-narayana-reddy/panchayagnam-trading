package com.py.brokers.motilal.repository;



import com.py.brokers.motilal.entity.MotilalTokenRefreshAuditEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotilalTokenRefreshAuditRepository
        extends JpaRepository<MotilalTokenRefreshAuditEntity, Long> {

    List<MotilalTokenRefreshAuditEntity> findByTriggeredByOrderByAttemptedAtDesc(
            String triggeredBy);
}