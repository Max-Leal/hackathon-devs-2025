package com.hackweek.scorebanking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_credit_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean approved;

    private Integer scoreValue; // 0 a 100

    @Column(precision = 15, scale = 2)
    private BigDecimal approvedLimit;

    @Column(precision = 5, scale = 4)
    private BigDecimal approvedInterestRate;

    private Integer maxInstallments; // Ex: 36x

    @Column(precision = 15, scale = 2)
    private BigDecimal withdrawalLimitValue; // Valor da parcela

    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL)
    private List<RuleViolation> violations;
}