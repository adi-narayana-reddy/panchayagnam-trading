package com.py.demataccount.service;

import com.py.brokers.motilal.websocket.MotilalWebSocketManager;
import com.py.common.config.AngelOneWebSocketClient;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;

@ServerEndpoint("/ws/holdings")
@Component
public class HoldingsWebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(HoldingsWebSocketServer.class);

    private static final Map<Long, Set<Session>> accountSessions
            = new ConcurrentHashMap<>();

    private static final Map<String, ReentrantLock> sessionLocks
            = new ConcurrentHashMap<>();

    private static LiveHoldingsWebSocketManager manager;

    public static void setManager(LiveHoldingsWebSocketManager m) {
        manager = m;
        startSessionHealthCheck();
    }

    private static void startSessionHealthCheck() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ws-session-health-check");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            accountSessions.forEach((accId, sessions) -> {
                sessions.removeIf(session -> {
                    if (!session.isOpen()) {
                        log.info("🧹 Removing dead session — accId: {} sessionId: {}",
                                accId, session.getId());
                        sessionLocks.remove(session.getId());
                        return true;
                    }
                    return false;
                });

                if (sessions.isEmpty()) {
                    accountSessions.remove(accId);
                    if (manager != null) {
                        manager.stopLiveFeed(accId);
                        log.info("🛑 All sessions dead — feed stopped — accId: {}", accId);
                    }
                }
            });
        }, 30, 30, TimeUnit.SECONDS);

        log.info("🩺 Session health check scheduler started");
    }

    @OnOpen
    public void onOpen(Session session) {
        String accIdParam = session.getRequestParameterMap()
                .getOrDefault("accId", List.of("0")).get(0);
        Long accId = Long.parseLong(accIdParam);

        session.getUserProperties().put("accId", accId);
        accountSessions.computeIfAbsent(accId,
                k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionLocks.put(session.getId(), new ReentrantLock());

        log.info("📱 Frontend connected — accId: {} sessionId: {}", accId, session.getId());

        if (manager != null) {
            // Check both AngelOne WS client and Motilal API-feed active marker
            AngelOneWebSocketClient existingClient =
                    LiveHoldingsWebSocketManager.getActiveClient(accId);
            boolean motilalActive = MotilalWebSocketManager.isActive(accId);

            if (existingClient != null || motilalActive) {
                log.info("📸 Feed alive — sending cached snapshot to new session — accId: {}", accId);
                String snapshot = LiveHoldingsWebSocketManager.buildSnapshot(accId);
                if (snapshot != null) {
                    safeSend(session, snapshot);
                }
            } else {
                log.info("🚀 Feed not alive — starting feed — accId: {}", accId);
                manager.startLiveFeed(accId);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        Long accId = (Long) session.getUserProperties().get("accId");
        cleanupSession(session);
        log.info("📱 Frontend disconnected — accId: {}", accId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("❌ Frontend WS error — sessionId: {} Error: {}",
                session.getId(), throwable.getMessage());
        cleanupSession(session);
    }

    private static void cleanupSession(Session session) {
        Long accId = (Long) session.getUserProperties().get("accId");
        sessionLocks.remove(session.getId());

        if (accId != null) {
            Set<Session> sessions = accountSessions.get(accId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    accountSessions.remove(accId);
                    if (manager != null) {
                        manager.stopLiveFeed(accId);
                    }
                }
            }
        }
    }

    private static void safeSend(Session s, String message) {
        if (!s.isOpen()) {
            cleanupSession(s);
            return;
        }
        ReentrantLock lock = sessionLocks.get(s.getId());
        if (lock == null) return;

        if (lock.tryLock()) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.warn("⚠️ Send failed — removing dead session: {} Error: {}",
                        s.getId(), e.getMessage());
                cleanupSession(s);
            } finally {
                lock.unlock();
            }
        }
    }

    public static void sendToAccount(Long accId, String rawMessage) {
        try {
            Set<Session> sessions = accountSessions.get(accId);
            if (sessions == null || sessions.isEmpty()) return;

            String enriched = enrichWithPnL(accId, rawMessage);
            for (Session s : sessions) {
                safeSend(s, enriched);
            }
        } catch (Exception e) {
            log.error("❌ Failed to send — accId: {} Error: {}", accId, e.getMessage());
        }
    }

    public static void sendSnapshotToAccount(Long accId, String message) {
        Set<Session> sessions = accountSessions.get(accId);
        if (sessions == null || sessions.isEmpty()) return;
        for (Session s : sessions) {
            safeSend(s, message);
        }
    }

    /**
     * Enriches a raw AngelOne tick with full holdings + positions + portfolio.
     * Uses broker-routed cache accessors — Motilal never calls this (no live ticks).
     *
     * FIX 1: reads "close" from holdings cache (AngelOne field name).
     * FIX 2: reads lowercase raw keys from positions cache.
     */
    private static String enrichWithPnL(Long accId, String rawMessage) {
        try {
            JSONObject liveData = new JSONObject(rawMessage);

            // Order update — forward as-is
            if ("ORDER_UPDATE".equals(liveData.optString("type"))) {
                return rawMessage;
            }

            String token      = liveData.optString("token");
            double ltp        = liveData.optDouble("ltp", 0);
            double closePrice = liveData.optDouble("close_price", 0);

            // ── Update holdings cache for ticked token ─────────────────────────
            Map<String, JSONObject> holdingsCache =
                    LiveHoldingsWebSocketManager.getHoldingsCacheForAccount(accId);

            JSONObject tickedHolding = holdingsCache.get(token);
            if (tickedHolding != null) {
                tickedHolding.put("livePrice",   ltp);
                tickedHolding.put("close",       closePrice);
                tickedHolding.put("close_price", closePrice);
            }

            // ── Update positions cache for ticked token ────────────────────────
            Map<String, JSONObject> positionsCache =
                    LiveHoldingsWebSocketManager.getPositionsCacheForAccount(accId);

            JSONObject tickedPosition = positionsCache.get(token);
            if (tickedPosition != null) {
                tickedPosition.put("ltp",   ltp);
                tickedPosition.put("close", closePrice);
            }

            // ── Build full holdings array ──────────────────────────────────────
            JSONArray holdingsArray = new JSONArray();
            double totalInvested  = 0;
            double totalCurrent   = 0;
            double totalDayChange = 0;
            double todayProfit    = 0;
            double todayLoss      = 0;

            for (JSONObject h : holdingsCache.values()) {
                double avg  = h.optDouble("averageprice", 0);
                int    qty  = h.optInt("quantity", 0);
                double live = h.has("livePrice") ? h.optDouble("livePrice", 0)
                                                 : h.optDouble("ltp", avg);

                double close = h.optDouble("close", h.optDouble("close_price", 0));

                double invested     = avg * qty;
                double current      = live * qty;
                double pnl          = current - invested;
                double pnlPct       = invested > 0 ? (pnl / invested) * 100 : 0;
                double dayChange    = close > 0 ? (live - close) * qty : 0;
                double dayChangePct = close > 0 ? ((live - close) / close) * 100 : 0;
                double todayPnl     = dayChange;
                double todayPnlPct  = close > 0 && qty > 0
                                      ? (todayPnl / (close * qty)) * 100 : 0;

                totalInvested  += invested;
                totalCurrent   += current;
                totalDayChange += dayChange;

                if (dayChange > 0) todayProfit += dayChange;
                else if (dayChange < 0) todayLoss += dayChange;

                JSONObject enrichedHolding = new JSONObject();
                enrichedHolding.put("token",            h.optString("symboltoken"));
                enrichedHolding.put("tradingsymbol",    h.optString("tradingsymbol"));
                enrichedHolding.put("exchange",         h.optString("exchange"));
                enrichedHolding.put("ltp",              round(live));
                enrichedHolding.put("avgPrice",         round(avg));
                enrichedHolding.put("quantity",         qty);
                enrichedHolding.put("investedValue",    round(invested));
                enrichedHolding.put("currentValue",     round(current));
                enrichedHolding.put("pnl",              round(pnl));
                enrichedHolding.put("pnlPercent",       round(pnlPct));
                enrichedHolding.put("closePrice",       round(close));
                enrichedHolding.put("dayChange",        round(dayChange));
                enrichedHolding.put("dayChangePercent", round(dayChangePct));
                enrichedHolding.put("todayPnl",         round(todayPnl));
                enrichedHolding.put("todayPnlPercent",  round(todayPnlPct));

                // Extra OHLCV only for the ticked token
                String hToken = h.optString("symboltoken");
                if (token.equals(hToken)) {
                    enrichedHolding.put("openPrice",
                            round(liveData.optDouble("open_price_of_the_day", 0)));
                    enrichedHolding.put("highPrice",
                            round(liveData.optDouble("high_price_of_the_day", 0)));
                    enrichedHolding.put("lowPrice",
                            round(liveData.optDouble("low_price_of_the_day", 0)));
                    enrichedHolding.put("volume",
                            liveData.optLong("volume_trade_for_the_day", 0));
                }

                holdingsArray.put(enrichedHolding);
            }

            // ── Build full positions array ─────────────────────────────────────
            JSONArray positionsArray = new JSONArray();
            double posTotalPnl = 0;

            for (JSONObject pos : positionsCache.values()) {
                double posLtp   = pos.optDouble("ltp", 0);
                int    posQty   = pos.optInt("netqty", 0);
                double posAvg   = pos.optDouble("averageprice", 0);
                double buyAmt   = pos.optDouble("buyamount", 0);
                double sellAmt  = pos.optDouble("sellamount", 0);
                double posClose = pos.optDouble("close", 0);

                double posInvested = posAvg * Math.abs(posQty);
                double posCurrent  = posLtp * Math.abs(posQty);

                double posPnl = posQty > 0
                        ? (posLtp - posAvg) * posQty
                        : (posAvg - posLtp) * Math.abs(posQty);

                double posPnlPct = posInvested > 0 ? (posPnl / posInvested) * 100 : 0;
                double posDayChange = posClose > 0
                        ? (posQty >= 0
                            ? (posLtp - posClose) * posQty
                            : (posClose - posLtp) * Math.abs(posQty))
                        : 0;

                posTotalPnl    += posPnl;
                totalDayChange += posDayChange;

                if (posDayChange > 0) todayProfit += posDayChange;
                else if (posDayChange < 0) todayLoss += posDayChange;

                JSONObject p = new JSONObject();
                p.put("token",         pos.optString("token"));
                p.put("tradingsymbol", pos.optString("tradingsymbol"));
                p.put("exchange",      pos.optString("exchange"));
                p.put("productType",   pos.optString("producttype"));
                p.put("netQty",        posQty);
                p.put("buyQty",        pos.optInt("buyqty", 0));
                p.put("sellQty",       pos.optInt("sellqty", 0));
                p.put("avgPrice",      round(posAvg));
                p.put("ltp",           round(posLtp));
                p.put("buyAmount",     round(buyAmt));
                p.put("sellAmount",    round(sellAmt));
                p.put("investedValue", round(posInvested));
                p.put("currentValue",  round(posCurrent));
                p.put("pnl",           round(posPnl));
                p.put("pnlPercent",    round(posPnlPct));
                p.put("dayChange",     round(posDayChange));
                positionsArray.put(p);
            }

            // ── Portfolio summary ──────────────────────────────────────────────
            double totalPnl          = totalCurrent - totalInvested;
            double totalPnlPercent   = totalInvested > 0 ? (totalPnl / totalInvested) * 100 : 0;
            double totalDayChangePct = totalInvested > 0 ? (totalDayChange / totalInvested) * 100 : 0;

            JSONObject portfolio = new JSONObject();
            portfolio.put("totalInvested",         round(totalInvested));
            portfolio.put("totalCurrentValue",     round(totalCurrent));
            portfolio.put("totalPnl",              round(totalPnl));
            portfolio.put("totalPnlPercent",       round(totalPnlPercent));
            portfolio.put("totalDayChange",        round(totalDayChange));
            portfolio.put("totalDayChangePercent", round(totalDayChangePct));
            portfolio.put("todayProfit",           round(todayProfit));
            portfolio.put("todayLoss",             round(todayLoss));

            JSONObject positionsSummary = new JSONObject();
            positionsSummary.put("totalPnl", round(posTotalPnl));
            positionsSummary.put("count",    positionsCache.size());

            JSONObject result = new JSONObject();
            result.put("type",             "TICK");
            result.put("tickedToken",      token);
            result.put("holdings",         holdingsArray);
            result.put("positions",        positionsArray);
            result.put("positionsSummary", positionsSummary);
            result.put("portfolio",        portfolio);

            return result.toString();

        } catch (Exception e) {
            log.error("❌ enrichWithPnL failed — accId: {} Error: {}", accId, e.getMessage());
            return rawMessage;
        }
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static void sendTradeUpdateToAccount(Long accId, String message) {
        try {
            JSONObject tradeUpdate = new JSONObject();
            tradeUpdate.put("type",  "TRADE_UPDATE");
            tradeUpdate.put("accId", accId);
            tradeUpdate.put("data",  new JSONObject(message));
            broadcastToAccount(accId, tradeUpdate.toString());
        } catch (Exception e) {
            log.warn("Failed to send trade update for accId={}: {}", accId, e.getMessage());
        }
    }

    public static void sendOrderUpdateToAccount(Long accId, String message) {
        try {
            JSONObject orderUpdate = new JSONObject();
            orderUpdate.put("type",  "ORDER_UPDATE");
            orderUpdate.put("accId", accId);
            orderUpdate.put("data",  new JSONObject(message));
            broadcastToAccount(accId, orderUpdate.toString());
        } catch (Exception e) {
            log.warn("Failed to send order update for accId={}: {}", accId, e.getMessage());
        }
    }

    private static void broadcastToAccount(Long accId, String message) {
        Set<Session> sessions = accountSessions.get(accId);
        if (sessions == null || sessions.isEmpty()) return;
        for (Session s : sessions) {
            safeSend(s, message);
        }
    }
}