package com.py.profitmart.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class ProfitmartAccountService {

    private static final Logger log = LoggerFactory.getLogger(ProfitmartAccountService.class);

    @Value("${trading.paper-trading.enabled:true}")
    private boolean paperTradingEnabled;

    // Base URL — update to Profitmart production URL when received
    private static final String BASE_URL = "https://profitmart.in/web/NorenWClient/";

    private static final String LOGIN_URL           = BASE_URL + "QuickAuth";
    private static final String LOGOUT_URL          = BASE_URL + "Logout";
    private static final String ORDER_BOOK_URL      = BASE_URL + "OrderBook";
    private static final String TRADE_BOOK_URL      = BASE_URL + "TradeBook";
    private static final String PLACE_ORDER_URL     = BASE_URL + "PlaceOrder";
    private static final String MODIFY_ORDER_URL    = BASE_URL + "ModifyOrder";
    private static final String CANCEL_ORDER_URL    = BASE_URL + "CancelOrder";
    private static final String ORDER_HISTORY_URL   = BASE_URL + "SingleOrdHist";
    private static final String HOLDINGS_URL        = BASE_URL + "Holdings";
    private static final String POSITIONS_URL       = BASE_URL + "PositionBook";
    private static final String LIMITS_URL          = BASE_URL + "Limits";
    private static final String SEARCH_URL          = BASE_URL + "SearchScrip";
    private static final String QUOTES_URL          = BASE_URL + "GetQuotes";
    private static final String SECURITY_INFO_URL   = BASE_URL + "GetSecurityInfo";

    @Autowired
    private RestTemplate restTemplate;

    // ── HELPER: build headers ─────────────────────────────────────────────────
    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return h;
    }

    // ── HELPER: Profitmart uses jData=<json>&jKey=<appkey> form body ──────────
    private String formBody(JSONObject data, String jKey) {
        return "jData=" + data.toString() + "&jKey=" + jKey;
    }

    // ── HELPER: POST and return JSONObject ────────────────────────────────────
    private JSONObject post(String url, String body) {
        HttpEntity<String> entity = new HttpEntity<>(body, headers());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        if (response.getBody() == null || response.getBody().isBlank()) {
            throw new RuntimeException("Empty response from Profitmart API: " + url);
        }
        return new JSONObject(response.getBody());
    }

    // ── HELPER: POST and return JSONArray ─────────────────────────────────────
    private JSONArray postArray(String url, String body) {
        HttpEntity<String> entity = new HttpEntity<>(body, headers());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        if (response.getBody() == null || response.getBody().isBlank()) {
            throw new RuntimeException("Empty response from Profitmart API: " + url);
        }
        String resp = response.getBody().trim();
        // API returns array on success, object on failure
        if (resp.startsWith("[")) {
            return new JSONArray(resp);
        }
        // failure — wrap in array for consistent handling
        JSONObject err = new JSONObject(resp);
        throw new RuntimeException("Profitmart API error: " + err.optString("emsg", "Unknown error"));
    }

    // ── HELPER: SHA-256 hash ──────────────────────────────────────────────────
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed: " + e.getMessage());
        }
    }

    // ── HELPER: assert stat=Ok ────────────────────────────────────────────────
    private void assertOk(JSONObject json, String context) {
        if (!"Ok".equals(json.optString("stat"))) {
            String msg = json.optString("emsg", context + " failed");
            throw new RuntimeException(context + " failed: " + msg);
        }
    }

    // ── EXCHANGE OAUTH TOKEN ──────────────────────────────────────────────────
    public JSONObject exchangeOAuthToken(String userId, String code, String checksum) {
        if (paperTradingEnabled) {
            log.info("🛡️ [PAPER TRADING] Profitmart OAuth token exchange simulated for userId={}", userId);
            JSONObject sim = new JSONObject();
            sim.put("stat", "Ok");
            String simToken = "PAPER-SIM-PM-" + System.currentTimeMillis();
            sim.put("susertoken", simToken);
            sim.put("access_token", simToken);
            sim.put("refresh_token", "PAPER-SIM-REFRESH-" + System.currentTimeMillis());
            sim.put("expires_in", "86400");
            sim.put("uname", (userId != null && !userId.isBlank()) ? userId : "Simulated Trader");
            return sim;
        }

        try {
            JSONObject data = new JSONObject();
            data.put("apkversion", "1.0");
            data.put("uid", userId != null ? userId : "");
            data.put("code", code);
            data.put("appkey", checksum);
            data.put("source", "API");

            String body = "jData=" + data.toString();
            HttpEntity<String> entity = new HttpEntity<>(body, headers());
            ResponseEntity<String> response = restTemplate.postForEntity(LOGIN_URL, entity, String.class);

            if (response.getBody() == null || response.getBody().isBlank()) {
                throw new RuntimeException("Empty response from Profitmart during OAuth exchange");
            }

            JSONObject json = new JSONObject(response.getBody());
            log.info("Profitmart OAuth response: stat={}", json.optString("stat"));
            return json;
        } catch (Exception e) {
            log.error("Profitmart OAuth token exchange failed: {}", e.getMessage());
            JSONObject err = new JSONObject();
            err.put("stat", "Not_Ok");
            err.put("emsg", "OAuth exchange failed: " + e.getMessage());
            return err;
        }
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    // Profitmart login: pwd = SHA-256(password), appkey = SHA-256(userid + apiSecret)
    public JSONObject login(String userId, String password,
                            String twoFa, String vendorCode,
                            String apiSecret, String imei) {

        if (paperTradingEnabled) {
            log.info("🛡️ [PAPER TRADING] Profitmart login simulated for userId={}", userId);
            JSONObject sim = new JSONObject();
            sim.put("stat", "Ok");
            String simToken = "PAPER-SIM-PM-" + System.currentTimeMillis();
            sim.put("susertoken", simToken);
            sim.put("access_token", simToken);
            sim.put("refresh_token", "PAPER-SIM-REFRESH-" + System.currentTimeMillis());
            sim.put("expires_in", "86400");
            sim.put("uname", (userId != null && !userId.isBlank()) ? userId : "Simulated Trader");
            return sim;
        }

        if (password == null || password.isBlank()) {
            JSONObject err = new JSONObject();
            err.put("stat", "Not_Ok");
            err.put("emsg", "Password is required for Profitmart credentials login");
            return err;
        }

        String hashedPwd    = sha256(password);
        String hashedAppKey = sha256((userId != null ? userId : "") + (apiSecret != null ? apiSecret : ""));

        JSONObject data = new JSONObject();
        data.put("apkversion", "1.0");
        data.put("uid",        userId != null ? userId : "");
        data.put("pwd",        hashedPwd);
        data.put("factor2",    twoFa != null ? twoFa : "");
        data.put("vc",         vendorCode != null ? vendorCode : "");
        data.put("appkey",     hashedAppKey);
        data.put("imei",       imei != null ? imei : "");
        data.put("source",     "API");

        // Login uses jData only — no jKey yet
        String body = "jData=" + data.toString();
        HttpEntity<String> entity = new HttpEntity<>(body, headers());
        ResponseEntity<String> response = restTemplate.postForEntity(LOGIN_URL, entity, String.class);

        JSONObject json = new JSONObject(response.getBody());
        log.info("Profitmart login response: stat={}", json.optString("stat"));
        assertOk(json, "Profitmart login");
        return json;
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────────
    public JSONObject logout(String userId, String susertoken) {
        JSONObject data = new JSONObject();
        data.put("uid", userId);

        JSONObject json = post(LOGOUT_URL, formBody(data, susertoken));
        log.info("Profitmart logout: stat={}", json.optString("stat"));
        return json;
    }

    // ── ORDER BOOK ────────────────────────────────────────────────────────────
    public JSONArray getOrderBook(String userId, String susertoken) {
        JSONObject data = new JSONObject();
        data.put("uid", userId);

        log.info("Profitmart order book | userId={}", userId);
        return postArray(ORDER_BOOK_URL, formBody(data, susertoken));
    }

    // ── TRADE BOOK ────────────────────────────────────────────────────────────
    public JSONArray getTradeBook(String userId, String susertoken) {
        JSONObject data = new JSONObject();
        data.put("uid",   userId);
        data.put("actid", userId);

        log.info("Profitmart trade book | userId={}", userId);
        return postArray(TRADE_BOOK_URL, formBody(data, susertoken));
    }

    // ── SINGLE ORDER HISTORY ──────────────────────────────────────────────────
    public JSONArray getOrderHistory(String userId, String susertoken, String orderNo) {
        JSONObject data = new JSONObject();
        data.put("uid",        userId);
        data.put("norenordno", orderNo);

        log.info("Profitmart order history | orderNo={}", orderNo);
        return postArray(ORDER_HISTORY_URL, formBody(data, susertoken));
    }

    // ── PLACE ORDER ───────────────────────────────────────────────────────────
    public JSONObject placeOrder(String userId, String susertoken,
                                 String exchange, String tradingSymbol,
                                 String buyOrSell, int quantity, int discloseQty,
                                 String priceType, double price, Double triggerPrice,
                                 String productType, String retention,
                                 String remarks, String amo) {

        if (paperTradingEnabled) {
            String simOrderNo = "PAPER-SIM-PM-" + System.currentTimeMillis();
            log.info("🛡️ [PAPER TRADING - SIMULATED ORDER] Profitmart order simulated | exchange={} tsym={} side={} qty={} price={} orderNo={}",
                    exchange, tradingSymbol, buyOrSell, quantity, price, simOrderNo);
            JSONObject sim = new JSONObject();
            sim.put("stat", "Ok");
            sim.put("norenordno", simOrderNo);
            sim.put("result", simOrderNo);
            sim.put("request_time", java.time.LocalDateTime.now().toString());
            return sim;
        }

        JSONObject data = new JSONObject();
        data.put("uid",    userId);
        data.put("actid",  userId);
        data.put("exch",   exchange);
        data.put("tsym",   tradingSymbol);
        data.put("trantype", buyOrSell);
        data.put("qty",    String.valueOf(quantity));
        data.put("dscqty", String.valueOf(discloseQty));
        data.put("prctyp", priceType);
        data.put("prc",    String.valueOf(price));
        data.put("trgprc", triggerPrice != null ? String.valueOf(triggerPrice) : "0");
        data.put("prd",    productType);
        data.put("ret",    retention != null ? retention : "DAY");
        data.put("remarks", remarks != null ? remarks : "");
        data.put("amo",    amo != null ? amo : "No");
        data.put("ordersource", "API");

        log.info("Profitmart place order | exchange={} tsym={} side={} qty={} price={}",
                exchange, tradingSymbol, buyOrSell, quantity, price);

        JSONObject json = post(PLACE_ORDER_URL, formBody(data, susertoken));
        assertOk(json, "Profitmart place order");
        log.info("✅ Profitmart order placed | norenordno={}", json.optString("norenordno"));
        return json;
    }

    // ── MODIFY ORDER ──────────────────────────────────────────────────────────
    public JSONObject modifyOrder(String userId, String susertoken,
                                  String orderNo, String exchange, String tradingSymbol,
                                  int newQuantity, String newPriceType,
                                  double newPrice, Double newTriggerPrice,
                                  String retention, String amo) {

        if (paperTradingEnabled) {
            log.info("🛡️ [PAPER TRADING - SIMULATED ORDER] Profitmart modify order simulated | orderNo={}", orderNo);
            JSONObject sim = new JSONObject();
            sim.put("stat", "Ok");
            sim.put("result", orderNo);
            return sim;
        }

        JSONObject data = new JSONObject();
        data.put("uid",        userId);
        data.put("norenordno", orderNo);
        data.put("exch",       exchange);
        data.put("tsym",       tradingSymbol);
        data.put("qty",        String.valueOf(newQuantity));
        data.put("prctyp",     newPriceType);
        data.put("prc",        String.valueOf(newPrice));
        data.put("trgprc",     newTriggerPrice != null ? String.valueOf(newTriggerPrice) : "0");
        data.put("ret",        retention != null ? retention : "DAY");
        data.put("amo",        amo != null ? amo : "No");

        log.info("Profitmart modify order | orderNo={} qty={} price={}",
                orderNo, newQuantity, newPrice);

        JSONObject json = post(MODIFY_ORDER_URL, formBody(data, susertoken));
        assertOk(json, "Profitmart modify order");
        log.info("✅ Profitmart order modified | result={}", json.optString("result"));
        return json;
    }

    // ── CANCEL ORDER ──────────────────────────────────────────────────────────
    public JSONObject cancelOrder(String userId, String susertoken, String orderNo) {
        if (paperTradingEnabled) {
            log.info("🛡️ [PAPER TRADING - SIMULATED ORDER] Profitmart cancel order simulated | orderNo={}", orderNo);
            JSONObject sim = new JSONObject();
            sim.put("stat", "Ok");
            sim.put("result", orderNo);
            return sim;
        }

        JSONObject data = new JSONObject();
        data.put("uid",        userId);
        data.put("norenordno", orderNo);

        log.info("Profitmart cancel order | orderNo={}", orderNo);

        JSONObject json = post(CANCEL_ORDER_URL, formBody(data, susertoken));
        assertOk(json, "Profitmart cancel order");
        log.info("✅ Profitmart order cancelled | result={}", json.optString("result"));
        return json;
    }

    // ── HOLDINGS ──────────────────────────────────────────────────────────────
    public JSONArray getHoldings(String userId, String susertoken) {
        JSONObject data = new JSONObject();
        data.put("uid",   userId);
        data.put("actid", userId);
        data.put("prd",   "C");  // CNC — delivery holdings

        log.info("Profitmart holdings | userId={}", userId);
        return postArray(HOLDINGS_URL, formBody(data, susertoken));
    }

    // ── POSITIONS ─────────────────────────────────────────────────────────────
    public JSONArray getPositions(String userId, String susertoken) {
        JSONObject data = new JSONObject();
        data.put("uid",   userId);
        data.put("actid", userId);

        log.info("Profitmart positions | userId={}", userId);
        return postArray(POSITIONS_URL, formBody(data, susertoken));
    }

    // ── LIMITS (MARGIN) ───────────────────────────────────────────────────────
    public JSONObject getLimits(String userId, String susertoken) {
        JSONObject data = new JSONObject();
        data.put("uid",   userId);
        data.put("actid", userId);

        log.info("Profitmart limits | userId={}", userId);
        JSONObject json = post(LIMITS_URL, formBody(data, susertoken));
        assertOk(json, "Profitmart limits");
        return json;
    }

    // ── SEARCH SCRIP ──────────────────────────────────────────────────────────
    public JSONObject searchScrip(String userId, String susertoken,
                                  String exchange, String searchText) {
        JSONObject data = new JSONObject();
        data.put("uid",   userId);
        data.put("stext", searchText);
        data.put("exch",  exchange);

        log.info("Profitmart search | exchange={} text={}", exchange, searchText);
        JSONObject json = post(SEARCH_URL, formBody(data, susertoken));
        assertOk(json, "Profitmart search");
        return json;
    }

    // ── GET QUOTES (LTP + market depth) ───────────────────────────────────────
    public JSONObject getQuotes(String userId, String susertoken,
                                String exchange, String token) {
        JSONObject data = new JSONObject();
        data.put("uid",   userId);
        data.put("exch",  exchange);
        data.put("token", token);

        log.info("Profitmart quotes | exchange={} token={}", exchange, token);
        JSONObject json = post(QUOTES_URL, formBody(data, susertoken));
        assertOk(json, "Profitmart quotes");
        return json;
    }

    // ── GET SECURITY INFO ─────────────────────────────────────────────────────
    public JSONObject getSecurityInfo(String userId, String susertoken,
                                      String exchange, String token) {
        JSONObject data = new JSONObject();
        data.put("uid",   userId);
        data.put("exch",  exchange);
        data.put("token", token);

        log.info("Profitmart security info | exchange={} token={}", exchange, token);
        JSONObject json = post(SECURITY_INFO_URL, formBody(data, susertoken));
        assertOk(json, "Profitmart security info");
        return json;
    }
}
