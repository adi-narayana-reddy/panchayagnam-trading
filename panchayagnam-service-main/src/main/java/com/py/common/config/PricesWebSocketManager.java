package com.py.common.config;

import com.py.brokers.angel.client.SmartConnect;
import com.py.brokers.angel.exception.SmartAPIException;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerSecret;
import com.py.demataccount.entity.PyBrokerToken;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.demataccount.repository.PyHotStockRepository;
import com.py.entity.PyHotStockEntity;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PricesWebSocketManager {

    private static final Logger log = LoggerFactory.getLogger(PricesWebSocketManager.class);

    // accId → dedicated independent AngelOne WS client
    private static final Map<Long, AngelOneWebSocketClient> dedicatedClients
            = new ConcurrentHashMap<>();

    // sessionId → set of subscribed tokens
    private static final Map<String, Set<String>> sessionSubscribed
            = new ConcurrentHashMap<>();

    // accId → (token → PyHotStockEntity) — for enriching ticks without hitting DB
    private static final Map<Long, Map<String, PyHotStockEntity>> hotStocksCache
            = new ConcurrentHashMap<>();

    @Autowired
    private PyHotStockRepository hotStockRepository;

    @Autowired
    private PyBrokerAccountRepository pyBrokerAccountRepository;

    @PostConstruct
    public void init() {
        PricesWebSocketServer.setManager(this);
        log.info("✅ PricesWebSocketManager initialized");
    }

    // ── Called on every new frontend WebSocket connection ────────────────────
    public void startPriceFeed(Long accId, Session session) {
        String sessionId = session.getId();
        sessionSubscribed.put(sessionId, ConcurrentHashMap.newKeySet());

        Thread t = new Thread(() -> {
            try {
                // Step 1: Load active hot stocks from DB
                List<PyHotStockEntity> hotStocks =
                        hotStockRepository.findByIsActiveTrueOrderByChangePercentDesc();

                // Step 2: No hot stocks — notify frontend and stop
                if (hotStocks.isEmpty()) {
                    log.warn("⚠️ No hot stocks in DB — accId: {} sessionId: {}", accId, sessionId);
                    JSONObject noData = new JSONObject();
                    noData.put("type",    "PRICE_SNAPSHOT");
                    noData.put("source",  "HOT_STOCKS");
                    noData.put("status",  "EMPTY");
                    noData.put("message", "No hot stocks found");
                    noData.put("stocks",  new JSONArray());
                    PricesWebSocketServer.sendToSession(sessionId, noData.toString());
                    return;
                }

                log.info("🔥 Hot stocks found: {} — accId: {} sessionId: {}",
                        hotStocks.size(), accId, sessionId);

                // Step 3: Build token list + cache for tick enrichment
                List<String> tokens = new ArrayList<>();
                Map<String, PyHotStockEntity> tokenMap = new HashMap<>();
                for (PyHotStockEntity stock : hotStocks) {
                    String token = stock.getSymbolToken();
                    tokens.add(token);
                    tokenMap.put(token, stock);
                }
                hotStocksCache.put(accId, tokenMap);

                // Step 4: Batch fetch live LTP for all tokens in ONE API call
                Map<String, Double> ltpMap = fetchLtpBatch(tokens, accId);

                // Step 5: Build enriched snapshot with live LTP already included
                JSONArray stocksPayload = new JSONArray();
                for (PyHotStockEntity stock : hotStocks) {
                    double ltp = ltpMap.getOrDefault(stock.getSymbolToken(), 0.0);
                    stocksPayload.put(buildHotStockJson(stock, ltp));
                }

                // Step 6: Track tokens for this session
                sessionSubscribed.get(sessionId).addAll(tokens);
                PricesWebSocketServer.addSessionTokens(sessionId, tokens);

                // Step 7: Send snapshot immediately with live LTP
                JSONObject snapshot = new JSONObject();
                snapshot.put("type",   "PRICE_SNAPSHOT");
                snapshot.put("source", "HOT_STOCKS");
                snapshot.put("status", "OK");
                snapshot.put("stocks", stocksPayload);
                PricesWebSocketServer.sendToSession(sessionId, snapshot.toString());
                log.info("📸 Price snapshot sent — accId: {} sessionId: {} stocks: {}",
                        accId, sessionId, tokens.size());

                // Step 8: Start dedicated Angel One WS for live tick updates
                startDedicatedClient(accId, tokens);

            } catch (Exception e) {
                log.error("❌ startPriceFeed failed — accId: {} sessionId: {} Error: {}",
                        accId, sessionId, e.getMessage());
            }
        }, "prices-feed-" + accId + "-" + sessionId);
        t.setDaemon(true);
        t.start();
    }

    // ── Batch fetch LTP for all hot stock tokens in ONE API call ─────────────
    private Map<String, Double> fetchLtpBatch(List<String> tokens, Long accId) {
        Map<String, Double> ltpMap = new HashMap<>();
        try {
            PyBrokerAccountEntity account = pyBrokerAccountRepository.findById(accId)
                    .orElse(null);
            if (account == null) return ltpMap;

            PyBrokerToken tokenEntity = account.getToken();
            PyBrokerSecret secret = account.getSecret();
            if (tokenEntity == null || secret == null) return ltpMap;

            SmartConnect smartConnect = new SmartConnect(secret.getApiKey());
            smartConnect.setAccessToken(tokenEntity.getAccessToken());

            // ✅ LTP mode — lightest call, one request for all tokens
            JSONObject exchangeTokens = new JSONObject();
            exchangeTokens.put("exchangeType", 1);
            exchangeTokens.put("tokens", new JSONArray(tokens));

            JSONObject params = new JSONObject();
            params.put("mode", "LTP");
            params.put("exchangeTokens", exchangeTokens);

            JSONObject response = smartConnect.marketData(params);

            if (response != null) {
                JSONArray fetched = response.optJSONArray("fetched");
                if (fetched != null) {
                    for (int i = 0; i < fetched.length(); i++) {
                        JSONObject item = fetched.getJSONObject(i);
                        String tkn = item.optString("symbolToken");
                        double ltp = item.optDouble("ltp", 0);
                        ltpMap.put(tkn, ltp);
                    }
                }
            }

            log.info("📊 Batch LTP fetched — accId: {} count: {}", accId, ltpMap.size());

        } catch (SmartAPIException | IOException e) {
            log.warn("⚠️ fetchLtpBatch API error — accId: {} Error: {}", accId, e.getMessage());
        } catch (Exception e) {
            log.warn("⚠️ fetchLtpBatch failed — accId: {} Error: {}", accId, e.getMessage());
        }
        return ltpMap;
    }

    // ── Start or reuse dedicated prices WS client ─────────────────────────────
    private void startDedicatedClient(Long accId, List<String> tokens) {
        try {
            AngelOneWebSocketClient existing = dedicatedClients.get(accId);
            if (existing != null) {
                existing.subscribeTokens(tokens);
                log.info("♻️ Reusing dedicated prices WS — accId: {} tokens: {}",
                        accId, tokens.size());
            } else {
                AngelOneWebSocketClient client = createDedicatedClient(accId, tokens);
                if (client != null) {
                    dedicatedClients.put(accId, client);
                    log.info("✅ Dedicated prices WS started — accId: {} tokens: {}",
                            accId, tokens.size());
                } else {
                    log.error("❌ Failed to create dedicated prices WS — accId: {}", accId);
                }
            }
        } catch (Exception e) {
            log.error("❌ startDedicatedClient failed — accId: {} Error: {}", accId, e.getMessage());
        }
    }

    // ── Create a brand new independent AngelOne WS client ────────────────────
    private AngelOneWebSocketClient createDedicatedClient(Long accId, List<String> tokens) {
        try {
            PyBrokerAccountEntity account = pyBrokerAccountRepository.findById(accId)
                    .orElseThrow(() -> new RuntimeException("Account not found: " + accId));

            PyBrokerToken tokenEntity = account.getToken();
            if (tokenEntity == null) {
                log.warn("❌ No token for accId: {}", accId);
                return null;
            }
            if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("❌ Token expired for accId: {}", accId);
                return null;
            }

            PyBrokerSecret secret = account.getSecret();
            if (secret == null) {
                log.warn("❌ No secret for accId: {}", accId);
                return null;
            }

            String feedToken = tokenEntity.getFeedToken();
            if (feedToken == null || feedToken.isBlank()) {
                log.warn("❌ Feed token empty for accId: {}", accId);
                return null;
            }

            return AngelOneWebSocketClient.connect(
                    accId,
                    account.getClientId(),
                    tokenEntity.getAccessToken(),
                    feedToken,
                    secret.getApiKey(),
                    tokens
            );

        } catch (Exception e) {
            log.error("❌ createDedicatedClient failed — accId: {} Error: {}", accId, e.getMessage());
            return null;
        }
    }

    // ── Called by AngelOneWebSocketClient on every price tick ─────────────────
    public static void onPriceTick(Long accId, String token,
                                   double ltp, double closePrice,
                                   double openPrice, double highPrice,
                                   double lowPrice, long volume) {
        try {
            Map<String, PyHotStockEntity> tokenMap = hotStocksCache.get(accId);
            if (tokenMap == null) return;

            PyHotStockEntity stock = tokenMap.get(token);
            if (stock == null) return;

            // ✅ Entry price — price at time of recommendation by admin
            double entryPrice  = stock.getPrice()  != null ? stock.getPrice().doubleValue()  : 0;
            double targetPrice = stock.getTarget() != null ? stock.getTarget().doubleValue() : 0;

            // ✅ Prediction P&L — how much gained/lost since recommendation
            double predictionPnl        = entryPrice > 0 ? ltp - entryPrice : 0;
            double predictionPnlPercent = entryPrice > 0
                    ? ((ltp - entryPrice) / entryPrice) * 100 : 0;

            // ✅ Upside remaining to target from current ltp
            double upside = targetPrice > 0 && ltp > 0
                    ? ((targetPrice - ltp) / ltp) * 100 : 0;

            // ✅ Day change vs yesterday close
            double dayChange        = closePrice > 0 ? ltp - closePrice : 0;
            double dayChangePercent = closePrice > 0 ? (dayChange / closePrice) * 100 : 0;

            // ✅ RecommendedBy user details
            String recommendedByName = "";
            Long   recommendedById   = null;
            if (stock.getRecommendedBy() != null) {
                recommendedById   = stock.getRecommendedBy().getId();
                recommendedByName = stock.getRecommendedBy().getFullName() != null
                        ? stock.getRecommendedBy().getFullName() : "";
            }

            JSONObject tick = new JSONObject();

            // ── Type ──────────────────────────────────────────────────────────
            tick.put("type",   "PRICE_TICK");
            tick.put("source", "HOT_STOCK");

            // ── Identity ──────────────────────────────────────────────────────
            tick.put("id",          stock.getId());
            tick.put("symbol",      stock.getSymbol());
            tick.put("token",       token);
            tick.put("companyName", stock.getCompanyName());
            tick.put("isActive",    stock.getIsActive());

            // ── Live price ────────────────────────────────────────────────────
            tick.put("ltp", round(ltp));

            // ── Prediction P&L (core feature) ─────────────────────────────────
            tick.put("entryPrice",           round(entryPrice));
            tick.put("predictionPnl",        round(predictionPnl));
            tick.put("predictionPnlPercent", round(predictionPnlPercent));
            tick.put("targetPrice",          round(targetPrice));
            tick.put("upside",               round(upside));

            // ── Day OHLCV ─────────────────────────────────────────────────────
            tick.put("openPrice",        round(openPrice));
            tick.put("highPrice",        round(highPrice));
            tick.put("lowPrice",         round(lowPrice));
            tick.put("closePrice",       round(closePrice));
            tick.put("volume",           volume);
            tick.put("dayChange",        round(dayChange));
            tick.put("dayChangePercent", round(dayChangePercent));

            // ── Admin recommendation metadata ──────────────────────────────────
            tick.put("changePercent",  stock.getChangePercent() != null
                    ? stock.getChangePercent() : 0);
            tick.put("tradeCategory",  stock.getCategory()  != null
                    ? stock.getCategory().name()  : "");
            tick.put("tradeDirection", stock.getDirection() != null
                    ? stock.getDirection().name() : "");
            tick.put("tradeSignal",    stock.getSignal()    != null
                    ? stock.getSignal().name()    : "");

            // ── Who recommended ───────────────────────────────────────────────
            tick.put("recommendedById",   recommendedById);
            tick.put("recommendedByName", recommendedByName);

            // ── Timestamps ────────────────────────────────────────────────────
            tick.put("createdAt", stock.getCreatedAt() != null
                    ? stock.getCreatedAt().toString() : "");
            tick.put("updatedAt", stock.getUpdatedAt() != null
                    ? stock.getUpdatedAt().toString() : "");

            PricesWebSocketServer.sendTickToSubscribers(accId, token, tick.toString());

        } catch (Exception e) {
            log.error("❌ onPriceTick error — accId: {} token: {} Error: {}",
                    accId, token, e.getMessage());
        }
    }

    // ── Called when frontend sends SUBSCRIBE with extra tokens ────────────────
    public void subscribeAdditionalTokens(Long accId, String sessionId, List<String> newTokens) {
        Thread t = new Thread(() -> {
            try {
                startDedicatedClient(accId, newTokens);
                sessionSubscribed.computeIfAbsent(sessionId,
                        k -> ConcurrentHashMap.newKeySet()).addAll(newTokens);
                PricesWebSocketServer.addSessionTokens(sessionId, newTokens);
                log.info("📡 Additional tokens subscribed — accId: {} sessionId: {} tokens: {}",
                        accId, sessionId, newTokens.size());
            } catch (Exception e) {
                log.error("❌ subscribeAdditionalTokens failed — accId: {} Error: {}",
                        accId, e.getMessage());
            }
        }, "prices-extra-" + sessionId);
        t.setDaemon(true);
        t.start();
    }

    // ── Called when a frontend session disconnects ────────────────────────────
    public void onSessionDisconnect(Long accId, String sessionId) {
        sessionSubscribed.remove(sessionId);

        boolean anySessionsLeft = sessionSubscribed.keySet().stream()
                .anyMatch(sid -> {
                    Long sid2 = PricesWebSocketServer.getSessionAccId(sid);
                    return sid2 != null && sid2.equals(accId);
                });

        if (!anySessionsLeft) {
            AngelOneWebSocketClient dedicated = dedicatedClients.remove(accId);
            if (dedicated != null) {
                dedicated.disconnect();
                log.info("🛑 Dedicated prices WS stopped — accId: {}", accId);
            }
            hotStocksCache.remove(accId);
            log.info("🧹 Hot stocks cache cleared — accId: {}", accId);
        }
    }

    // ── Build enriched snapshot JSON with live LTP already included ───────────
    private JSONObject buildHotStockJson(PyHotStockEntity stock, double ltp) {

        double entryPrice  = stock.getPrice()  != null ? stock.getPrice().doubleValue()  : 0;
        double targetPrice = stock.getTarget() != null ? stock.getTarget().doubleValue() : 0;

        // ✅ Prediction P&L calculated at snapshot time using live LTP
        double predictionPnl        = entryPrice > 0 ? ltp - entryPrice : 0;
        double predictionPnlPercent = entryPrice > 0
                ? ((ltp - entryPrice) / entryPrice) * 100 : 0;
        double upside = targetPrice > 0 && ltp > 0
                ? ((targetPrice - ltp) / ltp) * 100 : 0;

        // ✅ RecommendedBy user details
        String recommendedByName = "";
        Long   recommendedById   = null;
        if (stock.getRecommendedBy() != null) {
            recommendedById   = stock.getRecommendedBy().getId();
            recommendedByName = stock.getRecommendedBy().getFullName()!= null
                    ? stock.getRecommendedBy().getFullName(): "";
        }

        JSONObject obj = new JSONObject();

        // ── Identity ──────────────────────────────────────────────────────────
        obj.put("id",          stock.getId());
        obj.put("symbol",      stock.getSymbol());
        obj.put("token",       stock.getSymbolToken());
        obj.put("companyName", stock.getCompanyName());
        obj.put("isActive",    stock.getIsActive());

        // ── Live price at snapshot time ───────────────────────────────────────
        obj.put("ltp",                  round(ltp));
        obj.put("predictionPnl",        round(predictionPnl));
        obj.put("predictionPnlPercent", round(predictionPnlPercent));
        obj.put("upside",               round(upside));

        // ── Day change — not available at snapshot, updated via ticks ─────────
        obj.put("dayChange",        0);
        obj.put("dayChangePercent", 0);
        obj.put("openPrice",        0);
        obj.put("highPrice",        0);
        obj.put("lowPrice",         0);
        obj.put("closePrice",       0);
        obj.put("volume",           0);

        // ── Recommendation details ─────────────────────────────────────────────
        obj.put("entryPrice",    round(entryPrice));
        obj.put("targetPrice",   round(targetPrice));
        obj.put("changePercent", stock.getChangePercent() != null
                ? stock.getChangePercent() : 0);
        obj.put("tradeCategory",  stock.getCategory()  != null
                ? stock.getCategory().name()  : "");
        obj.put("tradeDirection", stock.getDirection() != null
                ? stock.getDirection().name() : "");
        obj.put("tradeSignal",    stock.getSignal()    != null
                ? stock.getSignal().name()    : "");

        // ── Who recommended ───────────────────────────────────────────────────
        obj.put("recommendedById",   recommendedById);
        obj.put("recommendedByName", recommendedByName);

        // ── Timestamps ────────────────────────────────────────────────────────
        obj.put("createdAt", stock.getCreatedAt() != null
                ? stock.getCreatedAt().toString() : "");
        obj.put("updatedAt", stock.getUpdatedAt() != null
                ? stock.getUpdatedAt().toString() : "");

        obj.put("source", "HOT_STOCK");
        return obj;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}