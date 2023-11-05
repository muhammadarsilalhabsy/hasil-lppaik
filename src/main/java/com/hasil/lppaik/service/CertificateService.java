package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.CertificateResponse;
import com.hasil.lppaik.model.response.SimpleUserResponse;

public interface CertificateService {

  SimpleUserResponse getUserCertificateWithId(String id);
  void createCertificate(User user);

  void removeCertificate(String id);
}
