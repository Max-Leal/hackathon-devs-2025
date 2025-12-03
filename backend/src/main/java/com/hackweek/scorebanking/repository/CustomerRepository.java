package com.hackweek.scorebanking.repository;

import com.hackweek.scorebanking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    Optional<Customer> findByEmail(String email);
}