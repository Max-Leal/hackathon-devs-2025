package com.hackweek.scorebanking.dto;

import com.hackweek.scorebanking.domain.RiskTier;
import java.math.BigDecimal;

public record ScoreResultResponse(
        Long customerId,
        int score,
        RiskTier riskTier,
        BigDecimal approvedLimit,
        BigDecimal maxMonthlyInstallment,
        Integer maxInstallments,
        BigDecimal interestRate
) {}
