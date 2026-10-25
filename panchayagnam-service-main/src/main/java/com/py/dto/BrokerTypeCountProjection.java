package com.py.dto;

/**
 * Projection for a single GROUP BY count query on PyBrokerAccount.brokerType.
 * Used by PyBrokerAccountRepository.countAllByBrokerType().
 */
public interface BrokerTypeCountProjection {
    String getBrokerType();
    long   getCount();
}
