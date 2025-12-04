package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.entity.CustomerScoreData;
import com.hackweek.scorebanking.dto.ScoreBreakdown;
import com.hackweek.scorebanking.dto.ScoreCalculationResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList; 
import java.util.List;      

@Service
public class CreditEngineService {

    public ScoreCalculationResult calculateDetailedScore(CustomerScoreData data) {
        List<ScoreBreakdown> audit = new ArrayList<>();
        int currentScore = 0;

        // 1. PONTUAÇÃO BASE
        int baseScore = 25;
        currentScore += baseScore;
        audit.add(new ScoreBreakdown("Score Base", "Início Padrão", baseScore));

        // 2. CHECK DE FRAUDE/IDADE (Bloqueios)
        if (Boolean.TRUE.equals(data.getFraudSuspicion())) {
            return new ScoreCalculationResult(0, List.of(new ScoreBreakdown("Fraude", "Detectada", -100)));
        }
        
        // 3. RENDA
        BigDecimal income = data.getMonthlyIncome() != null ? data.getMonthlyIncome() : BigDecimal.ZERO;
        int incomePoints = income.divide(new BigDecimal("500"), RoundingMode.FLOOR).intValue();
        incomePoints = Math.min(incomePoints, 40); // Teto de 40
        currentScore += incomePoints;
        audit.add(new ScoreBreakdown("Renda Mensal", "R$ " + income, incomePoints));

        // 4. SCORE EXTERNO (SERASA)
        int serasaScore = data.getCreditScore() != null ? data.getCreditScore() : 0;
        int externalPoints = (serasaScore / 100) * 5;
        currentScore += externalPoints;
        audit.add(new ScoreBreakdown("Score Serasa", String.valueOf(serasaScore), externalPoints));

        // 5. PROFISSÃO
        int profPoints = calculateProfessionPoints(data.getProfession());
        currentScore += profPoints;
        audit.add(new ScoreBreakdown("Profissão", data.getProfession(), profPoints));

        // 6. TEMPO DE CASA
        int tenurePoints = calculateTenurePoints(data.getMonthsInCurrentJob());
        currentScore += tenurePoints;
        audit.add(new ScoreBreakdown("Estabilidade", data.getMonthsInCurrentJob() + " meses", tenurePoints));

        // 7. DÍVIDA
        int debtPenalty = calculateDebtPenalty(data.getExternalDebt());
        currentScore -= debtPenalty; // Subtrai
        audit.add(new ScoreBreakdown("Dívida Externa", "R$ " + data.getExternalDebt(), -debtPenalty));

        // TRAVA 0 a 100
        int finalScore = Math.max(0, Math.min(100, currentScore));
        
        // Retorna o pacote completo
        return new ScoreCalculationResult(finalScore, audit);
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

    // calculo dos meses na empresa
    private int calculateTenurePoints(Integer months) {
        if (months == null) return 0;

        if (months < 3) return -5;  // Período de experiência (Risco)
        if (months < 12) return 0;  // Menos de 1 ano (Neutro)
        if (months < 36) return 5;  // 1 a 3 anos (Estável)
        
        return 10; // +3 anos (Muito Estável)
    }
        
    private int calculateProfessionPoints(String profession) {
        if (profession == null) return 0;
        
        // Normaliza para minúsculo para facilitar a busca
        String p = profession.toUpperCase().trim();

        // GRUPO 1: ALTA ESTABILIDADE (+10)
        if (p.equals("CONCURSADO") || p.equals("PÚBLICO") || p.equals("MILITAR") ||
            p.equals("MÉDICO") || p.equals("JUIZ") || p.equals("CLT") || 
            p.equals("POLICIAL") || p.equals("ANALISTA_TI")) {
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

    private int calculateDebtPenalty(BigDecimal externalDebt) {
        if (externalDebt == null || externalDebt.compareTo(BigDecimal.ZERO) <= 0) return 0;
        // 1 ponto por 1000 reais
        int penalty = externalDebt.divide(new BigDecimal("1000"), 0, RoundingMode.DOWN).intValue();
        // Teto de 10 pontos
        return Math.min(penalty, 10);
    }

    public List<String> generateFeedback(CustomerScoreData data) {
        List<String> feedback = new ArrayList<>();

        // 1. CHECAGEM DE BLOQUEIOS (Morte Súbita)
        if (Boolean.TRUE.equals(data.getFraudSuspicion())) {
            feedback.add("⚠️ CPF com restrição grave de segurança.");
            return feedback; // Se for fraude, nem fala o resto.
        }
        if (data.getAge() != null && data.getAge() < 18) {
            feedback.add("⚠️ Política interna: Crédito apenas para maiores de 18 anos.");
            return feedback;
        }

        // 2. CHECAGEM DE PONTOS POSITIVOS E NEGATIVOS
        
        // Renda
        if (data.getMonthlyIncome().compareTo(new BigDecimal("2000")) < 0) {
            feedback.add("📉 A renda informada limita o potencial de crédito alto.");
        } else {
            feedback.add("✅ Renda compatível com a política de crédito.");
        }

        // Dívida (AQUI É O IMPORTANTE)
        if (data.getExternalDebt() != null && data.getExternalDebt().compareTo(new BigDecimal("1000")) > 0) {
            feedback.add("⚠️ Alto comprometimento com dívidas externas impactou sua pontuação.");
        }

        // Profissão / Estabilidade
        if (data.getMonthsInCurrentJob() != null && data.getMonthsInCurrentJob() < 3) {
            feedback.add("📉 Tempo de casa recente (período de experiência) reduz a estabilidade.");
        } else if (data.getMonthsInCurrentJob() != null && data.getMonthsInCurrentJob() > 24) {
            feedback.add("✅ Alta estabilidade profissional contribuiu positivamente.");
        }

        // Score Serasa
        if (data.getCreditScore() != null && data.getCreditScore() < 400) {
            feedback.add("📉 Histórico externo (Bureau de Crédito) abaixo da média.");
        }

        return feedback;
    }

}