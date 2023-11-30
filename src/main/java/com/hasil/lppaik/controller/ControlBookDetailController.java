package com.hasil.lppaik.controller;


import com.google.zxing.WriterException;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateControlBookDetailRequest;
import com.hasil.lppaik.model.request.PagingRequest;
import com.hasil.lppaik.model.request.UpdateControlBookDetailRequest;
import com.hasil.lppaik.model.response.ControlBookDetailResponse;
import com.hasil.lppaik.model.response.PagingResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.service.ControlBookDetailServiceImpl;
import com.hasil.lppaik.service.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/control-book")
public class ControlBookDetailController {

  private final ControlBookDetailServiceImpl cbdService;

  @Autowired
  public ControlBookDetailController(ControlBookDetailServiceImpl cbdService) {
    this.cbdService = cbdService;
  }

  // GET CBD DETAIL WITH ID
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<ControlBookDetailResponse> getCbdDetailWithId(@PathVariable("id") String id){

    ControlBookDetailResponse response = cbdService.getCbdDetailWithId(id);
    return WebResponse.<ControlBookDetailResponse>builder()
            .data(response)
            .message("success get detail control book with id " + id)
            .build();
  }

  // delete other user control book detail [ADMIN, TUTOR]
  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> deleteCBD(User user, @PathVariable String id){

    cbdService.deleteCBD(user, id);

    return WebResponse.<String>builder()
            .data("OK")
            .message("lesson has been deleted")
            .build();
  }

  // update other user control book detail [ADMIN, TUTOR]
  @PatchMapping(path = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateCBD(User user,
                                    @PathVariable String id,
                                    @RequestBody UpdateControlBookDetailRequest request){

    request.setId(id);
    cbdService.updateCBD(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message("lesson has been updated")
            .build();
  }

  // add other user control book detail [ADMIN, TUTOR]
  @PostMapping(path = "/{username}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> addCBD(User user,
                                    @PathVariable String username,
                                    @RequestBody CreateControlBookDetailRequest request){

    request.setUsername(username);
    cbdService.addCBD(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .message(String.format("Success add new lesson for user with id %s", username))
            .build();
  }

  @GetMapping("/download")
  public ResponseEntity<byte[]> downloadFile(User user) throws IOException{

    Resource resource = cbdService.download(user);

    byte[] data = IOUtils.toByteArray(resource.getInputStream());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "output-report-user-cbd.pdf");

    return new ResponseEntity<>(data, headers, HttpStatus.OK);
  }
}
