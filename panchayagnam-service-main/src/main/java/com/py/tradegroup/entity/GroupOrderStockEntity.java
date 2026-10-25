/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.py.tradegroup.entity;

// PY_GROUP_ORDER_STOCK

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PY_GROUP_ORDER_STOCK")
public class GroupOrderStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id", nullable = false)
    private GroupOrderHeaderEntity header;

    @Column(name = "trading_symbol", nullable = false)
    private String tradingSymbol;

    @Column(name = "symbol_token", nullable = false)
    private String symbolToken;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "price", nullable = false)
    private Double price;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupOrderExecutionEntity> executions = new ArrayList<>();

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the header
     */
    public GroupOrderHeaderEntity getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(GroupOrderHeaderEntity header) {
        this.header = header;
    }

    /**
     * @return the tradingSymbol
     */
    public String getTradingSymbol() {
        return tradingSymbol;
    }

    /**
     * @param tradingSymbol the tradingSymbol to set
     */
    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
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

    /**
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * @param exchange the exchange to set
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the executions
     */
    public List<GroupOrderExecutionEntity> getExecutions() {
        return executions;
    }

    /**
     * @param executions the executions to set
     */
    public void setExecutions(List<GroupOrderExecutionEntity> executions) {
        this.executions = executions;
    }
}
