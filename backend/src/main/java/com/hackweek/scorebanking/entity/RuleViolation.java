package com.hackweek.scorebanking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tb_rule_violation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruleName;    // Ex: "SERASA_SCORE"
    private String description; // Ex: "Score externo abaixo de 300"

    @ManyToOne
    @JoinColumn(name = "analysis_id", nullable = false)
    private CreditAnalysis analysis;
}   