package com.hasil.lppaik.controller;


import com.hasil.lppaik.model.response.CertificateResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.service.CertificateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/certificate")
public class CertificateController {


  private final CertificateServiceImpl certificateService;

  @Autowired
  public CertificateController(CertificateServiceImpl certificateService) {
    this.certificateService = certificateService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<CertificateResponse> getUserCertificateWithId(@RequestParam("id") String id){

    CertificateResponse certificate = certificateService.getUserCertificateWithId(id);
    return WebResponse.<CertificateResponse>builder()
            .data(certificate)
            .message("Success get certificate")
            .build();
  }
}
