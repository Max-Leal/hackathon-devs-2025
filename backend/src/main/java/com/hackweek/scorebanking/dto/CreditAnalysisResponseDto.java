package com.hackweek.scorebanking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditAnalysisResponseDto(
        Long analysisId,
        Boolean approved,
        Integer score,
        BigDecimal approvedLimit,
        BigDecimal approvedInterestRate,
        Integer maxInstallments,
        String message,
        LocalDateTime date
) {}