package com.hackweek.scorebanking.controller;

import com.hackweek.scorebanking.dto.CustomerScoreDto;
import com.hackweek.scorebanking.dto.ScoreResultResponse;
import com.hackweek.scorebanking.entity.Customer;
import com.hackweek.scorebanking.entity.CustomerScoreData;
import com.hackweek.scorebanking.repository.CustomerRepository;
import com.hackweek.scorebanking.repository.CustomerScoreDataRepository;
import com.hackweek.scorebanking.service.CustomerScoreService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerScoreController {

  private final CustomerRepository customerRepository;
  private final CustomerScoreDataRepository scoreRepository;
  private final CustomerScoreService scoreService;

  public CustomerScoreController(CustomerRepository customerRepository,
                                 CustomerScoreDataRepository scoreRepository,
                                 CustomerScoreService scoreService) {
    this.customerRepository = customerRepository;
    this.scoreRepository = scoreRepository;
    this.scoreService = scoreService;
  }


  @PostMapping("/{id}/score")
  public ScoreResultResponse createOrUpdateScoreData(
          @PathVariable Long id,
          @RequestBody CustomerScoreDto request) {

    Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

    CustomerScoreData scoreData = scoreRepository.findByCustomerId(id)
            .orElse(new CustomerScoreData());

    scoreData.setCustomer(customer);
    scoreData.setAge(request.age());
    scoreData.setProfession(request.profession());
    scoreData.setMonthlyIncome(request.monthlyIncome());
    scoreData.setDependents(request.dependents());
    scoreData.setEducationLevel(request.educationLevel());
    scoreData.setHousingStatus(request.housingStatus());

    scoreRepository.save(scoreData);

    return scoreService.calculateCustomerScore(id);
  }
}