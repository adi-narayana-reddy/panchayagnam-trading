package com.py.service;

import com.py.common.dto.ApiResponse;
import com.py.dto.PyHotStockRequestDto;
import com.py.dto.PyHotStockGroupedResponseDto;
import com.py.dto.PyHotStockResponseDto;
import com.py.common.enums.HotStockCategory;
import java.time.LocalDate;

import java.util.List;

public interface PyHotStockService {

    ApiResponse<?> hotStockOperations(PyHotStockRequestDto requestDto);

    ApiResponse<PyHotStockGroupedResponseDto> getAllStocksGrouped();

    ApiResponse<List<PyHotStockResponseDto>> getStocksByCategory(HotStockCategory category);

    ApiResponse<List<PyHotStockResponseDto>> getStocksByUser(Long pyId);

    ApiResponse<PyHotStockGroupedResponseDto> getStocksByDate(LocalDate date);

    ApiResponse<List<PyHotStockResponseDto>> getStocksByDateAndCategory(
            LocalDate date, HotStockCategory category);

    ApiResponse<List<PyHotStockResponseDto>> getLatestStocksByCategory(HotStockCategory category);

    ApiResponse<List<PyHotStockResponseDto>> getLatestStocksByCategoryBeforeOrOn(
            HotStockCategory category, LocalDate onOrBeforeDate);
}
