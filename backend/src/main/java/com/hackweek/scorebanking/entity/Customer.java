package com.hackweek.scorebanking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tb_customer")
@Data
@NoArgsConstructor
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  private Integer age;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true, length = 11)
  private String cpf;

  @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
  private CustomerScoreData scoreData;
}