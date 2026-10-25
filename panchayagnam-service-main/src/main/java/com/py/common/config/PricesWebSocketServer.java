package com.py.common.config;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@ServerEndpoint("/ws/prices")
@Component
public class PricesWebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(PricesWebSocketServer.class);

    // sessionId → Session
    private static final Map<String, Session> sessions
            = new ConcurrentHashMap<>();

    // sessionId → accId
    private static final Map<String, Long> sessionAccId
            = new ConcurrentHashMap<>();

    // sessionId → Set of subscribed tokens
    private static final Map<String, Set<String>> sessionTokens
            = new ConcurrentHashMap<>();

    // sessionId → ReentrantLock (prevents TEXT_FULL_WRITING on concurrent sends)
    private static final Map<String, ReentrantLock> sessionLocks
            = new ConcurrentHashMap<>();

    private static PricesWebSocketManager manager;

    public static void setManager(PricesWebSocketManager m) {
        manager = m;
    }

    @OnOpen
    public void onOpen(Session session) {
        String accIdParam = session.getRequestParameterMap()
                .getOrDefault("accId", List.of("0")).get(0);
        Long accId = Long.parseLong(accIdParam);

        sessions.put(session.getId(), session);
        sessionAccId.put(session.getId(), accId);
        sessionTokens.put(session.getId(), ConcurrentHashMap.newKeySet());
        sessionLocks.put(session.getId(), new ReentrantLock());

        log.info("📱 Prices WS connected — accId: {} sessionId: {}", accId, session.getId());

        if (manager != null) {
            manager.startPriceFeed(accId, session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JSONObject msg = new JSONObject(message);
            String action = msg.optString("action");
            Long accId = sessionAccId.get(session.getId());

            if ("SUBSCRIBE".equals(action)) {
                JSONArray tokensArr = msg.optJSONArray("tokens");
                if (tokensArr == null || accId == null) return;

                List<String> newTokens = new java.util.ArrayList<>();
                for (int i = 0; i < tokensArr.length(); i++) {
                    String token = tokensArr.getString(i);
                    Set<String> existing = sessionTokens.get(session.getId());
                    if (existing != null && existing.add(token)) {
                        newTokens.add(token);
                    }
                }

                if (!newTokens.isEmpty() && manager != null) {
                    manager.subscribeAdditionalTokens(accId, session.getId(), newTokens);
                    log.info("📡 Extra tokens subscribed — accId: {} sessionId: {} tokens: {}",
                            accId, session.getId(), newTokens);
                }

            } else if ("UNSUBSCRIBE".equals(action)) {
                JSONArray tokensArr = msg.optJSONArray("tokens");
                if (tokensArr == null) return;

                for (int i = 0; i < tokensArr.length(); i++) {
                    Set<String> existing = sessionTokens.get(session.getId());
                    if (existing != null) existing.remove(tokensArr.getString(i));
                }
                log.info("🔕 Tokens unsubscribed — sessionId: {}", session.getId());
            }

        } catch (Exception e) {
            log.error("❌ Message parse error — sessionId: {} Error: {}",
                    session.getId(), e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        Long accId = sessionAccId.remove(session.getId());
        sessions.remove(session.getId());
        sessionTokens.remove(session.getId());
        sessionLocks.remove(session.getId());

        log.info("📱 Prices WS disconnected — accId: {} sessionId: {}", accId, session.getId());

        if (accId != null && manager != null) {
            manager.onSessionDisconnect(accId, session.getId());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("❌ Prices WS error — sessionId: {} Error: {}",
                session.getId(), throwable.getMessage());
    }

    // ── Thread-safe send — tryLock skips if session is mid-write ─────────────
    private static void safeSend(Session s, String message) {
        if (s == null || !s.isOpen()) return;
        ReentrantLock lock = sessionLocks.get(s.getId());
        if (lock == null) return;

        if (lock.tryLock()) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.warn("⚠️ Send failed — sessionId: {} Error: {}", s.getId(), e.getMessage());
            } finally {
                lock.unlock();
            }
        }
        // else: session busy writing — tick dropped silently, next arrives in ~1s
    }

    // ── Send snapshot or any message to a specific session ───────────────────
    public static void sendToSession(String sessionId, String message) {
        Session s = sessions.get(sessionId);
        safeSend(s, message);
    }

    // ── Called by PricesWebSocketManager.onPriceTick() ───────────────────────
    // Routes tick to all sessions that are subscribed to this token
    public static void sendTickToSubscribers(Long accId, String token, String tickJson) {
        sessionAccId.forEach((sessionId, sid) -> {
            if (sid.equals(accId)) {
                Set<String> tokens = sessionTokens.get(sessionId);
                if (tokens != null && tokens.contains(token)) {
                    Session s = sessions.get(sessionId);
                    safeSend(s, tickJson);
                }
            }
        });
    }

    // ── Track tokens per session ──────────────────────────────────────────────
    public static void addSessionTokens(String sessionId, List<String> tokens) {
        Set<String> existing = sessionTokens.get(sessionId);
        if (existing != null) existing.addAll(tokens);
    }

    public static Set<String> getSessionTokens(String sessionId) {
        return sessionTokens.getOrDefault(sessionId, ConcurrentHashMap.newKeySet());
    }

    // ── Used by PricesWebSocketManager.onSessionDisconnect() ─────────────────
    public static Long getSessionAccId(String sessionId) {
        return sessionAccId.get(sessionId);
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
        sessionAccId.remove(sessionId);
        sessionTokens.remove(sessionId);
        sessionLocks.remove(sessionId);
    }
}