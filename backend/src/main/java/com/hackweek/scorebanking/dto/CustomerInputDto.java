package com.hackweek.scorebanking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CustomerInputDto(
        @NotBlank String fullName,
        @NotBlank @Size(min = 11, max = 11) String cpf,
        @NotNull @Positive BigDecimal monthlyIncome,
        @NotNull @Min(18) Integer age,
        String phone,
        String profession
) {}