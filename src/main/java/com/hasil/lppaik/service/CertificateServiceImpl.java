package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.Certificate;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.CertificateResponse;
import com.hasil.lppaik.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

  private final CertificateRepository certificateRepository;

  private final Utils utils;

  @Autowired
  public CertificateServiceImpl(CertificateRepository certificateRepository, Utils utils) {
    this.certificateRepository = certificateRepository;
    this.utils = utils;
  }

  @Override
  public CertificateResponse getUserCertificateWithId(String id) {
    Certificate certificate = certificateRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate is NOT FOUND!"));

    return CertificateResponse.builder()
            .certificate(certificate.getId())
            .user(utils.userToSimpleUser(certificate.getUser()))
            .build();
  }

  @Override
  public void createCertificate(User user){
    Certificate certificate = new Certificate();
    certificate.setId(UUID.randomUUID().toString());
    certificate.setUser(user);

    certificateRepository.save(certificate);
  }


  @Override
  public void removeCertificate(String id){
    Certificate certificate = certificateRepository.findById(id).orElse(null);

    if(Objects.nonNull(certificate)){
      certificateRepository.delete(certificate);
    }

  }
}
