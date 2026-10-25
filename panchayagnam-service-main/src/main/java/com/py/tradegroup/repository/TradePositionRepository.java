package com.py.tradegroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.py.tradegroup.entity.TradePositionEntity;

import java.util.List;

@Repository
public interface TradePositionRepository extends JpaRepository<TradePositionEntity, Long> {

    // All OPEN positions for a group â€” used for GROUP SELL
    List<TradePositionEntity> findByGroupIdAndStatus(Long groupId, String status);

    // All OPEN positions for a specific user in a group
    List<TradePositionEntity> findByGroupIdAndDematAccountIdAndStatus(
            Long groupId, Long dematAccountId, String status);

    // All positions for a user
    List<TradePositionEntity> findByDematAccountIdOrderByCreatedAtDesc(Long dematAccountId);

    // All positions for a group
    List<TradePositionEntity> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    // Fetch OPEN positions with demat account details for sell
    @Query("""
        SELECT p FROM TradePositionEntity p
        WHERE p.groupId = :groupId
        AND p.status = 'OPEN'
    """)
    List<TradePositionEntity> findOpenPositionsByGroupId(@Param("groupId") Long groupId);
}

