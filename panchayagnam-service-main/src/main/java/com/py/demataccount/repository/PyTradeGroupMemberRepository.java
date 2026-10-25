package com.py.demataccount.repository;

import com.py.demataccount.entity.PyBrokerAccountEntity;
import com.py.entity.PyTradeGroupEntity;
import com.py.entity.PyTradeGroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PyTradeGroupMemberRepository extends JpaRepository<PyTradeGroupMemberEntity, Long> {

    // Get all members of a group
    List<PyTradeGroupMemberEntity> findByTradeGroup(PyTradeGroupEntity tradeGroup);

    // Get all groups a broker account belongs to
    List<PyTradeGroupMemberEntity> findByBrokerAccount(PyBrokerAccountEntity brokerAccount);

    // Check if broker already exists in this group
    boolean existsByTradeGroupAndBrokerAccount(
            PyTradeGroupEntity tradeGroup, PyBrokerAccountEntity brokerAccount);

    // Delete member from group
    void deleteByTradeGroupAndBrokerAccount(
            PyTradeGroupEntity tradeGroup, PyBrokerAccountEntity brokerAccount);

    // Count members in a group
    long countByTradeGroup(PyTradeGroupEntity tradeGroup);

    // Get only ACTIVE members of a group
    List<PyTradeGroupMemberEntity> findByTradeGroupAndIsLinked(
            PyTradeGroupEntity tradeGroup, Boolean isLinked);

// Count only active members
    long countByTradeGroupAndIsLinked(PyTradeGroupEntity tradeGroup, Boolean isLinked);

// Soft delete — find member to deactivate
    Optional<PyTradeGroupMemberEntity> findByTradeGroupAndBrokerAccount(
            PyTradeGroupEntity tradeGroup, PyBrokerAccountEntity brokerAccount);
}
