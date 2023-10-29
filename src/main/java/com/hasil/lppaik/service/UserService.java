package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.SearchUserRequest;
import com.hasil.lppaik.model.request.UpdateUserDetailRequest;
import com.hasil.lppaik.model.request.UpdateUserPasswordRequest;
import com.hasil.lppaik.model.request.UpdateUserRequest;
import com.hasil.lppaik.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

  Page<UserResponse> searchUser(User user, SearchUserRequest request);

  String updateUserWithId(User user, UpdateUserRequest request);

  void updateCurrentUserDetail(User user, UpdateUserDetailRequest request);

  void updateCurrentUserPassword(User user, UpdateUserPasswordRequest request);

  void updateCurrentUserAvatar(User user, MultipartFile file) throws IOException;
}
