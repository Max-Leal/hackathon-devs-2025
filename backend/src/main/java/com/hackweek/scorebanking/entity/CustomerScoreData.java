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

  private Integer age;

  private String profession;

  private BigDecimal monthlyIncome;

  private Integer dependents;

  private String educationLevel;

  private String housingStatus;

  private Integer creditScore;

  @OneToOne
  @JoinColumn(name = "customer_id", nullable = false)
  @JsonIgnore
  private Customer customer;
}