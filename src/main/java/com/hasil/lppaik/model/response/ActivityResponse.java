package com.hasil.lppaik.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityResponse {

  private String id;

  private String title;
  private String description;

  private LocalDate date;

  private String location;

  private LocalTime endTime;
  private LocalTime startTime;

  private List<String> images;
}
