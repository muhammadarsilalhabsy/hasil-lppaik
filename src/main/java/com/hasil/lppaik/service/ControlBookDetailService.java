package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateControlBookDetailRequest;
import com.hasil.lppaik.model.request.PagingRequest;
import com.hasil.lppaik.model.request.UpdateControlBookDetailRequest;
import com.hasil.lppaik.model.response.ControlBookDetailResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public interface ControlBookDetailService {

  void addCBD(User user, CreateControlBookDetailRequest request);

  void updateCBD(User user, UpdateControlBookDetailRequest request);

  void deleteCBD(User user, String id);

  Page<ControlBookDetailResponse> getCurrentCBD(User user, PagingRequest request);

  Page<ControlBookDetailResponse> getOtherUserCBD(User user, PagingRequest request);

  ControlBookDetailResponse getCbdDetailWithId(String id);

  Resource download(User user) throws FileNotFoundException, MalformedURLException;
}
