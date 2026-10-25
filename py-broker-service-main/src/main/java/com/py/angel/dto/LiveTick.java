package com.py.angel.dto;

public class LiveTick {

    private String token;
    private double ltp;
    private long exchangeTime;

    public LiveTick(String token, double ltp, long exchangeTime) {
        this.token = token;
        this.ltp = ltp;
        this.exchangeTime = exchangeTime;
    }

    public String getToken() {
        return token;
    }

    public double getLtp() {
        return ltp;
    }

    public long getExchangeTime() {
        return exchangeTime;
    }
}

