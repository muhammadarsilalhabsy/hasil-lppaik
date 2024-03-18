package com.hasil.lppaik.model.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityRegisterResponse {

  private String username;
  private String name;
  private String email;
  private String major;
  private String avatar;

  private String regId;
}
