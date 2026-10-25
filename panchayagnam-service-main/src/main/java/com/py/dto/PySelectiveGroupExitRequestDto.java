package com.py.dto;

import java.util.List;

public class PySelectiveGroupExitRequestDto {

    private List<Long> brokerAccountIds;
    private String orderType;
    private String productType;
    private String variety;
    private Integer triggerPrice;

    public List<Long> getBrokerAccountIds() {
        return brokerAccountIds;
    }

    public void setBrokerAccountIds(List<Long> brokerAccountIds) {
        this.brokerAccountIds = brokerAccountIds;
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

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public Integer getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(Integer triggerPrice) {
        this.triggerPrice = triggerPrice;
    }
}
