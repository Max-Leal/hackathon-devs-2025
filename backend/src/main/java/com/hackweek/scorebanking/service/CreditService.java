package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
import com.hackweek.scorebanking.dto.CreditAnalysisResponseDto;
import com.hackweek.scorebanking.dto.CustomerInputDto;
import com.hackweek.scorebanking.entity.CreditAnalysis;
import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.repository.CreditAnalysisRepository;
import com.hackweek.scorebanking.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
    
@Service
public class CreditService {

    private final CustomerRepository customerRepository;
    private final CreditAnalysisRepository analysisRepository;
    private final CreditEngineService creditEngine;

    public CreditService(CustomerRepository customerRepository,
                         CreditAnalysisRepository analysisRepository,
                         CreditEngineService creditEngine) {
        this.customerRepository = customerRepository;
        this.analysisRepository = analysisRepository;
        this.creditEngine = creditEngine;
    }

    @Transactional
    public CreditAnalysisResponseDto analyzeCredit(CustomerInputDto input) {
        Customer customer = customerRepository.findByCpf(input.cpf())
                .orElseGet(() -> createNewCustomer(input));

        int score = creditEngine.calculateScore(customer);
        RiskTier risk = creditEngine.determineRisk(score);
//        BigDecimal approvedLimit = creditEngine.calculateApprovedLimit(customer.getMonthlyIncome(), risk);
        boolean approved = risk != RiskTier.TERRIBLE;

        CreditAnalysis analysis = new CreditAnalysis();
        analysis.setCustomer(customer);
        analysis.setScoreValue(score);
        analysis.setApproved(approved);
//        analysis.setApprovedLimit(approvedLimit);
        analysis.setApprovedInterestRate(risk.getInterestRate());
        analysis.setMaxInstallments(risk.getMaxInstallments());
//        analysis.setWithdrawalLimitValue(customer.getMonthlyIncome().multiply(new BigDecimal("0.30")));

        analysisRepository.save(analysis);

        return new CreditAnalysisResponseDto(
            analysis.getId(),
            analysis.getApproved(),
            analysis.getScoreValue(),
            analysis.getApprovedLimit(),
            analysis.getApprovedInterestRate(),
            analysis.getMaxInstallments(),
            approved ? "Crédito Aprovado!" : "Reprovado: Risco alto.",
            analysis.getDate()
        );
    }

    private Customer createNewCustomer(CustomerInputDto input) {
        Customer newCustomer = new Customer();
        newCustomer.setCpf(input.cpf());
        newCustomer.setFullName(input.fullName());
//        newCustomer.setMonthlyIncome(input.monthlyIncome());
//        newCustomer.setAge(input.age());
//        newCustomer.setPhone(input.phone());
//        newCustomer.setProfession(input.profession());
        return customerRepository.save(newCustomer);
    }
}