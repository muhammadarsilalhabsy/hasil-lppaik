package com.hasil.lppaik.controller;


import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateControlBookDetailRequest;
import com.hasil.lppaik.model.request.PagingRequest;
import com.hasil.lppaik.model.request.UpdateControlBookDetailRequest;
import com.hasil.lppaik.model.response.ControlBookDetailResponse;
import com.hasil.lppaik.model.response.PagingResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.service.ControlBookDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/control-book")
public class ControlBookDetailController {

  private final ControlBookDetailServiceImpl cbdService;

  @Autowired
  public ControlBookDetailController(ControlBookDetailServiceImpl cbdService) {
    this.cbdService = cbdService;
  }

  // get other user list of control book (using paging) [ADMIN, TUTOR, DOSEN]
  @GetMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<ControlBookDetailResponse>> getOtherUserCBDDetail(User user,
                                                                                        @PathVariable("username") String username,
                                                                                        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                                                        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){

    PagingRequest request = PagingRequest.builder()
            .page(page).size(size)
            .username(username)
            .build();

    Page<ControlBookDetailResponse> detailResponse = cbdService.getOtherUserCBD(user, request);

    return WebResponseWithPaging.<List<ControlBookDetailResponse>>builder()
            .data(detailResponse.getContent())
            .message("Success get all btq details user with id " + username)
            .pagination(PagingResponse.builder()
                    .page(detailResponse.getNumber()) // current page
                    .totalItems(detailResponse.getContent().size()) // get total items
                    .pageSize(detailResponse.getTotalPages()) // total page keseluruhan
                    .size(detailResponse.getSize()) // limitnya
                    .build())
            .build();
  }

  // get current list of control book (using paging)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<ControlBookDetailResponse>> getCurrentUserCBDDetail(User user,
                                                                          @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                                          @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){


    PagingRequest request = PagingRequest.builder().page(page).size(size).build();

    Page<ControlBookDetailResponse> detailResponse = cbdService.getCurrentCBD(user, request);


    return WebResponseWithPaging.<List<ControlBookDetailResponse>>builder()
            .data(detailResponse.getContent())
            .message("Success get all btq details")
            .pagination(PagingResponse.builder()
                    .page(detailResponse.getNumber()) // current page
                    .totalItems(detailResponse.getContent().size()) // get total items
                    .pageSize(detailResponse.getTotalPages()) // total page keseluruhan
                    .size(detailResponse.getSize()) // limitnya
                    .build())
            .build();
  }
  // delete other user control book detail [ADMIN, TUTOR]
  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> deleteCBD(User user, @PathVariable String id){

    cbdService.deleteCBD(user, id);

    return WebResponse.<String>builder()
            .data("OK")
            .message("lesson has been deleted")
            .build();
  }

  // update other user control book detail [ADMIN, TUTOR]
  @PatchMapping(path = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateCBD(User user,
                                    @PathVariable String id,
                                    @RequestBody UpdateControlBookDetailRequest request){

    request.setId(id);
    cbdService.updateCBD(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("lesson has been updated")
            .build();
  }

  // add other user control book detail [ADMIN, TUTOR]
  @PostMapping(path = "/{username}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> addCBD(User user,
                                    @PathVariable String username,
                                    @RequestBody CreateControlBookDetailRequest request){

    request.setUsername(username);
    cbdService.addCBD(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message(String.format("Success add new lesson for user with id %s", username))
            .build();
  }
}
