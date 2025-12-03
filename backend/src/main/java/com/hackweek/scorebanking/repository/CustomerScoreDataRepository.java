package com.hackweek.scorebanking.repository;

import com.hackweek.scorebanking.entity.CustomerScoreData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerScoreDataRepository extends JpaRepository<CustomerScoreData, Long> {
  Optional<CustomerScoreData> findByCustomerId(Long customerId);
}