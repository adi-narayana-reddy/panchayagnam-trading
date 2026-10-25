package com.py.dto;


/**
 * Projection for a single GROUP BY count query on PyUser.role.
 * Used by PyUserRepository.countAllByRole().
 */
public interface RoleCountProjection {
    String getRole();
    long   getCount();
}
