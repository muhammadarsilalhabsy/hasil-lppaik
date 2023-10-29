package com.hasil.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {

  private Integer page; // current page
  private Integer totalItems; // banyak items
  private Integer pageSize; // limit nya berapa

  private Integer size; // limit nya berapa


}
