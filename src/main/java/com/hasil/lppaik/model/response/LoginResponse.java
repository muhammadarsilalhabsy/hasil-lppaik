package com.hasil.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

  private String token;

  private Long tokenExpiredAt;

  private UserResponse userResponse;

}
