package com.py.service;

import com.py.common.dto.ApiResponse;
import com.py.dto.PyGroupTradeLogResponseDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Group Trade Log.
 *
 * BUY  → saveBuyLog()  : creates a new row, status = BUY_SUCCESS or BUY_FAILED
 * SELL → saveSellLog() : finds the matching BUY row and UPDATES sell columns
 */
public interface PyGroupTradeLogService {

    // Called after a BUY order attempt — creates a new log row
    void saveBuyLog(Long groupId, String groupName,
                    String tradingSymbol, String symbolToken, String exchange,
                    Long brokerAccountId, String clientId, String userName,
                    String brokerType, String productType,
                    Integer quantity, Double price,
                    String orderId, String status, String remarks);

    // Called after a SELL order attempt — finds the BUY row and updates it
    void saveSellLog(Long groupId,
                     String tradingSymbol,
                     Long brokerAccountId,
                     Integer quantity, Double price,
                     String orderId, String status, String remarks);

    // ── Fetch APIs ────────────────────────────────────────────────────────────

    // All logs for a group
    ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroup(Long groupId);

    // By overall status: BUY_SUCCESS / BUY_FAILED / SELL_SUCCESS / SELL_FAILED / SELL_PENDING
    ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndStatus(Long groupId, String status);

    // All entries where BUY failed
    ApiResponse<List<PyGroupTradeLogResponseDto>> getBuyFailedByGroup(Long groupId);

    // All entries where SELL failed
    ApiResponse<List<PyGroupTradeLogResponseDto>> getSellFailedByGroup(Long groupId);

    // By broker account within a group
    ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndBroker(Long groupId, Long brokerAccountId);

    // By stock symbol within a group
    ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndStock(Long groupId, String tradingSymbol);

    // All logs for a broker across groups
    ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByBroker(Long brokerAccountId);

    // By date range
    ApiResponse<List<PyGroupTradeLogResponseDto>> getLogsByGroupAndDateRange(
            Long groupId, LocalDateTime from, LocalDateTime to);

    // Status count summary
    ApiResponse<?> getStatusSummaryByGroup(Long groupId);
}
