package com.hackweek.scorebanking.dto;

import java.math.BigDecimal;

public record CustomerScoreDto(
        String profession,
        BigDecimal monthlyIncome,
        Integer dependents,
        String educationLevel,
        String housingStatus
) {}