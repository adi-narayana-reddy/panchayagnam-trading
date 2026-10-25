package com.py.ipconfig.repository;

import com.py.ipconfig.entity.PyBrokerIpConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PyBrokerIpConfigRepository extends JpaRepository<PyBrokerIpConfigEntity, Long> {

    Optional<PyBrokerIpConfigEntity> findByIpAddress(String ipAddress);

}
