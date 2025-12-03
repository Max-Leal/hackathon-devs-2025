package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.entity.CustomerScoreData; // Importante!
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CreditEngineService {

    public int calculateScore(CustomerScoreData data) {

        // deixei aqui como counter de bots criando conta como loucos
        if (Boolean.TRUE.equals(data.getFraudSuspicion())) {
            return 0;
        }

        // Idade influência nos pontos (Exemplo: Ganha pontos se for mais velho, até max 20)
        // Se age for null, considera 18
        int age = data.getAge() != null ? data.getAge() : 18;
        int agePoints = (age > 18) ? Math.min((age - 18) * 2, 20) : 0;

        // 3. Pontos por Renda (Exemplo: 1 ponto a cada 500 reais, max 30)
        BigDecimal income = data.getMonthlyIncome() != null ? data.getMonthlyIncome() : BigDecimal.ZERO;
        int incomePoints = income.divide(new BigDecimal("500"), RoundingMode.FLOOR).intValue();
        incomePoints = Math.min(incomePoints, 30);

        // 4. Pontos pelo Score Externo (Serasa Mockado)
        // O mock preenche o creditScore, se for null considera 0
        int serasaScore = data.getCreditScore() != null ? data.getCreditScore() : 0;
        int externalScorePoints = (serasaScore / 100) * 5; // Max 50 pontos

        // 5. Penalidade por Dívida Externa
        int debtPenalty = 0;
        if (data.getExternalDebt() != null && 
            data.getExternalDebt().compareTo(new BigDecimal("1000")) > 0) {
            debtPenalty = 40; // Perde 40 pontos se dever muito
        }

        // CÁLCULO FINAL
        int totalScore = 30 + agePoints + incomePoints + externalScorePoints - debtPenalty;

        return Math.max(0, Math.min(100, totalScore));
    }

    public RiskTier determineRisk(int score) {
        if (score >= 90) return RiskTier.EXCELLENT;
        if (score >= 70) return RiskTier.GOOD;
        if (score >= 55) return RiskTier.MEDIUM;
        if (score >= 30) return RiskTier.HIGH;
        return RiskTier.TERRIBLE;
    }

    public BigDecimal calculateApprovedLimit(BigDecimal monthlyIncome, RiskTier risk) {
        if (risk == RiskTier.TERRIBLE || monthlyIncome == null) return BigDecimal.ZERO;

        BigDecimal maxInstallmentValue = monthlyIncome.multiply(new BigDecimal("0.30"));
        double pmt = maxInstallmentValue.doubleValue();
        double rate = risk.getInterestRate().doubleValue();
        int months = risk.getMaxInstallments();

        if (rate == 0) return maxInstallmentValue.multiply(BigDecimal.valueOf(months));

        double pv = pmt * ((1 - Math.pow(1 + rate, -months)) / rate);
        return BigDecimal.valueOf(pv).setScale(2, RoundingMode.HALF_DOWN);
    }
}