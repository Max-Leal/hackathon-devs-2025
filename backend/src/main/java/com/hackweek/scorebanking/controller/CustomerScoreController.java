package com.hackweek.scorebanking.controller;

import com.hackweek.scorebanking.dto.CustomerScoreDto;
import com.hackweek.scorebanking.dto.ScoreResultResponse;
import com.hackweek.scorebanking.service.CustomerScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
public class CustomerScoreController {

    private final CustomerScoreService scoreService;

    public CustomerScoreController(CustomerScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/{id}/score")
    public ResponseEntity<?> createOrUpdateScoreData(
            @PathVariable Long id,
            @RequestBody CustomerScoreDto request) {
        
        try {
            ScoreResultResponse result = scoreService.processScoreAnalysis(id, request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}