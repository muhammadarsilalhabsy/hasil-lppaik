package com.hasil.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleEmailVerifyResponse {

  private String username;
  private String name;
  private String major;
  private String avatar;
  private String token;
  private Long expired;
}
