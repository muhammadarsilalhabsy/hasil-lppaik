package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.model.request.*;
import com.hasil.lppaik.model.response.SimpleActivityResponse;
import com.hasil.lppaik.model.response.UserResponse;
import com.hasil.lppaik.repository.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private final Utils utils;

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final ImageServiceImpl imageService;

  private final CertificateServiceImpl certificateService;

  private final ActivityRepository activityRepository;
  private final MajorRepository majorRepository;

  private final ControlBookDetailRepository cbdRepository;
  private final CertificateRepository certificateRepository;

  @Autowired
  public UserServiceImpl(Utils utils, UserRepository userRepository, RoleRepository roleRepository, ImageServiceImpl imageService, CertificateServiceImpl certificateService, ActivityRepository activityRepository,
                         MajorRepository majorRepository, ControlBookDetailRepository cbdRepository,
                         CertificateRepository certificateRepository) {
    this.utils = utils;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.imageService = imageService;
    this.certificateService = certificateService;
    this.activityRepository = activityRepository;
    this.majorRepository = majorRepository;
    this.cbdRepository = cbdRepository;
    this.certificateRepository = certificateRepository;
  }

  @Override
  public UserResponse getUserById(String username) {
    User user = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is NOT FOUND"));

    return utils.getUserResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SimpleActivityResponse> getOtherUserActivities(User user, PagingRequest request) {

    int page = request.getPage() - 1;

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

    Pageable pageable = PageRequest.of(page, request.getSize(), Sort.by("date").descending());
    Page<Activity> activities = activityRepository.findAll(specification, pageable);
    List<SimpleActivityResponse> response = activities.getContent().stream()
            .map(utils::activityToSimpleActivityResponse).collect(Collectors.toList());

    return new PageImpl<>(response, pageable, activities.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SimpleActivityResponse> getUserCurrentActivities(User user, PagingRequest request) {

    int page = request.getPage() - 1;

    Specification<Activity> specification = (root, query, builder) -> {

      Join<Activity, User> current = root.join("users");

      return query.where(builder.equal(current.get("username"), user.getUsername())).getRestriction();

    };

    Pageable pageable = PageRequest.of(page, request.getSize(), Sort.by("date").descending());
    Page<Activity> activities = activityRepository.findAll(specification, pageable);
    List<SimpleActivityResponse> response = activities.getContent().stream()
            .map(utils::activityToSimpleActivityResponse).collect(Collectors.toList());

    return new PageImpl<>(response, pageable, activities.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResponse> searchUser(User user, SearchUserRequest request) {

    int page = request.getPage() - 1;

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

    Pageable pageable = PageRequest.of(page, request.getSize());
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

    if(Objects.nonNull(request.getPassword()) && !request.getPassword().isBlank()){
      candidate.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
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

    Major major = majorRepository.findById(request.getMajor()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Major with id %s is NOT FOUND", request.getMajor())));

    candidate.setMajor(major);


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

  @Override
  @Transactional
  public void createUser(User user, CreateUserRequest request) {
    utils.validate(request);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    User isExist = userRepository.findById(request.getUsername()).orElse(null);
    if(Objects.nonNull(isExist)){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This User already created");
    }
    User newUser = new User();

    newUser.setUsername(request.getUsername());
    newUser.setName(request.getName());
    newUser.setEmail(request.getEmail());
    newUser.setPassword(BCrypt.hashpw(request.getUsername(), BCrypt.gensalt()));
    Major major = majorRepository.findById(request.getMajor()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Major is not found")
    );
    newUser.setMajor(major);
    if(Gender.isValidGender(request.getGender())) {
      newUser.setGender(Gender.valueOf(request.getGender()));
    }else{
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Gender input");
    }

    for (RoleEnum role : request.getRoles()) {
      Role exsistingRole = roleRepository.findByName(role)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Role input"));
      newUser.getRoles().add(exsistingRole);
    }

    userRepository.save(newUser);
  }

  @Override
  @Transactional
  public void deleteUserWithId(User user, String username) {

    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    User candidate = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is NOT FOUND"));

    if(candidate.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.TUTOR))){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't delete ADMIN or TUTOR, it will break the application");
    }

    Set<Activity> activities = candidate.getActivities();
    if (Objects.nonNull(activities) && !activities.isEmpty()) {
      candidate.getActivities().clear(); // Hapus semua aktivitas
    }

    List<ControlBookDetail> cbd = candidate.getControlBookDetailUser();
    if (Objects.nonNull(cbd) && !cbd.isEmpty()) {
      cbdRepository.deleteAll(cbd); // Hapus semua ControlBookDetail dari basis data
    }

    Certificate certificate = candidate.getCertificate();
    if (Objects.nonNull(certificate)) {
      certificateRepository.delete(certificate); // Hapus sertifikat dari basis data
    }

    Set<Role> roles = candidate.getRoles();
    if (Objects.nonNull(roles) && !roles.isEmpty()) {
      candidate.getRoles().clear(); // Hapus semua peran
    }

    userRepository.delete(candidate); // Hapus pengguna setelah menghapus semua elemen yang terkait

    // find all user activity then remove all, (putuskan relasi)
    // minr
//    Set<Activity> activities = candidate.getActivities();
//    if(Objects.nonNull(activities) && activities.size() != 0) {
//      candidate.getActivities().removeAll(activities);
//    }
//
//    List<ControlBookDetail> cbd = candidate.getControlBookDetailUser();
//    if(Objects.nonNull(cbd) && cbd.size() != 0) {
//      cbdRepository.deleteAll(cbd);
//    }
//
//    Certificate certificate = candidate.getCertificate();
//    if(Objects.nonNull(certificate)){
//      certificateRepository.delete(certificate);
//    }
//
//    Set<Role> roles = candidate.getRoles();
//    if(Objects.nonNull(roles) && roles.size() != 0) {
//      for (Role role : roles) {
//        candidate.getRoles().remove(role);
//      }
//    }


    userRepository.delete(candidate);

  }
}
