package com.hasil.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ControlBookDetailResponse {

  private String id;

  private String tutor;

  private LocalDate date;

  private String lesson;

  private String description;
}
