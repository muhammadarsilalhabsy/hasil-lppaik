package com.hasil.lppaik.service;


import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateMajorRequest;
import com.hasil.lppaik.model.request.UpdateMajorRequest;
import com.hasil.lppaik.model.response.MajorResponse;

import java.util.List;

public interface MajorService {

  List<MajorResponse> getAllMajor();

  void deleteMajor(User user, String id);

  void updateMajor(User user, UpdateMajorRequest request);

  void createMajor(User user, CreateMajorRequest request);
}
