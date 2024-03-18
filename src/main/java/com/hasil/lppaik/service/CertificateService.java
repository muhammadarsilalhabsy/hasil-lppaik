package com.hasil.lppaik.service;

import com.google.zxing.WriterException;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.CertificateResponse;
import com.hasil.lppaik.model.response.SimpleUserResponse;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public interface CertificateService {

  SimpleUserResponse getUserCertificateWithId(String id);
  void createCertificate(User user);

  void removeCertificate(String id);

  Resource download(User user) throws IOException, WriterException;
}
