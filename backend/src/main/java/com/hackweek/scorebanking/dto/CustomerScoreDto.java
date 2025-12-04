package com.hackweek.scorebanking.dto;

import java.math.BigDecimal;

public record CustomerScoreDto(
        String profession,
        BigDecimal monthlyIncome,
        Integer dependents,
        Integer monthsInCurrentJob,
        String educationLevel,
        String housingStatus
) {}