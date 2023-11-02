package com.hasil.lppaik.controller;


import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.*;
import com.hasil.lppaik.model.response.*;
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

  // GET SIMPLE USER BY ID [ADMIN, DOSEN]

  // GET OTHER USER ACTIVITIES [ADMIN, DOSEN]

  // GET CURRENT USER ACTIVITIES [CURRENT USER ONLY]
  @GetMapping(path = "/activities",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<SimpleActivityResponse>> getUserCurrentActivities(User user,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){

    PagingRequest request = new PagingRequest();
    request.setPage(page);
    request.setSize(size);

    Page<SimpleActivityResponse> userCurrentActivities = userService.getUserCurrentActivities(user, request);


    return WebResponseWithPaging.<List<SimpleActivityResponse>>builder()
            .data(userCurrentActivities.getContent())
            .pagination(PagingResponse.builder()
                    .page(userCurrentActivities.getNumber())
                    .totalItems(userCurrentActivities.getContent().size())
                    .pageSize(userCurrentActivities.getTotalPages())
                    .size(userCurrentActivities.getSize())
                    .build())
            .message("Success get current activities")
            .build();
  }

  // GET LIST OF USERS [ADMIN, TUTOR, DOSEN, KATING]
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

  // PATCH USER WITH ID [ADMIN ONLY]
  @PatchMapping(path = "/{username}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
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

  // PATCH USER DETAIL [USER ONLY]
  @PatchMapping(
          path = "/detail",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateCurrentUserDetail(User user,
                                              @RequestBody UpdateUserDetailRequest request){

    userService.updateCurrentUserDetail(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("detail has been updated")
            .build();
  }

  // PATCH PASSWORD [CURRENT USER ONLY]
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

  // PATCH AVATAR [CURRENT USER ONLY]
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
