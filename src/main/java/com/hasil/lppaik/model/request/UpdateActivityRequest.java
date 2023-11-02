package com.hasil.lppaik.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateActivityRequest {

  @JsonIgnore
  private String id;

  @Size(min = 8, message = "min length should be {min}")
  private String title;

  @Size(min = 8, message = "min length should be {min}")
  private String description;

  private LocalDate date;

  private Boolean mandatory;

  @Size(min = 5, message = "min length should be {min}")
  private String location;

  private LocalTime endTime;
  private LocalTime startTime;

  @Size(min = 1, message = "at least upload {min} image for the cover")
  private List<String> images;
}
