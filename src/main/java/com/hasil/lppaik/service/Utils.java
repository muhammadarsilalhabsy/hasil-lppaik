package com.hasil.lppaik.service;


import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.model.request.RegisterUserRequest;
import com.hasil.lppaik.model.response.*;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.security.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
  private final RoleRepository roleRepository;

  @Autowired
  public Utils(Validator validator, MajorRepository majorRepository,
               RoleRepository roleRepository) {
    this.validator = validator;
    this.majorRepository = majorRepository;
    this.roleRepository = roleRepository;
  }

  public static PagingResponse getPagingResponse(Page<?> page){
    return PagingResponse.builder()
            .page(page.getNumber()) // current page
            .totalItems(page.getContent().size()) // get total items
            .pageSize(page.getTotalPages()) // total page keseluruhan
            .size(page.getSize())
            .build();
  }

  public SimpleActivityResponse activityToSimpleActivityResponse(Activity activity){
    SimpleActivityResponse response = new SimpleActivityResponse();
    response.setId(activity.getId());
    response.setTitle(activity.getTitle());
    response.setDate(activity.getDate());
    if(activity.getImages().size() != 0){
    response.setImage(activity.getImages().get(0).getImage());
    }

    return response;
  }

  public static boolean parseBoolean(String s) {
    return "true".equalsIgnoreCase(s);
  }
  public ActivityResponse activityToActivityResponse(Activity activity){

    ActivityResponse response = new ActivityResponse();
    response.setId(activity.getId());
    response.setDate(activity.getDate());
    response.setTitle(activity.getTitle());
    response.setEndTime(activity.getEndTime());
    response.setLocation(activity.getLocation());
    response.setStartTime(activity.getStartTime());
    response.setDescription(activity.getDescription());
    if(activity.getImages().size() != 0) {
      response.setImages(activity.getImages().stream().map(ActivityImage::getImage)
              .collect(Collectors.toList()));
    }
    return response;
  }
  public ControlBookDetailResponse cbdToCbdResponse(ControlBookDetail detail){
    return ControlBookDetailResponse.builder()
            .id(detail.getId())
            .date(detail.getDate())
            .lesson(detail.getLesson())
            .tutor(detail.getTutor().getName())
            .description(detail.getDescription())
            .build();
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

    Role role = roleRepository.findByName(RoleEnum.MAHASISWA).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Role is not found")
    );

    User user = new User();
    user.setUsername(request.getUsername());
    user.setName(request.getName());
    user.setPassword(BCrypt.hashpw(request.getUsername(), BCrypt.gensalt()));
    user.setEmail(request.getEmail());
    user.setMajor(major);
    user.setGender(request.getGender());
    user.getRoles().add(role);
    return user;
  }

  public SimpleUserResponse userToSimpleUser(User user){
    SimpleUserResponse response = new SimpleUserResponse();
    response.setName(user.getName());
    response.setEmail(user.getEmail());
    response.setAvatar(user.getAvatar());
    response.setUsername(user.getUsername());
    response.setCompleted(user.getCompleted());
    if(Objects.nonNull(user.getMajor())){
      response.setMajor(user.getMajor().getName());
    }
    return response;
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
