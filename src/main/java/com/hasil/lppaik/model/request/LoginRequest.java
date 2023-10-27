package com.hasil.lppaik.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

  @NotBlank(message = "username must be not blank")
  @Size(min = 8, max = 10, message = "username must be {min} - {max} character")
  private String username;

  @NotBlank(message = "password must be not blank")
  @Size(min = 5, message = "password length must be at least {min} character")
  private String password;
}
