package com.hasil.lppaik.endtoend.btq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.entity.ControlBookDetail;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateControlBookDetailRequest;
import com.hasil.lppaik.model.request.UpdateControlBookDetailRequest;
import com.hasil.lppaik.model.response.ControlBookDetailResponse;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CBDTest extends AbstractSetupEndToEndTest {

  @Autowired
  public CBDTest(ObjectMapper mapper, MockMvc mvc, ActivityImageRepository activityImageRepository, ActivityRepository activityRepository, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, activityImageRepository, activityRepository, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
  }

  @Test
  void testGetCBDDetail() throws Exception {
    String cbdId = "cbd-detail-1";
    User tutor = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    ControlBookDetail cbd = new ControlBookDetail();
    cbd.setLesson("test cbd belajar");
    cbd.setId(cbdId);
    cbd.setTutor(tutor);
    cbd.setUser(user);
    cbd.setDescription("simple cbd desc test");
    controlBookDetailRepository.save(cbd);

    mvc.perform(
            get(BASE_CBD_URL + "/" + cbdId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<ControlBookDetailResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("success get detail control book with id " + cbdId, response.getMessage());
      assertEquals("test cbd belajar", response.getData().getLesson());
      assertNotNull(response);
    });
  }

  @Test
  void testCreateCBDforUserSuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);

    String student = "87654321";
    CreateControlBookDetailRequest request = CreateControlBookDetailRequest.builder()
            .date(LocalDate.parse("2019-12-02"))
            .description("Simple description for btq")
            .lesson("simple lesson")
            .build();

    mvc.perform(
            post(BASE_CBD_URL + "/" +student)
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
      assertEquals("Success add new lesson for user with id " + student, response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testCreateCBDforUserForbidden() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    CreateControlBookDetailRequest request = CreateControlBookDetailRequest.builder()
            .date(LocalDate.parse("2019-12-02"))
            .description("Simple description for btq")
            .lesson("simple lesson")
            .build();

    mvc.perform(
            post(BASE_CBD_URL + "/" +user.getUsername())
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
    assertNotNull(admin);
  }

  @Test
  void testCreateCBDforUserUnauthorized() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);

    CreateControlBookDetailRequest request = CreateControlBookDetailRequest.builder()
            .date(LocalDate.parse("2019-12-02"))
            .description("Simple description for btq")
            .lesson("simple lesson")
            .build();

    mvc.perform(
            post(BASE_CBD_URL + "/" +user.getUsername())
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
    assertNotNull(user);
  }

  @Test
  void testDeleteCBDUsingIdSuccess() throws Exception {

    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    ControlBookDetail cbd = new ControlBookDetail();

    cbd.setId("test-delete");
    cbd.setTutor(admin);
    cbd.setDate(LocalDate.parse("2023-10-25"));
    cbd.setUser(user);
    cbd.setDescription("simple desc");
    controlBookDetailRepository.save(cbd);

    mvc.perform(
            delete(BASE_CBD_URL + "/test-delete")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("lesson has been deleted", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testDeleteCBDUsingIdForbidden() throws Exception {

    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    ControlBookDetail cbd = new ControlBookDetail();

    cbd.setId("test-delete");
    cbd.setTutor(admin);
    cbd.setDate(LocalDate.parse("2023-10-25"));
    cbd.setUser(user);
    cbd.setDescription("simple desc");
    controlBookDetailRepository.save(cbd);

    mvc.perform(
            delete(BASE_CBD_URL + "/test-delete")
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
  void testDeleteCBDUsingIdUnauthorized() throws Exception {

    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    ControlBookDetail cbd = new ControlBookDetail();

    cbd.setId("test-delete");
    cbd.setTutor(admin);
    cbd.setDate(LocalDate.parse("2023-10-25"));
    cbd.setUser(user);
    cbd.setDescription("simple desc");
    controlBookDetailRepository.save(cbd);

    mvc.perform(
            delete(BASE_CBD_URL + "/test-delete")
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
    assertNotNull(user);
  }

  @Test
  void testUpdateCBDUsingIdSuccess() throws Exception {

    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    ControlBookDetail cbd = new ControlBookDetail();

    cbd.setId("test-update");
    cbd.setTutor(admin);
    cbd.setDate(LocalDate.parse("2023-10-25"));
    cbd.setUser(user);
    cbd.setDescription("simple desc");
    controlBookDetailRepository.save(cbd);

    UpdateControlBookDetailRequest request = new UpdateControlBookDetailRequest();
    request.setDescription("new simple desc");

    mvc.perform(
            patch(BASE_CBD_URL + "/test-update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("lesson has been updated", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testUpdateCBDUsingIdForbidden() throws Exception {

    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    ControlBookDetail cbd = new ControlBookDetail();

    cbd.setId("test-update");
    cbd.setTutor(admin);
    cbd.setDate(LocalDate.parse("2023-10-25"));
    cbd.setUser(user);
    cbd.setDescription("simple desc");
    controlBookDetailRepository.save(cbd);

    UpdateControlBookDetailRequest request = new UpdateControlBookDetailRequest();
    request.setDescription("new simple desc");

    mvc.perform(
            patch(BASE_CBD_URL + "/test-update")
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
  void testUpdateCBDUsingIdUnauthorized() throws Exception {

    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    ControlBookDetail cbd = new ControlBookDetail();

    cbd.setId("test-update");
    cbd.setTutor(admin);
    cbd.setDate(LocalDate.parse("2023-10-25"));
    cbd.setUser(user);
    cbd.setDescription("simple desc");
    controlBookDetailRepository.save(cbd);

    UpdateControlBookDetailRequest request = new UpdateControlBookDetailRequest();
    request.setDescription("new simple desc");

    mvc.perform(
            patch(BASE_CBD_URL + "/test-update")
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
    assertNotNull(user);
  }
}
