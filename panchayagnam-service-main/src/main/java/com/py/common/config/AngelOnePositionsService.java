package com.py.common.config;


import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerToken;
import com.py.demataccount.repository.PyBrokerAccountRepository;
import com.py.demataccount.repository.PyBrokerTokenRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class AngelOnePositionsService {

    private static final Logger log = LoggerFactory.getLogger(AngelOnePositionsService.class);
    private static final String POSITIONS_URL =
            "https://apiconnect.angelbroking.com/rest/secure/angelbroking/order/v1/getPosition";

    private final PyBrokerAccountRepository accountRepo;
    private final PyBrokerTokenRepository tokenRepo;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AngelOnePositionsService(PyBrokerAccountRepository accountRepo,
                                    PyBrokerTokenRepository tokenRepo) {
        this.accountRepo = accountRepo;
        this.tokenRepo   = tokenRepo;
    }

    /**
     * Fetches open positions (netQty != 0) for the given account.
     * All field names are consistent with what the WS manager reads.
     */
    public List<JSONObject> fetchOpenPositions(Long accId) {
        List<JSONObject> result = new ArrayList<>();
        try {
            PyBrokerAccountEntity account = accountRepo.findById(accId).orElse(null);
            if (account == null) {
                log.warn("⚠️ Account not found — accId: {}", accId);
                return result;
            }

            PyBrokerToken tokenEntity = tokenRepo.findByBrokerAccountId(accId).orElse(null);
            if (tokenEntity == null || tokenEntity.getAccessToken() == null) {
                log.warn("⚠️ No token found — accId: {}", accId);
                return result;
            }

            String jwtToken  = tokenEntity.getAccessToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(POSITIONS_URL))
                    .header("Authorization",      "Bearer " + jwtToken)
                    .header("Content-Type",       "application/json")
                    .header("Accept",             "application/json")
                    .header("X-UserType",         "USER")
                    .header("X-SourceID",         "WEB")
                    .header("X-ClientLocalIP",    "127.0.0.1")
                    .header("X-ClientPublicIP",   "127.0.0.1")
                    .header("X-MACAddress",       "00:00:00:00:00:00")
                    .header("X-PrivateKey",       account.getSecret().getApiKey())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JSONObject body = new JSONObject(response.body());
            if (!body.optBoolean("status", false)) {
                log.warn("⚠️ Positions API error — accId: {} msg: {}",
                        accId, body.optString("message"));
                return result;
            }

            JSONArray data = body.optJSONArray("data");
            if (data == null) return result;

            for (int i = 0; i < data.length(); i++) {
                JSONObject pos = data.getJSONObject(i);
                int netQty = pos.optInt("netqty", 0);
                if (netQty == 0) continue;   // closed / squared-off — skip

                // FIX: All keys stored here must exactly match what WS manager reads
                JSONObject normalized = new JSONObject();
                normalized.put("token",         pos.optString("symboltoken"));  // cache key
                normalized.put("tradingsymbol", pos.optString("tradingsymbol"));
                normalized.put("exchange",      pos.optString("exchange"));
                normalized.put("producttype",   pos.optString("producttype"));  // MIS / CNC
                normalized.put("netqty",        netQty);                        // lowercase — WS manager reads this
                normalized.put("buyqty",        pos.optInt("buyqty", 0));
                normalized.put("sellqty",       pos.optInt("sellqty", 0));
                normalized.put("averageprice",  pos.optDouble("netprice", 0));  // WS manager reads averageprice
                normalized.put("buyamount",     pos.optDouble("buyamount", 0));
                normalized.put("sellamount",    pos.optDouble("sellamount", 0));
                normalized.put("ltp",           pos.optDouble("ltp", 0));
                normalized.put("close",         pos.optDouble("close", 0));
                normalized.put("pnl",           pos.optDouble("pnl", 0));

                result.add(normalized);
            }

            log.info("📋 Positions fetched — accId: {} open: {}", accId, result.size());

        } catch (Exception e) {
            log.error("❌ Failed to fetch positions — accId: {} Error: {}", accId, e.getMessage());
        }
        return result;
    }
}