package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.Activity;
import com.hasil.lppaik.entity.ActivityRegister;
import com.hasil.lppaik.entity.RoleEnum;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.UserActivityRegisterResponse;
import com.hasil.lppaik.repository.ActivityRegisterRepository;
import com.hasil.lppaik.repository.ActivityRepository;
import com.hasil.lppaik.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityRegisterServiceImpl implements ActivityRegisterService {

  private final ActivityRegisterRepository activityRegRepo;
  private final UserRepository userRepo;
  private final ActivityRepository activityRepo;

  private final Utils utils;

  @Autowired
  public ActivityRegisterServiceImpl(ActivityRegisterRepository activityRegRepo, UserRepository userRepo, ActivityRepository activityRepo, Utils utils) {
    this.activityRegRepo = activityRegRepo;
    this.userRepo = userRepo;
    this.activityRepo = activityRepo;
    this.utils = utils;
  }

  @Override
  public void register(User user, String activity) {

    User candidate = userRepo.findById(user.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + user.getUsername() + " is NOT FOUND"));

    Activity activityTarget = activityRepo.findById(activity)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + activity + " is NOT FOUND"));

    // Check if the user is already registered for the activity
    boolean isAlreadyRegistered = activityRegRepo.existsByUserAndActivity(candidate, activityTarget);
    boolean isUserAlreadyRegistered = activityRepo.existsByUsersAndId(candidate, activity);

    if(isAlreadyRegistered || isUserAlreadyRegistered){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already registered!");
    }

    ActivityRegister register = new ActivityRegister();
    register.setId(UUID.randomUUID().toString());
    register.setUser(candidate);
    register.setActivity(activityTarget);

    activityRegRepo.save(register);

  }

  @Override
  public List<UserActivityRegisterResponse> getAllRegisterByActivityId(User user, String activity) {
    Activity activityTarget = activityRepo.findById(activity)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + activity + " is NOT FOUND"));

    // validate if user doesn't contain role 'ADMIN || KETING || DOSEN
    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.DOSEN)
                    || role.getName().equals(RoleEnum.KATING));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    List<ActivityRegister> activityRegisters = activityRegRepo.findByActivityId(activityTarget.getId());
    return activityRegisters.stream()
            .map(activityRegister -> utils.userToActivityRegisterResponse(
                    activityRegister.getUser(),
                    activityRegister.getId()))
            .collect(Collectors.toList());
  }

  @Override
  public void remove(User user, String id) {
    // validate if user doesn't contain role 'ADMIN || KETING
    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN) || role.getName().equals(RoleEnum.KATING));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    ActivityRegister register = activityRegRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Activity register NOT FOUND"));

    activityRegRepo.delete(register);
  }

  @Override
  public boolean isRegistered(User user, String activity) {

    User candidate = userRepo.findById(user.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + user.getUsername() + " is NOT FOUND"));

    Activity activityTarget = activityRepo.findById(activity)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + activity + " is NOT FOUND"));

    // Check if the user is already registered for the activity

    return activityRepo.existsByUsersAndId(candidate, activity) ||
           activityRegRepo.existsByUserAndActivity(candidate, activityTarget);
  }
}
