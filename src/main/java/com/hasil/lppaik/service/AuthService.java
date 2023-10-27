package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.LoginRequest;
import com.hasil.lppaik.model.request.RegisterUserRequest;
import com.hasil.lppaik.model.response.LoginResponse;

import java.util.List;

public interface AuthService {

  Integer registerUsers(User user, List<RegisterUserRequest> requestList);
  LoginResponse loginUser(LoginRequest request);

  void logout(User user);
}
