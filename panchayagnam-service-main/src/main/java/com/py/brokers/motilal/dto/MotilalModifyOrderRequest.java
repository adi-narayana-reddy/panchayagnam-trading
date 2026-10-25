package com.py.brokers.motilal.dto;

// MotilalModifyOrderRequest.java
public class MotilalModifyOrderRequest extends MotilalBaseRequest {

    private String uniqueOrderId;
    private String clientCode;
    private String newOrderType;
    private String newOrderDuration;
    private Double newPrice;
    private Double newTriggerPrice;
    private Integer newQuantityInLot;
    private Integer newDisclosedQuantity;
    private String newGoodTillDate;
    private String lastModifiedTime;
    private Integer qtyTradedToday;

    public String getUniqueOrderId() {
        return uniqueOrderId;
    }

    public void setUniqueOrderId(String uniqueOrderId) {
        this.uniqueOrderId = uniqueOrderId;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getNewOrderType() {
        return newOrderType;
    }

    public void setNewOrderType(String newOrderType) {
        this.newOrderType = newOrderType;
    }

    public String getNewOrderDuration() {
        return newOrderDuration;
    }

    public void setNewOrderDuration(String newOrderDuration) {
        this.newOrderDuration = newOrderDuration;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }

    public Double getNewTriggerPrice() {
        return newTriggerPrice;
    }

    public void setNewTriggerPrice(Double newTriggerPrice) {
        this.newTriggerPrice = newTriggerPrice;
    }

    public Integer getNewQuantityInLot() {
        return newQuantityInLot;
    }

    public void setNewQuantityInLot(Integer newQuantityInLot) {
        this.newQuantityInLot = newQuantityInLot;
    }

    public Integer getNewDisclosedQuantity() {
        return newDisclosedQuantity;
    }

    public void setNewDisclosedQuantity(Integer newDisclosedQuantity) {
        this.newDisclosedQuantity = newDisclosedQuantity;
    }

    public String getNewGoodTillDate() {
        return newGoodTillDate;
    }

    public void setNewGoodTillDate(String newGoodTillDate) {
        this.newGoodTillDate = newGoodTillDate;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Integer getQtyTradedToday() {
        return qtyTradedToday;
    }

    public void setQtyTradedToday(Integer qtyTradedToday) {
        this.qtyTradedToday = qtyTradedToday;
    }
}
