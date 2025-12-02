package com.hackweek.scorebanking.domain;

import java.math.BigDecimal;

public enum RiskTier {
    EXCELLENT(new BigDecimal("0.0175"), 48),
    GOOD(new BigDecimal("0.0210"), 36),
    MEDIUM(new BigDecimal("0.0320"), 36),
    HIGH(new BigDecimal("0.0415"), 24),
    TERRIBLE(BigDecimal.ZERO, 0);

    private final BigDecimal interestRate;
    private final int maxInstallments;

    RiskTier(BigDecimal interestRate, int maxInstallments) {
        this.interestRate = interestRate;
        this.maxInstallments = maxInstallments;
    }
    public BigDecimal getInterestRate() { return interestRate; }
    public int getMaxInstallments() { return maxInstallments; }
}