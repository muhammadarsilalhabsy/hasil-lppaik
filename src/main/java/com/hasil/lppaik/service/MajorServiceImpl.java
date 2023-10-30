package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.Major;
import com.hasil.lppaik.entity.RoleEnum;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateMajorRequest;
import com.hasil.lppaik.model.request.UpdateMajorRequest;
import com.hasil.lppaik.model.response.MajorResponse;
import com.hasil.lppaik.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MajorServiceImpl implements MajorService {

  private final MajorRepository majorRepository;

  private final Utils utils;

  @Autowired
  public MajorServiceImpl(MajorRepository majorRepository, Utils utils) {
    this.majorRepository = majorRepository;
    this.utils = utils;
  }

  @Override
  @Transactional(readOnly = true)
  public List<MajorResponse> getAllMajor() {
    return majorRepository.findAll().stream()
            .map( major -> MajorResponse.builder()
            .id(major.getId())
            .name(major.getName())
            .build())
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteMajor(User user, String id) {


    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Major major = majorRepository.findById(id)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Major with id %s is NOT FOUND", id)));

    majorRepository.delete(major);

    // kalau majornya ada yang refrence
    // apa bila di delete majornya
    // akan break aplikasinya. so make sure tidak break aplikasinya.
  }

  @Override
  @Transactional
  public void updateMajor(User user, UpdateMajorRequest request) {
    utils.validate(request);
    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Major major = majorRepository.findById(request.getId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Major with id %s is NOT FOUND", request.getId())));

    if(Objects.nonNull(request.getName())){
      major.setName(request.getName());
    }

    majorRepository.save(major);

  }

  @Override
  @Transactional
  public void createMajor(User user, CreateMajorRequest request) {

    utils.validate(request);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Major major = new Major();

    major.setId(request.getId());
    major.setName(request.getName());

    majorRepository.save(major);
  }
}
