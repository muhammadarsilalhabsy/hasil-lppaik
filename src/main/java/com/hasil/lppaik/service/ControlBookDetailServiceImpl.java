package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.model.request.CreateControlBookDetailRequest;
import com.hasil.lppaik.model.request.PagingRequest;
import com.hasil.lppaik.model.request.UpdateControlBookDetailRequest;
import com.hasil.lppaik.model.response.ControlBookDetailResponse;
import com.hasil.lppaik.repository.ControlBookDetailRepository;
import com.hasil.lppaik.repository.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ControlBookDetailServiceImpl implements ControlBookDetailService {

  private final Utils utils;

  private final ControlBookDetailRepository cbdRepository;

  private final UserRepository userRepository;

  @Autowired
  public ControlBookDetailServiceImpl(Utils utils, ControlBookDetailRepository cbdRepository, UserRepository userRepository) {
    this.utils = utils;
    this.cbdRepository = cbdRepository;
    this.userRepository = userRepository;
  }

  @Override
  public ControlBookDetailResponse getCbdDetailWithId(String id) {
    ControlBookDetail cbd = cbdRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("control book detail with id %s is NOT FOUND", id)));

    return utils.cbdToCbdResponse(cbd);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ControlBookDetailResponse> getOtherUserCBD(User user, PagingRequest request) {

    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.TUTOR)
                    || role.getName().equals(RoleEnum.DOSEN));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    User student = userRepository.findById(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("user with id %s is NOT FOUND", request.getUsername())));

    Specification<ControlBookDetail> specification = (root, query, builder) -> {

      Join<ControlBookDetail, User> current = root.join("user");

      return query.where(builder.equal(current.get("username"), student.getUsername())).getRestriction();

    };
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("date").descending());
    Page<ControlBookDetail> cbds = cbdRepository.findAll(specification, pageable);
    List<ControlBookDetailResponse> cbdResponse = cbds.getContent().stream()
            .map(detail -> utils.cbdToCbdResponse(detail)).collect(Collectors.toList());

    return new PageImpl<>(cbdResponse, pageable, cbds.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ControlBookDetailResponse> getCurrentCBD(User user, PagingRequest request) {


    Specification<ControlBookDetail> specification = (root, query, builder) -> {

      Join<ControlBookDetail, User> current = root.join("user");

//      return query.where(builder.equal(root.get("user"), user.getUsername())).getRestriction(); // opsi 1

      return query.where(builder.equal(current.get("username"), user.getUsername())).getRestriction();

    };
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("date").descending());
    Page<ControlBookDetail> cbds = cbdRepository.findAll(specification, pageable);
    List<ControlBookDetailResponse> cbdResponse = cbds.getContent().stream()
            .map(detail -> utils.cbdToCbdResponse(detail)).collect(Collectors.toList());

    return new PageImpl<>(cbdResponse, pageable, cbds.getTotalElements());
  }

  @Override
  @Transactional
  public void addCBD(User user, CreateControlBookDetailRequest request) {

    utils.validate(request);

    // validate if user doesn't contain role 'ADMIN || TUTOR'
    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.TUTOR));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    User student = userRepository.findById(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("user with id %s is NOT FOUND", request.getUsername())));


    ControlBookDetail controlBookDetail = new ControlBookDetail();

    if(Objects.nonNull(request.getDate())){
      controlBookDetail.setDate(request.getDate());
    }else{
      controlBookDetail.setDate(LocalDate.now());
    }

    controlBookDetail.setId(UUID.randomUUID().toString());
    controlBookDetail.setLesson(request.getLesson());
    controlBookDetail.setDescription(request.getDescription());
    controlBookDetail.setTutor(user);
    controlBookDetail.setUser(student);

    cbdRepository.save(controlBookDetail);

  }

  @Override
  @Transactional
  public void updateCBD(User user, UpdateControlBookDetailRequest request) {

    utils.validate(request);

    // validate if user doesn't contain role 'ADMIN || TUTOR'
    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.TUTOR));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    ControlBookDetail cbd = cbdRepository.findById(request.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("control book detail with id %s is NOT FOUND", request.getId())));

    if(Objects.nonNull(request.getDescription())){
      cbd.setDescription(request.getDescription());
    }

    if(Objects.nonNull(request.getLesson())){
      cbd.setLesson(request.getLesson());
    }

    if(Objects.nonNull(request.getDate())){
      cbd.setDate(request.getDate());
    }

    cbd.setTutor(user);

    // tidak perlu update pemiliki (student) nya

   cbdRepository.save(cbd);
  }

  @Override
  public void deleteCBD(User user, String id) {

    // validate if user doesn't contain role 'ADMIN || TUTOR'
    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.TUTOR));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    ControlBookDetail cbd = cbdRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("control book detail with id %s is NOT FOUND", id)));

    cbdRepository.delete(cbd);
  }
}
