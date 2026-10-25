package com.py.demataccount.service;

import com.py.brokers.angel.client.SmartConnect;
import com.py.brokers.angel.exception.DeActivatedAccountException;
import com.py.brokers.angel.exception.SmartAPIException;
import com.py.brokers.motilal.websocket.MotilalWebSocketManager;
import com.py.common.config.AngelOnePositionsService;
import com.py.common.config.AngelOneWebSocketClient;
import com.py.demataccount.entity.*;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Service
public class LiveHoldingsWebSocketManager {

    private static final Logger log = LoggerFactory.getLogger(LiveHoldingsWebSocketManager.class);

    private static LiveHoldingsWebSocketManager instance;

    private static final Map<Long, AngelOneWebSocketClient> activeClients
            = new ConcurrentHashMap<>();

    private static final Map<Long, Map<String, JSONObject>> holdingsCache
            = new ConcurrentHashMap<>();

    private static final Map<Long, ConcurrentHashMap<String, JSONObject>> positionsCache
            = new ConcurrentHashMap<>();

    private final AngelOnePositionsService positionsService;

    @Autowired
    private MotilalWebSocketManager motilalWebSocketManager;

    @Autowired
    private PyBrokerAccountRepository pyBrokerAccountRepository;

    @jakarta.annotation.PostConstruct
    public void init() {
        instance = this;
        HoldingsWebSocketServer.setManager(this);
    }

    public LiveHoldingsWebSocketManager(AngelOnePositionsService positionsService) {
        this.positionsService = positionsService;
    }

    public void startLiveFeed(Long accId) {
        try {
            PyBrokerAccountEntity account = pyBrokerAccountRepository
                    .findById(accId)
                    .orElseThrow(() -> new RuntimeException("Account not found: " + accId));

            if(!account.getActive()){
                throw new DeActivatedAccountException("Live Feed can't be run for deactivate accounts: "+ accId);
            }
            switch (account.getBrokerType()) {
                case ANGELONE ->
                    startAngelOneLiveFeed(accId);
                case MOTILAL ->
                    motilalWebSocketManager.startLiveFeed(accId);
                default ->
                    log.warn("WebSocket not supported for broker: {}",
                            account.getBrokerType());
            }

        } catch (Exception e) {
            log.error("Failed to start live feed for accId={}: {}", accId, e.getMessage());
        }
    }

    public void stopLiveFeed(Long accId) {
        try {
            PyBrokerAccountEntity account = pyBrokerAccountRepository
                    .findById(accId)
                    .orElse(null);

            if (account == null) {
                log.warn("Account not found for stopLiveFeed accId={}", accId);
                return;
            }

            switch (account.getBrokerType()) {
                case ANGELONE ->
                    stopAngelOneLiveFeed(accId);
                case MOTILAL ->
                    motilalWebSocketManager.stopLiveFeed(accId);
                default ->
                    log.warn("WebSocket stop not supported for broker: {}",
                            account.getBrokerType());
            }

        } catch (Exception e) {
            log.error("Failed to stop live feed for accId={}: {}", accId, e.getMessage());
        }
    }

    public void startAngelOneLiveFeed(Long accId) {

        if (activeClients.containsKey(accId)) {
            log.info("WS already active for accId: {}", accId);
            return;
        }

        try {
            PyBrokerAccountEntity account = pyBrokerAccountRepository
                    .findById(accId)
                    .orElseThrow(() -> new RuntimeException("Account not found: " + accId));

            PyBrokerToken tokenEntity = account.getToken();
            if (tokenEntity == null) {
                log.warn("❌ No token for accId: {}", accId);
                return;
            }
            if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("❌ Token expired for accId: {}", accId);
                return;
            }

            PyBrokerSecret secret = account.getSecret();
            if (secret == null) {
                log.warn("❌ No secret for accId: {}", accId);
                return;
            }

            String feedToken = tokenEntity.getFeedToken();
            if (feedToken == null || feedToken.isBlank()) {
                log.warn("❌ Feed token empty for accId: {}", accId);
                return;
            }

            var smartConnect = new SmartConnect(secret.getApiKey());
            smartConnect.setAccessToken(tokenEntity.getAccessToken());

            var holdingsMap = new HashMap<String, JSONObject>();

            try {
                var allHoldingResponse = smartConnect.getAllHolding();
                log.info("📦 getAllHolding raw — accId: {} response: {}", accId, allHoldingResponse);

                if (allHoldingResponse != null) {
                    JSONArray holdingsArray = null;
                    var dataField = allHoldingResponse.opt("data");

                    if (dataField instanceof JSONArray) {
                        holdingsArray = (JSONArray) dataField;
                    } else if (dataField instanceof JSONObject dataObj) {
                        holdingsArray = dataObj.optJSONArray("holdings");
                    }

                    if (holdingsArray != null) {
                        for (int i = 0; i < holdingsArray.length(); i++) {
                            var holding = holdingsArray.getJSONObject(i);
                            var token = holding.optString("symboltoken");
                            if (!token.isBlank()) {
                                holdingsMap.put(token, holding);
                            }
                        }
                    }
                }

                log.info("📦 Holdings loaded — accId: {} count: {}", accId, holdingsMap.size());
            } catch (Exception e) {
                log.error("❌ getAllHolding failed — accId: {} Error: {}", accId, e.getMessage());
            } catch (SmartAPIException ex) {
                java.util.logging.Logger.getLogger(LiveHoldingsWebSocketManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (holdingsMap.isEmpty()) {
                log.warn("⚠️ No holdings found for accId: {} — WS not started", accId);
                return;
            }

            log.info("📦 Holdings fetched — accId: {} total tokens: {}", accId, holdingsMap.size());

            holdingsCache.put(accId, holdingsMap);
            loadPositions(accId);

            List<String> allTokens = new ArrayList<>(collectAllTokens(accId));

            AngelOneWebSocketClient wsClient = AngelOneWebSocketClient.connect(
                    accId,
                    account.getClientId(),
                    tokenEntity.getAccessToken(),
                    feedToken,
                    secret.getApiKey(),
                    allTokens
            );

            activeClients.put(accId, wsClient);

            final Long finalAccId = accId;

            Thread snapshotThread = new Thread(() -> {
                try {
                    Thread.sleep(500);
                    String snapshotJson = buildSnapshot(finalAccId);
                    if (snapshotJson != null) {
                        HoldingsWebSocketServer.sendSnapshotToAccount(finalAccId, snapshotJson);
                        log.info("📸 Snapshot sent — accId: {} holdings: {} positions: {}",
                                finalAccId,
                                holdingsCache.getOrDefault(finalAccId, new HashMap<>()).size(),
                                positionsCache.getOrDefault(finalAccId, new ConcurrentHashMap<>()).size());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "holdings-snapshot-" + accId);
            snapshotThread.setDaemon(true);
            snapshotThread.start();

            log.info("✅ Live feed started — accId: {} tokens: {}", accId, allTokens.size());

        } catch (Exception e) {
            log.error("❌ Failed to start live feed — accId: {} Error: {}", accId, e.getMessage());
        }
    }

    public static void reconnect(Long accId) {
        AngelOneWebSocketClient old = activeClients.remove(accId);
        if (old != null) {
            old.disconnect();
        }
        holdingsCache.remove(accId);
        positionsCache.remove(accId);

        if (instance != null) {
            instance.startLiveFeed(accId);
        } else {
            log.error("❌ Cannot reconnect — manager instance not ready");
        }
    }

    public void stopAngelOneLiveFeed(Long accId) {
        AngelOneWebSocketClient client = activeClients.remove(accId);
        if (client != null) {
            client.disconnect();
            log.info("🛑 Live feed stopped — accId: {}", accId);
        }
        holdingsCache.remove(accId);
        positionsCache.remove(accId);
    }

    /**
     * Returns holdings cache routed by broker type. AngelOne → local
     * holdingsCache Motilal → MotilalWebSocketManager.getHoldingsCache()
     * (PRIMARY only, no duplicates)
     */
    public static Map<String, JSONObject> getHoldingsCacheForAccount(Long accId) {
        Map<String, JSONObject> motilalHoldings = MotilalWebSocketManager.getHoldingsCache(accId);
        if (!motilalHoldings.isEmpty()) {
            return motilalHoldings;
        }
        return holdingsCache.getOrDefault(accId, new HashMap<>());
    }

    /**
     * Returns positions cache routed by broker type. AngelOne → local
     * positionsCache Motilal → MotilalWebSocketManager.getPositionsCache()
     */
    public static Map<String, JSONObject> getPositionsCacheForAccount(Long accId) {
        Map<String, JSONObject> motilalPositions = MotilalWebSocketManager.getPositionsCache(accId);
        if (!motilalPositions.isEmpty()) {
            return motilalPositions;
        }
        return positionsCache.getOrDefault(accId, new ConcurrentHashMap<>());
    }

    /**
     * Builds SNAPSHOT — same structure as TICK. Uses broker-routed cache
     * accessors (works for both AngelOne and Motilal).
     */
    public static String buildSnapshot(Long accId) {
        try {
            Map<String, JSONObject> holdingsMap = getHoldingsCacheForAccount(accId);
            Map<String, JSONObject> positionsMap = getPositionsCacheForAccount(accId);

            // ✅ REMOVE the early return — always build snapshot even if holdings empty
            if (holdingsMap.isEmpty() && positionsMap.isEmpty()) {
                log.warn("⚠️ buildSnapshot — no holdings or positions in cache for accId: {}", accId);
                return null;
            }

            // ── Holdings ──────────────────────────────────────────────────────────
            JSONArray holdingsArray = new JSONArray();
            double totalInvested = 0;
            double totalCurrent = 0;
            double totalDayChange = 0;
            double todayProfit = 0;
            double todayLoss = 0;

            for (JSONObject h : holdingsMap.values()) {
                double avg = h.optDouble("averageprice", 0);
                int qty = h.optInt("quantity", 0);
                double live = h.has("livePrice") ? h.optDouble("livePrice", 0)
                        : h.optDouble("ltp", avg);
                double close = h.optDouble("close", h.optDouble("close_price", 0));

                double invested = avg * qty;
                double current = live * qty;
                double pnl = current - invested;
                double pnlPct = invested > 0 ? (pnl / invested) * 100 : 0;
                double dayChange = close > 0 ? (live - close) * qty : 0;
                double dayChangePct = close > 0 ? ((live - close) / close) * 100 : 0;
                double todayPnl = dayChange;
                double todayPnlPct = close > 0 && qty > 0
                        ? (todayPnl / (close * qty)) * 100 : 0;

                totalInvested += invested;
                totalCurrent += current;
                totalDayChange += dayChange;

                if (dayChange > 0) {
                    todayProfit += dayChange;
                } else if (dayChange < 0) {
                    todayLoss += dayChange;
                }

                JSONObject enriched = new JSONObject();
                enriched.put("token", h.optString("symboltoken"));
                enriched.put("tradingsymbol", h.optString("tradingsymbol"));
                enriched.put("exchange", h.optString("exchange"));
                enriched.put("ltp", round(live));
                enriched.put("avgPrice", round(avg));
                enriched.put("quantity", qty);
                enriched.put("investedValue", round(invested));
                enriched.put("currentValue", round(current));
                enriched.put("pnl", round(pnl));
                enriched.put("pnlPercent", round(pnlPct));
                enriched.put("closePrice", round(close));
                enriched.put("dayChange", round(dayChange));
                enriched.put("dayChangePercent", round(dayChangePct));
                enriched.put("todayPnl", round(todayPnl));
                enriched.put("todayPnlPercent", round(todayPnlPct));
                holdingsArray.put(enriched);
            }

            // ── Positions ─────────────────────────────────────────────────────────
            JSONArray positionsArray = new JSONArray();
            double posTotalInvested = 0;  // ✅ track positions invested for portfolio
            double posTotalCurrent = 0;  // ✅ track positions current for portfolio
            double posTotalPnl = 0;

            for (JSONObject pos : positionsMap.values()) {
                double posLtp = pos.optDouble("ltp", 0);
                int posQty = pos.optInt("netqty", 0);
                double posAvg = pos.optDouble("averageprice", 0);
                double buyAmt = pos.optDouble("buyamount", 0);
                double sellAmt = pos.optDouble("sellamount", 0);
                double posClose = pos.optDouble("close", 0);

                double posInvested = posAvg * Math.abs(posQty);
                double posCurrent = posLtp * Math.abs(posQty);

                double posPnl = posQty > 0
                        ? (posLtp - posAvg) * posQty
                        : (posAvg - posLtp) * Math.abs(posQty);

                double posPnlPct = posInvested > 0 ? (posPnl / posInvested) * 100 : 0;
                double posDayChange = posClose > 0
                        ? (posQty >= 0
                                ? (posLtp - posClose) * posQty
                                : (posClose - posLtp) * Math.abs(posQty))
                        : 0;

                posTotalPnl += posPnl;
                posTotalInvested += posInvested;  // ✅ accumulate
                posTotalCurrent += posCurrent;   // ✅ accumulate
                totalDayChange += posDayChange;

                if (posDayChange > 0) {
                    todayProfit += posDayChange;
                } else if (posDayChange < 0) {
                    todayLoss += posDayChange;
                }

                JSONObject p = new JSONObject();
                p.put("token", pos.optString("token"));
                p.put("tradingsymbol", pos.optString("tradingsymbol"));
                p.put("exchange", pos.optString("exchange"));
                p.put("productType", pos.optString("producttype"));
                p.put("netQty", posQty);
                p.put("buyQty", pos.optInt("buyqty", 0));
                p.put("sellQty", pos.optInt("sellqty", 0));
                p.put("avgPrice", round(posAvg));
                p.put("ltp", round(posLtp));
                p.put("buyAmount", round(buyAmt));
                p.put("sellAmount", round(sellAmt));
                p.put("investedValue", round(posInvested));
                p.put("currentValue", round(posCurrent));
                p.put("pnl", round(posPnl));
                p.put("pnlPercent", round(posPnlPct));
                p.put("dayChange", round(posDayChange));
                positionsArray.put(p);
            }

            // ── Portfolio summary — combines holdings + positions ─────────────────
            // ✅ FIX: include positions invested/current in portfolio totals
            double combinedInvested = totalInvested + posTotalInvested;
            double combinedCurrent = totalCurrent + posTotalCurrent;

            double totalPnl = combinedCurrent - combinedInvested;
            double totalPnlPercent = combinedInvested > 0 ? (totalPnl / combinedInvested) * 100 : 0;
            double totalDayChangePct = combinedInvested > 0 ? (totalDayChange / combinedInvested) * 100 : 0;

            JSONObject portfolio = new JSONObject();
            portfolio.put("totalInvested", round(combinedInvested));
            portfolio.put("totalCurrentValue", round(combinedCurrent));
            portfolio.put("totalPnl", round(totalPnl));
            portfolio.put("totalPnlPercent", round(totalPnlPercent));
            portfolio.put("totalDayChange", round(totalDayChange));
            portfolio.put("totalDayChangePercent", round(totalDayChangePct));
            portfolio.put("todayProfit", round(todayProfit));
            portfolio.put("todayLoss", round(todayLoss));

            JSONObject positionsSummary = new JSONObject();
            positionsSummary.put("totalPnl", round(posTotalPnl));
            positionsSummary.put("count", positionsMap.size());

            JSONObject snapshot = new JSONObject();
            snapshot.put("type", "SNAPSHOT");
            snapshot.put("holdings", holdingsArray);
            snapshot.put("positions", positionsArray);
            snapshot.put("positionsSummary", positionsSummary);
            snapshot.put("portfolio", portfolio);

            return snapshot.toString();

        } catch (Exception e) {
            log.error("❌ buildSnapshot failed — accId: {}", accId, e);
            return null;
        }
    }

    private void loadPositions(Long accId) {
        try {
            List<JSONObject> positions = positionsService.fetchOpenPositions(accId);
            ConcurrentHashMap<String, JSONObject> cache = new ConcurrentHashMap<>();
            for (JSONObject pos : positions) {
                cache.put(pos.optString("token"), pos);
            }
            positionsCache.put(accId, cache);
            log.info("📋 Positions cached — accId: {} count: {}", accId, cache.size());
        } catch (Exception e) {
            log.error("❌ Failed to load positions — accId: {}", accId, e);
        }
    }

    private Set<String> collectAllTokens(Long accId) {
        Set<String> tokens = new HashSet<>();
        Map<String, JSONObject> holdings = holdingsCache.get(accId);
        if (holdings != null) {
            tokens.addAll(holdings.keySet());
        }
        Map<String, JSONObject> positions = positionsCache.get(accId);
        if (positions != null) {
            tokens.addAll(positions.keySet());
        }
        return tokens;
    }

    public void refreshPositions(Long accId) {
        loadPositions(accId);
        log.info("🔄 Positions refreshed — accId: {}", accId);
    }

    // AngelOne-only legacy accessors (used internally)
    public static Map<String, JSONObject> getHoldingsCache(Long accId) {
        return holdingsCache.getOrDefault(accId, new HashMap<>());
    }

    public static Map<String, JSONObject> getPositionsCache(Long accId) {
        return positionsCache.getOrDefault(accId, new ConcurrentHashMap<>());
    }

    public static AngelOneWebSocketClient getActiveClient(Long accId) {
        return activeClients.get(accId);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
