package com.py.dto;


/**
 * Projection for a single GROUP BY count query on PySubscription.status.
 */
public interface SubscriptionCountProjection {
    String getStatus();
    long   getCount();
}
