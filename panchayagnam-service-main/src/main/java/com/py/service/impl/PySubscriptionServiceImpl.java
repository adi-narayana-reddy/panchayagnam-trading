package com.py.service.impl;

import com.py.dto.*;
import com.py.entity.*;
import com.py.repo.*;
import com.py.service.PySubscriptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PySubscriptionServiceImpl implements PySubscriptionService {

    private static final Logger logger = LogManager.getLogger(PySubscriptionServiceImpl.class);

    @Autowired
    private PySubscriptionRepository subscriptionRepository;

    @Autowired
    private PyUserRepository userRepository;

    @Autowired
    private PyRoleUpgradeRequestRepository roleUpgradeRequestRepository;

    // ── CREATE TRIAL ON REGISTRATION ─────────────────────────────────────────
    @Override
    @Transactional
    public PySubscriptionResponseDto createTrialSubscription(Long userId) {
        logger.info("Creating CLIENT trial subscription | userId={}", userId);

        PyUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Avoid duplicate trial
        boolean trialExists = subscriptionRepository.existsByPyUserAndTypeAndStatus(
                user, PySubscription.Type.CLIENT, PySubscription.Status.TRIAL);
        if (trialExists) {
            throw new RuntimeException("Trial subscription already exists for this user");
        }

        LocalDateTime now = LocalDateTime.now();

        PySubscription subscription = new PySubscription();
        subscription.setPyUser(user);
        subscription.setType(PySubscription.Type.CLIENT);
        subscription.setIsTrial(true);
        subscription.setStatus(PySubscription.Status.TRIAL);
        subscription.setStartDate(now);
        subscription.setEndDate(now.plusMonths(1)); // 1 month free trial

        PySubscription saved = subscriptionRepository.save(subscription);
        logger.info("Trial subscription created | subscriptionId={}, userId={}", saved.getId(), userId);

        return mapToResponseDto(saved);
    }

    // ── GET SUBSCRIPTION STATUS (used in login) ───────────────────────────────
    @Override
    public PySubscriptionResponseDto getSubscriptionStatus(Long userId, PySubscription.Type type) {
        logger.info("Getting subscription status | userId={}, type={}", userId, type);

        PyUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PySubscription subscription = subscriptionRepository
                .findTopByPyUserAndTypeOrderByCreatedAtDesc(user, type)
                .orElse(null);

        if (subscription == null) {
            return null;
        }

        // Auto-expire if endDate passed
        if (subscription.getEndDate().isBefore(LocalDateTime.now())
                && subscription.getStatus() != PySubscription.Status.EXPIRED) {
            subscription.setStatus(PySubscription.Status.EXPIRED);
            subscriptionRepository.save(subscription);
        }

        return mapToResponseDto(subscription);
    }

    // ── RAISE SUBSCRIPTION REQUEST ────────────────────────────────────────────
    @Override
    @Transactional
    public PyRoleUpgradeResponseDto raiseSubscriptionRequest(Long userId, PySubscriptionRequestDto requestDto) {
        logger.info("Raise subscription request | userId={}, type={}, planMonths={}",
                userId, requestDto.getType(), requestDto.getPlanMonths());

        PyUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate plan months
        if (!List.of(1, 2, 12).contains(requestDto.getPlanMonths())) {
            throw new RuntimeException("Invalid plan. Choose 1, 2, or 12 months");
        }

        // Validate type
        PyRoleUpgradeRequest.RequestType requestType;
        try {
            requestType = PyRoleUpgradeRequest.RequestType.valueOf(requestDto.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid type. Must be CLIENT or GROUP");
        }

        // Check if pending request already exists
        boolean hasPending = roleUpgradeRequestRepository
                .existsByPyUserAndStatusAndRequestType(user,
                        PyRoleUpgradeRequest.Status.PENDING, requestType);
        if (hasPending) {
            throw new RuntimeException("You already have a pending subscription request");
        }

        PyRoleUpgradeRequest request = new PyRoleUpgradeRequest();
        request.setPyUser(user);
        request.setRequestedRole(requestType == PyRoleUpgradeRequest.RequestType.GROUP ? "ADMIN" : "USER");
        request.setRequestType(requestType);
        request.setPlanMonths(requestDto.getPlanMonths());
        request.setStatus(PyRoleUpgradeRequest.Status.PENDING);

        PyRoleUpgradeRequest saved = roleUpgradeRequestRepository.save(request);
        logger.info("Subscription request saved | requestId={}, userId={}", saved.getId(), userId);

        return mapRequestToResponseDto(saved);
    }

    // ── GET ALL SUBSCRIPTION REQUESTS ────────────────────────────────────────
    @Override
    public PyRoleUpgradeRequestSummaryDto getAllSubscriptionRequests() {

        // FIX 3: use correct enum type PyRoleUpgradeRequest.RequestType (not PySubscription.Type)
        List<PyRoleUpgradeRequest.RequestType> subscriptionTypes = List.of(
                PyRoleUpgradeRequest.RequestType.CLIENT,
                PyRoleUpgradeRequest.RequestType.GROUP);

        // FIX 1: pending — repo method takes single RequestType, so fetch CLIENT and GROUP separately and merge
        List<PyRoleUpgradeRequest> pendingClient = roleUpgradeRequestRepository
                .findByStatusAndRequestType(PyRoleUpgradeRequest.Status.PENDING,
                        PyRoleUpgradeRequest.RequestType.CLIENT);

        List<PyRoleUpgradeRequest> pendingGroup = roleUpgradeRequestRepository
                .findByStatusAndRequestType(PyRoleUpgradeRequest.Status.PENDING,
                        PyRoleUpgradeRequest.RequestType.GROUP);

        List<PyRoleUpgradeRequest> pending = new java.util.ArrayList<>();
        pending.addAll(pendingClient);
        pending.addAll(pendingGroup);

        // FIX 2: use findByStatusAndRequestTypeIn (now added to repo) with correct enum type
        List<PyRoleUpgradeRequest> approved = roleUpgradeRequestRepository
                .findByStatusAndRequestTypeIn(PyRoleUpgradeRequest.Status.APPROVED, subscriptionTypes);

        List<PyRoleUpgradeRequest> rejected = roleUpgradeRequestRepository
                .findByStatusAndRequestTypeIn(PyRoleUpgradeRequest.Status.REJECTED, subscriptionTypes);

        PyRoleUpgradeRequestSummaryDto summary = new PyRoleUpgradeRequestSummaryDto();
        summary.setPendingCount(pending.size());
        summary.setApprovedCount(approved.size());
        summary.setRejectedCount(rejected.size());
        summary.setTotalCount(pending.size() + approved.size() + rejected.size());
        summary.setPending(pending.stream().map(this::mapRequestToResponseDto).collect(Collectors.toList()));
        summary.setApproved(approved.stream().map(this::mapRequestToResponseDto).collect(Collectors.toList()));
        summary.setRejected(rejected.stream().map(this::mapRequestToResponseDto).collect(Collectors.toList()));

        return summary;
    }

    // ── REVIEW SUBSCRIPTION REQUEST (APPROVE / REJECT) ────────────────────────
    @Override
    @Transactional
    public PyRoleUpgradeResponseDto reviewSubscriptionRequest(Long superAdminId, PySubscriptionReviewDto reviewDto) {
        logger.info("Review subscription request | superAdminId={}, requestId={}, status={}",
                superAdminId, reviewDto.getRequestId(), reviewDto.getStatus());

        PyUser superAdmin = userRepository.findById(superAdminId)
                .orElseThrow(() -> new RuntimeException("Super Admin not found"));

        if (superAdmin.getRole() != PyUser.Role.SUPER_ADMIN) {
            throw new RuntimeException("Only Super Admin can review subscription requests");
        }

        PyRoleUpgradeRequest request = roleUpgradeRequestRepository.findById(reviewDto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != PyRoleUpgradeRequest.Status.PENDING) {
            throw new RuntimeException("Request already " + request.getStatus().name());
        }

        if (!reviewDto.getStatus().equalsIgnoreCase("APPROVED")
                && !reviewDto.getStatus().equalsIgnoreCase("REJECTED")) {
            throw new RuntimeException("Status must be APPROVED or REJECTED");
        }

        if (reviewDto.getStatus().equalsIgnoreCase("REJECTED")
                && (reviewDto.getReason() == null || reviewDto.getReason().isBlank())) {
            throw new RuntimeException("Reason is required when rejecting");
        }

        if (reviewDto.getStatus().equalsIgnoreCase("APPROVED")) {
            request.setStatus(PyRoleUpgradeRequest.Status.APPROVED);

            // Save subscription
            LocalDateTime now = LocalDateTime.now();
            PySubscription subscription = new PySubscription();
            subscription.setPyUser(request.getPyUser());
            subscription.setType(PySubscription.Type.valueOf(request.getRequestType().name()));
            subscription.setPlanMonths(request.getPlanMonths());
            subscription.setStartDate(now);
            subscription.setEndDate(now.plusMonths(request.getPlanMonths()));
            subscription.setStatus(PySubscription.Status.ACTIVE);
            subscription.setIsTrial(false);
            subscriptionRepository.save(subscription);

            // FIX 4: was comparing PyRoleUpgradeRequest.RequestType with PySubscription.Type.GROUP
            // — different enum types, always evaluated to false, ADMIN role was never assigned.
            // Fixed: compare with PyRoleUpgradeRequest.RequestType.GROUP
            if (request.getRequestType() == PyRoleUpgradeRequest.RequestType.GROUP) {
                PyUser user = request.getPyUser();
                user.setRole(PyUser.Role.ADMIN);
                userRepository.save(user);
                logger.info("User role upgraded to ADMIN | userId={}", user.getId());
            }

            logger.info("Subscription approved and created | userId={}, planMonths={}",
                    request.getPyUser().getId(), request.getPlanMonths());
        } else {
            request.setStatus(PyRoleUpgradeRequest.Status.REJECTED);
            request.setReason(reviewDto.getReason());
        }

        request.setReviewedBy(superAdmin);
        request.setReviewedAt(LocalDateTime.now());
        PyRoleUpgradeRequest updated = roleUpgradeRequestRepository.save(request);

        return mapRequestToResponseDto(updated);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private PySubscriptionResponseDto mapToResponseDto(PySubscription sub) {
        PySubscriptionResponseDto dto = new PySubscriptionResponseDto();
        dto.setSubscriptionId(sub.getId());
        dto.setUserId(sub.getPyUser().getId());
        dto.setFullName(sub.getPyUser().getFullName());
        dto.setType(sub.getType().name());
        dto.setPlanMonths(sub.getPlanMonths());
        dto.setStartDate(sub.getStartDate());
        dto.setEndDate(sub.getEndDate());
        dto.setStatus(sub.getStatus().name());
        dto.setIsTrial(sub.getIsTrial());

        long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), sub.getEndDate());
        dto.setExpiresInDays(Math.max(daysLeft, 0));
        dto.setExpiredFlag(sub.getEndDate().isBefore(LocalDateTime.now()));

        return dto;
    }

    private PyRoleUpgradeResponseDto mapRequestToResponseDto(PyRoleUpgradeRequest req) {
        PyRoleUpgradeResponseDto dto = new PyRoleUpgradeResponseDto();
        dto.setRequestId(req.getId());
        dto.setPyId(req.getPyUser().getId());
        dto.setFullName(req.getPyUser().getFullName());
        dto.setPhoneNumber(req.getPyUser().getPhoneNumber());
        dto.setEmail(req.getPyUser().getEmail());
        dto.setRequestedRole(req.getRequestedRole());
        dto.setStatus(req.getStatus().name());
        dto.setReason(req.getReason());
        dto.setCreatedAt(req.getCreatedAt());
        if (req.getReviewedBy() != null) {
            dto.setReviewedById(req.getReviewedBy().getId());
            dto.setReviewedByName(req.getReviewedBy().getFullName());
            dto.setReviewedAt(req.getReviewedAt());
        }
        return dto;
    }
}
