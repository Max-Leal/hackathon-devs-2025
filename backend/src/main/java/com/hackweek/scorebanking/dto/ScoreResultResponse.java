package com.hackweek.scorebanking.dto;

import com.hackweek.scorebanking.domain.RiskTier;
import java.math.BigDecimal;
import java.util.List;

public record ScoreResultResponse(
        Long customerId,
        int score,
        RiskTier riskTier,
        BigDecimal approvedLimit,
        BigDecimal maxMonthlyInstallment,
        Integer maxInstallments,
        BigDecimal interestRate,
        //List<String> feedback, ja adiciono
        List<ScoreBreakdown> scoreAudit
) {}
