package com.py.tradegroup.repository;

import com.py.tradegroup.entity.PyGroupTradeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PyGroupTradeLogRepository extends JpaRepository<PyGroupTradeLogEntity, Long> {

    // ── Used when SELL comes in: find the matching BUY log to UPDATE ─────────
    // Finds the oldest open BUY record for this group + broker + stock
    Optional<PyGroupTradeLogEntity>
    findTopByGroupIdAndBrokerAccountIdAndTradingSymbolAndStatusOrderByCreatedAtAsc(
            Long groupId, Long brokerAccountId, String tradingSymbol, String status);

    // ── Fetch all logs for a group ────────────────────────────────────────────
    List<PyGroupTradeLogEntity> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    // ── By overall status ─────────────────────────────────────────────────────
    List<PyGroupTradeLogEntity> findByGroupIdAndStatusOrderByCreatedAtDesc(
            Long groupId, String status);

    // ── By group + broker account ─────────────────────────────────────────────
    List<PyGroupTradeLogEntity> findByGroupIdAndBrokerAccountIdOrderByCreatedAtDesc(
            Long groupId, Long brokerAccountId);

    // ── By group + stock symbol ───────────────────────────────────────────────
    List<PyGroupTradeLogEntity> findByGroupIdAndTradingSymbolOrderByCreatedAtDesc(
            Long groupId, String tradingSymbol);

    // ── All logs for a broker account (across all groups) ────────────────────
    List<PyGroupTradeLogEntity> findByBrokerAccountIdOrderByCreatedAtDesc(Long brokerAccountId);

    // ── Only failed BUYs for a group ─────────────────────────────────────────
    List<PyGroupTradeLogEntity> findByGroupIdAndBuyStatusOrderByCreatedAtDesc(
            Long groupId, String buyStatus);

    // ── Only failed SELLs for a group ────────────────────────────────────────
    List<PyGroupTradeLogEntity> findByGroupIdAndSellStatusOrderByCreatedAtDesc(
            Long groupId, String sellStatus);

    // ── Logs in a date range for a group ─────────────────────────────────────
    @Query("SELECT e FROM PyGroupTradeLogEntity e " +
           "WHERE e.groupId = :groupId " +
           "AND e.createdAt BETWEEN :from AND :to " +
           "ORDER BY e.createdAt DESC")
    List<PyGroupTradeLogEntity> findByGroupIdAndDateRange(
            @Param("groupId") Long groupId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // ── Status summary: count per status for a group ──────────────────────────
    @Query("SELECT e.status, COUNT(e) FROM PyGroupTradeLogEntity e " +
           "WHERE e.groupId = :groupId GROUP BY e.status")
    List<Object[]> getStatusSummaryByGroupId(@Param("groupId") Long groupId);
}
