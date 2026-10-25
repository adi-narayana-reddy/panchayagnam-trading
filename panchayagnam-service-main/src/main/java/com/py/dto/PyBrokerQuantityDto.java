package com.py.dto;

import jakarta.validation.constraints.NotNull;

public class PyBrokerQuantityDto {

    @NotNull(message = "brokerId is required")
    private Long brokerId;

    @NotNull(message = "quantity is required")
    private Integer quantity;

    public Long getBrokerId() { return brokerId; }
    public void setBrokerId(Long brokerId) { this.brokerId = brokerId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}