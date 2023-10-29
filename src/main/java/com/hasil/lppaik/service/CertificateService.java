package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.CertificateResponse;

public interface CertificateService {

  CertificateResponse getUserCertificateWithId(String id);
  void createCertificate(User user);

  void removeCertificate(String id);
}
