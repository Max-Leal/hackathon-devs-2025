package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.entity.CustomerScoreData; // Importante!
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CreditEngineService {

    public int calculateScore(CustomerScoreData data) {
        // 1. FRAUDE = ZERO (Morte Súbita)
        if (Boolean.TRUE.equals(data.getFraudSuspicion())) {
            return 0;
        }

        // 2. IDADE < 18 = ZERO (Regra Legal)
        int age = data.getAge() != null ? data.getAge() : 0;
        if (age < 18) {
            return 0;
        }

        // 3. PONTOS POR RENDA (Aumentamos o peso)
        // Regra: 1 ponto a cada R$ 500. Teto subiu para 40 pontos.
        BigDecimal income = data.getMonthlyIncome() != null ? data.getMonthlyIncome() : BigDecimal.ZERO;
        int incomePoints = income.divide(new BigDecimal("500"), RoundingMode.FLOOR).intValue();
        incomePoints = Math.min(incomePoints, 40); // Max 40 (antes era 30)

        // 4. PONTOS POR SCORE EXTERNO (Serasa)
        int serasaScore = data.getCreditScore() != null ? data.getCreditScore() : 0;
        int externalScorePoints = (serasaScore / 100) * 5; 

        // 4. pontos por profissão
        int professionPoints = calculateProfessionPoints(data.getProfession());

        // 5. PENALIDADE POR DÍVIDA
        // Regra: Se deve mais de R$ 1.000, perde 40 pontos na cabeça.
        int debtPenalty = 0;
        if (data.getExternalDebt() != null && 
            data.getExternalDebt().compareTo(new BigDecimal("1000")) > 0) {
            debtPenalty = 40;
        }

        // 6. CÁLCULO FINAL (Ajuste Fino)
        // Base Score caiu para 25 (antes era 50).
        int baseScore = 25;
        
        int totalScore = baseScore + incomePoints + externalScorePoints + professionPoints - debtPenalty;

        // Trava entre 0 e 100
        return Math.max(0, Math.min(100, totalScore));
    }

    public RiskTier determineRisk(int score) {
        if (score >= 90) return RiskTier.EXCELLENT;
        if (score >= 70) return RiskTier.GOOD;
        if (score >= 55) return RiskTier.MEDIUM;
        if (score >= 30) return RiskTier.HIGH;
        return RiskTier.TERRIBLE;
    }

    
    public BigDecimal calculateMaxInstallment(BigDecimal monthlyIncome) {
        if (monthlyIncome == null) return BigDecimal.ZERO;
        
        // Retorna 30% da renda
        return monthlyIncome.multiply(new BigDecimal("0.30"))
                            .setScale(2, RoundingMode.HALF_DOWN);
    }

    public BigDecimal calculateApprovedLimit(BigDecimal monthlyIncome, RiskTier risk) {
        if (risk == RiskTier.TERRIBLE || monthlyIncome == null) return BigDecimal.ZERO;

        BigDecimal maxInstallmentValue = calculateMaxInstallment(monthlyIncome);
        double pmt = maxInstallmentValue.doubleValue();
        double rate = risk.getInterestRate().doubleValue();
        int months = risk.getMaxInstallments();

        if (rate == 0) return maxInstallmentValue.multiply(BigDecimal.valueOf(months));

        double pv = pmt * ((1 - Math.pow(1 + rate, -months)) / rate);
        return BigDecimal.valueOf(pv).setScale(2, RoundingMode.HALF_DOWN);
    }

        
    private int calculateProfessionPoints(String profession) {
        if (profession == null) return 0;
        
        // Normaliza para minúsculo para facilitar a busca
        String p = profession.toUpperCase().trim();

        // GRUPO 1: ALTA ESTABILIDADE (+10)
        if (p.equals("CONCURSADO") || p.equals("PÚBLICO") || p.equals("MILITAR") ||
            p.equals("MÉDICO") || p.equals("JUIZ") || p.equals("CLT") || 
            p.equals("POLICIAL")) {
            return 10;
        }

        // GRUPO 2: RISCO VOLÁTIL (-5)
        if (p.equals("AUTÔNOMO") || p.equals("FREELANCER") || p.equals("ESTUDANTE") || 
            p.equals("ESTAGIÁRIO") || p.equals("UBER") || p.equals("MOTORISTA APP")) {
            return -5;
        }

        // GRUPO 3: ALTO RISCO (-10)
        if (p.equals("DESEMPREGADO") || p.equals("DO LAR") || p.isEmpty()) {
            return -10;
        }

        return 0;
    }


}