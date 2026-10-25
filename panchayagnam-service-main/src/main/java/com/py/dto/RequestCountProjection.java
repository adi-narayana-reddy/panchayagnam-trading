package com.py.dto;


/**
 * Projection for a single GROUP BY count query on PyRoleUpgradeRequest.
 * Returns status + requestType + count in one query.
 */
public interface RequestCountProjection {
    String getStatus();
    String getRequestType();
    long   getCount();
}
