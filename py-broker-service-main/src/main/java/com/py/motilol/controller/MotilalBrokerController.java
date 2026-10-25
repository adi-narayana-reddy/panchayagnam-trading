package com.py.motilol.controller;

import com.py.motilol.dto.MotilalBaseRequest;
import com.py.motilol.dto.MotilalCancelOrderRequest;
import com.py.motilol.dto.MotilalConvertPositionRequest;
import com.py.motilol.dto.MotilalLoginRequest;
import com.py.motilol.dto.MotilalLtpRequest;
import com.py.motilol.dto.MotilalModifyOrderRequest;
import com.py.motilol.dto.MotilalPlaceOrderRequest;
import com.py.motilol.dto.MotilalTokenRequest;
import com.py.motilol.service.MotilalBrokerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/motilal")
@CrossOrigin(origins = "*")
public class MotilalBrokerController {

    private static final Logger log = LoggerFactory.getLogger(MotilalBrokerController.class);

    @Autowired
    private MotilalBrokerService motilalBrokerService;

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MotilalLoginRequest request) {
        log.info("Motilal login | userId={}", request.getUserId());
        JSONObject response = motilalBrokerService.login(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── GET ACCESS TOKEN ──────────────────────────────────────────────────────
    @PostMapping("/token")
    public ResponseEntity<String> getAccessToken(@RequestBody MotilalTokenRequest request) {
        log.info("Motilal get access token");
        JSONObject response = motilalBrokerService.getAccessToken(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── RMS ───────────────────────────────────────────────────────────────────
    @PostMapping("/rms")
    public ResponseEntity<String> getRMS(@RequestBody MotilalBaseRequest request) {
        log.info("Motilal RMS");
        JSONObject response = motilalBrokerService.getRMS(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── ORDER HISTORY ─────────────────────────────────────────────────────────
    @PostMapping("/order/history")
    public ResponseEntity<String> getOrderBook(@RequestBody MotilalBaseRequest request) {
        log.info("Motilal order history");
        JSONObject response = motilalBrokerService.getOrderBook(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── LTP ───────────────────────────────────────────────────────────────────
    @PostMapping("/ltp")
    public ResponseEntity<String> getLtp(@RequestBody MotilalLtpRequest request) {
        log.info("Motilal LTP | exchange={} scripCode={}",
                request.getExchange(), request.getScripCode());
        JSONObject response = motilalBrokerService.getLtp(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── PLACE ORDER ───────────────────────────────────────────────────────────
    @PostMapping("/order/place")
    public ResponseEntity<String> placeOrder(@RequestBody MotilalPlaceOrderRequest request) {
        log.info("Motilal place order | exchange={} token={} side={}",
                request.getExchange(), request.getSymbolToken(), request.getBuyOrSell());
        JSONObject response = motilalBrokerService.placeOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── MODIFY ORDER ──────────────────────────────────────────────────────────
    @PostMapping("/order/modify")
    public ResponseEntity<String> modifyOrder(@RequestBody MotilalModifyOrderRequest request) {
        log.info("Motilal modify order | uniqueOrderId={}", request.getUniqueOrderId());
        JSONObject response = motilalBrokerService.modifyOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── CANCEL ORDER ──────────────────────────────────────────────────────────
    @PostMapping("/order/cancel")
    public ResponseEntity<String> cancelOrder(@RequestBody MotilalCancelOrderRequest request) {
        log.info("Motilal cancel order | uniqueOrderId={}", request.getUniqueOrderId());
        JSONObject response = motilalBrokerService.cancelOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── HOLDINGS ──────────────────────────────────────────────────────────────
    @PostMapping("/holdings")
    public ResponseEntity<String> getHoldings(@RequestBody MotilalBaseRequest request) {
        log.info("Motilal holdings");
        JSONObject response = motilalBrokerService.getHoldings(request);
        return ResponseEntity.ok(response.toString());
    }

    // ── POSITIONS ─────────────────────────────────────────────────────────────
    @PostMapping("/positions")
    public ResponseEntity<String> getPositions(@RequestBody MotilalBaseRequest request) {
        log.info("Motilal positions");
        JSONObject response = motilalBrokerService.getPositions(request);
        return ResponseEntity.ok(response.toString());
    }
    // ── CONVERT POSITION ──────────────────────────────────────────────────────

    @PostMapping("/position/convert")
    public ResponseEntity<String> convertPosition(
            @RequestBody MotilalConvertPositionRequest request) {
        log.info("Motilal convert position | exchange={} scripcode={} {} -> {}",
                request.getExchange(), request.getScripcode(),
                request.getOldproduct(), request.getNewproduct());
        JSONObject response = motilalBrokerService.convertPosition(request);
        return ResponseEntity.ok(response.toString());
    }
}
