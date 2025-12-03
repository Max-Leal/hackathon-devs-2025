package com.hackweek.scorebanking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_customer_score")
@Data
@NoArgsConstructor
public class CustomerScoreData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- DADOS OBRIGATÓRIOS DO DESAFIO ---
    private Integer age;
    private BigDecimal monthlyIncome;
    private Integer creditScore; // Score Serasa

    @Column(name = "external_debt")
    private BigDecimal externalDebt;

    @Column(name = "fraud_suspicion")
    private Boolean fraudSuspicion;

    // --- DADOS OPCIONAIS DO DESAFIO ---
    private String profession;

    // "inovação" pra evitar que um monte de bot comece a criar conta
    private Long registrationTimeSeconds; 

    private Integer dependents;
    private String educationLevel;
    private String housingStatus;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;
}