package com.hackweek.scorebanking.dto;

public record RegisterDto(String fullName, Integer age, String email, String password, String cpf) {
}
