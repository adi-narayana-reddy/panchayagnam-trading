package com.py.brokers.motilal.websocket;

/**
 * Represents a Motilal scrip subscription.
 *
 * exchange : "NSE", "BSE", "NSEFO", "MCX", "NSECD", "NCDEX", "BSEFO"
 * exchangeType : "CASH" or "DERIVATIVES" scripCode : Motilal integer scrip code
 * (e.g. 11536 for INFY on NSE) token : optional string key for cache lookup
 * (defaults to String.valueOf(scripCode))
 */
public class MotilalScripSubscription {

    private final String exchange;
    private final String exchangeType;
    private final int scripCode;
    private final String token;       // cache key — typically String.valueOf(scripCode)

    public MotilalScripSubscription(String exchange, String exchangeType, int scripCode) {
        this.exchange = exchange;
        this.exchangeType = exchangeType;
        this.scripCode = scripCode;
        this.token = String.valueOf(scripCode);
    }

    public MotilalScripSubscription(String exchange, String exchangeType,
            int scripCode, String token) {
        this.exchange = exchange;
        this.exchangeType = exchangeType;
        this.scripCode = scripCode;
        this.token = token;
    }

    public String getExchange() {
        return exchange;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public int getScripCode() {
        return scripCode;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return exchange + ":" + exchangeType + ":" + scripCode;
    }
}
