package com.py.easytrade.repository;



import com.py.easytrade.entity.IndividualOrderAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IndividualOrderAuditRepository extends JpaRepository<IndividualOrderAuditEntity, Long> {
    List<IndividualOrderAuditEntity> findByClientIdOrderByExecutedAtDesc(String clientId);
    List<IndividualOrderAuditEntity> findByDematAccountIdOrderByExecutedAtDesc(Long dematAccountId);
}