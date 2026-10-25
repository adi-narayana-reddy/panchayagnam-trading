package com.py.brokers.motilal.dto;
// MotilalPlaceOrderRequest.java

public class MotilalPlaceOrderRequest extends MotilalBaseRequest {

    private String exchange;
    private Integer symbolToken;
    private String buyOrSell;
    private String orderType;
    private String productType;
    private String orderDuration;
    private Double price;
    private Double triggerPrice;
    private Integer quantityInLot;
    private Integer disclosedQuantity;
    private String amoOrder;
    private String goodTillDate;
    private String algoId;
    private String participantCode;

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

    public String getParticipantCode() {
        return participantCode;
    }

    public void setParticipantCode(String participantCode) {
        this.participantCode = participantCode;
    }
}
