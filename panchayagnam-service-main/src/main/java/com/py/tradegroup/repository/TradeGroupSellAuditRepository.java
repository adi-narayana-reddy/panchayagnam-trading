package com.py.tradegroup.repository;

import com.py.tradegroup.entity.TradeGroupSellAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TradeGroupSellAuditRepository extends JpaRepository<TradeGroupSellAuditEntity, Long> {

    List<TradeGroupSellAuditEntity> findByGroupIdOrderByExecutedAtDesc(Long groupId);

    List<TradeGroupSellAuditEntity> findByDematAccountIdOrderByExecutedAtDesc(Long dematAccountId);

    List<TradeGroupSellAuditEntity> findByGroupIdAndStatusOrderByExecutedAtDesc(Long groupId, String status);

    List<TradeGroupSellAuditEntity> findByClientIdOrderByExecutedAtDesc(String clientId);

    List<TradeGroupSellAuditEntity> findAllByOrderByExecutedAtDesc();
}
