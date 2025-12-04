package com.hackweek.scorebanking.controller;

import com.hackweek.scorebanking.dto.CustomerScoreDto;
import com.hackweek.scorebanking.dto.ScoreResultResponse;
import com.hackweek.scorebanking.service.CustomerScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class ScoreController {

    private final CustomerScoreService customerScoreService;

    public ScoreController(CustomerScoreService customerScoreService) {
        this.customerScoreService = customerScoreService;
    }

    @PostMapping("/{customerId}/score")
    public ResponseEntity<ScoreResultResponse> calculateScore(
            @PathVariable Long customerId,
            @RequestBody CustomerScoreDto request
    ) {
        ScoreResultResponse response = customerScoreService.processScoreAnalysis(customerId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}/dashboard")
    public ResponseEntity<ScoreResultResponse> getDashboardData(@PathVariable Long customerId) {
        ScoreResultResponse response = customerScoreService.getLatestScoreData(customerId);
        return ResponseEntity.ok(response);
    }
}