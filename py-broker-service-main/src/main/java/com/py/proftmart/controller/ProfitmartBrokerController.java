package com.py.proftmart.controller;

import com.py.profitmart.dto.ProfitmartBaseRequest;
import com.py.profitmart.dto.*;
import com.py.profitmart.service.ProfitmartBrokerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profitmart")
@CrossOrigin(origins = "*")
public class ProfitmartBrokerController {

    private static final Logger log = LoggerFactory.getLogger(ProfitmartBrokerController.class);

    @Autowired
    private ProfitmartBrokerService profitmartBrokerService;

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody ProfitmartLoginRequest request) {
        log.info("Profitmart login | userId={}", request.getUserId());
        JSONObject response = profitmartBrokerService.login(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody ProfitmartBaseRequest request) {
        log.info("Profitmart logout | userId={}", request.getUserId());
        JSONObject response = profitmartBrokerService.logout(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── ORDER BOOK ────────────────────────────────────────────────────────────
    @PostMapping("/order/book")
    public ResponseEntity<String> getOrderBook(@RequestBody ProfitmartBaseRequest request) {
        log.info("Profitmart order book | userId={}", request.getUserId());
        JSONArray response = profitmartBrokerService.getOrderBook(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── TRADE BOOK ────────────────────────────────────────────────────────────
    @PostMapping("/order/trades")
    public ResponseEntity<String> getTradeBook(@RequestBody ProfitmartBaseRequest request) {
        log.info("Profitmart trade book | userId={}", request.getUserId());
        JSONArray response = profitmartBrokerService.getTradeBook(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── SINGLE ORDER HISTORY ──────────────────────────────────────────────────
    @PostMapping("/order/history")
    public ResponseEntity<String> getOrderHistory(
            @RequestBody ProfitmartOrderHistoryRequest request) {
        log.info("Profitmart order history | orderNo={}", request.getOrderNo());
        JSONArray response = profitmartBrokerService.getOrderHistory(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── PLACE ORDER ───────────────────────────────────────────────────────────
    @PostMapping("/order/place")
    public ResponseEntity<String> placeOrder(
            @RequestBody ProfitmartPlaceOrderRequest request) {
        log.info("Profitmart place order | exchange={} tsym={} side={}",
                request.getExchange(), request.getTradingSymbol(), request.getBuyOrSell());
        JSONObject response = profitmartBrokerService.placeOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── MODIFY ORDER ──────────────────────────────────────────────────────────
    @PostMapping("/order/modify")
    public ResponseEntity<String> modifyOrder(
            @RequestBody ProfitmartModifyOrderRequest request) {
        log.info("Profitmart modify order | orderNo={}", request.getOrderNo());
        JSONObject response = profitmartBrokerService.modifyOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── CANCEL ORDER ──────────────────────────────────────────────────────────
    @PostMapping("/order/cancel")
    public ResponseEntity<String> cancelOrder(
            @RequestBody ProfitmartCancelOrderRequest request) {
        log.info("Profitmart cancel order | orderNo={}", request.getOrderNo());
        JSONObject response = profitmartBrokerService.cancelOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── HOLDINGS ──────────────────────────────────────────────────────────────
    @PostMapping("/holdings")
    public ResponseEntity<String> getHoldings(@RequestBody ProfitmartBaseRequest request) {
        log.info("Profitmart holdings | userId={}", request.getUserId());
        JSONArray response = profitmartBrokerService.getHoldings(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── POSITIONS ─────────────────────────────────────────────────────────────
    @PostMapping("/positions")
    public ResponseEntity<String> getPositions(@RequestBody ProfitmartBaseRequest request) {
        log.info("Profitmart positions | userId={}", request.getUserId());
        JSONArray response = profitmartBrokerService.getPositions(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── LIMITS (MARGIN) ───────────────────────────────────────────────────────
    @PostMapping("/limits")
    public ResponseEntity<String> getLimits(@RequestBody ProfitmartBaseRequest request) {
        log.info("Profitmart limits | userId={}", request.getUserId());
        JSONObject response = profitmartBrokerService.getLimits(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── SEARCH SCRIP ──────────────────────────────────────────────────────────
    @PostMapping("/search")
    public ResponseEntity<String> searchScrip(@RequestBody ProfitmartSearchRequest request) {
        log.info("Profitmart search | exchange={} text={}",
                request.getExchange(), request.getSearchText());
        JSONObject response = profitmartBrokerService.searchScrip(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── QUOTES (LTP + depth) ──────────────────────────────────────────────────
    @PostMapping("/quotes")
    public ResponseEntity<String> getQuotes(@RequestBody ProfitmartQuotesRequest request) {
        log.info("Profitmart quotes | exchange={} token={}",
                request.getExchange(), request.getToken());
        JSONObject response = profitmartBrokerService.getQuotes(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── SECURITY INFO ─────────────────────────────────────────────────────────
    @PostMapping("/security-info")
    public ResponseEntity<String> getSecurityInfo(
            @RequestBody ProfitmartQuotesRequest request) {
        log.info("Profitmart security info | exchange={} token={}",
                request.getExchange(), request.getToken());
        JSONObject response = profitmartBrokerService.getSecurityInfo(request);
        return ResponseEntity.ok(response.toString());
    }
}