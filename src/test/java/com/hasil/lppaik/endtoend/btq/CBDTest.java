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
  public CBDTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
  }

  @Test
  void testGetOtherCBDSuccess() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);
    User kating = userRepository.findById("99122211").orElse(null);

    for (int i = 1; i <= 22; i++) {
      ControlBookDetail cbd = new ControlBookDetail();
      cbd.setId(UUID.randomUUID().toString());
      cbd.setTutor(admin);
      if(i < 10){
        cbd.setDate(LocalDate.parse("2023-10-0"+i));
      }else {
        cbd.setDate(LocalDate.parse("2023-10-"+i));
      }
      cbd.setLesson("Lesson btq " + i);
      if(i % 2 == 0){
        cbd.setUser(user);
      }else{
        cbd.setUser(kating);
      }
      controlBookDetailRepository.save(cbd);
    }

    mvc.perform(
            get(BASE_CBD_URL + "/" + kating.getUsername())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
                    .queryParam("page","1")
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<ControlBookDetailResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      System.out.println(response.getData());
      System.out.println(response.getPagination());
      assertEquals(1,response.getData().size());
      assertEquals(10,response.getPagination().getSize());
      assertEquals(1,response.getPagination().getPage());
      assertEquals(2,response.getPagination().getPageSize());
      assertEquals(1,response.getPagination().getTotalItems());
      assertEquals("Success get all btq details user with id " + kating.getUsername(), response.getMessage());
    });
    assertNotNull(user);
    assertNotNull(admin);
    assertNotNull(kating);
  }

  @Test
  void testGetOtherCBDUnauthorized() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);
    User kating = userRepository.findById("99122211").orElse(null);

    for (int i = 1; i <= 22; i++) {
      ControlBookDetail cbd = new ControlBookDetail();
      cbd.setId(UUID.randomUUID().toString());
      cbd.setTutor(admin);
      if(i < 10){
        cbd.setDate(LocalDate.parse("2023-10-0"+i));
      }else {
        cbd.setDate(LocalDate.parse("2023-10-"+i));
      }
      cbd.setLesson("Lesson btq " + i);
      if(i % 2 == 0){
        cbd.setUser(user);
      }else{
        cbd.setUser(kating);
      }
      controlBookDetailRepository.save(cbd);
    }

    mvc.perform(
            get(BASE_CBD_URL + "/" + kating.getUsername())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("Unauthorized", response.getMessage());
    });
    assertNotNull(user);
    assertNotNull(admin);
    assertNotNull(kating);
  }

  @Test
  void testGetOtherCBDForbidden() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);
    User kating = userRepository.findById("99122211").orElse(null);

    for (int i = 1; i <= 22; i++) {
      ControlBookDetail cbd = new ControlBookDetail();
      cbd.setId(UUID.randomUUID().toString());
      cbd.setTutor(admin);
      if(i < 10){
        cbd.setDate(LocalDate.parse("2023-10-0"+i));
      }else {
        cbd.setDate(LocalDate.parse("2023-10-"+i));
      }
      cbd.setLesson("Lesson btq " + i);
      if(i % 2 == 0){
        cbd.setUser(user);
      }else{
        cbd.setUser(kating);
      }
      controlBookDetailRepository.save(cbd);
    }

    mvc.perform(
            get(BASE_CBD_URL + "/" + kating.getUsername())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isForbidden()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("This Operation is not support for you role!", response.getMessage());
    });
    assertNotNull(user);
    assertNotNull(admin);
    assertNotNull(kating);
  }
  @Test
  void testGetCurrentCBDSuccess() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    for (int i = 1; i <= 22; i++) {
      ControlBookDetail cbd = new ControlBookDetail();
      cbd.setId(UUID.randomUUID().toString());
      cbd.setTutor(admin);
      if(i < 10){
        cbd.setDate(LocalDate.parse("2023-10-0"+i));
      }else {
        cbd.setDate(LocalDate.parse("2023-10-"+i));
      }
      cbd.setLesson("Lesson btq " + i);
      cbd.setUser(user);
      controlBookDetailRepository.save(cbd);
    }

    mvc.perform(
            get(BASE_CBD_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<ControlBookDetailResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      System.out.println(response.getData());
      System.out.println(response.getPagination());
      assertEquals(10,response.getData().size());
      assertEquals(0,response.getPagination().getPage());
      assertEquals(3,response.getPagination().getPageSize());
      assertEquals(10,response.getPagination().getTotalItems());
      assertEquals("Success get all btq details", response.getMessage());
    });
    assertNotNull(user);
    assertNotNull(admin);
  }

  @Test
  void testGetCurrentCBDUnauthorized() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    for (int i = 1; i <= 22; i++) {
      ControlBookDetail cbd = new ControlBookDetail();
      cbd.setId(UUID.randomUUID().toString());
      cbd.setTutor(admin);
      if(i < 10){
        cbd.setDate(LocalDate.parse("2023-10-0"+i));
      }else {
        cbd.setDate(LocalDate.parse("2023-10-"+i));
      }
      cbd.setLesson("Lesson btq " + i);
      cbd.setUser(user);
      controlBookDetailRepository.save(cbd);
    }

    mvc.perform(
            get(BASE_CBD_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("Unauthorized", response.getMessage());
    });
    assertNotNull(user);
    assertNotNull(admin);
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
