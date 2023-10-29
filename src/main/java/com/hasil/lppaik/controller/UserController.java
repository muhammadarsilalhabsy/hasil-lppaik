package com.hasil.lppaik.controller;


import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.SearchUserRequest;
import com.hasil.lppaik.model.request.UpdateUserDetailRequest;
import com.hasil.lppaik.model.request.UpdateUserPasswordRequest;
import com.hasil.lppaik.model.request.UpdateUserRequest;
import com.hasil.lppaik.model.response.PagingResponse;
import com.hasil.lppaik.model.response.UserResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

  private final UserServiceImpl userService;

  @Autowired
  public UserController(UserServiceImpl userService) {
    this.userService = userService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<UserResponse>> search (User user,
                                                           @RequestParam(name = "identity", required = false) String identity,
                                                           @RequestParam(name = "major", required = false) String major,
                                                           @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

    SearchUserRequest request = SearchUserRequest.builder()
            .identity(identity)
            .major(major)
            .size(size)
            .page(page)
            .build();

    Page<UserResponse> userResponses = userService.searchUser(user, request);

    return WebResponseWithPaging.<List<UserResponse>>builder()
            .data(userResponses.getContent())
            .message("Search Success")
            .pagination(PagingResponse.builder()
                    .page(userResponses.getNumber()) // current page
                    .totalItems(userResponses.getContent().size()) // get total items
                    .pageSize(userResponses.getTotalPages()) // total page keseluruhan
                    .size(userResponses.getSize()) // limitnya
                    .build())
            .build();
  }

  @PatchMapping(path = "/{username}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<String> updateUserWithId(User user,
                                              @PathVariable("username") String username,
                                              @RequestBody UpdateUserRequest request){
    request.setUsername(username);
    String userUsername = userService.updateUserWithId(user, request);
    return WebResponse.<String>builder()
            .data("OK")
            .message("user with id " + userUsername + " has been updated")
            .build();
  }

  @PatchMapping(
          path = "/detail",
          produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateCurrentUserDetail(User user,
                                              @RequestBody UpdateUserDetailRequest request){

    userService.updateCurrentUserDetail(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("detail has been updated")
            .build();
  }

  @PatchMapping(path = "/password",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateCurrentUserPassword(User user,
                                                     @RequestBody UpdateUserPasswordRequest request){

    userService.updateCurrentUserPassword(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("password has been updated")
            .build();
  }

  @PatchMapping(path = "/avatar",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateCurrentUserAvatar(User user,
                                                     @RequestParam("avatar") MultipartFile file) throws IOException {

    userService.updateCurrentUserAvatar(user, file);

    return WebResponse.<String>builder()
            .data("OK")
            .message("avatar has been updated")
            .build();
  }
}
