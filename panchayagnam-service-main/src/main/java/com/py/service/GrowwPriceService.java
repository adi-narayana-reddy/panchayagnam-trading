package com.py.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GrowwPriceService {

    private static final Logger log = LoggerFactory.getLogger(GrowwPriceService.class);

    private final RestTemplate restTemplate;

    public GrowwPriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JSONObject getLTP(String symbol) {
        log.info("Fetching LTP from Groww | symbol={}", symbol);
        try {
            // Split by '-' and take the first part
            String baseSymbol = symbol.split("-")[0];

            String url = "https://groww.in/v1/api/stocks_data/v1/tr_live_prices/exchange/NSE/segment/CASH/"
                         + baseSymbol + "/latest";

            String raw = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(raw);

            JSONObject data = new JSONObject();
            data.put("exchange", "NSE");
            data.put("tradingsymbol", baseSymbol);
            data.put("ltp", json.optDouble("ltp")); // adjust if Groww uses another field

            JSONObject wrapper = new JSONObject();
            wrapper.put("status", true);
            wrapper.put("data", data);

            log.info("LTP fetched | symbol={}, ltp={}", baseSymbol, json.optDouble("ltp"));
            return wrapper.getJSONObject("data");

        } catch (Exception e) {
            log.error("Groww LTP failed | symbol={}: {}", symbol, e.getMessage(), e);
            throw new RuntimeException("Groww LTP failed: " + e.getMessage());
        }
    }
}
