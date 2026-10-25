package com.py.demataccount.repository;

import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PyBrokerTokenRepository extends JpaRepository<PyBrokerToken, Long> {

    // Get token by broker account
    Optional<PyBrokerToken> findByBrokerAccount(PyBrokerAccountEntity brokerAccount);

    // Get token by broker account id
    Optional<PyBrokerToken> findByBrokerAccountId(Long brokerAccountId);

    // Get active token by broker account id
    Optional<PyBrokerToken> findByBrokerAccountIdAndIsActive(
            Long brokerAccountId, Boolean isActive);

    // Check if token already exists for broker account
    boolean existsByBrokerAccount(PyBrokerAccountEntity brokerAccount);

// In PyBrokerTokenRepository.java
// Get token by clientId via broker account
    Optional<PyBrokerToken> findByBrokerAccountClientId(String clientId);

// Get active token by clientId
    Optional<PyBrokerToken> findByBrokerAccountClientIdAndIsActive(
            String clientId, Boolean isActive);
}
