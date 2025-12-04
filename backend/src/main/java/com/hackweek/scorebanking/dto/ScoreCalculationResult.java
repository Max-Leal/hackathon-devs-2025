package com.hackweek.scorebanking.dto;

import java.util.List;

public record ScoreCalculationResult(
    int totalScore,
    List<ScoreBreakdown> breakdown
) {}