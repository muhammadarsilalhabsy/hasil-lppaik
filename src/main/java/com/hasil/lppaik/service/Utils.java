package com.hasil.lppaik.service;


import com.hasil.lppaik.entity.Major;
import com.hasil.lppaik.entity.Role;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.RegisterUserRequest;
import com.hasil.lppaik.model.response.SimpleUserResponse;
import com.hasil.lppaik.model.response.UserResponse;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.security.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class Utils {

  private final Validator validator;

  private final MajorRepository majorRepository;
  @Autowired
  public Utils(Validator validator, MajorRepository majorRepository) {
    this.validator = validator;
    this.majorRepository = majorRepository;
  }

  public void validate(Object request){
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
    if (constraintViolations.size() != 0){
      throw new ConstraintViolationException(constraintViolations);
    }
  }

  public User registerUserRequestToUser(RegisterUserRequest request){
    Major major = majorRepository.findById(request.getMajor()).orElseThrow(() ->
              new ResponseStatusException(HttpStatus.NOT_FOUND, "Major is not found")
            );

      User user = new User();
      user.setUsername(request.getUsername());
      user.setName(request.getName());
      user.setPassword(BCrypt.hashpw(request.getUsername(), BCrypt.gensalt()));
      user.setEmail(request.getEmail());
      user.setMajor(major);
      user.setGender(request.getGender());
    return user;
  }

  public SimpleUserResponse userToSimpleUser(User user){
    return SimpleUserResponse.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .avatar(user.getAvatar())
            .name(user.getName())
            .major(user.getMajor().getName())
            .completed(user.getCompleted())
            .build();
  }
  public UserResponse getUserResponse(User user){
    return UserResponse.builder()
            .username(user.getUsername())
            .motto(user.getMotto())
            .email(user.getEmail())
            .gender(user.getGender())
            .avatar(user.getAvatar())
            .name(user.getName())
            .major(user.getMajor().getName())
            .completed(user.getCompleted())
            .roles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()))
            .build();
  }
  public static byte[] compressImage(byte[] data) {
    Deflater deflater = new Deflater();

    deflater.setLevel(Deflater.BEST_COMPRESSION);
    deflater.setInput(data);
    deflater.finish();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] temp = new byte[4 * 1024];

    while (!deflater.finished()) {
      int size = deflater.deflate(temp);
      outputStream.write(temp, 0, size);
    }

    try {
      outputStream.close();
    } catch (Exception ignore) {
    }

    return outputStream.toByteArray();

  }

  public static byte[] decompressImage(byte[] data) {
    Inflater inflater = new Inflater();
    inflater.setInput(data);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] temp = new byte[4 * 1024];

    try {
      while (!inflater.finished()) {
        int count = inflater.inflate(temp);
        outputStream.write(temp, 0, count);
      }
      outputStream.close();
    } catch (Exception ignore) {
    }
    return outputStream.toByteArray();
  }

  public static String nameConversion(MultipartFile file){
    LocalDate date = LocalDate.now();
    LocalTime time = LocalTime.now();

    String tanggal = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    String jam = time.format(DateTimeFormatter.ofPattern("HH-mm"));

    return tanggal + "-" + jam + "-" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s","-");
  }
}
