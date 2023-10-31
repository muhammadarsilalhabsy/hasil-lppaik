package com.hasil.lppaik.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingRequest {

  @NotNull
  private Integer page;

  @NotNull
  private Integer size;

  @JsonIgnore
  private String username;
}
