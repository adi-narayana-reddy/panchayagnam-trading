package com.py.angel.controller;

import com.py.angel.dto.*;
import com.py.angel.service.AngelOneApisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
@RequestMapping("/api/smart")
@CrossOrigin(origins = "*")
@Tag(name = "Angel SmartConnect", description = "Angel Broking SmartConnect APIs for trading, market data, GTT, and more")
public class SmartConnectController {

    @Autowired
    private AngelOneApisService angelOneApisService;



    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> health() {
        JSONObject response = new JSONObject();
        response.put("status", "UP");
        response.put("message", "AngelOne Smart Connect application is deployed and running");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response.toString());
    }

    // ─── LOGIN ───────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody AngelOneLoginRequest request) {
        JSONObject response = angelOneApisService.login(request);
        return ResponseEntity.ok(response.toString());
    }

    // ─── RMS ─────────────────────────────────────────────────────────────────
    @PostMapping("/rms")
    public ResponseEntity<String> getRMS(@RequestBody AngelOneRMSRequest request) {
        JSONObject response = angelOneApisService.getRMS(request);
        return ResponseEntity.ok(response.toString());
    }

    // ─── ORDER HISTORY ───────────────────────────────────────────────────────
    @PostMapping("/order/history")
    public ResponseEntity<String> getOrderHistory(@RequestBody AngelOneOrderHistoryRequest request) {
        JSONObject response = angelOneApisService.getOrderHistory(request);
        return ResponseEntity.ok(response.toString());
    }

    // ─── PLACE ORDER ─────────────────────────────────────────────────────────
    @PostMapping("/order/place")
    public ResponseEntity<String> placeOrder(
            @RequestBody AngelOnePlaceOrderRequest request) {
        JSONObject response = angelOneApisService.placeOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ─── MODIFY ORDER ────────────────────────────────────────────────────────
    @PostMapping("/order/modify")
    public ResponseEntity<String> modifyOrder(
            @RequestBody AngelOneModifyOrderRequest request) {
        JSONObject response = angelOneApisService.modifyOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    // ─── CANCEL ORDER ────────────────────────────────────────────────────────
    @PostMapping("/order/cancel")
    public ResponseEntity<String> cancelOrder(
            @RequestBody AngelOneCancelOrderRequest request) {
        JSONObject response = angelOneApisService.cancelOrder(request);
        return ResponseEntity.ok(response.toString());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        // silently ignore favicon.ico and other missing static resources
        return ResponseEntity.notFound().build();
    }
    // ─── LTP ─────────────────────────────────────────────────────────────────

    @PostMapping("/ltp")
    public ResponseEntity<String> getLTP(@RequestBody AngelOneLtpRequest request) {
        JSONObject response = angelOneApisService.getLTP(request);
        return ResponseEntity.ok(response.toString());
    }

    // ─── CONVERT POSITION ────────────────────────────────────────────────────
    @PostMapping("/convertPosition")
    public ResponseEntity<String> convertPosition(
            @RequestBody AngelOneConvertPositionRequest request) {
        JSONObject response = angelOneApisService.convertPosition(request);
        return ResponseEntity.ok(response.toString());
    }

    // ─── COMMENTED ENDPOINTS (kept for reference) ────────────────────────────
    // ... (all previously commented-out endpoints remain unchanged below)
}
