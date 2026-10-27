package com.py.demataccount.repository;

import com.py.common.enums.HotStockCategory;
import com.py.entity.PyHotStockEntity;
import com.py.entity.PyUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PyHotStockRepository extends JpaRepository<PyHotStockEntity, Long> {

    @Query("SELECT COUNT(s) > 0 FROM PyHotStockEntity s WHERE s.symbol = :symbol AND s.isActive = true " +
            "AND s.category = :category AND s.createdAt >= :startOfDay AND s.createdAt < :endOfDay")
    boolean validateHotStock(
            @Param("symbol") String symbol,
            @Param("category") HotStockCategory category,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    default boolean validateHotStock(String symbol, HotStockCategory category, LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        return validateHotStock(symbol, category, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    // Fetch all active stocks
    List<PyHotStockEntity> findByIsActive(Boolean isActive);

    // Fetch active stocks by category
    List<PyHotStockEntity> findByCategoryAndIsActive(
            HotStockCategory category, Boolean isActive);

    // Fetch all stocks recommended by a user
    List<PyHotStockEntity> findByRecommendedBy(PyUser recommendedBy);

    // Fetch active stocks recommended by a user
    List<PyHotStockEntity> findByRecommendedByAndIsActive(
            PyUser recommendedBy, Boolean isActive);

    // Find by symbol and category
    Optional<PyHotStockEntity> findBySymbolAndCategory(
            String symbol, HotStockCategory category);

    @Query("SELECT s FROM PyHotStockEntity s WHERE s.createdAt >= :startOfDay AND s.createdAt < :endOfDay")
    List<PyHotStockEntity> findByCreatedAtDate(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    default List<PyHotStockEntity> findByCreatedAtDate(LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        return findByCreatedAtDate(target.atStartOfDay(), target.plusDays(1).atStartOfDay());
    }

    // Fetch all stocks by date and category
    @Query("SELECT s FROM PyHotStockEntity s WHERE s.createdAt >= :startOfDay AND s.createdAt < :endOfDay AND s.category = :category ORDER BY s.changePercent DESC")
    List<PyHotStockEntity> findByCreatedAtDateAndCategory(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("category") HotStockCategory category);

    default List<PyHotStockEntity> findByCreatedAtDateAndCategory(LocalDate date, HotStockCategory category) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        return findByCreatedAtDateAndCategory(target.atStartOfDay(), target.plusDays(1).atStartOfDay(), category);
    }

    // ── FALLBACK QUERY: Find latest active recommendations for category ──────
    @Query("SELECT MAX(s.createdAt) FROM PyHotStockEntity s WHERE s.category = :category AND s.isActive = true AND s.createdAt < :endOfDay")
    LocalDateTime findLatestCreatedAtByCategoryBeforeOrOn(
            @Param("category") HotStockCategory category,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT MAX(s.createdAt) FROM PyHotStockEntity s WHERE s.category = :category AND s.isActive = true")
    LocalDateTime findLatestCreatedAtByCategory(@Param("category") HotStockCategory category);

    default List<PyHotStockEntity> findLatestActiveByCategoryBeforeOrOn(HotStockCategory category, LocalDate onOrBeforeDate) {
        LocalDate target = (onOrBeforeDate != null) ? onOrBeforeDate : LocalDate.now();
        LocalDateTime endOfDay = target.plusDays(1).atStartOfDay();
        LocalDateTime latest = findLatestCreatedAtByCategoryBeforeOrOn(category, endOfDay);
        if (latest == null) {
            return java.util.Collections.emptyList();
        }
        LocalDate latestDate = latest.toLocalDate();
        return findByCreatedAtDateAndCategory(latestDate.atStartOfDay(), latestDate.plusDays(1).atStartOfDay(), category);
    }

    default List<PyHotStockEntity> findLatestActiveByCategory(HotStockCategory category) {
        return findLatestActiveByCategoryBeforeOrOn(category, LocalDate.now());
    }

//    List<PyHotStockEntity> findByIsActiveTrueOrderByChangePercentDesc();

    @Query("SELECT s FROM PyHotStockEntity s LEFT JOIN FETCH s.recommendedBy WHERE s.isActive = true ORDER BY s.changePercent DESC")
    List<PyHotStockEntity> findByIsActiveTrueOrderByChangePercentDesc();

}
