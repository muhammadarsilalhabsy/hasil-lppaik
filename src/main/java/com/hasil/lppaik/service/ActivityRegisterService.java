package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.SimpleUserResponse;

import java.util.List;

public interface ActivityRegisterService {

  void register(User user, String activity);

  List<SimpleUserResponse> getAllRegisterByActivityId(User user, String activity);

  void remove(User user, String id);

  boolean isRegistered(User user, String activity);


}
