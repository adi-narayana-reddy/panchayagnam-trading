package com.py.service.impl;

import com.py.common.dto.ApiResponse;
import com.py.dto.PyGroupTradeLogResponseDto;
import com.py.service.PyGroupTradeLogService;
import com.py.tradegroup.entity.PyGroupTradeLogEntity;
import com.py.tradegroup.repository.PyGroupTradeLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PyGroupTradeLogServiceImpl implements PyGroupTradeLogService {

    private static final Logger log = LoggerFactory.getLogger(PyGroupTradeLogServiceImpl.class);

    @Autowired
    private PyGroupTradeLogRepository tradeLogRepository;

    // ═══════════════════════════════════════════════════════════════════════════
    // BUY LOG — create a new row
    // Status after BUY:
    //   SUCCESS → BUY_SUCCESS  (position is open, waiting for sell)
    //   FAILED  → BUY_FAILED   (buy never happened)
    // ═══════════════════════════════════════════════════════════════════════════
    @Override
    public void saveBuyLog(Long groupId, String groupName,
                           String tradingSymbol, String symbolToken, String exchange,
                           Long brokerAccountId, String clientId, String userName,
                           String brokerType, String productType,
                           Integer quantity, Double price,
                           String orderId, String status, String remarks) {
        try {
            PyGroupTradeLogEntity entity = new PyGroupTradeLogEntity();
            entity.setGroupId(groupId);
            entity.setGroupName(groupName);
            entity.setTradingSymbol(tradingSymbol);
            entity.setSymbolToken(symbolToken);
            entity.setExchange(exchange);
            entity.setBrokerAccountId(brokerAccountId);
            entity.setClientId(clientId);
            entity.setUserName(userName);
            entity.setBrokerType(brokerType);
            entity.setProductType(productType);
            entity.setBuyQuantity(quantity);
            entity.setBuyPrice(price);
            entity.setBuyOrderId(orderId);
            entity.setBuyStatus(status);              // SUCCESS / FAILED
            entity.setBuyRemarks(remarks);
            entity.setBoughtAt(LocalDateTime.now());

            // Overall status
            if ("SUCCESS".equals(status)) {
                entity.setStatus("BUY_SUCCESS");      // open position — sell pending
                entity.setSellStatus("PENDING");
            } else {
                entity.setStatus("BUY_FAILED");       // buy itself failed
                entity.setSellStatus(null);
            }

            tradeLogRepository.save(entity);
            log.info("✅ BUY log saved — groupId={} broker={} symbol={} status={}",
                    groupId, brokerAccountId, tradingSymbol, status);

        } catch (Exception e) {
            log.warn("⚠️ BUY log save failed — groupId={} broker={} symbol={}: {}",
                    groupId, brokerAccountId, tradingSymbol, e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SELL LOG — find the matching BUY row and UPDATE it
    // Lookup: groupId + brokerAccountId + tradingSymbol + status = BUY_SUCCESS (oldest first)
    // Status after SELL:
    //   SUCCESS → SELL_SUCCESS
    //   FAILED  → SELL_FAILED
    // ═══════════════════════════════════════════════════════════════════════════
    @Override
    public void saveSellLog(Long groupId,
                            String tradingSymbol,
                            Long brokerAccountId,
                            Integer quantity, Double price,
                            String orderId, String status, String remarks) {
        try {
            // Find the matching open BUY row (FIFO — oldest first)
            Optional<PyGroupTradeLogEntity> opt = tradeLogRepository
                    .findTopByGroupIdAndBrokerAccountIdAndTradingSymbolAndStatusOrderByCreatedAtAsc(
                            groupId, brokerAccountId, tradingSymbol, "BUY_SUCCESS");

            if (opt.isEmpty()) {
                log.warn("⚠️ SELL log — no matching BUY_SUCCESS row found for " +
                         "groupId={} broker={} symbol={}", groupId, brokerAccountId, tradingSymbol);
                return;
            }

            PyGroupTradeLogEntity entity = opt.get();

            // Update SELL columns on the existing BUY row
            entity.setSellQuantity(quantity);
            entity.setSellPrice(price);
            entity.setSellOrderId(orderId);
            entity.setSellStatus(status);             // SUCCESS / FAILED
            entity.setSellRemarks(remarks);
            entity.setSoldAt(LocalDateTime.now());

            // Update overall status
            if ("SUCCESS".equals(status)) {
                entity.setStatus("SELL_SUCCESS");     // fully closed
            } else {
                entity.setStatus("SELL_FAILED");      // sell failed — position still open
            }

            tradeLogRepository.save(entity);
            log.info("✅ SELL log updated — groupId={} broker={} symbol={} status={}",
                    groupId, brokerAccountId, tradingSymbol, status);

        } catch (Exception e) {
            log.warn("⚠️ SELL log update failed — groupId={} broker={} symbol={}: {}",
                    groupId, brokerAccountId, tradingSymbol, e.getMessage());
        }
    }

    // ── Fetch: all logs for a group ──────────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroup(Long groupId) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByGroupIdOrderByCreatedAtDesc(groupId)
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " logs", logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Fetch: by overall status ─────────────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndStatus(
            Long groupId, String status) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByGroupIdAndStatusOrderByCreatedAtDesc(groupId, status.toUpperCase())
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " logs with status " + status, logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Fetch: BUY failed ────────────────────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getBuyFailedByGroup(Long groupId) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByGroupIdAndBuyStatusOrderByCreatedAtDesc(groupId, "FAILED")
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " BUY failed logs", logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Fetch: SELL failed ───────────────────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getSellFailedByGroup(Long groupId) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByGroupIdAndSellStatusOrderByCreatedAtDesc(groupId, "FAILED")
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " SELL failed logs", logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Fetch: by group + broker ─────────────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndBroker(
            Long groupId, Long brokerAccountId) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByGroupIdAndBrokerAccountIdOrderByCreatedAtDesc(groupId, brokerAccountId)
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " logs", logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Fetch: by group + stock ──────────────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndStock(
            Long groupId, String tradingSymbol) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByGroupIdAndTradingSymbolOrderByCreatedAtDesc(groupId, tradingSymbol)
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " logs for " + tradingSymbol, logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Fetch: by broker across groups ───────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByBroker(Long brokerAccountId) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByBrokerAccountIdOrderByCreatedAtDesc(brokerAccountId)
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " logs", logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Fetch: by date range ─────────────────────────────────────────────────
    @Override
    public ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndDateRange(
            Long groupId, LocalDateTime from, LocalDateTime to) {
        try {
            List<PyGroupTradeLogResponseDto> logs = tradeLogRepository
                    .findByGroupIdAndDateRange(groupId, from, to)
                    .stream().map(this::toDto).collect(Collectors.toList());
            return ApiResponse.success("Fetched " + logs.size() + " logs in range", logs);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Status summary ───────────────────────────────────────────────────────
    @Override
    public ApiResponse<?> getStatusSummaryByGroup(Long groupId) {
        try {
            List<Object[]> rawResults = tradeLogRepository.getStatusSummaryByGroupId(groupId);
            Map<String, Long> summary = new HashMap<>();
            for (Object[] row : rawResults) {
                summary.put((String) row[0], (Long) row[1]);
            }
            // Ensure all keys are present even if zero
            summary.putIfAbsent("BUY_SUCCESS",  0L);
            summary.putIfAbsent("BUY_FAILED",   0L);
            summary.putIfAbsent("SELL_SUCCESS",  0L);
            summary.putIfAbsent("SELL_FAILED",   0L);

            Map<String, Object> response = new HashMap<>();
            response.put("groupId",     groupId);
            response.put("totalOrders", summary.values().stream().mapToLong(Long::longValue).sum());
            response.put("summary",     summary);
            return ApiResponse.success("Summary fetched", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed: " + e.getMessage());
        }
    }

    // ── Entity → DTO mapper ──────────────────────────────────────────────────
    private PyGroupTradeLogResponseDto toDto(PyGroupTradeLogEntity e) {
        PyGroupTradeLogResponseDto dto = new PyGroupTradeLogResponseDto();
        dto.setId(e.getId());
        dto.setGroupId(e.getGroupId());
        dto.setGroupName(e.getGroupName());
        dto.setTradingSymbol(e.getTradingSymbol());
        dto.setSymbolToken(e.getSymbolToken());
        dto.setExchange(e.getExchange());
        dto.setBrokerAccountId(e.getBrokerAccountId());
        dto.setClientId(e.getClientId());
        dto.setUserName(e.getUserName());
        dto.setBrokerType(e.getBrokerType());
        dto.setProductType(e.getProductType());
        dto.setBuyQuantity(e.getBuyQuantity());
        dto.setBuyPrice(e.getBuyPrice());
        dto.setBuyOrderId(e.getBuyOrderId());
        dto.setBuyStatus(e.getBuyStatus());
        dto.setBuyRemarks(e.getBuyRemarks());
        dto.setBoughtAt(e.getBoughtAt());
        dto.setSellQuantity(e.getSellQuantity());
        dto.setSellPrice(e.getSellPrice());
        dto.setSellOrderId(e.getSellOrderId());
        dto.setSellStatus(e.getSellStatus());
        dto.setSellRemarks(e.getSellRemarks());
        dto.setSoldAt(e.getSoldAt());
        dto.setStatus(e.getStatus());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
