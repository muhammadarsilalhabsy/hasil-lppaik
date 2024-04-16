package com.hasil.lppaik.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcceptEmailRequest {

  private String id;

  private String username;

  private Long expired;

}
