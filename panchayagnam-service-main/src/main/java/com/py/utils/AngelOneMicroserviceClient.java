package com.py.utils;

import com.py.brokers.angel.client.service.AngelOneApisService;
import com.py.brokers.angel.dto.AngelOneCancelOrderRequest;
import com.py.brokers.angel.dto.AngelOneConvertPositionRequest;
import com.py.brokers.angel.dto.AngelOneLoginRequest;
import com.py.brokers.angel.dto.AngelOneLtpRequest;
import com.py.brokers.angel.dto.AngelOneModifyOrderRequest;
import com.py.brokers.angel.dto.AngelOneOrderHistoryRequest;
import com.py.brokers.angel.dto.AngelOnePlaceOrderRequest;
import com.py.brokers.angel.dto.AngelOneRMSRequest;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AngelOneMicroserviceClient {

    private final WebClient.Builder webClientBuilder;
    @Autowired
    private AngelOneApisService angelOneApisService;
    private static final Logger log = LoggerFactory.getLogger(AngelOneMicroserviceClient.class);

    public AngelOneMicroserviceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private WebClient clientFor(String baseUrl) {
        return webClientBuilder
                .clone()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    // ─── LOGIN ───────────────────────────────────────────────────────────────
    public JSONObject login(String baseUrl, String clientCode,
            String password, String totp, String apiKey) {
        log.info("AngelOne login request | baseUrl={} clientCode={}", baseUrl, clientCode);

        AngelOneLoginRequest request = new AngelOneLoginRequest();
        request.setClientCode(clientCode);
        request.setPassword(password);
        request.setTotp(totp);
        request.setApiKey(apiKey);

        return angelOneApisService.login(request);
    }

    // ─── PLACE ORDER ─────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> placeOrder(
            String baseUrl, AngelOnePlaceOrderRequest request) {
        log.info("AngelOne place order request | baseUrl={} symbol={} qty={} type={}",
                baseUrl, request.getTradingsymbol(),
                request.getQuantity(), request.getTransactiontype());

        return clientFor(baseUrl).post()
                .uri("/api/smart/order/place")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> {
                            log.error("AngelOne place order failed | baseUrl={} symbol={} error={}",
                                    baseUrl, request.getTradingsymbol(), body);
                            return new RuntimeException("Place order failed: " + body);
                        }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("AngelOne place order success | baseUrl={} symbol={} orderId={}",
                            baseUrl, request.getTradingsymbol(), json.optString("orderId"));
                    return json;
                })
                .toFuture();
    }

    // ─── MODIFY ORDER ────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> modifyOrder(
            String baseUrl, AngelOneModifyOrderRequest request) {
        log.info("AngelOne modify order request | baseUrl={} orderId={} symbol={} qty={} price={}",
                baseUrl, request.getOrderId(), request.getTradingSymbol(),
                request.getQuantity(), request.getPrice());

        return clientFor(baseUrl).post()
                .uri("/api/smart/order/modify")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> {
                            log.error("AngelOne modify order failed | baseUrl={} orderId={} error={}",
                                    baseUrl, request.getOrderId(), body);
                            return new RuntimeException("Modify order failed: " + body);
                        }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("AngelOne modify order success | baseUrl={} orderId={}",
                            baseUrl, json.optString("orderId"));
                    return json;
                })
                .toFuture();
    }

    // ─── CANCEL ORDER ────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> cancelOrder(
            String baseUrl, AngelOneCancelOrderRequest request) {
        log.info("AngelOne cancel order request | baseUrl={} orderId={} variety={}",
                baseUrl, request.getOrderId(), request.getVariety());

        return clientFor(baseUrl).post()
                .uri("/api/smart/order/cancel")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> {
                            log.error("AngelOne cancel order failed | baseUrl={} orderId={} error={}",
                                    baseUrl, request.getOrderId(), body);
                            return new RuntimeException("Cancel order failed: " + body);
                        }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("AngelOne cancel order success | baseUrl={} orderId={}",
                            baseUrl, json.optString("orderId"));
                    return json;
                })
                .toFuture();
    }

    // ─── ORDER HISTORY ───────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> getOrderHistory(String baseUrl,
            String apiKey, String accessToken) {
        log.info("AngelOne order history request | baseUrl={}", baseUrl);

        AngelOneOrderHistoryRequest request = new AngelOneOrderHistoryRequest();
        request.setApiKey(apiKey);
        request.setAccessToken(accessToken);

        return CompletableFuture.supplyAsync(() -> angelOneApisService.getOrderHistory(request));
    }

    // ─── RMS ─────────────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> getRMS(String baseUrl,
            String apiKey, String accessToken) {
        log.info("AngelOne RMS request | baseUrl={}", baseUrl);

        AngelOneRMSRequest request = new AngelOneRMSRequest();
        request.setApiKey(apiKey);
        request.setAccessToken(accessToken);

        return CompletableFuture.supplyAsync(() -> angelOneApisService.getRMS(request));
    }

    // ─── LTP ─────────────────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> getLTP(String baseUrl,
            String apiKey, String accessToken,
            String exchange, String tradingSymbol, String symbolToken) {
        log.info("AngelOne LTP request | baseUrl={} exchange={} symbol={} token={}",
                baseUrl, exchange, tradingSymbol, symbolToken);

        AngelOneLtpRequest request = new AngelOneLtpRequest();
        request.setApiKey(apiKey);
        request.setAccessToken(accessToken);
        request.setExchange(exchange);
        request.setTradingSymbol(tradingSymbol);
        request.setSymbolToken(symbolToken);

        return CompletableFuture.supplyAsync(() -> angelOneApisService.getLTP(request));
    }

    // ─── CONVERT POSITION ────────────────────────────────────────────────────
    public CompletableFuture<JSONObject> convertPosition(String baseUrl,
            AngelOneConvertPositionRequest request) {
        log.info("AngelOne convert position request | baseUrl={} symbol={} {} -> {}",
                baseUrl, request.getTradingsymbol(),
                request.getOldproducttype(), request.getNewproducttype());

        return clientFor(baseUrl).post()
                .uri("/api/smart/convertPosition")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res
                        -> res.bodyToMono(String.class)
                        .map(body -> {
                            log.error("AngelOne convert position failed | baseUrl={} symbol={} error={}",
                                    baseUrl, request.getTradingsymbol(), body);
                            return new RuntimeException("Convert position failed: " + body);
                        }))
                .bodyToMono(String.class)
                .map(body -> {
                    JSONObject json = new JSONObject(body);
                    log.info("AngelOne convert position success | baseUrl={} symbol={}",
                            baseUrl, request.getTradingsymbol());
                    return json;
                })
                .toFuture();
    }
}
