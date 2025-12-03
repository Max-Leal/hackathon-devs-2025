package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.entity.Customer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CreditEngineService {

  public int calculateScore(Customer customer) {
    char lastDigit = customer.getCpf().charAt(customer.getCpf().length() - 1);
    int externalScoreMock = (lastDigit == '1' || lastDigit == '2') ? 200 :
            (lastDigit == '9' ? 900 : 600);
    int externalDebtPenalty = (lastDigit == '1' || lastDigit == '2') ? 60 : 0;
    int externalScorePoints = (externalScoreMock / 100) * 5;

    return Math.max(0, Math.min(externalScorePoints - externalDebtPenalty, 100));
  }

  public RiskTier determineRisk(int score) {
    if (score >= 90) return RiskTier.EXCELLENT;
    if (score >= 70) return RiskTier.GOOD;
    if (score >= 55) return RiskTier.MEDIUM;
    if (score >= 30) return RiskTier.HIGH;
    return RiskTier.TERRIBLE;
  }

  public BigDecimal calculateApprovedLimit(BigDecimal monthlyIncome, RiskTier risk) {
    if (risk == RiskTier.TERRIBLE) return BigDecimal.ZERO;

    BigDecimal maxInstallmentValue = monthlyIncome.multiply(new BigDecimal("0.30"));
    double pmt = maxInstallmentValue.doubleValue();
    double rate = risk.getInterestRate().doubleValue();
    int months = risk.getMaxInstallments();

    if (rate == 0) return maxInstallmentValue.multiply(BigDecimal.valueOf(months));

    double pv = pmt * ((1 - Math.pow(1 + rate, -months)) / rate);
    return BigDecimal.valueOf(pv).setScale(2, RoundingMode.HALF_DOWN);
  }
}