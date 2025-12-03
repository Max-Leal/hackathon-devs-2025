package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.domain.RiskTier;
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
    private final CreditEngineService creditEngineService;

    public CustomerScoreService(CustomerRepository customerRepository,
                                CreditEngineService creditEngineService) {
        this.customerRepository = customerRepository;
        this.creditEngineService = creditEngineService;
    }

    @Transactional
    public ScoreResultResponse calculateCustomerScore(Long customerId) {

        // 1. Buscar o cliente
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // 2. Obter os dados financeiros (ScoreData)
        CustomerScoreData scoreData = customer.getScoreData();
        
        // Validação vital: Se o usuário acabou de se cadastrar e não preencheu dados
        if (scoreData == null) {
            // Em um cenário real, redirecionaria para preencher dados.
            // Aqui, podemos criar um vazio ou lançar erro.
            throw new RuntimeException("Dados financeiros incompletos. Por favor, atualize seu cadastro.");
        }

        // 3. ENRIQUECIMENTO (SIMULAÇÃO): Preenche fraude/dívida baseado no CPF
        enrichWithMockData(scoreData, customer.getCpf());

        // --- A CORREÇÃO ESTÁ AQUI EMBAIXO ---
        
        // 4. Calcular score (Passando scoreData, NÃO customer)
        int score = creditEngineService.calculateScore(scoreData); 

        // 5. Determinar classificação
        RiskTier riskTier = creditEngineService.determineRisk(score);

        // 6. Calcular limite
        BigDecimal approvedLimit = creditEngineService.calculateApprovedLimit(
                scoreData.getMonthlyIncome(), 
                riskTier
        );

        // 7. Retornar DTO
        return new ScoreResultResponse(
                customer.getId(),
                score,
                riskTier,
                approvedLimit
        );
    }

    // Lógica de Simulação baseada no final do CPF
    private void enrichWithMockData(CustomerScoreData data, String cpf) {
        char lastDigit = cpf.charAt(cpf.length() - 1);

        if (lastDigit == '0') { // FRAUDE
            data.setFraudSuspicion(true);
            data.setExternalDebt(BigDecimal.ZERO);
            data.setCreditScore(0);
        } else if (lastDigit == '1') { // ENDIVIDADO
            data.setFraudSuspicion(false);
            data.setExternalDebt(new BigDecimal("15000.00"));
            data.setCreditScore(250);
        } else if (lastDigit == '9') { // RICO
            data.setFraudSuspicion(false);
            data.setExternalDebt(BigDecimal.ZERO);
            data.setCreditScore(950);
        } else { // MÉDIO
            data.setFraudSuspicion(false);
            data.setExternalDebt(new BigDecimal("500.00"));
            data.setCreditScore(600);
        }
    }
}