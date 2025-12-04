package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.dto.CustomerScoreDto;
import com.hackweek.scorebanking.dto.ScoreResultResponse;
import com.hackweek.scorebanking.dto.ScoreCalculationResult;
import com.hackweek.scorebanking.dto.ScoreBreakdown;
import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.entity.CustomerScoreData;
import com.hackweek.scorebanking.repository.CustomerRepository;
import com.hackweek.scorebanking.repository.CustomerScoreDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerScoreService {

    private final CustomerRepository customerRepository;
    private final CustomerScoreDataRepository scoreDataRepository;
    private final CreditEngineService creditEngineService;

    public CustomerScoreService(CustomerRepository customerRepository,
                                CustomerScoreDataRepository scoreDataRepository,
                                CreditEngineService creditEngineService) {
        this.customerRepository = customerRepository;
        this.scoreDataRepository = scoreDataRepository;
        this.creditEngineService = creditEngineService;
    }

    @Transactional
    public ScoreResultResponse processScoreAnalysis(Long customerId, CustomerScoreDto request) {
        
        // 1. Buscas iniciais
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        CustomerScoreData scoreData = scoreDataRepository.findByCustomerId(customerId)
                .orElse(new CustomerScoreData());
        scoreData.setCustomer(customer);

        // 2. Atualiza dados vindos do Front (DTO 'request')
        scoreData.setProfession(request.profession());
        scoreData.setMonthlyIncome(request.monthlyIncome());
        scoreData.setDependents(request.dependents());
        scoreData.setMonthsInCurrentJob(request.monthsInCurrentJob());
        
        // Fallbacks opcionais (se existirem no DTO)
        if (request.educationLevel() != null) scoreData.setEducationLevel(request.educationLevel());
        if (request.housingStatus() != null) scoreData.setHousingStatus(request.housingStatus());
        
        // Garante que a idade está sincronizada
        scoreData.setAge(customer.getAge());

        // 3. Mock (Simulação de Bureau)
        enrichWithMockData(scoreData, customer.getCpf());

        // 4. Cálculo do Score Detalhado
        ScoreCalculationResult calculation = creditEngineService.calculateDetailedScore(scoreData);
        int score = calculation.totalScore();

        // 5. Definição de Risco e Limites
        RiskTier riskTier = creditEngineService.determineRisk(score);
        BigDecimal approvedLimit = creditEngineService.calculateApprovedLimit(scoreData.getMonthlyIncome(), riskTier);
        BigDecimal safeInstallment = creditEngineService.calculateMaxInstallment(scoreData.getMonthlyIncome());
        
        // Auxiliares de Tier
        int installments = (riskTier == RiskTier.TERRIBLE) ? 0 : riskTier.getMaxInstallments();
        BigDecimal rate = (riskTier == RiskTier.TERRIBLE) ? BigDecimal.ZERO : riskTier.getInterestRate();

        // 6. SALVAR NO BANCO (Persistência do Resultado)
        scoreData.setLastCalculatedScore(score);
        scoreData.setLastRiskTier(riskTier);
        scoreData.setLastApprovedLimit(approvedLimit);
        
        scoreDataRepository.save(scoreData);

        // 7. Gera Feedback e Retorna
        List<String> feedbackMessages = creditEngineService.generateFeedback(scoreData);

        return new ScoreResultResponse(
                customerId,
                score,
                riskTier,
                approvedLimit,
                safeInstallment,
                installments,
                rate,
                feedbackMessages,
                calculation.breakdown() 
        );
    }

    public ScoreResultResponse getLatestScoreData(Long customerId) {
        
        // 1. Busca os dados no banco
        CustomerScoreData data = scoreDataRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Nenhum cálculo encontrado. Solicite uma análise primeiro."));

        // 2. Valida se já foi calculado
        if (data.getLastCalculatedScore() == null) {
            // Retorna vazio se nunca calculou
            return new ScoreResultResponse(customerId, 0, RiskTier.TERRIBLE, BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, List.of(), List.of());
        }

        // 3. Recupera os valores salvos
        int score = data.getLastCalculatedScore();
        RiskTier tier = data.getLastRiskTier();
        BigDecimal limit = data.getLastApprovedLimit();

        // 4. Recalcula auxiliares (Parcela, Taxa, Feedback, Audit)
        // Isso é rápido e evita salvar JSON gigante no banco
        BigDecimal safeInstallment = creditEngineService.calculateMaxInstallment(data.getMonthlyIncome());
        int installments = (tier == RiskTier.TERRIBLE) ? 0 : tier.getMaxInstallments();
        BigDecimal rate = (tier == RiskTier.TERRIBLE) ? BigDecimal.ZERO : tier.getInterestRate();

        List<String> feedback = creditEngineService.generateFeedback(data);
        ScoreCalculationResult calcResult = creditEngineService.calculateDetailedScore(data);

        return new ScoreResultResponse(
                customerId,
                score,
                tier,
                limit,
                safeInstallment,
                installments,
                rate,
                feedback,
                calcResult.breakdown()
        );
    }

    private void enrichWithMockData(CustomerScoreData data, String cpf) {
        if (cpf == null || cpf.isEmpty()) return;
        
        char lastDigit = cpf.charAt(cpf.length() - 1);

        if (lastDigit == '0') { 
             data.setFraudSuspicion(true); data.setExternalDebt(BigDecimal.ZERO); data.setCreditScore(0);
        } else if (lastDigit == '1') {
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("15000.00")); data.setCreditScore(250);
        } else if(lastDigit == '2' ){
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("13000.00")); data.setCreditScore(600);
        } else if(lastDigit == '3' ){
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("11000.00")); data.setCreditScore(600);
        } else if(lastDigit == '4' ){
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("9000.00")); data.setCreditScore(600);
        } else if(lastDigit == '5' ){
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("7000.00")); data.setCreditScore(600);
        } else if(lastDigit == '6' ){
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("5000.00")); data.setCreditScore(600);
        } else if(lastDigit == '7' ){
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("3000.00")); data.setCreditScore(600);
        } else if (lastDigit == '9') {
             data.setFraudSuspicion(false); data.setExternalDebt(BigDecimal.ZERO); data.setCreditScore(950);
        } else {
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("500.00")); data.setCreditScore(600);
        }
    }
}