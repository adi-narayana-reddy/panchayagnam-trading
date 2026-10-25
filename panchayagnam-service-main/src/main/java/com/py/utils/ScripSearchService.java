package com.py.utils;

import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ScripSearchService {

    private static final Logger log = LoggerFactory.getLogger(ScripSearchService.class);

    private static final String SCRIP_URL
            = "https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json";

    private static final int    MAX_RETRIES    = 5;
    private static final long   RETRY_DELAY_MS = 30_000; // 30 seconds

    // ✅ In-memory store — loaded once at startup
    private final List<Map<String, String>> scripList = new ArrayList<>();

    // ✅ Track when last loaded
    private LocalDateTime lastLoadedAt;

    // ── LOAD ON STARTUP (async — don't block app start) ──────────────────────
    @PostConstruct
    public void load() {
        CompletableFuture.runAsync(() -> {
            log.info("Loading scrip master in background...");
            loadScripsWithRetry();
        });
    }

    // ── SCHEDULED — every day 8 AM ────────────────────────────────────────────
    @Scheduled(cron = "0 0 8 * * *")
    public void scheduledReload() {
        log.info("Scheduled scrip master reload at 8 AM...");
        loadScripsWithRetry();
    }

    // ── SCHEDULED — check every 1 hour if missed 8 AM reload ─────────────────
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void reloadIfStale() {
        if (lastLoadedAt == null) {
            log.warn("Scrip master not loaded — loading now...");
            loadScripsWithRetry();
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayReloadTime = LocalDate.now().atTime(LocalTime.of(8, 0));

        // ✅ Past 8 AM today but last load was before 8 AM — missed reload
        boolean missedReload = now.isAfter(todayReloadTime)
                && lastLoadedAt.isBefore(todayReloadTime);

        if (missedReload) {
            log.warn("Missed 8 AM scrip reload — server was likely down. Reloading now...");
            loadScripsWithRetry();
        }
    }

    // ── RETRY WRAPPER ─────────────────────────────────────────────────────────
    // Attempts loadScrips() up to MAX_RETRIES times with RETRY_DELAY_MS between each.
    // Old data is preserved throughout — only replaced on success.
    private void loadScripsWithRetry() {
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            attempt++;
            log.info("Scrip master load attempt {}/{}...", attempt, MAX_RETRIES);

            boolean success = loadScrips();

            if (success) {
                log.info("✅ Scrip master loaded successfully on attempt {}/{}", attempt, MAX_RETRIES);
                return;
            }

            if (attempt < MAX_RETRIES) {
                log.warn("Attempt {}/{} failed — retrying in {} seconds...",
                        attempt, MAX_RETRIES, RETRY_DELAY_MS / 1000);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry sleep interrupted — aborting retries");
                    return;
                }
            }
        }

        log.error("❌ All {}/{} scrip master load attempts failed. Old data preserved (if any).",
                MAX_RETRIES, MAX_RETRIES);
    }

    // ── CORE LOAD METHOD ──────────────────────────────────────────────────────
    // Returns true on success, false on failure.
    // Old scripList is never cleared on failure — safe to retry.
    private boolean loadScrips() {
        try {
            log.info("Fetching AngelOne scrip master from URL...");

            RestTemplate rest = new RestTemplate();
            String raw = rest.getForObject(SCRIP_URL, String.class);

            if (raw == null || raw.isBlank()) {
                log.error("Scrip master response is empty");
                return false;
            }

            JSONArray array = new JSONArray(raw);
            List<Map<String, String>> newList = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String exchSeg = obj.optString("exch_seg", "");

                // ✅ Only keep NSE, BSE, NFO, MCX
                if (!List.of("NSE", "BSE", "NFO", "MCX").contains(exchSeg)) {
                    continue;
                }

                Map<String, String> scrip = new LinkedHashMap<>();
                scrip.put("token",          obj.optString("token"));
                scrip.put("symbol",         obj.optString("symbol"));
                scrip.put("name",           obj.optString("name"));
                scrip.put("exchange",       exchSeg);
                scrip.put("instrumentType", obj.optString("instrumenttype"));
                scrip.put("lotSize",        obj.optString("lotsize"));
                scrip.put("tickSize",       obj.optString("tick_size"));
                scrip.put("expiry",         obj.optString("expiry"));
                scrip.put("strike",         obj.optString("strike"));
                newList.add(scrip);
            }

            // ✅ Atomic swap — no blank results during reload
            synchronized (scripList) {
                scripList.clear();
                scripList.addAll(newList);
                lastLoadedAt = LocalDateTime.now();
            }

            log.info("✅ Scrip master loaded — {} instruments at {}",
                    scripList.size(), lastLoadedAt);

            return true;

        } catch (Exception e) {
            log.error("❌ Scrip master load failed: {}", e.getMessage(), e);
            // ✅ Old data preserved — do not clear on failure
            return false;
        }
    }

    // ── SEARCH — all instruments ──────────────────────────────────────────────
    public List<Map<String, String>> search(String query, String exchange) {
        if (query == null || query.length() < 2) {
            return List.of();
        }

        String q = query.toUpperCase().trim();

        return scripList.stream()
                .filter(s -> exchange == null
                || exchange.isBlank()
                || s.get("exchange").equalsIgnoreCase(exchange))
                .filter(s -> s.get("symbol").toUpperCase().contains(q)
                || s.get("name").toUpperCase().contains(q))
                .sorted(Comparator.comparingInt(s -> {
                    String sym = ((Map<String, String>) s).get("symbol").toUpperCase();
                    if (sym.equals(q))      return 0;
                    if (sym.startsWith(q))  return 1;
                    if (sym.contains(q))    return 2;
                    return 3;
                }))
                .limit(10)
                .toList();
    }

    // ── SEARCH EQUITY ONLY ────────────────────────────────────────────────────
    public List<Map<String, String>> searchEquity(String query, String exchange) {
        if (query == null || query.length() < 2) {
            return List.of();
        }

        String q = query.toUpperCase().trim();

        return scripList.stream()
                .filter(s -> exchange == null
                || exchange.isBlank()
                || s.get("exchange").equalsIgnoreCase(exchange))
                .filter(s -> {
                    String sym  = s.get("symbol").toUpperCase();
                    String exch = s.get("exchange");
                    boolean isNseEquity = "NSE".equals(exch) && sym.endsWith("-EQ");
                    boolean isBseEquity = "BSE".equals(exch) && !sym.contains("-");
                    boolean isNseFEquity="NFO".equals(exch);
                    return isNseEquity || isBseEquity|| isNseFEquity;
                })
                .filter(s -> s.get("symbol").toUpperCase().contains(q)
                || s.get("name").toUpperCase().contains(q))
                .sorted(Comparator.comparingInt(s -> {
                    String sym = ((Map<String, String>) s).get("symbol").toUpperCase();
                    if (sym.equals(q))      return 0;
                    if (sym.startsWith(q))  return 1;
                    if (sym.contains(q))    return 2;
                    return 3;
                }))
                .limit(10)
                .toList();
    }

    // ── GET BY EXACT SYMBOL + EXCHANGE ────────────────────────────────────────
    public Optional<Map<String, String>> getBySymbol(String symbol, String exchange) {
        return scripList.stream()
                .filter(s -> s.get("symbol").equalsIgnoreCase(symbol)
                && s.get("exchange").equalsIgnoreCase(exchange))
                .findFirst();
    }

    // ── GET BY TOKEN + EXCHANGE ───────────────────────────────────────────────
    public Optional<Map<String, String>> getByToken(String token, String exchange) {
        return scripList.stream()
                .filter(s -> s.get("token").equals(token)
                && s.get("exchange").equalsIgnoreCase(exchange))
                .findFirst();
    }

    // ── HEALTH / STATUS ───────────────────────────────────────────────────────
    public boolean isLoaded() {
        return !scripList.isEmpty();
    }

    public int getTotalLoaded() {
        return scripList.size();
    }

    public LocalDateTime getLastLoadedAt() {
        return lastLoadedAt;
    }
}