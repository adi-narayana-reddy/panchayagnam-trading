package com.py.dto;

import jakarta.validation.constraints.NotNull;

public class PySubscriptionReviewDto {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Status is required")
    private String status; // APPROVED or REJECTED

    private String reason;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
