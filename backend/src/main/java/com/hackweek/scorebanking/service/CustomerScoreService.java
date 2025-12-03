package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.dto.ScoreResultResponse;
import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerScoreService {

  private final CustomerRepository customerRepository;
  private final CreditEngineService creditEngineService;

  public CustomerScoreService(CustomerRepository customerRepository,
                              CreditEngineService creditEngineService) {
    this.customerRepository = customerRepository;
    this.creditEngineService = creditEngineService;
  }

  public ScoreResultResponse calculateCustomerScore(Long customerId) {

    // Buscar o cliente
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

    // 1. Calcular score
    int score = creditEngineService.calculateScore(customer);

    // 2. Determinar classificação de risco
    var riskTier = creditEngineService.determineRisk(score);

    // 3. Calcular limite aprovado
    var approvedLimit =
            creditEngineService.calculateApprovedLimit(customer.getScoreData().getMonthlyIncome(), riskTier);

    // 4. Retornar DTO
    return new ScoreResultResponse(
            customer.getId(),
            score,
            riskTier,
            approvedLimit
    );
  }
}