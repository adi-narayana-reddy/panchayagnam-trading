package com.py.dto;

import jakarta.validation.constraints.NotNull;

public class PySubscriptionRequestDto {

    @NotNull(message = "Plan months is required")
    private Integer planMonths; // 1, 2, 12

    @NotNull(message = "Type is required")
    private String type; // CLIENT or GROUP

    public Integer getPlanMonths() {
        return planMonths;
    }

    public void setPlanMonths(Integer planMonths) {
        this.planMonths = planMonths;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
