package com.py.tradegroup.repository;

import com.py.tradegroup.entity.PyGroupPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PyGroupPositionRepository
        extends JpaRepository<PyGroupPositionEntity, Long> {

    // ── Get all positions for a group by status ───────────────────────────────
    List<PyGroupPositionEntity> findByGroupIdAndStatus(
            Long groupId, PyGroupPositionEntity.Status status);

    // ── Get all positions for a group (all statuses) ──────────────────────────
    List<PyGroupPositionEntity> findByGroupId(Long groupId);

    // ── Get all positions for a broker account by status ──────────────────────
    List<PyGroupPositionEntity> findByBrokerAccountIdAndStatus(
            Long brokerAccountId, PyGroupPositionEntity.Status status);

    // ── FIFO — oldest open position first (for SELL closePosition) ────────────
    Optional<PyGroupPositionEntity> findTopByGroupIdAndBrokerAccountIdAndTradingSymbolAndStatusOrderByBoughtAtAsc(
            Long groupId, Long brokerAccountId,
            String tradingSymbol, PyGroupPositionEntity.Status status);

    // ── Get position by buyOrderId and status ─────────────────────────────────
    Optional<PyGroupPositionEntity> findByBuyOrderIdAndStatus(
            String buyOrderId, PyGroupPositionEntity.Status status);
}
