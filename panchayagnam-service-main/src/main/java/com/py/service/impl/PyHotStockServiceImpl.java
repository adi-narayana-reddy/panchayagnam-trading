package com.py.service.impl;

import com.py.common.dto.ApiResponse;
import com.py.common.enums.HotStockCategory;
import com.py.dto.PyHotStockGroupedResponseDto;
import com.py.dto.PyHotStockRequestDto;
import com.py.dto.PyHotStockResponseDto;
import com.py.entity.PyHotStockEntity;
import com.py.entity.PyUser;
import com.py.repo.PyUserRepository;
import com.py.demataccount.repository.*;
import com.py.service.PyHotStockService;
import java.time.LocalDate;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PyHotStockServiceImpl implements PyHotStockService {

    private static final Logger log = LoggerFactory.getLogger(PyHotStockServiceImpl.class);

    @Autowired
    private PyHotStockRepository pyHotStockRepository;

    @Autowired
    private PyUserRepository pyUserRepository;

    // ── MAIN ENTRY POINT ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<?> hotStockOperations(PyHotStockRequestDto requestDto) {
        try {
            return switch (requestDto.getActionType()) {
                case ADD ->
                    addStock(requestDto);
                case UPDATE ->
                    updateStock(requestDto);
                case DELETE ->
                    deleteStock(requestDto);
                default ->
                    ApiResponse.error(400, "Invalid action type");
            };
        } catch (RuntimeException e) {
            log.error("Hot stock operation failed: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Operation failed: " + e.getMessage());
        }
    }

    // ── ADD STOCK ────────────────────────────────────────────────────────────
    private ApiResponse<?> addStock(PyHotStockRequestDto requestDto) {
        if (requestDto.getPyId() == null) {
            return ApiResponse.error(400, "pyId is required for ADD");
        }
        if (requestDto.getSymbol() == null || requestDto.getSymbol().isEmpty()) {
            return ApiResponse.error(400, "symbol is required for ADD");
        }
        if (requestDto.getSymbolToken() == null || requestDto.getSymbolToken().isEmpty()) {
            return ApiResponse.error(400, "symbol token is required for ADD");
        }
        if (requestDto.getCompanyName() == null || requestDto.getCompanyName().isEmpty()) {
            return ApiResponse.error(400, "companyName is required for ADD");
        }
        if (requestDto.getPrice() == null) {
            return ApiResponse.error(400, "price is required for ADD");
        }
        if (requestDto.getTarget() == null) {
            return ApiResponse.error(400, "target is required for ADD");
        }
        if (requestDto.getDirection() == null) {
            return ApiResponse.error(400, "direction is required for ADD");
        }
        if (requestDto.getSignal() == null) {
            return ApiResponse.error(400, "signal is required for ADD");
        }
        if (requestDto.getCategory() == null) {
            return ApiResponse.error(400, "category is required for ADD");
        }

        // 2. Fetch user
        PyUser user = pyUserRepository.findById(requestDto.getPyId())
                .orElseThrow(() -> new RuntimeException(
                "User not found with pyId: " + requestDto.getPyId()));

        // 3. Check role — only ADMIN and SUPER_ADMIN
        if (user.getRole() == PyUser.Role.USER) {
            return ApiResponse.error(403,
                    "Only ADMIN and SUPER_ADMIN can recommend stocks");
        }

        // 4. Check duplicate symbol + category
        if (pyHotStockRepository.validateHotStock(
                requestDto.getSymbol().toUpperCase(),
                requestDto.getCategory(), LocalDate.now())) {
            return ApiResponse.error(409,
                    "Stock " + requestDto.getSymbol().toUpperCase()
                    + " already exists in category "
                    + requestDto.getCategory().name());
        }

        // 5. Build entity
        PyHotStockEntity stock = new PyHotStockEntity();
        stock.setSymbol(requestDto.getSymbol().toUpperCase());
        stock.setCompanyName(requestDto.getCompanyName());
        stock.setPrice(requestDto.getPrice());
        stock.setTarget(requestDto.getTarget());
        stock.setChangePercent(requestDto.getChangePercent());
        stock.setDirection(requestDto.getDirection());
        stock.setSignal(requestDto.getSignal());
        stock.setCategory(requestDto.getCategory());
        stock.setRecommendedBy(user);
        stock.setIsActive(true);
        stock.setSymbolToken(requestDto.getSymbolToken());

        PyHotStockEntity saved = pyHotStockRepository.save(stock);

        log.info("Hot stock added: {} category: {} by pyId: {}",
                saved.getSymbol(), saved.getCategory(), requestDto.getPyId());

        return ApiResponse.success("Stock added successfully", null);
    }

    // ── UPDATE STOCK ─────────────────────────────────────────────────────────
    private ApiResponse<?> updateStock(PyHotStockRequestDto requestDto) {

        // 1. Validate stockId
        if (requestDto.getStockId() == null) {
            return ApiResponse.error(400, "stockId is required for UPDATE");
        }

        // 2. Fetch stock
        PyHotStockEntity stock = pyHotStockRepository.findById(requestDto.getStockId())
                .orElseThrow(() -> new RuntimeException(
                "Stock not found with id: " + requestDto.getStockId()));

        // 3. Check duplicate only if symbol OR category changed
        String newSymbol = requestDto.getSymbol() != null
                && !requestDto.getSymbol().isEmpty()
                ? requestDto.getSymbol().toUpperCase()
                : stock.getSymbol();

        HotStockCategory newCategory = requestDto.getCategory() != null
                ? requestDto.getCategory()
                : stock.getCategory();

        boolean symbolChanged = !newSymbol.equals(stock.getSymbol());
        boolean categoryChanged = newCategory != stock.getCategory();

        if (symbolChanged || categoryChanged) {
            if (pyHotStockRepository.validateHotStock(newSymbol, newCategory, LocalDate.now())) {
                return ApiResponse.error(409,
                        "Stock " + newSymbol
                        + " already exists in category " + newCategory.name());
            }
        }

        // 4. Update all fields — new value if provided, else keep old value
        stock.setSymbol(newSymbol);
        stock.setCategory(newCategory);
        stock.setSymbolToken(requestDto.getSymbolToken());

        stock.setCompanyName(
                requestDto.getCompanyName() != null && !requestDto.getCompanyName().isEmpty()
                ? requestDto.getCompanyName()
                : stock.getCompanyName());

        stock.setPrice(
                requestDto.getPrice() != null
                ? requestDto.getPrice()
                : stock.getPrice());

        stock.setTarget(
                requestDto.getTarget() != null
                ? requestDto.getTarget()
                : stock.getTarget());

        stock.setChangePercent(
                requestDto.getChangePercent() != null
                ? requestDto.getChangePercent()
                : stock.getChangePercent());

        stock.setDirection(
                requestDto.getDirection() != null
                ? requestDto.getDirection()
                : stock.getDirection());

        stock.setSignal(
                requestDto.getSignal() != null
                ? requestDto.getSignal()
                : stock.getSignal());

        // JPA dirty checking handles save automatically
        log.info("Hot stock updated: {} id: {}", stock.getSymbol(), stock.getId());

        return ApiResponse.success("Stock updated successfully", null);
    }

    // ── DELETE STOCK ─────────────────────────────────────────────────────────
    private ApiResponse<?> deleteStock(PyHotStockRequestDto requestDto) {

        // 1. Validate stockId
        if (requestDto.getStockId() == null) {
            return ApiResponse.error(400, "stockId is required for DELETE");
        }

        // 2. Fetch stock
        PyHotStockEntity stock = pyHotStockRepository.findById(requestDto.getStockId())
                .orElseThrow(() -> new RuntimeException(
                "Stock not found with id: " + requestDto.getStockId()));

        // 3. Soft delete
        stock.setIsActive(false);

        log.info("Hot stock deleted: {} id: {}", stock.getSymbol(), stock.getId());

        return ApiResponse.success("Stock deleted successfully", null);
    }

    // ── GET ALL STOCKS GROUPED ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PyHotStockGroupedResponseDto> getAllStocksGrouped() {
        try {
            PyHotStockGroupedResponseDto grouped = new PyHotStockGroupedResponseDto();

            grouped.setIntraday(getActiveMappedStocks(HotStockCategory.INTRADAY));
            grouped.setShortTerm(getActiveMappedStocks(HotStockCategory.SHORT_TERM));
            grouped.setMidTerm(getActiveMappedStocks(HotStockCategory.MID_TERM));
            grouped.setLongTerm(getActiveMappedStocks(HotStockCategory.LONG_TERM));

            return ApiResponse.success("Stocks fetched successfully", grouped);

        } catch (Exception e) {
            log.error("Failed to fetch stocks: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to fetch stocks: " + e.getMessage());
        }
    }

    // ── GET STOCKS BY CATEGORY ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PyHotStockResponseDto>> getStocksByCategory(
            HotStockCategory category) {
        try {
            List<PyHotStockResponseDto> stocks = getActiveMappedStocks(category);
            return ApiResponse.success(
                    category.name() + " stocks fetched successfully", stocks);
        } catch (Exception e) {
            log.error("Failed to fetch stocks by category: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to fetch stocks: " + e.getMessage());
        }
    }

    // ── GET STOCKS BY USER ────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PyHotStockResponseDto>> getStocksByUser(Long pyId) {
        try {
            PyUser user = pyUserRepository.findById(pyId)
                    .orElseThrow(() -> new RuntimeException(
                    "User not found with pyId: " + pyId));

            List<PyHotStockResponseDto> stocks = pyHotStockRepository
                    .findByRecommendedByAndIsActive(user, true)
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());

            return ApiResponse.success("Stocks fetched successfully", stocks);

        } catch (Exception e) {
            log.error("Failed to fetch stocks for pyId={}: {}", pyId, e.getMessage());
            return ApiResponse.error(500, "Failed to fetch stocks: " + e.getMessage());
        }
    }

    // ── HELPER: Get Active Stocks by Category ────────────────────────────────
    private List<PyHotStockResponseDto> getActiveMappedStocks(HotStockCategory category) {
        return pyHotStockRepository
                .findByCategoryAndIsActive(category, true)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ── HELPER: Map Entity to Response DTO ───────────────────────────────────
    private PyHotStockResponseDto mapToResponseDto(PyHotStockEntity stock) {
        PyHotStockResponseDto dto = new PyHotStockResponseDto();
        dto.setStockId(stock.getId());
        dto.setSymbol(stock.getSymbol());
        dto.setCompanyName(stock.getCompanyName());
        dto.setPrice(stock.getPrice());
        dto.setTarget(stock.getTarget());
        dto.setChangePercent(stock.getChangePercent());
        dto.setDirection(stock.getDirection());
        dto.setSignal(stock.getSignal());
        dto.setCategory(stock.getCategory());
        dto.setRecommendedByPyId(stock.getRecommendedBy().getId());
        dto.setRecommendedByName(stock.getRecommendedBy().getFullName());
        dto.setIsActive(stock.getIsActive());
        dto.setCreatedAt(stock.getCreatedAt());
        dto.setUpdatedAt(stock.getUpdatedAt());
        dto.setSymbolToken(stock.getSymbolToken());
        return dto;
    }
    // ── GET STOCKS BY DATE ────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PyHotStockGroupedResponseDto> getStocksByDate(LocalDate date) {
        try {
            PyHotStockGroupedResponseDto grouped = new PyHotStockGroupedResponseDto();

            grouped.setIntraday(getMappedStocksByDateAndCategory(date, HotStockCategory.INTRADAY));
            grouped.setShortTerm(getMappedStocksByDateAndCategory(date, HotStockCategory.SHORT_TERM));
            grouped.setMidTerm(getMappedStocksByDateAndCategory(date, HotStockCategory.MID_TERM));
            grouped.setLongTerm(getMappedStocksByDateAndCategory(date, HotStockCategory.LONG_TERM));

            return ApiResponse.success("Stocks fetched for date: " + date, grouped);

        } catch (Exception e) {
            log.error("Failed to fetch stocks by date: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to fetch stocks: " + e.getMessage());
        }
    }

// ── GET STOCKS BY DATE AND CATEGORY (WITH FALLBACK) ───────────────────────
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PyHotStockResponseDto>> getStocksByDateAndCategory(
            LocalDate date, HotStockCategory category) {
        try {
            LocalDate targetDate = (date != null) ? date : LocalDate.now();
            List<PyHotStockResponseDto> stocks = getMappedStocksByDateAndCategory(
                    targetDate, category);

            // If stocks exist for the specified date, return them normally
            if (!stocks.isEmpty()) {
                return ApiResponse.success(
                        category.name() + " stocks fetched for date: " + targetDate, stocks);
            }

            // Fallback: If no stocks exist for targetDate, check if latest active recommendations exist on or before targetDate
            log.info("No stocks found for date={} category={}, attempting fallback to latest active recommendations on or before {}",
                    targetDate, category, targetDate);
            return getLatestStocksByCategoryBeforeOrOn(category, targetDate);

        } catch (Exception e) {
            log.error("Failed to fetch stocks by date and category: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to fetch stocks: " + e.getMessage());
        }
    }

// ── GET LATEST STOCKS BY CATEGORY ─────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PyHotStockResponseDto>> getLatestStocksByCategory(HotStockCategory category) {
        return getLatestStocksByCategoryBeforeOrOn(category, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<PyHotStockResponseDto>> getLatestStocksByCategoryBeforeOrOn(
            HotStockCategory category, LocalDate onOrBeforeDate) {
        try {
            LocalDate target = (onOrBeforeDate != null) ? onOrBeforeDate : LocalDate.now();
            List<PyHotStockResponseDto> latestStocks = pyHotStockRepository
                    .findLatestActiveByCategoryBeforeOrOn(category, target)
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());

            if (!latestStocks.isEmpty()) {
                LocalDate fallbackDate = latestStocks.get(0).getCreatedAt().toLocalDate();
                log.info("Found {} latest active recommendations for category={} on or before {} (actual date={})",
                        latestStocks.size(), category, target, fallbackDate);
                return ApiResponse.success(
                        "Recent recommendations for " + category.name() + " — dated " + fallbackDate,
                        latestStocks);
            }

            return ApiResponse.success(
                    "No recommendations found for " + category.name(),
                    Collections.emptyList());

        } catch (Exception e) {
            log.error("Failed to fetch latest stocks for category {} on or before {}: {}", category, onOrBeforeDate, e.getMessage(), e);
            return ApiResponse.error(500, "Failed to fetch latest stocks: " + e.getMessage());
        }
    }

// ── HELPER: Get Stocks by Date and Category ───────────────────────────────
    private List<PyHotStockResponseDto> getMappedStocksByDateAndCategory(
            LocalDate date, HotStockCategory category) {
        return pyHotStockRepository
                .findByCreatedAtDateAndCategory(date, category)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
}
