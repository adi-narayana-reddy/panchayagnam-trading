package com.py.demataccount.repository;


import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.demataccount.entity.PyBrokerSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PyBrokerSecretRepository extends JpaRepository<PyBrokerSecret, Long> {

    // Get secret by broker account
    Optional<PyBrokerSecret> findByBrokerAccount(PyBrokerAccountEntity brokerAccount);

    // Get secret by broker account id
    Optional<PyBrokerSecret> findByBrokerAccountId(Long brokerAccountId);

    // Check if secret already exists for broker account
    boolean existsByBrokerAccount(PyBrokerAccountEntity brokerAccount);
}
