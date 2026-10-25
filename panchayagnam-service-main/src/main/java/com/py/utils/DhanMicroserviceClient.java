package com.py.utils;

import com.py.brokers.dhan.dto.DhanBaseRequest;
import com.py.brokers.dhan.dto.DhanCancelOrderRequest;
import com.py.brokers.dhan.dto.DhanLoginRequest;
import com.py.brokers.dhan.dto.DhanModifyOrderRequest;
import com.py.brokers.dhan.dto.DhanPlaceOrderRequest;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DhanMicroserviceClient {

    private static final Logger log = LoggerFactory.getLogger(DhanMicroserviceClient.class);

    private final WebClient.Builder webClientBuilder;
    
    public DhanMicroserviceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private WebClient clientFor(String baseUrl) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────
    public JSONObject login(String baseUrl, String clientId,
            String mpin, String totp) {

        log.info("Dhan login request | baseUrl={} clientId={}", baseUrl, clientId);

        DhanLoginRequest request = new DhanLoginRequest();
        request.setClientId(clientId);
        request.setMpin(mpin);
        request.setTotp(totp);

        JSONObject response = clientFor(baseUrl).post()
                .uri("/api/dhan/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan login failed | baseUrl={} clientId={} error={}",
                                            baseUrl, clientId, body);
                                    return new RuntimeException("Dhan login failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(JSONObject::new)
                .block();

        log.info("Dhan login success | baseUrl={} clientId={}", baseUrl, clientId);
        return response;
    }

    // ─── RMS ──────────────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> getRMS(String baseUrl,
            String clientId, String accessToken) {

        log.info("Dhan RMS request | baseUrl={} clientId={}", baseUrl, clientId);

        DhanBaseRequest request = new DhanBaseRequest();
        request.setClientId(clientId);
        request.setAccessToken(accessToken);

        return clientFor(baseUrl).post()
                .uri("/api/dhan/rms")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan RMS failed | baseUrl={} clientId={} error={}",
                                            baseUrl, clientId, body);
                                    return new RuntimeException("Dhan RMS failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Dhan RMS success | baseUrl={} clientId={}", baseUrl, clientId);
                    return json;
                })
                .toFuture();
    }

    // ─── PLACE ORDER ──────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> placeOrder(String baseUrl,
            DhanPlaceOrderRequest request) {

        log.info("Dhan place order request | baseUrl={} clientId={} securityId={} side={}",
                baseUrl, request.getClientId(),
                request.getSecurityId(), request.getTransactionType());

        return clientFor(baseUrl).post()
                .uri("/api/dhan/order/place")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan place order failed | baseUrl={} clientId={} error={}",
                                            baseUrl, request.getClientId(), body);
                                    return new RuntimeException("Dhan place order failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Dhan place order success | baseUrl={} clientId={} orderId={}",
                            baseUrl, request.getClientId(), json.optString("orderId"));
                    return json;
                })
                .toFuture();
    }

    // ─── MODIFY ORDER ─────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> modifyOrder(String baseUrl,
            DhanModifyOrderRequest request) {

        log.info("Dhan modify order request | baseUrl={} clientId={} orderId={}",
                baseUrl, request.getClientId(), request.getOrderId());

        return clientFor(baseUrl).post()
                .uri("/api/dhan/order/modify")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan modify order failed | baseUrl={} clientId={} orderId={} error={}",
                                            baseUrl, request.getClientId(), request.getOrderId(), body);
                                    return new RuntimeException("Dhan modify order failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Dhan modify order success | baseUrl={} clientId={} orderId={}",
                            baseUrl, request.getClientId(), json.optString("orderId"));
                    return json;
                })
                .toFuture();
    }

    // ─── CANCEL ORDER ─────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> cancelOrder(String baseUrl,
            DhanCancelOrderRequest request) {

        log.info("Dhan cancel order request | baseUrl={} clientId={} orderId={}",
                baseUrl, request.getClientId(), request.getOrderId());

        return clientFor(baseUrl).post()
                .uri("/api/dhan/order/cancel")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan cancel order failed | baseUrl={} clientId={} orderId={} error={}",
                                            baseUrl, request.getClientId(), request.getOrderId(), body);
                                    return new RuntimeException("Dhan cancel order failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Dhan cancel order success | baseUrl={} clientId={} orderId={}",
                            baseUrl, request.getClientId(), json.optString("orderId"));
                    return json;
                })
                .toFuture();
    }

    // ─── ORDER HISTORY ────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> getOrderHistory(String baseUrl,
            String clientId, String accessToken) {

        log.info("Dhan order history request | baseUrl={} clientId={}", baseUrl, clientId);

        DhanBaseRequest request = new DhanBaseRequest();
        request.setClientId(clientId);
        request.setAccessToken(accessToken);

        return clientFor(baseUrl).post()
                .uri("/api/dhan/order/history")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan order history failed | baseUrl={} clientId={} error={}",
                                            baseUrl, clientId, body);
                                    return new RuntimeException("Dhan order history failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Dhan order history success | baseUrl={} clientId={}", baseUrl, clientId);
                    return json;
                })
                .toFuture();
    }

    // ─── HOLDINGS ─────────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> getHoldings(String baseUrl,
            String clientId, String accessToken) {

        log.info("Dhan holdings request | baseUrl={} clientId={}", baseUrl, clientId);

        DhanBaseRequest request = new DhanBaseRequest();
        request.setClientId(clientId);
        request.setAccessToken(accessToken);

        return clientFor(baseUrl).post()
                .uri("/api/dhan/holdings")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan holdings failed | baseUrl={} clientId={} error={}",
                                            baseUrl, clientId, body);
                                    return new RuntimeException("Dhan holdings failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Dhan holdings success | baseUrl={} clientId={}", baseUrl, clientId);
                    return json;
                })
                .toFuture();
    }

    // ─── POSITIONS ────────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> getPositions(String baseUrl,
            String clientId, String accessToken) {

        log.info("Dhan positions request | baseUrl={} clientId={}", baseUrl, clientId);

        DhanBaseRequest request = new DhanBaseRequest();
        request.setClientId(clientId);
        request.setAccessToken(accessToken);

        return clientFor(baseUrl).post()
                .uri("/api/dhan/positions")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Dhan positions failed | baseUrl={} clientId={} error={}",
                                            baseUrl, clientId, body);
                                    return new RuntimeException("Dhan positions failed: " + body);
                                }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("Dhan positions success | baseUrl={} clientId={}", baseUrl, clientId);
                    return json;
                })
                .toFuture();
    }
}