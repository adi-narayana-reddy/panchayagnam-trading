//package com.py.controller;
//
//import com.py.dto.*;
//import com.py.entity.PySubscription;
//import com.py.service.PySubscriptionService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/subscription")
//@CrossOrigin(origins = "*")
//@Tag(name = "Subscription", description = "Subscription management APIs")
//public class PySubscriptionController {
//
//    private static final Logger log = LogManager.getLogger(PySubscriptionController.class);
//
//    @Autowired
//    private PySubscriptionService subscriptionService;
//
//    // ── USER: Get Subscription Status ─────────────────────────────────────────
//    @Operation(summary = "Get subscription status for a user")
//    @GetMapping("/status/{userId}")
//    public ResponseEntity<PyApiResponseDto<PySubscriptionResponseDto>> getStatus(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "CLIENT") String type) {
//        try {
//            PySubscription.Type subType = PySubscription.Type.valueOf(type.toUpperCase());
//            PySubscriptionResponseDto response = subscriptionService.getSubscriptionStatus(userId, subType);
//            return ResponseEntity.ok(PyApiResponseDto.success("Subscription status fetched", response));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(PyApiResponseDto.failure(e.getMessage()));
//        }
//    }
//
//    // ── USER: Raise Subscription Request ──────────────────────────────────────
//    @Operation(summary = "User raises subscription request with plan")
//    @PostMapping("/raise-request/{userId}")
//    public ResponseEntity<PyApiResponseDto<PyRoleUpgradeResponseDto>> raiseRequest(
//            @PathVariable Long userId,
//            @Valid @RequestBody PySubscriptionRequestDto requestDto) {
//        log.info("POST /subscription/raise-request | userId={}, type={}, planMonths={}",
//                userId, requestDto.getType(), requestDto.getPlanMonths());
//        try {
//            PyRoleUpgradeResponseDto response = subscriptionService.raiseSubscriptionRequest(userId, requestDto);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(PyApiResponseDto.success("Subscription request raised successfully", response));
//        } catch (RuntimeException e) {
//            log.error("Raise subscription request failed | userId={}: {}", userId, e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(PyApiResponseDto.failure(e.getMessage()));
//        }
//    }
//
//    // ── SUPER ADMIN: Get All Subscription Requests ────────────────────────────
//    @Operation(summary = "Super Admin views all subscription requests")
//    @GetMapping("/all-requests")
//    public ResponseEntity<PyApiResponseDto<PyRoleUpgradeRequestSummaryDto>> getAllRequests() {
//        try {
//            PyRoleUpgradeRequestSummaryDto response = subscriptionService.getAllSubscriptionRequests();
//            return ResponseEntity.ok(PyApiResponseDto.success("Requests fetched successfully", response));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(PyApiResponseDto.failure(e.getMessage()));
//        }
//    }
//
//    // ── SUPER ADMIN: Review Subscription Request ──────────────────────────────
//    @Operation(summary = "Super Admin approves or rejects subscription request")
//    @PutMapping("/review/{superAdminId}")
//    public ResponseEntity<PyApiResponseDto<PyRoleUpgradeResponseDto>> reviewRequest(
//            @PathVariable Long superAdminId,
//            @Valid @RequestBody PySubscriptionReviewDto reviewDto) {
//        log.info("PUT /subscription/review | superAdminId={}, requestId={}, status={}",
//                superAdminId, reviewDto.getRequestId(), reviewDto.getStatus());
//        try {
//            PyRoleUpgradeResponseDto response = subscriptionService
//                    .reviewSubscriptionRequest(superAdminId, reviewDto);
//            return ResponseEntity.ok(PyApiResponseDto.success("Request reviewed successfully", response));
//        } catch (RuntimeException e) {
//            log.error("Review subscription failed: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(PyApiResponseDto.failure(e.getMessage()));
//        }
//    }
//}
