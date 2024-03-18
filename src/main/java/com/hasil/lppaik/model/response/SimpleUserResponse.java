package com.hasil.lppaik.model.response;

import com.fasterxml.jackson.annotation.JsonRootName;
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
  private String major;
  private String avatar;
  private Boolean completed;

  private String certificate;
}
