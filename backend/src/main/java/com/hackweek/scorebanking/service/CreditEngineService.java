package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.entity.Customer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CreditEngineService {

    public int calculateScore(Customer customer) {
        // Lógica do cálculo
//        int agePoints = (customer.getAge() > 18) ? Math.min((customer.getAge() - 18) * 2, 20) : 0;
//        int incomePoints = customer.getMonthlyIncome().divide(new BigDecimal("500"), RoundingMode.FLOOR).intValue();
//        incomePoints = Math.min(incomePoints, 30);

        char lastDigit = customer.getCpf().charAt(customer.getCpf().length() - 1);
        int externalScoreMock = (lastDigit == '1' || lastDigit == '2') ? 200 : (lastDigit == '9' ? 900 : 600);
        int externalDebtPenalty = (lastDigit == '1' || lastDigit == '2') ? 60 : 0;
        int externalScorePoints = (externalScoreMock / 100) * 5;

//        int totalScore = 30 + agePoints + incomePoints + externalScorePoints - externalDebtPenalty;
        //if (totalScore > 100) return 100;
      //if (totalScore < 0) return 0;
      return 0;
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

        if (rate == 0) return maxInstallmentValue.multiply(new BigDecimal(months));

        double pv = pmt * ( (1 - Math.pow(1 + rate, -months)) / rate );
        return new BigDecimal(pv).setScale(2, RoundingMode.HALF_DOWN);
    }
}