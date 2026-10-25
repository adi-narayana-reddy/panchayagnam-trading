package com.py.angel.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Default implementation of MarketPriceService.
 * Replace with actual market data source integration.
 */
@Service
public class DefaultMarketPriceService implements MarketPriceService {

    private static final Logger log = LoggerFactory.getLogger(DefaultMarketPriceService.class);

    @Override
    public JSONObject getLTP(String tradingSymbol) {
        log.warn("Using default MarketPriceService. Implement a real provider for production.");
        JSONObject response = new JSONObject();
        response.put("symbol", tradingSymbol);
        response.put("ltp", 0.0);
        response.put("message", "Default implementation - configure a real market data provider");
        return response;
    }
}
