package com.py.dhan.controllers;

import com.py.dhan.dto.*;
import com.py.dhan.service.DhanApiService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dhan")
@CrossOrigin(origins = "*")
public class DhanBrokerController {

    private static final Logger log = LoggerFactory.getLogger(DhanBrokerController.class);

    @Autowired
    private DhanApiService dhanApiService;

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody DhanLoginRequest request) {
        log.info("Dhan login | clientId={}", request.getClientId());
        JSONObject response = dhanApiService.login(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── FUND LIMIT / RMS ──────────────────────────────────────────────────────
    @PostMapping("/rms")
    public ResponseEntity<String> getFundLimit(@RequestBody DhanBaseRequest request) {
        log.info("Dhan RMS | clientId={}", request.getClientId());
        JSONObject response = dhanApiService.getFundLimit(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── PLACE ORDER ───────────────────────────────────────────────────────────
    @PostMapping("/order/place")
    public ResponseEntity<String> placeOrder(@RequestBody DhanPlaceOrderRequest request) {
        log.info("Dhan place order | clientId={} securityId={} side={}",
                request.getClientId(), request.getSecurityId(), request.getTransactionType());
        JSONObject response = dhanApiService.placeOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── MODIFY ORDER ──────────────────────────────────────────────────────────
    @PostMapping("/order/modify")
    public ResponseEntity<String> modifyOrder(@RequestBody DhanModifyOrderRequest request) {
        log.info("Dhan modify order | clientId={} orderId={}",
                request.getClientId(), request.getOrderId());
        JSONObject response = dhanApiService.modifyOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── CANCEL ORDER ──────────────────────────────────────────────────────────
    @PostMapping("/order/cancel")
    public ResponseEntity<String> cancelOrder(@RequestBody DhanCancelOrderRequest request) {
        log.info("Dhan cancel order | clientId={} orderId={}",
                request.getClientId(), request.getOrderId());
        JSONObject response = dhanApiService.cancelOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── ORDER HISTORY ─────────────────────────────────────────────────────────
    @PostMapping("/order/history")
    public ResponseEntity<String> getOrderBook(@RequestBody DhanBaseRequest request) {
        log.info("Dhan order history | clientId={}", request.getClientId());
        JSONObject response = dhanApiService.getOrderBook(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── HOLDINGS ──────────────────────────────────────────────────────────────
    @PostMapping("/holdings")
    public ResponseEntity<String> getHoldings(@RequestBody DhanBaseRequest request) {
        log.info("Dhan holdings | clientId={}", request.getClientId());
        JSONObject response = dhanApiService.getHoldings(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── POSITIONS ─────────────────────────────────────────────────────────────
    @PostMapping("/positions")
    public ResponseEntity<String> getPositions(@RequestBody DhanBaseRequest request) {
        log.info("Dhan positions | clientId={}", request.getClientId());
        JSONObject response = dhanApiService.getPositions(request);
        return ResponseEntity.ok(response.toString());
    }
}
