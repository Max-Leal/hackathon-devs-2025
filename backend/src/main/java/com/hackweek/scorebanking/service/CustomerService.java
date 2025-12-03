package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Customer getById(Long id) {
    return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
  }

  public List<Customer> getAll() {
    return customerRepository.findAll();
  }

  public Customer update(Long id, Customer updated) {
    Customer customer = getById(id);

    customer.setFullName(updated.getFullName());
    customer.setEmail(updated.getEmail());
    // Não alterar senha aqui
    customer.setCpf(updated.getCpf());

    return customerRepository.save(customer);
  }
}