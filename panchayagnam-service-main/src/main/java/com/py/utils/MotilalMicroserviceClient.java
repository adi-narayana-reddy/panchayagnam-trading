package com.py.utils;

import com.py.brokers.motilal.MotilalBrokerService;
import com.py.brokers.motilal.dto.MotilalBaseRequest;
import com.py.brokers.motilal.dto.MotilalCancelOrderRequest;
import com.py.brokers.motilal.dto.MotilalConvertPositionRequest;
import com.py.brokers.motilal.dto.MotilalLoginRequest;
import com.py.brokers.motilal.dto.MotilalLtpRequest;
import com.py.brokers.motilal.dto.MotilalModifyOrderRequest;
import com.py.brokers.motilal.dto.MotilalPlaceOrderRequest;
import com.py.brokers.motilal.dto.MotilalTokenRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class MotilalMicroserviceClient {

    private static final Logger log = LoggerFactory.getLogger(MotilalMicroserviceClient.class);

    @Autowired
    private MotilalBrokerService motilalBrokerService;

    @Autowired
    private ExecutorService rmsExecutor;

    // ── WebClient only for place/modify/cancel (cross-microservice calls) ─────
    private final WebClient.Builder webClientBuilder;

    public MotilalMicroserviceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private WebClient clientFor(String baseUrl) {
        return webClientBuilder
                .clone()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    // ─── LOGIN — blocking (called during login flow) ──────────────────────────
    public JSONObject login(String baseUrl, String userId, String hashedPassword,
            String twoFa, String totp,
            String apiKey, String apiSecret, String vendorInfo) {

        log.info("Motilal login | userId={}", userId);

        MotilalLoginRequest request = new MotilalLoginRequest();
        request.setUserId(userId);
        request.setHashedPassword(hashedPassword);
        request.setTwoFa(twoFa);
        request.setTotp(totp);
        request.setApiKey(apiKey);
        request.setApiSecret(apiSecret);
        request.setVendorInfo(vendorInfo);

        long start = System.currentTimeMillis();
        JSONObject response = motilalBrokerService.login(request);
        log.info("Motilal login SUCCESS | userId={} durationMs={}", userId, System.currentTimeMillis() - start);
        return response;
    }

    // ─── GET ACCESS TOKEN — blocking (called during login flow) ──────────────
    public JSONObject getAccessToken(String baseUrl, String authToken,
            String apiKey, String apiSecret, String vendorInfo) {

        log.info("Motilal getAccessToken initiated");

        MotilalTokenRequest request = new MotilalTokenRequest();
        request.setAuthToken(authToken);
        request.setApiKey(apiKey);
        request.setApiSecret(apiSecret);
        request.setVendorInfo(vendorInfo);

        long start = System.currentTimeMillis();
        JSONObject response = motilalBrokerService.getAccessToken(request);
        log.info("Motilal getAccessToken SUCCESS | durationMs={}", System.currentTimeMillis() - start);
        return response;
    }

    // ─── RMS — async with rmsExecutor (parallel fetch across accounts) ────────
    public CompletableFuture<JSONObject> getRMS(String baseUrl,
            String apiKey, String apiSecret,
            String vendorInfo, String authToken, String accessToken) {

        log.info("Motilal RMS initiated");

        MotilalBaseRequest request = new MotilalBaseRequest();
        request.setApiKey(apiKey);
        request.setApiSecret(apiSecret);
        request.setVendorInfo(vendorInfo);
        request.setAuthToken(authToken);
        request.setAccessToken(accessToken);

        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            JSONObject result = motilalBrokerService.getRMS(request);
            log.info("Motilal RMS SUCCESS | durationMs={}", System.currentTimeMillis() - start);
            return result;
        }, rmsExecutor);
    }

    // ─── ORDER HISTORY — async with rmsExecutor ───────────────────────────────
    public CompletableFuture<JSONObject> getOrderHistory(String baseUrl,
            String apiKey, String apiSecret,
            String vendorInfo, String authToken, String accessToken) {

        log.info("Motilal order history initiated");

        MotilalBaseRequest request = new MotilalBaseRequest();
        request.setApiKey(apiKey);
        request.setApiSecret(apiSecret);
        request.setVendorInfo(vendorInfo);
        request.setAuthToken(authToken);
        request.setAccessToken(accessToken);

        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            JSONObject result = motilalBrokerService.getOrderBook(request);
            log.info("Motilal order history SUCCESS | durationMs={}", System.currentTimeMillis() - start);
            return result;
        }, rmsExecutor);
    }

    // ─── LTP — blocking (called inline during order placement) ───────────────
    public JSONObject getLtp(String baseUrl,
            String apiKey, String apiSecret, String vendorInfo,
            String authToken, String accessToken,
            String exchange, long scripCode) {

        log.info("Motilal LTP initiated | exchange={} scripCode={}", exchange, scripCode);

        MotilalLtpRequest request = new MotilalLtpRequest();
        request.setApiKey(apiKey);
        request.setApiSecret(apiSecret);
        request.setVendorInfo(vendorInfo);
        request.setAuthToken(authToken);
        request.setAccessToken(accessToken);
        request.setExchange(exchange);
        request.setScripCode(scripCode);

        long start = System.currentTimeMillis();
        JSONObject result = motilalBrokerService.getLtp(request);
        log.info("Motilal LTP SUCCESS | exchange={} scripCode={} ltp={} durationMs={}",
                exchange, scripCode,
                result != null ? result.optDouble("ltp", 0) : "null",
                System.currentTimeMillis() - start);
        return result;
    }

    // ─── PLACE ORDER — WebClient (cross-microservice call) ────────────────────
    public CompletableFuture<JSONObject> placeOrder(String baseUrl,
            MotilalPlaceOrderRequest request) {

        log.info("Motilal place order | exchange={} token={} side={}",
                request.getExchange(), request.getSymbolToken(), request.getBuyOrSell());

        long start = System.currentTimeMillis();
        return clientFor(baseUrl).post()
                .uri("/api/motilal/order/place")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Motilal place order failed: " + body)))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Motilal place order SUCCESS | uniqueOrderId={} durationMs={}",
                            json.optString("uniqueorderid"), System.currentTimeMillis() - start);
                    return json;
                })
                .toFuture();
    }

    // ─── MODIFY ORDER — WebClient (cross-microservice call) ───────────────────
    public CompletableFuture<JSONObject> modifyOrder(String baseUrl,
            MotilalModifyOrderRequest request) {

        log.info("Motilal modify order | uniqueOrderId={}", request.getUniqueOrderId());

        long start = System.currentTimeMillis();
        return clientFor(baseUrl).post()
                .uri("/api/motilal/order/modify")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Motilal modify order failed: " + body)))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Motilal modify order SUCCESS | uniqueOrderId={} durationMs={}",
                            json.optString("uniqueorderid"), System.currentTimeMillis() - start);
                    return json;
                })
                .toFuture();
    }

    // ─── CANCEL ORDER — WebClient (cross-microservice call) ───────────────────
    public CompletableFuture<JSONObject> cancelOrder(String baseUrl,
            MotilalCancelOrderRequest request) {

        log.info("Motilal cancel order | uniqueOrderId={}", request.getUniqueOrderId());

        long start = System.currentTimeMillis();
        return clientFor(baseUrl).post()
                .uri("/api/motilal/order/cancel")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Motilal cancel order failed: " + body)))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Motilal cancel order SUCCESS | uniqueOrderId={} durationMs={}",
                            json.optString("uniqueorderid"), System.currentTimeMillis() - start);
                    return json;
                })
                .toFuture();
    }

    // ─── CONVERT POSITION — WebClient (cross-microservice call) ───────────────
    public CompletableFuture<JSONObject> convertPosition(String baseUrl,
            MotilalConvertPositionRequest request) {

        log.info("Motilal convert position | exchange={} scripcode={} {} -> {}",
                request.getExchange(), request.getScripcode(),
                request.getOldproduct(), request.getNewproduct());

        long start = System.currentTimeMillis();
        return clientFor(baseUrl).post()
                .uri("/api/motilal/position/convert")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Motilal convert position failed: " + body)))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Motilal convert position SUCCESS | exchange={} scripcode={} durationMs={}",
                            request.getExchange(), request.getScripcode(),
                            System.currentTimeMillis() - start);
                    return json;
                })
                .toFuture();
    }
}
