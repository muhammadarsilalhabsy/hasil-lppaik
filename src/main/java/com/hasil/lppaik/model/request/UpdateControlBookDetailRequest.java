package com.hasil.lppaik.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateControlBookDetailRequest {


  @Size(min = 5, max = 255, message = "length must be between {min} - {max} characters")
  private String lesson;


  @Size(min = 5,  message = "length must at least {min} characters")
  private String description;

  private LocalDate date;


  @JsonIgnore
  private String id;
}
