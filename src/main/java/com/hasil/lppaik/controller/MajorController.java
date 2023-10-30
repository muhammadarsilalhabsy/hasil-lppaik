package com.hasil.lppaik.controller;

import java.util.List;

import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateMajorRequest;
import com.hasil.lppaik.model.request.UpdateMajorRequest;
import com.hasil.lppaik.model.response.MajorResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.service.MajorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/majors")
public class MajorController {

  private final MajorServiceImpl majorService;

  @Autowired
  public MajorController(MajorServiceImpl majorService) {
    this.majorService = majorService;
  }


  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<List<MajorResponse>> getAllMajor(){
    List<MajorResponse> responses = majorService.getAllMajor();
    return WebResponse.<List<MajorResponse>>builder()
            .data(responses)
            .message("Success get majors")
            .build();
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> createMajor(User user, @RequestBody CreateMajorRequest request){

    majorService.createMajor(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("Success create new major")
            .build();
  }

  @PatchMapping(path = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateMajor(User user,
                                         @PathVariable("id") String id,
                                         @RequestBody UpdateMajorRequest request){

    request.setId(id);
    majorService.updateMajor(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("major has been updated")
            .build();
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> deleteMajor(User user, @PathVariable("id") String id){

    majorService.deleteMajor(user, id);

    return WebResponse.<String>builder()
            .data("OK")
            .message("major has been deleted")
            .build();
  }

}
