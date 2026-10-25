package com.py.brokers.motilal.websocket;

import com.py.brokers.motilal.MotilalBrokerService;
import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerSecret;
import com.py.demataccount.entity.PyBrokerToken;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.demataccount.service.HoldingsWebSocketServer;
import com.py.demataccount.service.LiveHoldingsWebSocketManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Motilal live-feed manager — API-only (no WebSocket).
 *
 * BUG FIXES applied: FIX 1 — LTP and close from Motilal getLtp() are in PAISE →
 * divide by 100 to get rupees. FIX 2 — Duplicate holdings: holdingsMap was
 * keyed by both NSE and BSE tokens pointing to the same JSONObject;
 * buildSnapshot iterated values() and emitted duplicates. Solution: a separate
 * primaryHoldingsMap (keyed by primaryToken only) is used for snapshot
 * iteration; the full aliased map is only for LTP enrichment lookup. FIX 3 —
 * Quantity was always 0 because "outstandingquantity" field name doesn't exist
 * in Motilal's actual holdings response. Added multi-field fallback resolution.
 */
@Service
public class MotilalWebSocketManager {

    private static final Logger log = LoggerFactory.getLogger(MotilalWebSocketManager.class);

    // Active-feed guard
    private static final Set<Long> activeFeeds = ConcurrentHashMap.newKeySet();

    // ── PRIMARY holdings cache: accId → { primaryToken → normalizedHoldingJson }
    // Keyed by primaryToken ONLY (no BSE alias duplication).
    // This is what buildSnapshot / getHoldingsCache iterates.
    private static final Map<Long, Map<String, JSONObject>> holdingsCache
            = new ConcurrentHashMap<>();

    // ── ALIAS lookup map: accId → { nseToken|bseToken → same JSONObject }
    // Used internally during LTP enrichment so a BSE-exchange LTP tick
    // (if ever needed) can still update the correct holding object.
    // NOT exposed via getHoldingsCache — prevents duplicate snapshot rows.
    private static final Map<Long, Map<String, JSONObject>> holdingsAliasMap
            = new ConcurrentHashMap<>();

    // Positions cache: accId → { token → normalizedPositionJson }
    private static final Map<Long, ConcurrentHashMap<String, JSONObject>> positionsCache
            = new ConcurrentHashMap<>();

    @Autowired
    private PyBrokerAccountRepository pyBrokerAccountRepository;

    @Autowired
    private MotilalBrokerService motilalBrokerService;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────
    public void startLiveFeed(Long accId) {

        if (activeFeeds.contains(accId)) {
            log.info("⚡ Motilal feed already active — accId: {}", accId);
            String snapshot = LiveHoldingsWebSocketManager.buildSnapshot(accId);
            if (snapshot != null) {
                HoldingsWebSocketServer.sendSnapshotToAccount(accId, snapshot);
            }
            return;
        }

        try {
            PyBrokerAccountEntity account = pyBrokerAccountRepository
                    .findById(accId)
                    .orElseThrow(() -> new RuntimeException("Account not found: " + accId));

            PyBrokerToken tokenEntity = account.getToken();
            if (tokenEntity == null) {
                log.warn("❌ No token for Motilal accId: {}", accId);
                return;
            }
            if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("❌ Token expired for Motilal accId: {}", accId);
                return;
            }

            PyBrokerSecret secret = account.getSecret();
            if (secret == null) {
                log.warn("❌ No secret for Motilal accId: {}", accId);
                return;
            }

            // Fetch & enrich holdings
            fetchAndEnrichHoldings(accId, secret, tokenEntity);
            if (holdingsCache.getOrDefault(accId, new HashMap<>()).isEmpty()) {
                log.warn("⚠️ No Motilal holdings for accId: {} — feed not started", accId);
//                return;
            }

            // Fetch & enrich positions
            loadAndEnrichPositions(accId, secret, tokenEntity);

            activeFeeds.add(accId);

            final Long finalAccId = accId;
            Thread snapshotThread = new Thread(() -> {
                try {
                    Thread.sleep(300);
                    String snapshot = LiveHoldingsWebSocketManager.buildSnapshot(finalAccId);
                    if (snapshot != null) {
                        HoldingsWebSocketServer.sendSnapshotToAccount(finalAccId, snapshot);
                        log.info("📸 Motilal snapshot sent — accId: {} holdings: {} positions: {}",
                                finalAccId,
                                holdingsCache.getOrDefault(finalAccId, new HashMap<>()).size(),
                                positionsCache.getOrDefault(finalAccId, new ConcurrentHashMap<>()).size());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "motilal-snapshot-" + accId);
            snapshotThread.setDaemon(true);
            snapshotThread.start();

            log.info("✅ Motilal feed started (API-only) — accId: {} holdings: {} positions: {}",
                    accId,
                    holdingsCache.getOrDefault(accId, new HashMap<>()).size(),
                    positionsCache.getOrDefault(accId, new ConcurrentHashMap<>()).size());

        } catch (Exception e) {
            log.error("❌ Failed to start Motilal feed — accId: {} error: {}",
                    accId, e.getMessage(), e);
        }
    }

    public void stopLiveFeed(Long accId) {
        activeFeeds.remove(accId);
        holdingsCache.remove(accId);
        holdingsAliasMap.remove(accId);
        positionsCache.remove(accId);
        log.info("🛑 Motilal feed stopped — accId: {}", accId);
    }

    public void refreshPositions(Long accId) {
        PyBrokerAccountEntity account = pyBrokerAccountRepository.findById(accId).orElse(null);
        if (account == null || account.getSecret() == null || account.getToken() == null) {
            return;
        }
        loadAndEnrichPositions(accId, account.getSecret(), account.getToken());
        log.info("🔄 Motilal positions refreshed — accId: {}", accId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Static accessors — same signatures as LiveHoldingsWebSocketManager
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns PRIMARY holdings cache (no duplicates). Used by buildSnapshot and
     * enrichWithPnL for iteration.
     */
    public static Map<String, JSONObject> getHoldingsCache(Long accId) {
        return holdingsCache.getOrDefault(accId, new HashMap<>());
    }

    public static Map<String, JSONObject> getPositionsCache(Long accId) {
        return positionsCache.getOrDefault(accId, new ConcurrentHashMap<>());
    }

    public static boolean isActive(Long accId) {
        return activeFeeds.contains(accId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Holdings — fetch + LTP enrich
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * FIX 2: Builds TWO maps: primaryMap — one entry per scrip (primaryToken
     * key) → used for iteration aliasMap — NSE + BSE keys → same object, for
     * LTP lookup only
     *
     * FIX 3: Quantity resolved via multi-field fallback because Motilal API
     * field name varies. Raw holding is logged at INFO level on first item so
     * the real field name can be confirmed in logs, then this comment + the raw
     * log line can be removed once confirmed.
     */
    private void fetchAndEnrichHoldings(Long accId,
        PyBrokerSecret secret,
        PyBrokerToken tokenEntity) {
    Map<String, JSONObject> primaryMap = new LinkedHashMap<>();
    Map<String, JSONObject> aliasMap   = new HashMap<>();

    try {
        com.py.brokers.motilal.dto.MotilalBaseRequest req = buildBaseRequest(secret, tokenEntity);
        JSONObject response = motilalBrokerService.getHoldings(req);

        if (response == null) {
            log.warn("⚠️ Motilal getHoldings null response — accId: {}", accId);
            holdingsCache.put(accId, primaryMap);
            holdingsAliasMap.put(accId, aliasMap);
            return;
        }

        JSONArray data = response.optJSONArray("data");
        if (data == null) {
            log.warn("⚠️ Motilal getHoldings no data array — accId: {} response: {}", accId, response);
            holdingsCache.put(accId, primaryMap);
            holdingsAliasMap.put(accId, aliasMap);
            return;
        }

        log.info("📦 Motilal raw holdings count — accId: {} count: {}", accId, data.length());

        for (int i = 0; i < data.length(); i++) {
            JSONObject h = data.getJSONObject(i);

            if (i == 0) {
                log.info("🔍 Motilal holding raw fields (first item) — accId: {} data: {}", accId, h);
            }

            int    nseToken  = h.optInt("nsesymboltoken", 0);
            int    bseToken  = h.optInt("bsesymboltoken", 0);
            String scripName = h.optString("scripname", "");
            String isin      = h.optString("scripisinno", "");
            double avgPrice  = h.optDouble("buyavgprice", 0);

            // ── Quantity: dpquantity is the real holdable qty in Motilal ────────
            // "outstandingquantity" is always 0 in their API — never use it first.
            int qty = h.optInt("dpquantity",          0);
            if (qty == 0) qty = h.optInt("DpQuantity",       0);
            if (qty == 0) qty = h.optInt("dpQty",            0);
            if (qty == 0) qty = h.optInt("DPQty",            0);
            if (qty == 0) qty = h.optInt("poaquantity",      0); // POA qty as fallback
            if (qty == 0) qty = h.optInt("outstandingquantity", 0);
            if (qty == 0) qty = h.optInt("Quantity",         0);
            if (qty == 0) qty = h.optInt("quantity",         0);
            if (qty == 0) qty = h.optInt("HoldingQty",       0);
            if (qty == 0) qty = h.optInt("holdingquantity",  0);
            if (qty == 0) qty = h.optInt("holdingqty",       0);
            if (qty == 0) qty = h.optInt("NetQuantity",      0);
            if (qty == 0) qty = h.optInt("netquantity",      0);
            if (qty == 0) qty = h.optInt("NetQty",           0);
            if (qty == 0) qty = h.optInt("netqty",           0);
            if (qty == 0) qty = h.optInt("TotalQty",         0);
            if (qty == 0) qty = h.optInt("totalquantity",    0);
            if (qty == 0) qty = h.optInt("SalableQty",       0);
            if (qty == 0) qty = h.optInt("salablequantity",  0);
            if (qty == 0) qty = h.optInt("availablequantity",0);

            // dp quantity kept separately for reference fields
            int dpQty = h.optInt("dpquantity", 0);
            if (dpQty == 0) dpQty = h.optInt("DpQuantity", 0);
            if (dpQty == 0) dpQty = h.optInt("dpQty",      0);
            if (dpQty == 0) dpQty = h.optInt("DPQty",      0);

            log.debug("📊 Motilal holding qty resolved — scrip: {} qty: {} dpQty: {}",
                    scripName, qty, dpQty);

            // ── Skip zero-quantity holdings — nothing to show ────────────────
            if (qty == 0) {
                log.warn("⚠️ Motilal holding skipped — qty 0 — scrip: {}", scripName);
                continue;
            }

            int    primaryToken = nseToken > 0 ? nseToken : bseToken;
            String exchange     = nseToken > 0 ? "NSE"    : "BSE";

            if (primaryToken == 0) {
                log.warn("⚠️ Motilal holding skipped — no token — scrip: {}", scripName);
                continue;
            }

            String primaryKey = String.valueOf(primaryToken);

            JSONObject normalized = new JSONObject();
            normalized.put("symboltoken",          primaryKey);
            normalized.put("tradingsymbol",        scripName);
            normalized.put("exchange",             exchange);
            normalized.put("quantity",             qty);
            normalized.put("dpquantity",           dpQty);
            normalized.put("averageprice",         avgPrice);
            normalized.put("isin",                 isin);
            normalized.put("nsesymboltoken",       nseToken);
            normalized.put("bsesymboltoken",       bseToken);
            normalized.put("clientcode",           h.optString("clientcode", ""));
            normalized.put("tpinquantity",         h.optInt("tpinquantity",         0));
            normalized.put("poaquantity",          h.optInt("poaquantity",          0));
            normalized.put("blockedquantity",      h.optInt("blockedquantity",      0));
            normalized.put("collateralquantity",   h.optInt("collateralquantity",   0));
            normalized.put("rmssellingquantity",   h.optInt("rmssellingquantity",   0));

            // LTP defaults — avgPrice fallback keeps PnL neutral until enriched
            normalized.put("ltp",        avgPrice);
            normalized.put("livePrice",  avgPrice);
            normalized.put("close",      0.0);
            normalized.put("close_price",0.0);

            // FIX 2: primaryMap — ONE entry per scrip (no BSE duplicate rows)
            primaryMap.put(primaryKey, normalized);

            // aliasMap — both NSE + BSE keys point to same object for LTP lookup
            if (nseToken > 0) aliasMap.put(String.valueOf(nseToken), normalized);
            if (bseToken > 0) aliasMap.put(String.valueOf(bseToken), normalized);
        }

        // Enrich LTP via alias map (finds object by either NSE or BSE token)
        enrichHoldingsLtp(accId, aliasMap, secret, tokenEntity);

    } catch (Exception e) {
        log.error("❌ Motilal fetchAndEnrichHoldings failed — accId: {} error: {}",
                accId, e.getMessage());
    }

    holdingsCache.put(accId, primaryMap);
    holdingsAliasMap.put(accId, aliasMap);

    log.info("📦 Motilal holdings stored — accId: {} unique scrips: {} alias keys: {}",
            accId, primaryMap.size(), aliasMap.size());
}

    /**
     * FIX 1: Motilal getLtp() returns prices in PAISE → divide by 100.
     *
     * Iterates unique JSONObject instances (de-duped by identity) from
     * aliasMap. Updates ltp / livePrice / close / close_price on the shared
     * object, so both primaryMap and aliasMap entries reflect the enriched
     * values.
     */
    private void enrichHoldingsLtp(Long accId,
            Map<String, JSONObject> aliasMap,
            PyBrokerSecret secret,
            PyBrokerToken tokenEntity) {

        // De-dup by identity so each JSONObject is enriched exactly once
        Set<JSONObject> unique = Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        unique.addAll(aliasMap.values());

        int enriched = 0, failed = 0;

        for (JSONObject h : unique) {
            String exchange = h.optString("exchange", "NSE");
            int primaryToken = h.optInt("nsesymboltoken", 0);
            if (primaryToken == 0) {
                primaryToken = h.optInt("bsesymboltoken", 0);
            }
            if (primaryToken == 0) {
                continue;
            }

            double avgPrice = h.optDouble("averageprice", 0);

            try {
                JSONObject ltpResponse = motilalBrokerService.getLtp(
                        buildLtpRequest(secret, tokenEntity, exchange, primaryToken));

                if (ltpResponse != null) {
                    // FIX 1: Motilal LTP API returns values in PAISE → divide by 100
                    double ltpRaw = ltpResponse.optDouble("ltp",
                            ltpResponse.optDouble("Ltp", avgPrice * 100));
                    double closeRaw = ltpResponse.optDouble("close",
                            ltpResponse.optDouble("PrevClose",
                                    ltpResponse.optDouble("prevclose", 0.0)));

                    double ltp = ltpRaw / 100.0;
                    double close = closeRaw / 100.0;

                    if (ltp == 0) {
                        ltp = avgPrice; // never show 0
                    }
                    h.put("ltp", ltp);
                    h.put("livePrice", ltp);
                    h.put("close", close);
                    h.put("close_price", close);

                    enriched++;
                    log.debug("💹 Motilal holding LTP — token: {} ltpRaw: {} ltp: {} closeRaw: {} close: {}",
                            primaryToken, ltpRaw, ltp, closeRaw, close);
                } else {
                    failed++;
                    log.warn("⚠️ Motilal LTP null — token: {} using avgPrice: {}",
                            primaryToken, avgPrice);
                }

            } catch (Exception e) {
                failed++;
                log.warn("⚠️ Motilal LTP failed — token: {} error: {}", primaryToken, e.getMessage());
            }
        }

        log.info("💹 Motilal holdings LTP enriched — accId: {} success: {} failed: {}",
                accId, enriched, failed);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Positions — fetch + LTP enrich
    // ─────────────────────────────────────────────────────────────────────────
    private void loadAndEnrichPositions(Long accId,
            PyBrokerSecret secret,
            PyBrokerToken tokenEntity) {
        ConcurrentHashMap<String, JSONObject> cache = new ConcurrentHashMap<>();
        try {
            com.py.brokers.motilal.dto.MotilalBaseRequest req = buildBaseRequest(secret, tokenEntity);
            JSONObject response = motilalBrokerService.getPositions(req);
            if (response == null) {
                positionsCache.put(accId, cache);
                return;
            }

            JSONArray data = response.optJSONArray("data");
            if (data == null) {
                positionsCache.put(accId, cache);
                return;
            }

            for (int i = 0; i < data.length(); i++) {
                JSONObject pos = data.getJSONObject(i);

                // ── Token ────────────────────────────────────────────────────────
                // AngelOne uses "symboltoken"; Motilal legacy used "scripcode"
                String token = pos.optString("symboltoken",
                        String.valueOf(pos.optInt("scripcode",
                                pos.optInt("ScripCode", 0))));

                if (token.equals("0") || token.isEmpty()) {
                    log.warn("⚠️ Position skipped — no token/scripCode: {}", pos);
                    continue;
                }

                // ── Quantities ───────────────────────────────────────────────────
                // AngelOne: buyquantity / sellquantity (day fields)
                // Motilal:  BuyQuantity / SellQuantity
                int buyQty = pos.optInt("buyquantity",
                        pos.optInt("daybuyquantity",
                                pos.optInt("BuyQuantity",
                                        pos.optInt("buyqty", 0))));

                int sellQty = pos.optInt("sellquantity",
                        pos.optInt("daysellquantity",
                                pos.optInt("SellQuantity",
                                        pos.optInt("sellqty", 0))));

                // Prefer explicit NetQuantity; fall back to buy - sell
                int netQty = pos.has("NetQuantity") ? pos.optInt("NetQuantity")
                        : pos.has("netqty") ? pos.optInt("netqty")
                        : (buyQty - sellQty);

                if (netQty == 0) {
                    continue; // skip fully closed positions
                }
                // ── Prices ───────────────────────────────────────────────────────
                // AngelOne: LTP field; Motilal: ltp / close
                double ltp = pos.optDouble("LTP",
                        pos.optDouble("ltp", 0));

                // AngelOne: buyamount / daybuyamount; Motilal: BuyAmount
                double buyAmount = pos.optDouble("buyamount",
                        pos.optDouble("daybuyamount",
                                pos.optDouble("BuyAmount", 0)));

                double sellAmount = pos.optDouble("sellamount",
                        pos.optDouble("daysellamount",
                                pos.optDouble("SellAmount", 0)));

                // Average price: derive from buyamount/buyqty if not explicit
                double avgPrice;
                if (pos.has("AvgPrice")) {
                    avgPrice = pos.optDouble("AvgPrice");
                } else if (pos.has("averageprice")) {
                    avgPrice = pos.optDouble("averageprice");
                } else {
                    avgPrice = (buyQty > 0) ? (buyAmount / buyQty) : ltp;
                }

                double prevClose = pos.optDouble("PrevClose",
                        pos.optDouble("close", 0));

                // ── Exchange & Product ────────────────────────────────────────────
                String exchange = pos.optString("exchange",
                        pos.optString("Exchange", "NSE"));

                // AngelOne: productname (e.g. "DELIVERY"); Motilal: ProductType
                String productType = pos.optString("productname",
                        pos.optString("ProductType",
                                pos.optString("producttype", "")));

                // AngelOne: symbol field; Motilal: Symbol / tradingsymbol
                String tradingSymbol = pos.optString("symbol",
                        pos.optString("Symbol",
                                pos.optString("tradingsymbol", "")));

                // ── Normalize ────────────────────────────────────────────────────
                JSONObject normalized = new JSONObject();
                normalized.put("token", token);
                normalized.put("tradingsymbol", tradingSymbol);
                normalized.put("exchange", exchange);
                normalized.put("producttype", productType);
                normalized.put("netqty", netQty);
                normalized.put("buyqty", buyQty);
                normalized.put("sellqty", sellQty);
                normalized.put("averageprice", avgPrice);
                normalized.put("buyamount", buyAmount);
                normalized.put("sellamount", sellAmount);
                normalized.put("close", prevClose);
                normalized.put("ltp", ltp > 0 ? ltp : avgPrice); // enriched below

                cache.put(token, normalized);
            }

            enrichPositionsLtp(accId, cache, secret, tokenEntity);
            positionsCache.put(accId, cache);
            log.info("📋 Positions cached — accId: {} count: {}", accId, cache.size());

        } catch (Exception e) {
            log.error("❌ loadAndEnrichPositions failed — accId: {} error: {}", accId, e.getMessage(), e);
            positionsCache.put(accId, cache);
        }
    }

    /**
     * FIX 1 (positions): same paise → rupees division for position LTP.
     */
    private void enrichPositionsLtp(Long accId,
            ConcurrentHashMap<String, JSONObject> cache,
            PyBrokerSecret secret,
            PyBrokerToken tokenEntity) {
        int enriched = 0, failed = 0;

        for (JSONObject pos : cache.values()) {
            int scripCode = 0;
            double avgPrice = pos.optDouble("averageprice", 0);
            String exchange = pos.optString("exchange", "NSE");

            try {
                scripCode = Integer.parseInt(pos.optString("token", "0"));
            } catch (NumberFormatException ignored) {
            }
            if (scripCode == 0) {
                continue;
            }

            try {
                JSONObject ltpResponse = motilalBrokerService.getLtp(
                        buildLtpRequest(secret, tokenEntity, exchange, scripCode));

                if (ltpResponse != null) {
                    // FIX 1: paise → rupees
                    double ltpRaw = ltpResponse.optDouble("ltp",
                            ltpResponse.optDouble("Ltp", avgPrice * 100));
                    double closeRaw = ltpResponse.optDouble("close",
                            ltpResponse.optDouble("PrevClose",
                                    ltpResponse.optDouble("prevclose",
                                            pos.optDouble("close", 0.0) * 100)));

                    double ltp = ltpRaw / 100.0;
                    double close = closeRaw / 100.0;

                    if (ltp == 0) {
                        ltp = avgPrice;
                    }

                    pos.put("ltp", ltp);
                    pos.put("close", close);
                    enriched++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                failed++;
                log.warn("⚠️ Motilal position LTP failed — scripCode: {} error: {}", scripCode, e.getMessage());
            }
        }

        log.info("💹 Motilal positions LTP enriched — accId: {} success: {} failed: {}",
                accId, enriched, failed);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private com.py.brokers.motilal.dto.MotilalBaseRequest buildBaseRequest(
            PyBrokerSecret secret, PyBrokerToken tokenEntity) {

        com.py.brokers.motilal.dto.MotilalBaseRequest req
                = new com.py.brokers.motilal.dto.MotilalBaseRequest();
        req.setApiKey(secret.getApiKey());
        req.setApiSecret(secret.getApiSecret());
        req.setVendorInfo(secret.getVenderInfo());
        req.setAuthToken(tokenEntity.getRefreshToken());
        req.setAccessToken(tokenEntity.getAccessToken());
        return req;
    }

    private com.py.brokers.motilal.dto.MotilalLtpRequest buildLtpRequest(
            PyBrokerSecret secret, PyBrokerToken tokenEntity,
            String exchange, long scripCode) {

        com.py.brokers.motilal.dto.MotilalLtpRequest req
                = new com.py.brokers.motilal.dto.MotilalLtpRequest();
        req.setApiKey(secret.getApiKey());
        req.setApiSecret(secret.getApiSecret());
        req.setVendorInfo(secret.getVenderInfo());
        req.setAuthToken(tokenEntity.getRefreshToken());
        req.setAccessToken(tokenEntity.getAccessToken());
        req.setExchange(exchange);
        req.setScripCode(scripCode);
        return req;
    }
}
