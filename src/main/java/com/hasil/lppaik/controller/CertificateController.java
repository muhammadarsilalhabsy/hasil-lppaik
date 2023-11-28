package com.hasil.lppaik.controller;


import com.google.zxing.WriterException;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.CertificateResponse;
import com.hasil.lppaik.model.response.SimpleUserResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.service.CertificateServiceImpl;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("api/v1/certificate")
public class CertificateController {

  public static final String DIRECTORY = System.getenv("PWD").concat("/assets/");
  private final CertificateServiceImpl certificateService;

  @Autowired
  public CertificateController(CertificateServiceImpl certificateService) {
    this.certificateService = certificateService;
  }

  // ALL ROLES
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<SimpleUserResponse> getUserCertificateWithId(@RequestParam("id") String id){

    SimpleUserResponse response = certificateService.getUserCertificateWithId(id);
    return WebResponse.<SimpleUserResponse>builder()
            .data(response)
            .message("Success get certificate")
            .build();
  }

  @GetMapping("/download")
  public ResponseEntity<byte[]> downloadFile(User user) throws IOException, WriterException {

    Resource resource = certificateService.download(user);

    byte[] data = IOUtils.toByteArray(resource.getInputStream());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "certificate.pdf");

    return new ResponseEntity<>(data, headers, HttpStatus.OK);
  }
}
