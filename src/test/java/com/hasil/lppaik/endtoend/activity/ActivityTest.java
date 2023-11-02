package com.hasil.lppaik.endtoend.activity;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.entity.Activity;
import com.hasil.lppaik.entity.ActivityImage;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateActivityRequest;
import com.hasil.lppaik.model.request.SearchActivityRequest;
import com.hasil.lppaik.model.request.UpdateActivityRequest;
import com.hasil.lppaik.model.response.ActivityResponse;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.repository.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ActivityTest extends AbstractSetupEndToEndTest {


  @Autowired
  public ActivityTest(ObjectMapper mapper, MockMvc mvc, ActivityImageRepository activityImageRepository, ActivityRepository activityRepository, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, activityImageRepository, activityRepository, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
  }

  @Test
  @Disabled
  void testGetListActivitySuccess() throws Exception {

    mvc.perform(
            get(BASE_ACTIVITIES_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("mandatory", "false")
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<ActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals(0, response.getPagination().getPage());
      assertEquals(1, response.getPagination().getPageSize());
      assertEquals(10, response.getPagination().getSize());
      assertEquals(5, response.getPagination().getTotalItems());
      System.out.println(response.getPagination());
      System.out.println(response.getData());

      assertNotNull(response);
    });

  }
  @Test
  void testAddActivityToOtherUserSuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);
    User other = userRepository.findById("87654321").orElse(null);

    mvc.perform(
            post(BASE_ACTIVITIES_URL + "/test-get-activity" + "/for/" + other.getUsername())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Success add activity for user with id " + other.getUsername(), response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testDeleteActivitySuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);


    mvc.perform(
            delete(BASE_ACTIVITIES_URL + "/test-get-activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Activity has been deleted", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testDeleteActivityNotFound() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);


    mvc.perform(
            delete(BASE_ACTIVITIES_URL + "/test-salah")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Activity with id test-salah is NOT FOUND", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testDeleteActivityUnauthorized() throws Exception {

    mvc.perform(
            delete(BASE_ACTIVITIES_URL + "/test-get-activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Unauthorized", response.getMessage());
      assertNotNull(response);
    });
  }

  @Test
  void testDeleteActivityForbidden() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);


    mvc.perform(
            delete(BASE_ACTIVITIES_URL + "/test-get-activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isForbidden()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("This Operation is not support for you role!", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }
  @Test
  void testUpdateActivitySuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);

    UpdateActivityRequest request = UpdateActivityRequest.builder()
            .date(LocalDate.now())
            .title("simple title baru")
            .description("simple desc baru")
            .startTime(LocalTime.now())
            .location("tempat tertentu baru")
            .endTime(LocalTime.now())
            .images(List.of("img 6", "img 7"))
            .build();


    mvc.perform(
            patch(BASE_ACTIVITIES_URL + "/test-get-activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("activity has been updated", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testUpdateActivityForbidden() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);

    UpdateActivityRequest request = UpdateActivityRequest.builder()
            .date(LocalDate.now())
            .title("simple title baru")
            .description("simple desc baru")
            .startTime(LocalTime.now())
            .location("tempat tertentu baru")
            .endTime(LocalTime.now())
            .images(List.of("img 6", "img 7"))
            .build();


    mvc.perform(
            patch(BASE_ACTIVITIES_URL + "/test-get-activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isForbidden()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("This Operation is not support for you role!", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testUpdateActivityUnauthorized() throws Exception {


    UpdateActivityRequest request = UpdateActivityRequest.builder()
            .date(LocalDate.now())
            .title("simple title baru")
            .description("simple desc baru")
            .startTime(LocalTime.now())
            .location("tempat tertentu baru")
            .endTime(LocalTime.now())
            .images(List.of("img 6", "img 7"))
            .build();


    mvc.perform(
            patch(BASE_ACTIVITIES_URL + "/test-get-activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Unauthorized", response.getMessage());
      assertNotNull(response);
    });
  }

  @Test
  void testGetActivitySuccess() throws Exception {

    mvc.perform(
            get(BASE_ACTIVITIES_URL + "/test-get-activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<ActivityResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("lr. simple location", response.getData().getLocation());
      assertEquals("simple title", response.getData().getTitle());
      assertEquals("simple desc", response.getData().getDescription());
      assertNotNull(response.getData().getDate());
      assertNotNull(response.getData().getStartTime());
      assertNotNull(response.getData().getEndTime());

      assertNotNull(response);
    });

  }

  @Test
  void testGetActivityNotFound() throws Exception {

    mvc.perform(
            get(BASE_ACTIVITIES_URL + "/test-salah")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Activity with id test-salah is NOT FOUND", response.getMessage());
      assertNotNull(response);
    });

  }

  @Test
  void testCreateActivitySuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);

    CreateActivityRequest request = CreateActivityRequest.builder()
            .date(LocalDate.now())
            .title("simple title")
            .description("simple desc")
            .mandatory(true)
            .startTime(LocalTime.now())
            .location("tempat tertentu")
            .endTime(LocalTime.now())
            .images(List.of("img 1", "img 2", "img 3", "img 4"))
            .build();


    mvc.perform(
            post(BASE_ACTIVITIES_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testCreateActivityUnauthorized() throws Exception {

    CreateActivityRequest request = CreateActivityRequest.builder()
            .date(LocalDate.now())
            .title("simple title")
            .description("simple desc")
            .mandatory(true)
            .startTime(LocalTime.now())
            .location("tempat tertentu")
            .endTime(LocalTime.now())
            .images(List.of("img 1", "img 2", "img 3", "img 4"))
            .build();


    mvc.perform(
            post(BASE_ACTIVITIES_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Unauthorized", response.getMessage());
      assertNotNull(response);
    });

  }

  @Test
  void testCreateActivityForbidden() throws Exception {

    User user = userRepository.findById("87654321").orElse(null);

    CreateActivityRequest request = CreateActivityRequest.builder()
            .date(LocalDate.now())
            .title("simple title")
            .description("simple desc")
            .mandatory(true)
            .startTime(LocalTime.now())
            .location("tempat tertentu")
            .endTime(LocalTime.now())
            .images(List.of("img 1", "img 2", "img 3", "img 4"))
            .build();


    mvc.perform(
            post(BASE_ACTIVITIES_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isForbidden()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("This Operation is not support for you role!", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }
}
