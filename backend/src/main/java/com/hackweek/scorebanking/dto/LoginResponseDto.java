package com.hackweek.scorebanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
  private Long id;
  private String fullNome;
  private String email;
  private boolean authenticated;
}
