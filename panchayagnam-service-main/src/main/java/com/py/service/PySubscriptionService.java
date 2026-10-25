package com.py.service;

import com.py.dto.*;
import com.py.entity.PySubscription;

public interface PySubscriptionService {

    // Called on user registration — create CLIENT trial
    PySubscriptionResponseDto createTrialSubscription(Long userId);

    // Get subscription status for login response
    PySubscriptionResponseDto getSubscriptionStatus(Long userId, PySubscription.Type type);

    // User raises subscription request
    PyRoleUpgradeResponseDto raiseSubscriptionRequest(Long userId, PySubscriptionRequestDto requestDto);

    // Super Admin views all subscription requests
    PyRoleUpgradeRequestSummaryDto getAllSubscriptionRequests();

    // Super Admin approves or rejects
    PyRoleUpgradeResponseDto reviewSubscriptionRequest(Long superAdminId, PySubscriptionReviewDto reviewDto);
}
