package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.AcceptEmailRequest;
import com.hasil.lppaik.model.request.EmailRequest;
import com.hasil.lppaik.model.response.EmailExpiredResponse;
import com.hasil.lppaik.model.response.SimpleEmailVerifyResponse;
import com.hasil.lppaik.model.response.SimpleUserResponse;

public interface EmailVerificationService {

  String sendEmailRequest(User user, String candidateId);
  void sendHtmlEmail(EmailRequest request);

  void acceptEmail(AcceptEmailRequest request);

  void remove(String id);

  boolean isExpired(Long time);

  SimpleEmailVerifyResponse findById(String id);

  EmailExpiredResponse emailExpired(String username);
}
