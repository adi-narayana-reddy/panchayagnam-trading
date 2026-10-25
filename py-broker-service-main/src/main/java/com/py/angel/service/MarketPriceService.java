package com.py.angel.service;

import org.json.JSONObject;

/**
 * Service interface for fetching market price data (LTP).
 * Implement this with your preferred data source (e.g., Groww, NSE, Angel API).
 */
public interface MarketPriceService {
    JSONObject getLTP(String tradingSymbol);
}
