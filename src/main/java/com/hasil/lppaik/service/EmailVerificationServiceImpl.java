package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.EmailVerification;
import com.hasil.lppaik.entity.RoleEnum;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.AcceptEmailRequest;
import com.hasil.lppaik.model.request.EmailRequest;
import com.hasil.lppaik.model.response.EmailExpiredResponse;
import com.hasil.lppaik.model.response.SimpleEmailVerifyResponse;
import com.hasil.lppaik.repository.EmailVerificationRepository;
import com.hasil.lppaik.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Objects;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

  // static
  public static final String SUBJECT_EMAIL = "Persetujuan Kelulusan BTQ";
  public static final String UTF_8_ENCODING = "UTF-8";
//  private static final String host = "https://my-lppaik.netlify.app";
  private static final String host = "http://localhost:5173";
  private static final String FROM_EMAIL = "tumbalku00000@gmail.com"; // harusnya di simpan di .properties
  private static final String TO_EMAIL = "meaku00000@gmail.com"; // harusnya di simpan di .properties


  // service

  private final EmailVerificationRepository emailVerificationRepository;
  private final CertificateServiceImpl certificateService;
  private final UserRepository userRepository;

  private final TemplateEngine templateEngine;
  private final JavaMailSender sender;
  private final Utils utils;

  @Autowired
  public EmailVerificationServiceImpl(EmailVerificationRepository emailVerificationRepository, CertificateServiceImpl certificateService, UserRepository userRepository, Utils utils, TemplateEngine templateEngine, JavaMailSender sender) {
    this.emailVerificationRepository = emailVerificationRepository;
    this.certificateService = certificateService;
    this.userRepository = userRepository;
    this.templateEngine = templateEngine;
    this.sender = sender;
    this.utils = utils;
  }

  public static String getVerification(String host, String token) {
    return host + "/email/verify?token=" + token;
  }

  @Override
  @Transactional(readOnly = true)
  public SimpleEmailVerifyResponse findById(String id) {
    EmailVerification emailVerification = emailVerificationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found!"));
    return utils.userToSimpleUser(emailVerification.getUser(),
            emailVerification.getId(),
            emailVerification.getExpired());
  }

  @Override
  @Transactional(readOnly = true)
  public EmailExpiredResponse emailExpired(String username){
    EmailVerification email = emailVerificationRepository.findByUserUsername(username)
            .orElse(null);

    if(!Objects.nonNull(email)){
       return EmailExpiredResponse.builder()
               .id(null)
               .expired(null)
               .build();
    }

    return EmailExpiredResponse.builder()
            .id(email.getId())
            .expired(email.getExpired())
            .build();
  }
  @Override
  @Transactional
  public String sendEmailRequest(User user, String candidateId){

    boolean isAllow = user.getRoles().stream()
            .anyMatch(role ->
                    role.getName().equals(RoleEnum.ADMIN) ||
                            role.getName().equals(RoleEnum.TUTOR));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    User candidate = userRepository.findById(candidateId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + candidateId + " is NOT FOUND"));

    if(Objects.nonNull(candidate)){

      EmailVerification emailVerification = new EmailVerification();
      emailVerification.setId(UUID.randomUUID().toString());
      emailVerification.setUser(candidate);
      emailVerification.setExpired(System.currentTimeMillis() + (5L * 1_00_000 * 24 * 30));

      emailVerificationRepository.save(emailVerification);

      EmailRequest request = new EmailRequest();
      request.setUserId(candidateId);
      request.setTutorName(user.getName());
      request.setUserName(candidate.getName());
      request.setToken(emailVerification.getId());

      sendHtmlEmail(request);
    }

    return "Email has been sent";
  }


  @Override
  @Async
  public void sendHtmlEmail(EmailRequest request) {
    try {
      Context context = new Context();
      context.setVariable("name", request.getUserName());
      context.setVariable("username", request.getUserId());
      context.setVariable("tutor", request.getTutorName());
      context.setVariable("url", getVerification(host, request.getToken()));

      String text = templateEngine.process("email", context);

      MimeMessage message = sender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

      helper.setPriority(1);
      helper.setSubject(SUBJECT_EMAIL);
      helper.setFrom(FROM_EMAIL);
      helper.setTo(TO_EMAIL);
      helper.setText(text, true);

      sender.send(message);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void acceptEmail(AcceptEmailRequest request) {

    // Cek apakah expired
    if(!isExpired(request.getExpired())){

      // kalau tidak expired lanjutkan
      User candidate = userRepository.findById(request.getUsername())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + request.getUsername() + " is NOT FOUND"));

      if (!candidate.getCompleted()) {
        candidate.setCompleted(true);
        certificateService.createCertificate(candidate);
        remove(request.getId());
      } else {
        remove(request.getId());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate already exists!");
      }

    }else{
      remove(request.getId());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email verification is expired!");
    }


  }

  @Override
  @Transactional
  public void remove(String id) {
    EmailVerification email = emailVerificationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid"));
    emailVerificationRepository.delete(email);
  }

  @Override
  public boolean isExpired(Long time) {
    return time < System.currentTimeMillis();
  }
}
