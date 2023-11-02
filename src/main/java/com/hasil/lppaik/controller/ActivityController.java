package com.hasil.lppaik.controller;

import com.hasil.lppaik.entity.ActivityImage;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateActivityRequest;
import com.hasil.lppaik.model.request.SearchActivityRequest;
import com.hasil.lppaik.model.request.UpdateActivityRequest;
import com.hasil.lppaik.model.response.ActivityResponse;
import com.hasil.lppaik.model.response.PagingResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
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

  @Autowired
  public ActivityController(ActivityServiceImpl activityService) {
    this.activityService = activityService;
  }


  // GET ALL ACTIVITIES [ALL ROLES]
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponseWithPaging<List<ActivityResponse>> getAllActivities(@RequestParam(name = "title", required = false) String title,
                                                                        @RequestParam(name = "mandatory", required = false, defaultValue = "false") String mandatory,
                                                                        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                                        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){


    SearchActivityRequest request = SearchActivityRequest.builder()
            .mandatory(Utils.parseBoolean(mandatory))
            .title(title)
            .page(page)
            .size(size)
            .build();

    Page<ActivityResponse> response = activityService.getAllActivities(request);

    return WebResponseWithPaging.<List<ActivityResponse>>builder()
            .data(response.getContent())
            .pagination(PagingResponse.builder()
                    .totalItems(response.getContent().size())
                    .pageSize(response.getTotalPages())
                    .page(response.getNumber())
                    .size(response.getSize())
                    .build())
            .message("success get activities")
            .build();
  }

  // ADD ACTIVITY TO OTHER USER [ADMIN, KATING]
  @PostMapping(path = "/{id}/for/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> addActivityToOtherUser(User user,
                                                    @PathVariable("username") String username,
                                                    @PathVariable("id") String id){

    activityService.addActivityToOtherUser(user, id, username);

    return WebResponse.<String>builder()
            .data("OK")
            .message(String.format("Success add activity for user with id %s", username))
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
