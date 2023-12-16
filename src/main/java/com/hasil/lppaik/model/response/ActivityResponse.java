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

  private String link;

  private String date;

  private String location;

  private String endTime;

  private String startTime;

  private Boolean mandatory;

  private List<String> images;
}
