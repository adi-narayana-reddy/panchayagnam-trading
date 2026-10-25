package com.py.brokers.motilal.websocket;

import com.py.common.config.PricesWebSocketManager;
import com.py.demataccount.service.HoldingsWebSocketServer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Motilal Oswal WebSocket client for live price broadcast (ws1feed).
 *
 * Protocol (from MOFSLOPENAPI.py):
 *   WS URL  : wss://ws1feed.motilaloswal.com/jwebsocket/jwebsocket
 *   Auth    : No HTTP headers; send binary LoginPacket immediately on open
 *   Subscribe: Per-scrip binary RegisterPacket (LoginPacket must precede each)
 *   Inbound : Binary stream of N × 30-byte packets
 *             [0]     1 byte  exchange char (N/B/M/D/C/G)
 *             [1..4]  4 bytes scrip code (little-endian int32)
 *             [5..8]  4 bytes epoch offset from 1980-01-01 (little-endian int32)
 *             [9]     1 byte  msg type (A=LTP, G=DayOHLC, B-F=MarketDepth, 1=Heartbeat)
 *             [10..29] 20 bytes body
 *   Heartbeat: Server sends msgtype='1'; client must reply pack("=cH",'1',0) = 3 bytes
 */
public class MotilalWebSocketClient extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(MotilalWebSocketClient.class);

    public static final String WS_URL_LIVE =
            "wss://ws1feed.motilaloswal.com/jwebsocket/jwebsocket";

    private static final int  PACKET_SIZE            = 30;
    private static final int  RECONNECT_DELAY_SEC    = 5;
    private static final int  MAX_RECONNECT_ATTEMPTS = 5;
    private static final String WS_VERSION           = "VER 2.0";

    private final Long   accId;
    private final String clientCode;
    private final String authToken;
    private final List<MotilalScripSubscription> subscriptions;

    private final AtomicBoolean disconnecting    = new AtomicBoolean(false);
    private final AtomicBoolean reconnecting     = new AtomicBoolean(false);
    private int reconnectAttempts = 0;

    // OHLC cache per scrip — populated by DayOHLC packets (msgtype='G')
    private final Map<Integer, OhlcData> ohlcCache = new ConcurrentHashMap<>();

    // Subscriptions queued before connection is open
    private final Queue<MotilalScripSubscription> pendingSubscriptions = new ConcurrentLinkedQueue<>();

    // ─────────────────────────────────────────────────────────────────────────

    public MotilalWebSocketClient(Long accId, String clientCode, String authToken,
                                   List<MotilalScripSubscription> subscriptions) throws Exception {
        super(new URI(WS_URL_LIVE));
        this.accId         = accId;
        this.clientCode    = clientCode;
        this.authToken     = authToken;
        this.subscriptions = subscriptions;
    }

    public static MotilalWebSocketClient connect(Long accId, String clientCode,
                                                  String authToken,
                                                  List<MotilalScripSubscription> subscriptions)
            throws Exception {
        MotilalWebSocketClient client =
                new MotilalWebSocketClient(accId, clientCode, authToken, subscriptions);
        client.connect();
        return client;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── WebSocket lifecycle ────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("✅ Motilal WS connected — accId: {} clientCode: {}", accId, clientCode);
        reconnecting.set(false);
        reconnectAttempts = 0;

        sendLoginPacket('Q');

        for (MotilalScripSubscription sub : subscriptions) {
            sendRegisterPacket(sub, true);
        }

        MotilalScripSubscription pending;
        while ((pending = pendingSubscriptions.poll()) != null) {
            sendRegisterPacket(pending, true);
        }
    }

    @Override
    public void onMessage(String message) {
        log.debug("📨 Motilal WS text (ignored) — accId: {} msg: {}", accId, message);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            handleBinaryMessage(bytes.array());
        } catch (Exception e) {
            log.error("❌ Motilal binary parse error — accId: {} error: {}", accId, e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("⚠️ Motilal WS closed — accId: {} code: {} reason: {} remote: {}",
                accId, code, reason, remote);
        if (!disconnecting.get()) {
            scheduleReconnect();
        }
    }

    @Override
    public void onError(Exception ex) {
        log.error("❌ Motilal WS error — accId: {} error: {}", accId, ex.getMessage());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Packet builders ────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * LoginPacket (type Q=login, q=relogin).
     * Python: pack("=cHB15sB30sBBBB10sBBBBB45s", msgType, 111, ...)
     * Total: 1+2+1+15+1+30+1+1+1+1+10+1+1+1+1+1+45 = 114 bytes
     */
    private void sendLoginPacket(char type) {
        try {
            int totalSize = 1 + 2 + 1 + 15 + 1 + 30 + 1 + 1 + 1 + 1 + 10 + 1 + 1 + 1 + 1 + 1 + 45;
            ByteBuffer buf = ByteBuffer.allocate(totalSize).order(ByteOrder.LITTLE_ENDIAN);

            byte[] code1  = padRight(clientCode, 15);
            byte[] code2  = padRight(clientCode, 30);
            byte[] verBuf = padRight(WS_VERSION,  10);

            buf.put((byte) type);
            buf.putShort((short) 111);
            buf.put((byte) clientCode.length());
            buf.put(code1);
            buf.put((byte) clientCode.length());
            buf.put(code2);
            buf.put((byte) 1);
            buf.put((byte) 1);
            buf.put((byte) 1);
            buf.put((byte) WS_VERSION.length());
            buf.put(verBuf);
            buf.put((byte) 0);
            buf.put((byte) 0);
            buf.put((byte) 0);
            buf.put((byte) 0);
            buf.put((byte) 1);
            buf.put(new byte[45]);  // padding

            send(buf.array());
            log.debug("📤 Motilal LoginPacket({}) sent — accId: {}", type, accId);
        } catch (Exception e) {
            log.error("❌ Motilal LoginPacket failed — accId: {} error: {}", accId, e.getMessage());
        }
    }

    /**
     * RegisterPacket — subscribe/unsubscribe one scrip.
     * Python: pack("=cHcciB", 'D', 7, exchange_char, exchangetype_char, scripcode, addToList)
     * Total: 1+2+1+1+4+1 = 10 bytes
     * Note: Motilal requires LoginPacket to precede each RegisterPacket.
     */
    private void sendRegisterPacket(MotilalScripSubscription sub, boolean add) {
        try {
            sendLoginPacket('Q'); // Motilal mandates LoginPacket before every Register

            byte exchangeChar     = resolveExchangeChar(sub.getExchange());
            byte exchangeTypeChar = (byte) sub.getExchangeType().toUpperCase().charAt(0);

            ByteBuffer buf = ByteBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN);
            buf.put((byte) 'D');
            buf.putShort((short) 7);
            buf.put(exchangeChar);
            buf.put(exchangeTypeChar);
            buf.putInt(sub.getScripCode());
            buf.put(add ? (byte) 1 : (byte) 0);

            send(buf.array());
            log.info("📡 Motilal {} — accId: {} exchange: {} scrip: {}",
                    add ? "Registered" : "Unregistered",
                    accId, sub.getExchange(), sub.getScripCode());
        } catch (Exception e) {
            log.error("❌ Motilal RegisterPacket failed — accId: {} scrip: {} error: {}",
                    accId, sub.getScripCode(), e.getMessage());
        }
    }

    /**
     * HeartbeatResponse — sent when server sends msgtype='1'.
     * Python: pack("=cH", '1', 0) = 3 bytes
     */
    private void sendHeartbeatResponse() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN);
            buf.put((byte) '1');
            buf.putShort((short) 0);
            send(buf.array());
            log.debug("🏓 Motilal heartbeat response sent — accId: {}", accId);
        } catch (Exception e) {
            log.warn("⚠️ Motilal heartbeat response failed — accId: {} error: {}", accId, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Binary message parsing ─────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    private void handleBinaryMessage(byte[] data) {
        if (data == null || data.length < PACKET_SIZE) return;

        int fullPackets = data.length / PACKET_SIZE;
        for (int i = 0; i < fullPackets; i++) {
            byte[] pkt = Arrays.copyOfRange(data, i * PACKET_SIZE, (i + 1) * PACKET_SIZE);
            processPacket(pkt);
        }
    }

    private void processPacket(byte[] pkt) {
        char   exchange  = (char) pkt[0];
        int    scripCode = ByteBuffer.wrap(pkt, 1, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        char   msgType   = (char) pkt[9];
        byte[] body      = Arrays.copyOfRange(pkt, 10, 30);

        switch (msgType) {
            case 'A'                         -> parseLTP(exchange, scripCode, body);
            case 'G'                         -> parseDayOHLC(exchange, scripCode, body);
            case '1'                         -> sendHeartbeatResponse();
            case 'B', 'C', 'D', 'E', 'F'    -> { /* MarketDepth — not needed for holdings */ }
            default -> log.debug("🔎 Motilal unknown msgType: {} scrip: {}", msgType, scripCode);
        }
    }

    /**
     * LTP packet body (20 bytes):
     * [0..3]   float  LTP Rate
     * [4..7]   int32  LTP Qty
     * [8..11]  int32  Cumulative Qty (volume)
     * [12..15] float  Avg Trade Price
     * [16..19] int32  Open Interest
     */
    private void parseLTP(char exchange, int scripCode, byte[] body) {
        float  ltp     = ByteBuffer.wrap(body, 0,  4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        int    qty     = ByteBuffer.wrap(body, 4,  4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int    cumQty  = ByteBuffer.wrap(body, 8,  4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        float  avgPx   = ByteBuffer.wrap(body, 12, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

        double ltpVal = round(ltp);
        String token  = String.valueOf(scripCode);   // Motilal uses int scripCode as token
        String exStr  = resolveExchangeString(exchange, scripCode);

        OhlcData ohlc = ohlcCache.getOrDefault(scripCode, OhlcData.EMPTY);

        // Build tick — field names match what HoldingsWebSocketServer.enrichWithPnL() reads
        JSONObject tick = new JSONObject();
        tick.put("token",                    token);
        tick.put("exchange",                 exStr);
        tick.put("ltp",                      ltpVal);
        tick.put("last_traded_price",        (long)(ltp * 100));
        tick.put("last_traded_quantity",     qty);
        tick.put("average_traded_price",     round(avgPx));
        tick.put("volume_trade_for_the_day", cumQty);
        tick.put("open_price_of_the_day",    ohlc.open);
        tick.put("high_price_of_the_day",    ohlc.high);
        tick.put("low_price_of_the_day",     ohlc.low);
        tick.put("close_price",              ohlc.prevClose);  // prevDayClose = Angel One close_price

        log.debug("📊 Motilal LTP — accId: {} scrip: {} ltp: {}", accId, scripCode, ltpVal);

        // 1. Holdings/positions P&L enrichment (same as Angel One path)
        HoldingsWebSocketServer.sendToAccount(accId, tick.toString());

        // 2. PricesWebSocketManager for other subscribers
        PricesWebSocketManager.onPriceTick(
                accId, token,
                ltpVal, ohlc.prevClose,
                ohlc.open, ohlc.high, ohlc.low,
                cumQty
        );
    }

    /**
     * DayOHLC packet body (20 bytes):
     * [0..3]   float  Open
     * [4..7]   float  High
     * [8..11]  float  Low
     * [12..15] float  PrevDayClose  (= close_price for Angel One compatibility)
     * [16..19] reserved
     */
    private void parseDayOHLC(char exchange, int scripCode, byte[] body) {
        float open  = ByteBuffer.wrap(body, 0,  4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float high  = ByteBuffer.wrap(body, 4,  4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float low   = ByteBuffer.wrap(body, 8,  4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float close = ByteBuffer.wrap(body, 12, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        ohlcCache.put(scripCode, new OhlcData(round(open), round(high), round(low), round(close)));
        log.debug("📊 Motilal OHLC cached — accId: {} scrip: {} O:{} H:{} L:{} C:{}",
                accId, scripCode, round(open), round(high), round(low), round(close));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Public API ─────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    /** Subscribe additional scrips on an already-live connection. */
    public void subscribeScrips(List<MotilalScripSubscription> newScrips) {
        for (MotilalScripSubscription sub : newScrips) {
            if (isOpen()) {
                sendRegisterPacket(sub, true);
            } else {
                pendingSubscriptions.add(sub);
                log.warn("⚠️ WS not open — queued scrip: {}", sub.getScripCode());
            }
        }
    }

    public void unsubscribeScrip(MotilalScripSubscription sub) {
        if (isOpen()) sendRegisterPacket(sub, false);
    }

    /** Cleanly disconnect. */
    public void disconnect() {
        disconnecting.set(true);
        try {
            if (isOpen()) close();
        } catch (Exception e) {
            log.error("❌ Error disconnecting Motilal WS — accId: {}", accId);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Reconnect ──────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    private void scheduleReconnect() {
        if (!reconnecting.compareAndSet(false, true)) {
            log.warn("⚠️ Motilal reconnect in progress — accId: {}", accId);
            return;
        }
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            log.error("❌ Motilal max reconnects reached — accId: {}", accId);
            reconnecting.set(false);
            return;
        }

        reconnectAttempts++;
        Thread t = new Thread(() -> {
            try {
                log.info("🔄 Motilal reconnecting in {}s — accId: {} attempt: {}",
                        RECONNECT_DELAY_SEC, accId, reconnectAttempts);
                Thread.sleep(RECONNECT_DELAY_SEC * 1000L);
                reconnect();   // java-websocket built-in reconnect
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("❌ Motilal reconnect failed — accId: {} error: {}", accId, e.getMessage());
                reconnecting.set(false);
            }
        }, "motilal-reconnect-" + accId);
        t.setDaemon(true);
        t.start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Helpers ─────────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    private byte resolveExchangeChar(String exchange) {
        return switch (exchange.toUpperCase()) {
            case "NSECD" -> (byte) 'C';
            case "NCDEX" -> (byte) 'D';
            case "BSEFO" -> (byte) 'G';
            default      -> (byte) exchange.toUpperCase().charAt(0);
        };
    }

    private String resolveExchangeString(char exchange, int scripCode) {
        return switch (exchange) {
            case 'N' -> (scripCode <= 34999 || (scripCode >= 888801 && scripCode <= 888820))
                        ? "NSE" : "NSEFO";
            case 'B' -> "BSE";
            case 'M' -> "MCX";
            case 'D' -> "NCDEX";
            case 'C' -> "NSECD";
            case 'G' -> "BSEFO";
            default  -> "UNKNOWN";
        };
    }

    private byte[] padRight(String s, int length) {
        byte[] buf = new byte[length];
        Arrays.fill(buf, (byte) ' ');
        byte[] src = s.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        System.arraycopy(src, 0, buf, 0, Math.min(src.length, length));
        return buf;
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Inner types ─────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    static class OhlcData {
        static final OhlcData EMPTY = new OhlcData(0, 0, 0, 0);
        final double open, high, low, prevClose;
        OhlcData(double open, double high, double low, double prevClose) {
            this.open = open; this.high = high; this.low = low; this.prevClose = prevClose;
        }
    }
}