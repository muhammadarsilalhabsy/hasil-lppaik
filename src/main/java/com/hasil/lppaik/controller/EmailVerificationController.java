package com.hasil.lppaik.controller;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.AcceptEmailRequest;
import com.hasil.lppaik.model.response.EmailExpiredResponse;
import com.hasil.lppaik.model.response.SimpleEmailVerifyResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.service.EmailVerificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email/verify")
public class EmailVerificationController {

  private final EmailVerificationServiceImpl emailVerificationService;

  @Autowired
  public EmailVerificationController(EmailVerificationServiceImpl emailVerificationService) {
    this.emailVerificationService = emailVerificationService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<SimpleEmailVerifyResponse> getUser(@RequestParam("token") String token){

    SimpleEmailVerifyResponse response = emailVerificationService.findById(token);

    return WebResponse.<SimpleEmailVerifyResponse>builder()
            .data(response)
            .message("Success get verify user")
            .build();
  }
  @GetMapping(path = "{candidateId}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<EmailExpiredResponse> emailExpired(@PathVariable("candidateId") String candidateId){

    EmailExpiredResponse response = emailVerificationService.emailExpired(candidateId);

    return WebResponse.<EmailExpiredResponse>builder()
            .data(response)
            .message("OK")
            .build();
  }
  @PostMapping(
          path = "{candidateId}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> sendEmail(User user, @PathVariable("candidateId") String candidateId){

    String response = emailVerificationService.sendEmailRequest(user, candidateId);

    return WebResponse.<String>builder()
            .data(response)
            .message("Email has been sent")
            .build();
  }

  @PostMapping(
          path = "/accept",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> accept(@RequestBody AcceptEmailRequest request){

    emailVerificationService.acceptEmail(request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("accepted")
            .build();
  }

}
