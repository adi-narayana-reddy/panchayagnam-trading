package com.py.tradegroup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Request payload to create a trade group")
public class CreateGroupRequest {

    @Schema(description = "Name of the trade group", example = "NIFTY50 Group")
    private String groupName;
    @Schema(description = "Description of the group", example = "Group for NIFTY 50 stocks trading")
    private String description;
    @Schema(description = "Creator identifier", example = "admin")
    private String createdBy;
    @Schema(description = "List of demat account IDs to add as members", example = "[1, 2, 3]")
    private List<Long> memberIds;
    @Schema(description = "List of stocks to add to the group")
    private List<StockRequest> stocks;

    @Schema(description = "Stock details for a trade group")
    public static class StockRequest {

        @Schema(description = "Exchange", example = "NSE", allowableValues = {"NSE", "BSE"})
        private String exchange;
        @Schema(description = "Trading symbol", example = "SBIN-EQ")
        private String tradingSymbol;
        @Schema(description = "Symbol token", example = "3045")
        private String symbolToken;
        @Schema(description = "Quantity to trade", example = "1")
        private Integer quantity;
        @Schema(description = "Order type", example = "MARKET", allowableValues = {"LIMIT", "MARKET", "SL", "SL-M"})
        private String orderType;
        @Schema(description = "Product type", example = "CNC", allowableValues = {"CNC", "MIS", "NRML"})
        private String productType;
        @Schema(description = "Price (0 for MARKET orders)", example = "0")
        private Double price;

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getTradingSymbol() {
            return tradingSymbol;
        }

        public void setTradingSymbol(String tradingSymbol) {
            this.tradingSymbol = tradingSymbol;
        }

        public String getSymbolToken() {
            return symbolToken;
        }

        public void setSymbolToken(String symbolToken) {
            this.symbolToken = symbolToken;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }

    public List<StockRequest> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockRequest> stocks) {
        this.stocks = stocks;
    }
}
