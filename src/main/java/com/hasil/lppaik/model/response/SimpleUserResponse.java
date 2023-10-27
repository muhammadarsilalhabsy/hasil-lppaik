package com.hasil.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleUserResponse {

  private String username;
  private String name;
  private String email;
  private String avatar;
  private Boolean completed;
}
