package com.hackweek.scorebanking.dto;

public record ScoreBreakdown(
    String attribute, 
    String valueLog,   
    int points         
) {}