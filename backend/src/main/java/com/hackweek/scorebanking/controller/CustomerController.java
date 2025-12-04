package com.hackweek.scorebanking.controller;

import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.service.CustomerService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
  public final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping
  public List<Customer> getAllCustomers() {
    return customerService.getAll();
  }
}
