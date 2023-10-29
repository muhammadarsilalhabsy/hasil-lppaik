package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.RoleEnum;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.LoginRequest;
import com.hasil.lppaik.model.request.RegisterUserRequest;
import com.hasil.lppaik.model.response.LoginResponse;
import com.hasil.lppaik.repository.UserRepository;
import com.hasil.lppaik.security.BCrypt;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  private final Utils utils;

  @Autowired
  public AuthServiceImpl(UserRepository userRepository, Utils utils) {
    this.userRepository = userRepository;
    this.utils = utils;
  }

  @Override
  @Transactional
  public Integer registerUsers(User user, List<RegisterUserRequest> requestList) {

    utils.validate(requestList);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    List<User> users = requestList.stream().map(utils::registerUserRequestToUser)
            .toList();

    userRepository.saveAll(users);
    return requestList.size();
  }

  @Override
  @Transactional
  public LoginResponse loginUser(LoginRequest request) {

    utils.validate(request);

    User user = userRepository.findById(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This username maybe not exist"));
    if(BCrypt.checkpw(request.getPassword(), user.getPassword())){

      // success login
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(System.currentTimeMillis() + (36L * 1_00_000 * 24 * 30));
      userRepository.save(user);

      return LoginResponse.builder()
              .token(user.getToken())
              .tokenExpiredAt(user.getTokenExpiredAt())
              .user(utils.getUserResponse(user))
              .build();
    }else{
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You send wrong password!");
    }
  }

  @Override
  @Transactional
  public void logout(User user){
    user.setTokenExpiredAt(null);
    user.setToken(null);

    userRepository.save(user);
  }
}
