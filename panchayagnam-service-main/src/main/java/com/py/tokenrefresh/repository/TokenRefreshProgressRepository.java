package com.py.tokenrefresh.repository;


import com.py.tokenrefresh.entity.TokenRefreshProgressEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRefreshProgressRepository
        extends JpaRepository<TokenRefreshProgressEntity, Long> {

    List<TokenRefreshProgressEntity> findByStatus(String status);

    List<TokenRefreshProgressEntity> findByRefreshDateBetween(
            LocalDateTime from, LocalDateTime to);
}