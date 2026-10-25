package com.py.motilol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MotilalModifyOrderParams {

    @JsonProperty("clientcode")
    private String clientCode;

    @JsonProperty("uniqueorderid")
    private String uniqueOrderId;

    @JsonProperty("newordertype")
    private String newOrderType;

    @JsonProperty("neworderduration")
    private String newOrderDuration;

    @JsonProperty("newprice")
    private Double newPrice;

    @JsonProperty("newtriggerprice")
    private Double newTriggerPrice;

    @JsonProperty("newquantityinlot")
    private Integer newQuantityInLot;

    @JsonProperty("newdisclosedquantity")
    private Integer newDisclosedQuantity;

    @JsonProperty("newgoodtilldate")
    private String newGoodTillDate;

    @JsonProperty("lastmodifiedtime")
    private String lastModifiedTime;

    @JsonProperty("qtytradedtoday")
    private Integer qtyTradedToday;

    // Getters and Setters
    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getUniqueOrderId() {
        return uniqueOrderId;
    }

    public void setUniqueOrderId(String uniqueOrderId) {
        this.uniqueOrderId = uniqueOrderId;
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
