package com.hackweek.scorebanking.controller;

import com.hackweek.scorebanking.dto.LoginDto;
import com.hackweek.scorebanking.dto.LoginResponseDto;
import com.hackweek.scorebanking.dto.RegisterDto;
import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.service.CustumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // liberar para Angular no MVP
public class AuthController {

  @Autowired
  private CustumerService customerService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterDto register) {
    try {
      Customer customer = customerService.register(register);
      return ResponseEntity.ok("Usuário registrado com sucesso: " + customer.getEmail());
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDto request) {
    try {
      LoginResponseDto response = customerService.login(request);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
