package com.hasil.lppaik.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchActivityRequest {

  private String title;

  private Boolean mandatory;

  @NotNull
  private Integer page;

  @NotNull
  private Integer size;

}
