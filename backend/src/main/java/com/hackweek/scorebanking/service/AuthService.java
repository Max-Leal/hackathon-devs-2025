package com.hackweek.scorebanking.service;

import com.hackweek.scorebanking.dto.LoginDto;
import com.hackweek.scorebanking.dto.LoginResponseDto;
import com.hackweek.scorebanking.dto.RegisterDto;
import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired
  private CustomerRepository customerRepository;

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public Customer register(RegisterDto registerDto) {
    if (customerRepository.findByEmail(registerDto.email()).isPresent()) {
      throw new RuntimeException("Email já cadastrado!");
    }

    Customer customer = new Customer();
    customer.setFullName(registerDto.fullName());
    customer.setEmail(registerDto.email());
    customer.setPassword(encoder.encode(registerDto.password()));
    customer.setCpf(registerDto.cpf());

    return customerRepository.save(customer);
  }

  public LoginResponseDto login(LoginDto loginDto) {

    Customer customer = customerRepository.findByEmail(loginDto.email())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!encoder.matches(loginDto.password(), customer.getPassword())) {
      throw new RuntimeException("Senha incorreta");
    }

    return new LoginResponseDto(
            customer.getId(),
            customer.getFullName(),
            customer.getEmail(),
            true
    );
  }
}
