package com.py.common.config;

import com.py.demataccount.service.HoldingsWebSocketServer;
import com.py.demataccount.service.LiveHoldingsWebSocketManager;
import jakarta.websocket.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AngelOneWebSocketClient extends Endpoint {

    private static final Logger log = LoggerFactory.getLogger(AngelOneWebSocketClient.class);

    // ✅ Ping every 20s (Angel One drops connection after ~30s of silence)
    private static final int PING_INTERVAL_SECONDS = 20;
    // ✅ Reconnect delay after disconnect
    private static final int RECONNECT_DELAY_SECONDS = 5;

    private final Long accId;
    private final String clientId;
    private final List<String> tokens;
    private Session session;

    // ✅ NEW — ping scheduler and reconnect guard
    private ScheduledExecutorService pingScheduler;
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    public AngelOneWebSocketClient(Long accId, String clientId, List<String> tokens) {
        this.accId = accId;
        this.clientId = clientId;
        this.tokens = tokens;
    }

    public static AngelOneWebSocketClient connect(Long accId, String clientId,
            String jwtToken, String feedToken, String apiKey, List<String> tokens) throws Exception {

        AngelOneWebSocketClient client = new AngelOneWebSocketClient(accId, clientId, tokens);

        ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                .configurator(new ClientEndpointConfig.Configurator() {
                    @Override
                    public void beforeRequest(Map<String, List<String>> headers) {
                        headers.put("Authorization", List.of("Bearer " + jwtToken));
                        headers.put("x-api-key", List.of(apiKey));
                        headers.put("x-client-code", List.of(clientId));
                        headers.put("x-feed-token", List.of(feedToken));
                    }
                }).build();

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxSessionIdleTimeout(0);

        container.connectToServer(client, config,
                new URI("wss://smartapisocket.angelone.in/smart-stream"));

        return client;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.reconnecting.set(false); // ✅ reset reconnect guard on successful open

        session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
            @Override
            public void onMessage(ByteBuffer bytes) {
                try {
                    JSONObject parsed = parseBinaryMessage(bytes);
                    if (parsed == null) {
                        return;
                    }

                    String token = parsed.optString("token");
                    if (token.isBlank()) {
                        return;
                    }

                    if (parsed.has("orderid") || parsed.has("orderstatus")) {
                        handleOrderUpdate(accId, parsed);
                    } else {
                        HoldingsWebSocketServer.sendToAccount(accId, parsed.toString());

                        PricesWebSocketManager.onPriceTick(
                                accId,
                                token,
                                parsed.optDouble("ltp", 0),
                                parsed.optDouble("close_price", 0),
                                parsed.optDouble("open_price_of_the_day", 0),
                                parsed.optDouble("high_price_of_the_day", 0),
                                parsed.optDouble("low_price_of_the_day", 0),
                                parsed.optLong("volume_trade_for_the_day", 0)
                        );
                    }
                } catch (Exception e) {
                    log.error("❌ Binary message parse error — accId: {} Error: {}",
                            accId, e.getMessage());
                }
            }
        });

        log.info("✅ Angel One WS connected — accId: {} clientId: {}", accId, clientId);
        subscribeTokens();

        // ✅ NEW — start heartbeat ping after connection is open
        startPingScheduler();
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        log.warn("⚠️ Angel One WS closed — accId: {} Reason: {}", accId, reason.getReasonPhrase());
        stopPingScheduler(); // ✅ always stop ping first
//        scheduleReconnect();
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        log.error("❌ Angel One WS error — accId: {} Error: {}", accId, throwable.getMessage());
        // onClose will be called after onError by the WS container — no need to reconnect here
    }

    // ✅ NEW — start sending heartbeat ping every 20 seconds
    private void startPingScheduler() {
        stopPingScheduler(); // ensure no duplicate scheduler

        pingScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "angel-ping-" + accId);
            t.setDaemon(true); // won't block JVM shutdown
            return t;
        });

        pingScheduler.scheduleAtFixedRate(() -> {
            try {
                if (session != null && session.isOpen()) {
                    // ✅ Angel One SmartStream heartbeat format: action=0
                    JSONObject ping = new JSONObject();
                    ping.put("action", 0);
                    session.getAsyncRemote().sendText(ping.toString());
                    log.debug("🏓 Ping sent — accId: {}", accId);
                } else {
                    log.warn("⚠️ Ping skipped — session closed — accId: {}", accId);
                    stopPingScheduler();
                }
            } catch (Exception e) {
                log.warn("⚠️ Ping failed — accId: {} Error: {}", accId, e.getMessage());
            }
        }, PING_INTERVAL_SECONDS, PING_INTERVAL_SECONDS, TimeUnit.SECONDS);

        log.info("🏓 Ping scheduler started — accId: {} interval: {}s", accId, PING_INTERVAL_SECONDS);
    }

    // ✅ NEW — cleanly stop ping scheduler
    private void stopPingScheduler() {
        if (pingScheduler != null && !pingScheduler.isShutdown()) {
            pingScheduler.shutdownNow();
            pingScheduler = null;
            log.info("🏓 Ping scheduler stopped — accId: {}", accId);
        }
    }

    // ✅ FIXED — daemon thread + named + reconnect guard to prevent double reconnect
    private void scheduleReconnect() {
        if (!reconnecting.compareAndSet(false, true)) {
            log.warn("⚠️ Reconnect already in progress — accId: {}", accId);
            return;
        }

        Thread t = new Thread(() -> {
            try {
                log.info("🔄 Reconnecting in {}s — accId: {}", RECONNECT_DELAY_SECONDS, accId);
                Thread.sleep(RECONNECT_DELAY_SECONDS * 1000L);
                LiveHoldingsWebSocketManager.reconnect(accId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("⚠️ Reconnect interrupted — accId: {}", accId);
            } catch (Exception e) {
                log.error("❌ Reconnect failed — accId: {} Error: {}", accId, e.getMessage());
                reconnecting.set(false); // reset so next disconnect can retry
            }
        }, "angel-reconnect-" + accId);

        t.setDaemon(true); // ✅ won't block JVM shutdown
        t.start();
    }

    private void subscribeTokens() {
        try {
            // Subscribe 1 — Live Prices
            JSONArray tokenList = new JSONArray();
            JSONObject exchangeTokens = new JSONObject();
            exchangeTokens.put("exchangeType", 1);
            exchangeTokens.put("tokens", new JSONArray(tokens));
            tokenList.put(exchangeTokens);

            JSONObject priceParams = new JSONObject();
            priceParams.put("mode", 2);
            priceParams.put("tokenList", tokenList);

            JSONObject priceMsg = new JSONObject();
            priceMsg.put("action", 1);
            priceMsg.put("params", priceParams);

            session.getAsyncRemote().sendText(priceMsg.toString());
            log.info("📡 Price tokens subscribed — accId: {} count: {}", accId, tokens.size());

            // Subscribe 2 — Order Updates
            JSONObject orderParams = new JSONObject();
            orderParams.put("mode", 4);

            JSONObject orderMsg = new JSONObject();
            orderMsg.put("action", 1);
            orderMsg.put("params", orderParams);

            session.getAsyncRemote().sendText(orderMsg.toString());
            log.info("📡 Order updates subscribed — accId: {}", accId);

        } catch (Exception e) {
            log.error("❌ Subscription failed — accId: {} Error: {}", accId, e.getMessage());
        }
    }

    // ✅ PATCH 1 — Public method so PricesWebSocketManager can subscribe
    // additional tokens on an already-open Angel One WS session
    public void subscribeTokens(List<String> newTokens) {
        try {
            if (session == null || !session.isOpen()) {
                log.warn("⚠️ Cannot subscribe — session not open — accId: {}", accId);
                return;
            }

            JSONArray tokenList = new JSONArray();
            JSONObject exchangeTokens = new JSONObject();
            exchangeTokens.put("exchangeType", 1);
            exchangeTokens.put("tokens", new JSONArray(newTokens));
            tokenList.put(exchangeTokens);

            JSONObject priceParams = new JSONObject();
            priceParams.put("mode", 2);
            priceParams.put("tokenList", tokenList);

            JSONObject priceMsg = new JSONObject();
            priceMsg.put("action", 1);
            priceMsg.put("params", priceParams);

            session.getAsyncRemote().sendText(priceMsg.toString());
            log.info("📡 Extra tokens subscribed on existing WS — accId: {} count: {}",
                    accId, newTokens.size());

        } catch (Exception e) {
            log.error("❌ subscribeTokens(List) failed — accId: {} Error: {}", accId, e.getMessage());
        }
    }

    private void handleOrderUpdate(Long accId, JSONObject orderData) {
        try {
            JSONObject enriched = new JSONObject();
            enriched.put("type", "ORDER_UPDATE");
            enriched.put("orderId", orderData.optString("orderid"));
            enriched.put("tradingsymbol", orderData.optString("tradingsymbol"));
            enriched.put("status", orderData.optString("orderstatus"));
            enriched.put("quantity", orderData.optInt("quantity"));
            enriched.put("price", orderData.optDouble("price"));
            enriched.put("transactionType", orderData.optString("transactiontype"));
            enriched.put("exchange", orderData.optString("exchange"));
            enriched.put("filledQuantity", orderData.optInt("filledshares"));
            enriched.put("remarks", orderData.optString("text"));
            enriched.put("timestamp", orderData.optString("exchtime"));

            log.info("📋 Order update — accId: {} orderId: {} status: {}",
                    accId,
                    enriched.optString("orderId"),
                    enriched.optString("status"));

            // 1. Send to local instance clients
            HoldingsWebSocketServer.sendToAccount(accId, enriched.toString());

// 2. Publish to Redis for other instances
//            RedisPublisher.publish("LIVE_FEED_" + accId, enriched.toString());

        } catch (Exception e) {
            log.error("❌ Order update failed: {}", e.getMessage());
        }
    }

    // ✅ FIXED — stop ping before closing session
    public void disconnect() {
        stopPingScheduler(); // ✅ stop ping first
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            log.error("❌ Error disconnecting WS — accId: {}", accId);
        }
    }

    private JSONObject parseBinaryMessage(ByteBuffer buffer) {
        try {
            byte[] bytes = buffer.array();
            if (bytes.length < 51) {
                log.warn("⚠️ Binary message too short: {} bytes", bytes.length);
                return null;
            }

            JSONObject result = new JSONObject();

            int mode = Byte.toUnsignedInt(bytes[0]);
            result.put("mode", mode);

            int exchangeType = Byte.toUnsignedInt(bytes[1]);
            result.put("exchangeType", exchangeType);

            String token = new String(bytes, 2, 25).trim().replace("\u0000", "");
            result.put("token", token);

            long sequenceNo = ByteBuffer.wrap(bytes, 27, 8)
                    .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
            result.put("sequenceNo", sequenceNo);

            long exchangeTimestamp = ByteBuffer.wrap(bytes, 35, 8)
                    .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
            result.put("exchangeTimestamp", exchangeTimestamp);

            long ltpRaw = ByteBuffer.wrap(bytes, 43, 8)
                    .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
            double ltp = ltpRaw / 100.0;
            result.put("last_traded_price", ltpRaw);
            result.put("ltp", ltp);

            if (bytes.length >= 59) {
                long ltq = ByteBuffer.wrap(bytes, 51, 8)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
                result.put("last_traded_quantity", ltq);
            }

            if (bytes.length >= 67) {
                long atpRaw = ByteBuffer.wrap(bytes, 59, 8)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
                result.put("average_traded_price", atpRaw / 100.0);
            }

            if (bytes.length >= 75) {
                long volume = ByteBuffer.wrap(bytes, 67, 8)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
                result.put("volume_trade_for_the_day", volume);
            }

            if (bytes.length >= 99) {
                long openRaw = ByteBuffer.wrap(bytes, 91, 8)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
                result.put("open_price_of_the_day", openRaw / 100.0);
            }

            if (bytes.length >= 107) {
                long highRaw = ByteBuffer.wrap(bytes, 99, 8)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
                result.put("high_price_of_the_day", highRaw / 100.0);
            }

            if (bytes.length >= 115) {
                long lowRaw = ByteBuffer.wrap(bytes, 107, 8)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
                result.put("low_price_of_the_day", lowRaw / 100.0);
            }

            if (bytes.length >= 123) {
                long closeRaw = ByteBuffer.wrap(bytes, 115, 8)
                        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
                result.put("close_price", closeRaw / 100.0);
            }

            log.debug("📊 Parsed tick — token: {} ltp: {}", token, ltp);
            return result;

        } catch (Exception e) {
            log.error("❌ Failed to parse binary message: {}", e.getMessage());
            return null;
        }
    }
}
