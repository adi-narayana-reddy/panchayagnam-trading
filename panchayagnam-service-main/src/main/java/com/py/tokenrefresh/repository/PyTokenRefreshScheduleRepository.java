package com.py.tokenrefresh.repository;

import com.py.common.enums.BrokerType;
import com.py.tokenrefresh.entity.PyTokenRefreshScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PyTokenRefreshScheduleRepository
        extends JpaRepository<PyTokenRefreshScheduleEntity, Long> {

    Optional<PyTokenRefreshScheduleEntity> findByBrokerType(BrokerType brokerType);
}