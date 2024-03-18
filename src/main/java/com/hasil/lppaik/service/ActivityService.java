package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateActivityRequest;
import com.hasil.lppaik.model.request.PagingRequest;
import com.hasil.lppaik.model.request.SearchActivityRequest;
import com.hasil.lppaik.model.request.UpdateActivityRequest;
import com.hasil.lppaik.model.response.ActivityResponse;
import com.hasil.lppaik.model.response.UserResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface ActivityService {

  ActivityResponse getActivityById(String id);

  void createActivity(User user, CreateActivityRequest request);

  void updateActivity(User user, UpdateActivityRequest request);

  void deleteActivity(User user, String id);

  void addActivityToOtherUser(User user, String id, String username, String regId);

  void addActivityToUser(User user, String id, String username);

  Page<ActivityResponse> getAllActivities(SearchActivityRequest request);

  Page<UserResponse> getAttendance(User user, PagingRequest request);

  void removeUserFromActivity(User user, String id, String username);

  Resource downloadCurrentUserActivity(User user) throws IOException;
}
