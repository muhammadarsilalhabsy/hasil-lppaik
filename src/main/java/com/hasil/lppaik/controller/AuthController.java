package com.hasil.lppaik.controller;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.LoginRequest;
import com.hasil.lppaik.model.request.RegisterUserRequest;
import com.hasil.lppaik.model.response.LoginResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.service.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/auth")
@Validated
public class AuthController {

  private final AuthServiceImpl authService;

  @Autowired
  public AuthController(AuthServiceImpl authService) {
    this.authService = authService;
  }

  @PostMapping(
          path = "/register",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> registerUsers(User user, @RequestBody List<@Valid RegisterUserRequest> requestList){

    Integer userSize = authService.registerUsers(user, requestList);
    return WebResponse.<String>builder()
            .data("OK")
            .message("Success create " + userSize + " users")
            .build();
  }

  @PostMapping(
          path = "/login",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<LoginResponse> loginUser(@RequestBody LoginRequest request){
    LoginResponse res = authService.loginUser(request);

    return WebResponse.<LoginResponse>builder()
            .data(res)
            .message("Logged in Successfully")
            .build();
  }

  @DeleteMapping(
          path = "/logout",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> logout(User user){
    authService.logout(user);
    return WebResponse.<String>builder()
            .data("OK")
            .message("Logged out Successfully")
            .build();
  }
}
