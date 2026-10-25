package com.py.brokers.motilal.websocket;

import com.py.demataccount.service.HoldingsWebSocketServer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Motilal Oswal WebSocket client for Trade/Order status (ws2).
 *
 * Protocol (from MOFSLOPENAPI.py):
 *   - WS URL (LIVE): wss://openapi.motilaloswal.com/ws
 *   - WS URL (UAT) : wss://openapi.motilaloswaluat.com/ws
 *   - Auth         : On open, send JSON {"clientid","authtoken","apikey"}
 *   - Subscribe    : Send {"clientid","action":"OrderSubscribe"} and {"action":"TradeSubscribe"}
 *   - Heartbeat    : Send {"clientid","action":"heartbeat"} every 30s
 *   - Inbound      : JSON text messages
 */
public class MotilalTradeStatusClient {

    private static final Logger log = LoggerFactory.getLogger(MotilalTradeStatusClient.class);

    private static final String TRADE_WS_URL_LIVE = "wss://openapi.motilaloswal.com/ws";
    private static final String TRADE_WS_URL_UAT  = "wss://openapi.motilaloswaluat.com/ws";

    private static final int HEARTBEAT_INTERVAL_SEC  = 30;
    private static final int RECONNECT_DELAY_SEC     = 5;
    private static final int MAX_RECONNECT_ATTEMPTS  = 3;  // stop after 3 — likely auth issue

    private final Long   accId;
    private final String clientCode;
    private final String authToken;
    private final String apiKey;
    private final boolean uat;

    private org.java_websocket.client.WebSocketClient wsClient;
    private ScheduledExecutorService heartbeatScheduler;
    private final AtomicBoolean disconnecting  = new AtomicBoolean(false);
    private final AtomicBoolean reconnecting   = new AtomicBoolean(false);
    private int reconnectAttempts = 0;

    public MotilalTradeStatusClient(Long accId, String clientCode,
                                     String authToken, String apiKey, boolean uat) {
        this.accId      = accId;
        this.clientCode = clientCode;
        this.authToken  = authToken;
        this.apiKey     = apiKey;
        this.uat        = uat;
    }

    public static MotilalTradeStatusClient connect(Long accId, String clientCode,
                                                    String authToken, String apiKey,
                                                    boolean uat) throws Exception {
        MotilalTradeStatusClient client =
                new MotilalTradeStatusClient(accId, clientCode, authToken, apiKey, uat);
        client.connectInternal();
        return client;
    }

    private void connectInternal() throws Exception {
        String url = uat ? TRADE_WS_URL_UAT : TRADE_WS_URL_LIVE;
        URI uri = new URI(url);

        wsClient = new org.java_websocket.client.WebSocketClient(uri) {

            @Override
            public void onOpen(org.java_websocket.handshake.ServerHandshake handshake) {
                log.info("✅ Motilal TradeStatus WS connected — accId: {}", accId);
                reconnecting.set(false);
                reconnectAttempts = 0;  // reset on successful connect

                // Step 1: Send trade login
                sendTradeLogin();

                // Step 2: Subscribe to orders and trades
                sendOrderSubscribe();
                sendTradeSubscribe();

                // Step 3: Start heartbeat
                startHeartbeat();
            }

            @Override
            public void onMessage(String message) {
                handleTradeMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.warn("⚠️ Motilal TradeStatus WS closed — accId: {} reason: {}", accId, reason);
                stopHeartbeat();
                if (!disconnecting.get()) {
                    scheduleReconnect();
                }
            }

            @Override
            public void onError(Exception ex) {
                log.error("❌ Motilal TradeStatus WS error — accId: {} error: {}", accId, ex.getMessage());
            }
        };

        wsClient.connect();
    }

    // ── Login / Subscribe / Unsubscribe ──────────────────────────────────────

    private void sendTradeLogin() {
        JSONObject data = new JSONObject();
        data.put("clientid",  clientCode);
        data.put("authtoken", authToken);
        data.put("apikey",    apiKey);
        safeSend(data.toString());
        log.info("📤 Motilal Tradelogin sent — accId: {}", accId);
    }

    private void sendOrderSubscribe() {
        JSONObject data = new JSONObject();
        data.put("clientid", clientCode);
        data.put("action",   "OrderSubscribe");
        safeSend(data.toString());
        log.info("📡 Motilal OrderSubscribe sent — accId: {}", accId);
    }

    private void sendTradeSubscribe() {
        JSONObject data = new JSONObject();
        data.put("clientid", clientCode);
        data.put("action",   "TradeSubscribe");
        safeSend(data.toString());
        log.info("📡 Motilal TradeSubscribe sent — accId: {}", accId);
    }

    public void sendOrderUnsubscribe() {
        JSONObject data = new JSONObject();
        data.put("clientid", clientCode);
        data.put("action",   "OrderUnsubscribe");
        safeSend(data.toString());
    }

    public void sendTradeUnsubscribe() {
        JSONObject data = new JSONObject();
        data.put("clientid", clientCode);
        data.put("action",   "TradeUnsubscribe");
        safeSend(data.toString());
    }

    private void sendHeartbeatNow() {
        JSONObject data = new JSONObject();
        data.put("clientid", clientCode);
        data.put("action",   "heartbeat");
        safeSend(data.toString());
        log.debug("🏓 Motilal TradeStatus heartbeat sent — accId: {}", accId);
    }

    // ── Incoming trade/order message handler ──────────────────────────────────

    private void handleTradeMessage(String raw) {
        try {
            JSONObject msg = new JSONObject(raw);

            // Determine if it's an order update or trade update based on fields
            boolean isOrder = msg.has("uniqueorderid") || msg.has("orderstatus")
                           || msg.has("OrderStatus");
            boolean isTrade = msg.has("tradeid") || msg.has("TradeId");

            if (isOrder) {
                JSONObject enriched = new JSONObject();
                enriched.put("type",            "ORDER_UPDATE");
                enriched.put("orderId",         msg.optString("uniqueorderid",
                                                msg.optString("UniqueOrderId", "")));
                enriched.put("tradingsymbol",   msg.optString("tradingsymbol",
                                                msg.optString("Symbol", "")));
                enriched.put("status",          msg.optString("orderstatus",
                                                msg.optString("OrderStatus", "")));
                enriched.put("quantity",        msg.optInt("quantityinlot",
                                                msg.optInt("Quantity", 0)));
                enriched.put("price",           msg.optDouble("price",
                                                msg.optDouble("Price", 0)));
                enriched.put("transactionType", msg.optString("buyorsell",
                                                msg.optString("BuyOrSell", "")));
                enriched.put("exchange",        msg.optString("exchange",
                                                msg.optString("Exchange", "")));
                enriched.put("filledQuantity",  msg.optInt("tradedqty",
                                                msg.optInt("TradedQty", 0)));
                enriched.put("scripCode",       msg.optInt("symboltoken",
                                                msg.optInt("ScripCode", 0)));

                log.info("📋 Motilal Order update — accId: {} orderId: {} status: {}",
                        accId,
                        enriched.optString("orderId"),
                        enriched.optString("status"));

                HoldingsWebSocketServer.sendToAccount(accId, enriched.toString());

            } else if (isTrade) {
                JSONObject enriched = new JSONObject();
                enriched.put("type",          "TRADE_UPDATE");
                enriched.put("tradeId",       msg.optString("tradeid", msg.optString("TradeId", "")));
                enriched.put("orderId",       msg.optString("uniqueorderid", ""));
                enriched.put("tradingsymbol", msg.optString("tradingsymbol", ""));
                enriched.put("price",         msg.optDouble("tradedprice", 0));
                enriched.put("quantity",      msg.optInt("tradedqty", 0));
                enriched.put("exchange",      msg.optString("exchange", ""));

                log.info("📋 Motilal Trade update — accId: {} tradeId: {}",
                        accId, enriched.optString("tradeId"));

                HoldingsWebSocketServer.sendTradeUpdateToAccount(accId, enriched.toString());

            } else {
                log.debug("📨 Motilal TradeStatus raw — accId: {} msg: {}", accId, raw);
            }

        } catch (Exception e) {
            log.error("❌ Motilal TradeStatus message parse failed — accId: {} error: {}",
                    accId, e.getMessage());
        }
    }

    // ── Heartbeat scheduler ───────────────────────────────────────────────────

    private void startHeartbeat() {
        stopHeartbeat();
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "motilal-trade-hb-" + accId);
            t.setDaemon(true);
            return t;
        });
        heartbeatScheduler.scheduleAtFixedRate(
                this::sendHeartbeatNow,
                HEARTBEAT_INTERVAL_SEC, HEARTBEAT_INTERVAL_SEC, TimeUnit.SECONDS);
        log.info("🏓 Motilal TradeStatus heartbeat started — accId: {} interval: {}s",
                accId, HEARTBEAT_INTERVAL_SEC);
    }

    private void stopHeartbeat() {
        if (heartbeatScheduler != null && !heartbeatScheduler.isShutdown()) {
            heartbeatScheduler.shutdownNow();
            heartbeatScheduler = null;
        }
    }

    // ── Reconnect ─────────────────────────────────────────────────────────────

    private void scheduleReconnect() {
        if (!reconnecting.compareAndSet(false, true)) return;

        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            log.warn("⚠️ Motilal TradeStatus max reconnect attempts ({}) reached — giving up — accId: {}",
                    MAX_RECONNECT_ATTEMPTS, accId);
            reconnecting.set(false);
            return;
        }

        reconnectAttempts++;
        log.info("🔄 Motilal TradeStatus reconnecting in {}s — accId: {} attempt: {}/{}",
                RECONNECT_DELAY_SEC, accId, reconnectAttempts, MAX_RECONNECT_ATTEMPTS);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(RECONNECT_DELAY_SEC * 1000L);
                connectInternal();
                reconnecting.set(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("❌ Motilal TradeStatus reconnect failed — accId: {} error: {}",
                        accId, e.getMessage());
                reconnecting.set(false);
            }
        }, "motilal-trade-reconnect-" + accId);

        t.setDaemon(true);
        t.start();
    }

    // ── Disconnect ────────────────────────────────────────────────────────────

    public void disconnect() {
        disconnecting.set(true);
        stopHeartbeat();
        try {
            if (wsClient != null && wsClient.isOpen()) {
                // Politely logout before closing
                JSONObject logout = new JSONObject();
                logout.put("clientid", clientCode);
                logout.put("action",   "logout");
                safeSend(logout.toString());
                Thread.sleep(200); // allow send to complete
                wsClient.close();
            }
        } catch (Exception e) {
            log.error("❌ Error disconnecting Motilal TradeStatus WS — accId: {}", accId);
        }
        log.info("🛑 Motilal TradeStatus WS disconnected — accId: {}", accId);
    }

    private void safeSend(String msg) {
        try {
            if (wsClient != null && wsClient.isOpen()) {
                wsClient.send(msg);
            }
        } catch (Exception e) {
            log.warn("⚠️ Motilal TradeStatus send failed — accId: {} error: {}", accId, e.getMessage());
        }
    }

    public boolean isOpen() {
        return wsClient != null && wsClient.isOpen();
    }
}