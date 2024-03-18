package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.UserActivityRegisterResponse;

import java.util.List;

public interface ActivityRegisterService {

  void register(User user, String activity);

  List<UserActivityRegisterResponse> getAllRegisterByActivityId(User user, String activity);

  void remove(User user, String id);
  void remove(User user);

  boolean isRegistered(User user, String activity);


}
