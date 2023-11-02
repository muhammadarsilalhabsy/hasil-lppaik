package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.model.request.CreateActivityRequest;
import com.hasil.lppaik.model.request.SearchActivityRequest;
import com.hasil.lppaik.model.request.UpdateActivityRequest;
import com.hasil.lppaik.model.response.ActivityResponse;
import com.hasil.lppaik.model.response.UserResponse;
import com.hasil.lppaik.repository.ActivityImageRepository;
import com.hasil.lppaik.repository.ActivityRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

  private final ActivityRepository activityRepository;

  private final ActivityImageRepository activityImageRepository;

  private final UserRepository userRepository;

  private final Utils utils;

  @Autowired
  public ActivityServiceImpl(ActivityRepository activityRepository, ActivityImageRepository activityImageRepository, UserRepository userRepository, Utils utils) {
    this.activityRepository = activityRepository;
    this.activityImageRepository = activityImageRepository;
    this.userRepository = userRepository;
    this.utils = utils;
  }

  @Override
  public Page<ActivityResponse> getAllActivities(SearchActivityRequest request) {
    utils.validate(request);

    // query
    Specification<Activity> specification = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if(Objects.nonNull(request.getTitle())){
        predicates.add(builder.like(root.get("title"),"%" + request.getTitle() +"%"));
      }

      if (Objects.nonNull(request.getMandatory())){
        predicates.add(builder.equal(root.get("mandatory"), request.getMandatory()));
      }


      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("date").descending());
    Page<Activity> activities = activityRepository.findAll(specification, pageable);
    List<ActivityResponse> activityResponse = activities.getContent().stream()
            .map(utils::activityToActivityResponse)
            .toList();

    return new PageImpl<>(activityResponse, pageable, activities.getTotalElements());
  }

  @Override
  @Transactional
  public void addActivityToOtherUser(User user, String id, String username) {

    boolean isAllow = user.getRoles()
            .stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN) ||
                    role.getName().equals(RoleEnum.TUTOR));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    User candidate = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is NOT FOUND"));

    candidate.getActivities().add(activity);

    userRepository.save(candidate);
  }

  @Override
  @Transactional
  public void updateActivity(User user, UpdateActivityRequest request) {
    utils.validate(request);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(request.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + request.getId() + " is NOT FOUND"));

    if(Objects.nonNull(request.getLocation())){
      activity.setLocation(request.getLocation());
    }
    if(Objects.nonNull(request.getDescription())){
      activity.setDescription(request.getDescription());
    }
    if(Objects.nonNull(request.getMandatory())){
      activity.setMandatory(request.getMandatory());
    }
    if(Objects.nonNull(request.getEndTime())){
      activity.setEndTime(request.getEndTime());
    }
    if(Objects.nonNull(request.getStartTime())){
      activity.setStartTime(request.getStartTime());
    }
    if(Objects.nonNull(request.getTitle())){
      activity.setTitle(request.getTitle());
    }
    if(Objects.nonNull(request.getDate())){
      activity.setDate(request.getDate());
    }


    if(Objects.nonNull(request.getImages()) && request.getImages().size() != 0){
      activityImageRepository.deleteAll(activity.getImages());

      for(String data : request.getImages()){
        ActivityImage activityImage = new ActivityImage();
        activityImage.setId(UUID.randomUUID().toString());
        activityImage.setImage(data);
        activityImage.setActivity(activity);
        activityImageRepository.save(activityImage);
      }
    }

    activityRepository.save(activity);

  }

  @Override
  @Transactional
  public void createActivity(User user, CreateActivityRequest request) {

    utils.validate(request);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = new Activity();
    activity.setId(UUID.randomUUID().toString());
    activity.setDescription(request.getDescription());
    activity.setMandatory(request.getMandatory());
    activity.setStartTime(request.getStartTime());
    activity.setLocation(request.getLocation());
    activity.setEndTime(request.getEndTime());
    activity.setTitle(request.getTitle());
    activity.setDate(request.getDate());

    activityRepository.save(activity);

    for(String data : request.getImages()){
      ActivityImage activityImage = new ActivityImage();
      activityImage.setId(UUID.randomUUID().toString());
      activityImage.setImage(data);
      activityImage.setActivity(activity);
      activityImageRepository.save(activityImage);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ActivityResponse getActivityById(String id) {

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    return utils.activityToActivityResponse(activity);
  }

  @Override
  @Transactional
  public void deleteActivity(User user, String id) {

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    // delete all imagenya
    activityImageRepository.deleteAll(activity.getImages());

    // ambil usernya lalu putuskan relasinya
    activity.getUsers().forEach( u -> u.getActivities().remove(activity));

    // save usernya
    userRepository.saveAll(activity.getUsers());

    // delete activitynya
    activityRepository.delete(activity);

  }
}
