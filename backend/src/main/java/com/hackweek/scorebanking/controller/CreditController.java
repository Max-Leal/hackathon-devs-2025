package com.hackweek.scorebanking.controller;

import com.hackweek.scorebanking.dto.CreditAnalysisResponseDto;
import com.hackweek.scorebanking.dto.CustomerInputDto;
import com.hackweek.scorebanking.service.CreditService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit")
@CrossOrigin(origins = "*")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<CreditAnalysisResponseDto> requestAnalysis(
            @RequestBody @Valid CustomerInputDto inputDto) {

        CreditAnalysisResponseDto result = creditService.analyzeCredit(inputDto);
        return ResponseEntity.ok(result);
    }
}