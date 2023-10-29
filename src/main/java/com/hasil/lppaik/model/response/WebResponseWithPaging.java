package com.hasil.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponseWithPaging<T> {

  private T data;

  private String message;

  private PagingResponse pagination;
}
