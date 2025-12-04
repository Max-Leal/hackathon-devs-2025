package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.dto.CustomerScoreDto;
import com.hackweek.scorebanking.repository.CustomerScoreDataRepository;
import com.hackweek.scorebanking.dto.ScoreResultResponse;
import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.entity.CustomerScoreData;
import com.hackweek.scorebanking.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
        
        // 1. Busca Cliente
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // 2. Busca ou Cria ScoreData (Vinculado corretamente)
        CustomerScoreData scoreData = scoreDataRepository.findByCustomerId(customerId)
                .orElse(new CustomerScoreData());
        
        // Garante o vínculo bidirecional (Essencial para o JPA não se perder)
        scoreData.setCustomer(customer);
        // Se a entidade Customer tiver o setScoreData, use também: customer.setScoreData(scoreData);

        // 3. Atualiza com os dados do JSON (DTO)
        scoreData.setProfession(request.profession());
        scoreData.setMonthlyIncome(request.monthlyIncome());
        scoreData.setDependents(request.dependents());
        scoreData.setEducationLevel(request.educationLevel());
        scoreData.setHousingStatus(request.housingStatus());

        scoreData.setAge(customer.getAge());

        // 4. ENRIQUECIMENTO (SIMULAÇÃO) - Preenche fraude/dívida
        enrichWithMockData(scoreData, customer.getCpf());

        // 5. SALVA TUDO ANTES DE CALCULAR
        // Como estamos dentro de um @Transactional, esse save é visível imediatamente para as próximas linhas
        scoreData = scoreDataRepository.save(scoreData); 

        // 6. CÁLCULO (Agora garantimos que scoreData tem dados mockados e ID)
        int score = creditEngineService.calculateScore(scoreData);
        RiskTier riskTier = creditEngineService.determineRisk(score);
        BigDecimal approvedLimit = creditEngineService.calculateApprovedLimit(scoreData.getMonthlyIncome(), riskTier);
        BigDecimal safeInstallment = creditEngineService.calculateMaxInstallment(scoreData.getMonthlyIncome());

        int installments = (riskTier == RiskTier.TERRIBLE) ? 0 : riskTier.getMaxInstallments();

        return new ScoreResultResponse(
                customer.getId(),
                score,
                riskTier,
                approvedLimit,
                safeInstallment,
                installments
        );
    }

    // Lógica de Simulação baseada no final do CPF
    private void enrichWithMockData(CustomerScoreData data, String cpf) {
        char lastDigit = cpf.charAt(cpf.length() - 1);
        if (lastDigit == '0') { 
             data.setFraudSuspicion(true); data.setExternalDebt(BigDecimal.ZERO); data.setCreditScore(0);
        } else if (lastDigit == '1') {
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("15000.00")); data.setCreditScore(250);
        } else if (lastDigit == '9') {
             data.setFraudSuspicion(false); data.setExternalDebt(BigDecimal.ZERO); data.setCreditScore(950);
        } else {
             data.setFraudSuspicion(false); data.setExternalDebt(new BigDecimal("500.00")); data.setCreditScore(600);
        }
    }
}