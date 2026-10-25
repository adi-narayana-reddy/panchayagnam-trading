package com.py.motilol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MotilolOrderParams {

    @JsonProperty("clientcode")
    private String clientCode;

    @NotBlank(message = "Exchange is required")
    @JsonProperty("exchange")
    private String exchange;

    @NotNull(message = "Symbol token is required")
    @JsonProperty("symboltoken")
    private Integer symbolToken;

    @NotBlank(message = "Buy or Sell is required")
    @JsonProperty("buyorsell")
    private String buyOrSell;

    @NotBlank(message = "Order type is required")
    @JsonProperty("ordertype")
    private String orderType;

    @NotBlank(message = "Product type is required")
    @JsonProperty("producttype")
    private String productType;

    @NotBlank(message = "Order duration is required")
    @JsonProperty("orderduration")
    private String orderDuration;

    @NotNull(message = "Price is required")
    @JsonProperty("price")
    private Double price;

    @JsonProperty("triggerprice")
    private Double triggerPrice = 0.0;

    @NotNull(message = "Quantity is required")
    @JsonProperty("quantityinlot")
    private Integer quantityInLot;

    @JsonProperty("disclosedquantity")
    private Integer disclosedQuantity = 0;

    @JsonProperty("amoorder")
    private String amoOrder = "N";

    @JsonProperty("goodtilldate")
    private String goodTillDate = "";

    @JsonProperty("algoid")
    private String algoId = "";

    @JsonProperty("tag")
    private String tag = "";

    @JsonProperty("participantcode")
    private String participantCode = "";

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Integer getSymbolToken() {
        return symbolToken;
    }

    public void setSymbolToken(Integer symbolToken) {
        this.symbolToken = symbolToken;
    }

    public String getBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        this.buyOrSell = buyOrSell;
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

    public String getOrderDuration() {
        return orderDuration;
    }

    public void setOrderDuration(String orderDuration) {
        this.orderDuration = orderDuration;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(Double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public Integer getQuantityInLot() {
        return quantityInLot;
    }

    public void setQuantityInLot(Integer quantityInLot) {
        this.quantityInLot = quantityInLot;
    }

    public Integer getDisclosedQuantity() {
        return disclosedQuantity;
    }

    public void setDisclosedQuantity(Integer disclosedQuantity) {
        this.disclosedQuantity = disclosedQuantity;
    }

    public String getAmoOrder() {
        return amoOrder;
    }

    public void setAmoOrder(String amoOrder) {
        this.amoOrder = amoOrder;
    }

    public String getGoodTillDate() {
        return goodTillDate;
    }

    public void setGoodTillDate(String goodTillDate) {
        this.goodTillDate = goodTillDate;
    }

    public String getAlgoId() {
        return algoId;
    }

    public void setAlgoId(String algoId) {
        this.algoId = algoId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getParticipantCode() {
        return participantCode;
    }

    public void setParticipantCode(String participantCode) {
        this.participantCode = participantCode;
    }
}
