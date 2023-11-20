package com.hasil.lppaik.controller;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.*;
import com.hasil.lppaik.model.response.*;
import com.hasil.lppaik.service.ActivityRegisterServiceImpl;
import com.hasil.lppaik.service.ActivityServiceImpl;
import com.hasil.lppaik.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/activities")
public class ActivityController {

  private final ActivityServiceImpl activityService;
  private final ActivityRegisterServiceImpl activityRegisterService;

  @Autowired
  public ActivityController(ActivityServiceImpl activityService, ActivityRegisterServiceImpl activityRegisterService) {
    this.activityService = activityService;
    this.activityRegisterService = activityRegisterService;
  }

  // GET ALL USER REGISTER FROM ACTIVITIES
  @GetMapping(path = "/{id}/register/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<List<SimpleUserResponse>> getAllRegisterByActivityId(User user, @PathVariable("id") String id){

    List<SimpleUserResponse> response = activityRegisterService.getAllRegisterByActivityId(user, id);

    return WebResponse.<List<SimpleUserResponse>>builder()
            .data(response)
            .message("Success get all users registered")
            .build();
  }

  // REGISTER TO ACTIVITY
  @PostMapping(path = "/{id}/register", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> register(User user, @PathVariable("id") String id){

    activityRegisterService.register(user, id);

    return WebResponse.<String>builder()
            .message("Registered has been successfully")
            .build();
  }

  // REMOVE USER REGISTER
  @DeleteMapping(path = "/{id}/register", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> removeUserRegister(User user, @PathVariable("id") String id){

    activityRegisterService.remove(user, id);

    return WebResponse.<String>builder()
            .message("User has been successfully removed")
            .build();
  }

  // IS ALREADY REGISTERD
  @GetMapping(path = "/{id}/register", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<Boolean> isRegistered(User user, @PathVariable("id") String id){

    return WebResponse.<Boolean>builder()
            .data(activityRegisterService.isRegistered(user, id))
            .build();
  }

  // GET ALL ACTIVITIES [ALL ROLES]
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<ActivityResponse>> getAllActivities(@RequestParam(name = "title", required = false) String title,
                                                                        @RequestParam(name = "mandatory", required = false, defaultValue = "false") String mandatory,
                                                                        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){


    SearchActivityRequest request = SearchActivityRequest.builder()
            .mandatory(Utils.parseBoolean(mandatory))
            .title(title)
            .page(page)
            .size(size)
            .build();

    Page<ActivityResponse> pages = activityService.getAllActivities(request);

    return WebResponseWithPaging.<List<ActivityResponse>>builder()
            .data(pages.getContent())
            .pagination(Utils.getPagingResponse(pages))
            .message("success get activities")
            .build();
  }

  // ADD ACTIVITY TO OTHER USER [ADMIN, KATING]
  @PostMapping(path = "{id}/for/{username}/with-register/{reg}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> addActivityToOtherUser(User user,
                                                    @PathVariable("username") String username,
                                                    @PathVariable("id") String id,
                                                    @PathVariable("reg") String regId){

    activityService.addActivityToOtherUser(user, id, username, regId);

    return WebResponse.<String>builder()
            .data("OK")
            .message(String.format("Success add activity for user with id %s", username))
            .build();
  }

  // DELETE ACTIVITY FOR OTHER USER
  @DeleteMapping(path = "/{id}/for/{username}",produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> removeUserFromActivity(User user,
                                                    @PathVariable("username") String username,
                                                    @PathVariable("id") String id){

    activityService.removeUserFromActivity(user, id, username);

    return WebResponse.<String>builder()
            .data("OK")
            .message(String.format("User %s has been removed", username))
            .build();
  }

  // DELETE ACTIVITY [ADMIN]
  @DeleteMapping(path = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> deleteActivity(User user, @PathVariable("id") String id){

    activityService.deleteActivity(user, id);

    return WebResponse.<String>builder()
            .data("OK")
            .message("Activity has been deleted")
            .build();
  }

  // UPDATE ACTIVITY [ADMIN]
  @PatchMapping(path = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateActivity(User user,
                                            @PathVariable("id") String id,
                                            @RequestBody UpdateActivityRequest request){

    request.setId(id);
    activityService.updateActivity(user, request);
    return WebResponse.<String>builder()
            .data("OK")
            .message("activity has been updated")
            .build();
  }

  // GET ACTIVITY BY ID [ALL ROLES]
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<ActivityResponse> getActivityById(@PathVariable("id") String id){

    ActivityResponse response = activityService.getActivityById(id);

    return WebResponse.<ActivityResponse>builder()
            .data(response)
            .message("Success get activity with id " + id)
            .build();
  }

  // GET ACTIVITY ATTENDANCE
  @GetMapping(path = "/{id}/attendance", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<UserResponse>> getAttendance (User user,
                                                           @PathVariable("id") String id,
                                                           @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                           @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

    PagingRequest request = PagingRequest.builder()
            .size(size)
            .page(page)
            .username(id)
            .build();

    Page<UserResponse> pages = activityService.getAttendance(user, request);

    return WebResponseWithPaging.<List<UserResponse>>builder()
            .data(pages.getContent())
            .message("Success get attendance")
            .pagination(Utils.getPagingResponse(pages))
            .build();
  }

  // CREATE ACTIVITY [ADMIN]
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> createActivity(User user, @RequestBody CreateActivityRequest request){

    activityService.createActivity(user, request);
    return WebResponse.<String>builder()
            .data("OK")
            .message("Success create new activity")
            .build();
  }
}
