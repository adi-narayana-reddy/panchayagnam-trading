package com.py.tradegroup.repository;

import com.py.tradegroup.entity.TradeGroupOrderAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TradeGroupOrderAuditRepository extends JpaRepository<TradeGroupOrderAuditEntity, Long> {

    List<TradeGroupOrderAuditEntity> findByGroupIdOrderByExecutedAtDesc(Long groupId);

    List<TradeGroupOrderAuditEntity> findByDematAccountIdOrderByExecutedAtDesc(Long dematAccountId);

    List<TradeGroupOrderAuditEntity> findByGroupIdAndStatusOrderByExecutedAtDesc(Long groupId, String status);

    List<TradeGroupOrderAuditEntity> findByClientIdOrderByExecutedAtDesc(String clientId);

    List<TradeGroupOrderAuditEntity> findAllByOrderByExecutedAtDesc();
}
