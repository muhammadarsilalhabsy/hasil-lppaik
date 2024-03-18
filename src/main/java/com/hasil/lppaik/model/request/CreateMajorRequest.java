package com.hasil.lppaik.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMajorRequest {

  @NotNull
  @NotBlank
  private String name;

  @NotNull
  @NotBlank
  private String id;
}
