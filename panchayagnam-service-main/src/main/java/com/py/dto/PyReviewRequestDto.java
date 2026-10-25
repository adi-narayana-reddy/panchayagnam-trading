package com.py.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PyReviewRequestDto {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotBlank(message = "Status is required")
    private String status; // APPROVED or REJECTED

    private String reason; // Required only if REJECTED

    public PyReviewRequestDto() {
    }

    public PyReviewRequestDto(Long requestId, String status, String reason) {
        this.requestId = requestId;
        this.status = status;
        this.reason = reason;
    }

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
