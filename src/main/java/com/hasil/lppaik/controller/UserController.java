package com.hasil.lppaik.controller;


import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.*;
import com.hasil.lppaik.model.response.*;
import com.hasil.lppaik.service.ControlBookDetailServiceImpl;
import com.hasil.lppaik.service.UserServiceImpl;
import com.hasil.lppaik.service.Utils;
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

  private final ControlBookDetailServiceImpl cbdService;

  @Autowired
  public UserController(UserServiceImpl userService, ControlBookDetailServiceImpl cbdService) {
    this.userService = userService;
    this.cbdService = cbdService;
  }

  // GET USER CURRENT CERTIFICATE (WITH PDF)
  @GetMapping(path = "/certificate/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> getCertificate(User user){
    return null;
  }

  // POST CREATE USERS [ADMIN ONLY]
  @PostMapping(path = "/create",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> createUser(User user, @RequestBody CreateUserRequest request){

    userService.createUser(user, request);
    return WebResponse.<String>builder()
            .data("OK")
            .message("Success create new user")
            .build();
  }

  // GET OTHER USER CBD LIST [ADMIN, TUTOR, DOSEN]
  @GetMapping(path = "/control-book/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<ControlBookDetailResponse>> getOtherUserCBDDetail(User user,
                                                                                      @PathVariable("username") String username,
                                                                                      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                                      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){

    PagingRequest request = PagingRequest.builder()
            .page(page).size(size)
            .username(username)
            .build();

    Page<ControlBookDetailResponse> pages = cbdService.getOtherUserCBD(user, request);

    return WebResponseWithPaging.<List<ControlBookDetailResponse>>builder()
            .data(pages.getContent())
            .message("Success get all btq details user with id " + username)
            .pagination(Utils.getPagingResponse(pages))
            .build();
  }


  // GET USER CURRENT CBD LIST [CURRENT USER ONLY]
  @GetMapping(path = "/control-book", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<ControlBookDetailResponse>> getCurrentUserCBDDetail(User user,
                                                                                        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                                        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){


    PagingRequest request = PagingRequest.builder().page(page).size(size).build();

    Page<ControlBookDetailResponse> pages = cbdService.getCurrentCBD(user, request);


    return WebResponseWithPaging.<List<ControlBookDetailResponse>>builder()
            .data(pages.getContent())
            .message("Success get all btq details")
            .pagination(Utils.getPagingResponse(pages))
            .build();
  }

  // GET OTHER USER ACTIVITIES [ADMIN, DOSEN, KATING]
  @GetMapping(path = "/activities/{username}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<SimpleActivityResponse>> getOtherUserActivities(User user,
                                                                                      @PathVariable("username") String username,
                                                                                      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                                      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){

    PagingRequest request = new PagingRequest();
    request.setPage(page);
    request.setSize(size);
    request.setUsername(username);

    Page<SimpleActivityResponse> pages = userService.getOtherUserActivities(user, request);


    return WebResponseWithPaging.<List<SimpleActivityResponse>>builder()
            .data(pages.getContent())
            .pagination(Utils.getPagingResponse(pages))
            .message("Success get other activities")
            .build();
  }

  // GET USER BY ID [ALL ROLES]
  @GetMapping(path="/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> getUserById(@PathVariable("username") String username){

    UserResponse response = userService.getUserById(username);
    return WebResponse.<UserResponse>builder()
            .data(response)
            .message("Success get user with id " + username)
            .build();
  }

  // GET CURRENT USER ACTIVITIES [CURRENT USER ONLY]
  @GetMapping(path = "/activities",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<SimpleActivityResponse>> getUserCurrentActivities(User user,
                                                      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){

    PagingRequest request = new PagingRequest();
    request.setPage(page);
    request.setSize(size);

    Page<SimpleActivityResponse> pages = userService.getUserCurrentActivities(user, request);


    return WebResponseWithPaging.<List<SimpleActivityResponse>>builder()
            .data(pages.getContent())
            .pagination(Utils.getPagingResponse(pages))
            .message("Success get current activities")
            .build();
  }

  // GET LIST OF USERS [ADMIN, TUTOR, DOSEN, KATING]
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<UserResponse>> search (User user,
                                                           @RequestParam(name = "identity", required = false) String identity,
                                                           @RequestParam(name = "major", required = false) String major,
                                                           @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                           @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

    SearchUserRequest request = SearchUserRequest.builder()
            .identity(identity)
            .major(major)
            .size(size)
            .page(page)
            .build();

    Page<UserResponse> pages = userService.searchUser(user, request);

    return WebResponseWithPaging.<List<UserResponse>>builder()
            .data(pages.getContent())
            .message("Search Success")
            .pagination(Utils.getPagingResponse(pages))
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

  // DELETE USER WITH ID [ADMIN ONLY]
  @DeleteMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> deleteUserWithId(User user, @PathVariable("username") String username){

    userService.deleteUserWithId(user, username);
    return WebResponse.<String>builder()
            .data("OK")
            .message("User deleted successfuly")
            .build();
  }
}
