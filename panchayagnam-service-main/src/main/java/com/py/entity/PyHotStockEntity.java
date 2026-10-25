package com.py.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.py.common.enums.HotStockCategory;
import com.py.common.enums.HotStockDirection;
import com.py.common.enums.HotStockSignal;

@Entity
@Table(
        name = "py_hot_stocks_table",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_symbol_category",
                    columnNames = {"symbol", "category"}
            )
        }
)
public class PyHotStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @Column(name = "SYMBOL_TOKEN", nullable = false, length = 20)
    private String symbolToken;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "target", nullable = false, precision = 10, scale = 2)
    private BigDecimal target;

    @Column(name = "change_percent", precision = 10, scale = 2)
    private BigDecimal changePercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_direction", nullable = false, length = 10)
    private HotStockDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_signal", nullable = false, length = 20)
    private HotStockSignal signal;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_category", nullable = false, length = 20)
    private HotStockCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_by", nullable = false)
    private PyUser recommendedBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }

    public PyUser getRecommendedBy() {
        return recommendedBy;
    }

    public void setRecommendedBy(PyUser recommendedBy) {
        this.recommendedBy = recommendedBy;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the direction
     */
    public HotStockDirection getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(HotStockDirection direction) {
        this.direction = direction;
    }

    /**
     * @return the signal
     */
    public HotStockSignal getSignal() {
        return signal;
    }

    /**
     * @param signal the signal to set
     */
    public void setSignal(HotStockSignal signal) {
        this.signal = signal;
    }

    /**
     * @return the category
     */
    public HotStockCategory getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(HotStockCategory category) {
        this.category = category;
    }

    /**
     * @return the symbolToken
     */
    public String getSymbolToken() {
        return symbolToken;
    }

    /**
     * @param symbolToken the symbolToken to set
     */
    public void setSymbolToken(String symbolToken) {
        this.symbolToken = symbolToken;
    }
}
