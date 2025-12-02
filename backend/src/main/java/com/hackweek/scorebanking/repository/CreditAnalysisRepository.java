package com.hackweek.scorebanking.repository;

import com.hackweek.scorebanking.entity.CreditAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditAnalysisRepository extends JpaRepository<CreditAnalysis, Long> {
}