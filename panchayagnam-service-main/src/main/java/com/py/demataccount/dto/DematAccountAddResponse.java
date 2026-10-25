package com.py.demataccount.dto;

public class DematAccountAddResponse {

    private Long dematAccountId;
    private String message;

    public DematAccountAddResponse(Long dematAccountId, String message) {
        this.dematAccountId = dematAccountId;
        this.message = message;
    }

    public Long getDematAccountId() {
        return dematAccountId;
    }

    public String getMessage() {
        return message;
    }
}

