package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.*;
import com.hasil.lppaik.model.response.SimpleActivityResponse;
import com.hasil.lppaik.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

  // GET

  Page<UserResponse> searchUser(User user, SearchUserRequest request);

  Page<SimpleActivityResponse> getUserCurrentActivities(User user, PagingRequest request);

  // PATCH

  String updateUserWithId(User user, UpdateUserRequest request);

  void updateCurrentUserDetail(User user, UpdateUserDetailRequest request);

  void updateCurrentUserPassword(User user, UpdateUserPasswordRequest request);

  void updateCurrentUserAvatar(User user, MultipartFile file) throws IOException;
}
