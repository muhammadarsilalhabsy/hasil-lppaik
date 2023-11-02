package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.model.request.*;
import com.hasil.lppaik.model.response.ControlBookDetailResponse;
import com.hasil.lppaik.model.response.SimpleActivityResponse;
import com.hasil.lppaik.model.response.SimpleUserResponse;
import com.hasil.lppaik.model.response.UserResponse;
import com.hasil.lppaik.repository.ActivityRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;
import com.hasil.lppaik.security.BCrypt;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private final Utils utils;

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final ImageServiceImpl imageService;

  private final CertificateServiceImpl certificateService;

  private final ActivityRepository activityRepository;

  @Autowired
  public UserServiceImpl(Utils utils, UserRepository userRepository, RoleRepository roleRepository, ImageServiceImpl imageService, CertificateServiceImpl certificateService, ActivityRepository activityRepository) {
    this.utils = utils;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.imageService = imageService;
    this.certificateService = certificateService;
    this.activityRepository = activityRepository;
  }

  @Override
  public SimpleUserResponse getUserById(String username) {
    User user = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is NOT FOUND"));

    return utils.userToSimpleUser(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SimpleActivityResponse> getOtherUserActivities(User user, PagingRequest request) {

    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.DOSEN)
                    || role.getName().equals(RoleEnum.KATING));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Specification<Activity> specification = (root, query, builder) -> {

      Join<Activity, User> current = root.join("users");

      return query.where(builder.equal(current.get("username"), request.getUsername())).getRestriction();

    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("date").descending());
    Page<Activity> activities = activityRepository.findAll(specification, pageable);
    List<SimpleActivityResponse> response = activities.getContent().stream()
            .map(utils::activityToSimpleActivityResponse).collect(Collectors.toList());

    return new PageImpl<>(response, pageable, activities.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SimpleActivityResponse> getUserCurrentActivities(User user, PagingRequest request) {

    Specification<Activity> specification = (root, query, builder) -> {

      Join<Activity, User> current = root.join("users");

      return query.where(builder.equal(current.get("username"), user.getUsername())).getRestriction();

    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("date").descending());
    Page<Activity> activities = activityRepository.findAll(specification, pageable);
    List<SimpleActivityResponse> response = activities.getContent().stream()
            .map(utils::activityToSimpleActivityResponse).collect(Collectors.toList());

    return new PageImpl<>(response, pageable, activities.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResponse> searchUser(User user, SearchUserRequest request) {

    utils.validate(request);

    // validate if user doesn't contain role 'ADMIN || KETING || DOSEN || TUTOR'
    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                            || role.getName().equals(RoleEnum.DOSEN)
                            || role.getName().equals(RoleEnum.TUTOR)
                            || role.getName().equals(RoleEnum.KATING));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    boolean isKATING = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.KATING));

    // query
    Specification<User> specification = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if(Objects.nonNull(request.getIdentity())){
        predicates.add(builder.or(
                        builder.equal(root.get("username"), request.getIdentity()),
                        builder.like(root.get("name"), "%" + request.getIdentity() + "%")));
      }

      if (Objects.nonNull(request.getMajor())){
        Join<User, Major> major = root.join("major");
        predicates.add(builder.equal(major.get("name"), request.getMajor()));
      }

      if(isKATING){
        Join<User, Role> role = root.join("roles");
        predicates.add(builder.and(
                builder.notEqual(role.get("name"), RoleEnum.ADMIN),
                builder.notEqual(role.get("name"), RoleEnum.DOSEN)
        ));
      }

//      if(isKATING){
//        Join<User, Role> role = root.join("roles");
//        predicates.add(
//                builder.and(
//                        builder.or(
//                                builder.equal(role.get("name"), RoleEnum.KATING),
//                                builder.equal(role.get("name"), RoleEnum.MAHASISWA)
//                        ),
//                        builder.or(
//                                builder.notEqual(role.get("name"), RoleEnum.ADMIN),
//                                builder.notEqual(role.get("name"), RoleEnum.DOSEN))
//                )
//        );
//      }

      ///-----------
      // Tambahkan kriteria untuk role yang diizinkan (RoleEnum.KATING) dan tidak memiliki Role ADMIN atau DOSEN
//      Join<User, Role> roleJoin = root.join("roles");
//      Expression<String> roleNameExpression = roleJoin.get("name");
//      Predicate roleAllowedPredicate = builder.equal(roleNameExpression, RoleEnum.KATING);
//
//      // Tambahkan kriteria untuk menghindari Role ADMIN atau DOSEN
//      Predicate roleNotAllowedPredicate = builder.or(
//              builder.notEqual(roleNameExpression, RoleEnum.ADMIN),
//              builder.notEqual(roleNameExpression, RoleEnum.DOSEN)
//      );
//
//      predicates.add(builder.and(roleAllowedPredicate, roleNotAllowedPredicate));
      ///-----------

      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
    Page<User> users = userRepository.findAll(specification, pageable);
    List<UserResponse> userResponses = users.getContent().stream()
            .map(person -> utils.getUserResponse(person))
            .collect(Collectors.toList());

    return new PageImpl<>(userResponses, pageable, users.getTotalElements());
  }

  @Override
  @Transactional
  public String updateUserWithId(User user, UpdateUserRequest request) {

    utils.validate(request);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    User candidate = userRepository.findById(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + request.getUsername() + " is NOT FOUND"));

    if(Objects.nonNull(request.getName())){
      candidate.setName(request.getName());
    }

    if(Objects.nonNull(request.getEmail())){
      candidate.setEmail(request.getEmail());
    }

    if(Objects.nonNull(request.getCompleted())){
      candidate.setCompleted(request.getCompleted());

      if(request.getCompleted()){

        // kalau dia true buat certificate baru
        if(!Objects.nonNull(candidate.getCertificate())) {
          certificateService.createCertificate(candidate);
        }

      }else{

        // kalau dia false cari dulu certificatenya, lalu hapus certificatenya kalau ada
        if(Objects.nonNull(candidate.getCertificate())){
          certificateService.removeCertificate(candidate.getCertificate().getId());
        }

      }
    }

    if(Objects.nonNull(request.getGender())){
      if(Gender.isValidGender(request.getGender())) {
        candidate.setGender(Gender.valueOf(request.getGender()));
      }else{
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Gender input");
      }
    }


    if(Objects.nonNull(request.getRoles()) && request.getRoles().size() != 0){
      candidate.getRoles().clear();
      for (RoleEnum role : request.getRoles()) {
        Role exsistingRole = roleRepository.findByName(role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Role input"));
        candidate.getRoles().add(exsistingRole);
      }
    }

    userRepository.save(candidate);

    return candidate.getUsername();
  }

  @Override
  @Transactional
  public void updateCurrentUserDetail(User user, UpdateUserDetailRequest request) {
    utils.validate(request);

    if(Objects.nonNull(request.getEmail())){
      user.setEmail(request.getEmail());
    }

    if(Objects.nonNull(request.getMotto())){
      user.setMotto(request.getMotto());
    }

    userRepository.save(user);

  }

  @Override
  @Transactional
  public void updateCurrentUserPassword(User user, UpdateUserPasswordRequest request) {

    if(!request.getNewPassword().equals(request.getConfirmNewPassword())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password and confirm password not match!");
    }

    user.setPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt()));
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void updateCurrentUserAvatar(User user, MultipartFile file) throws IOException {

    // supaya tidak buang buang memory
    if(Objects.nonNull(user.getAvatar())){
      imageService.removePrevImage(user.getAvatar());
    }
    user.setAvatar(imageService.saveImageToDb(file));
    userRepository.save(user);
  }
}
