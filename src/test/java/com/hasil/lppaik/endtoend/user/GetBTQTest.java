package com.hasil.lppaik.endtoend.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.entity.ControlBookDetail;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.ControlBookDetailResponse;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GetBTQTest extends AbstractSetupEndToEndTest {

  @Autowired
  public GetBTQTest(ObjectMapper mapper, MockMvc mvc, ActivityImageRepository activityImageRepository, ActivityRepository activityRepository, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, activityImageRepository, activityRepository, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
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
            get(BASE_USERS_URL + "/control-book/" + kating.getUsername())
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
            get(BASE_USERS_URL + "/control-book/" + kating.getUsername())
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
            get(BASE_USERS_URL + "/control-book/" + kating.getUsername())
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
            get(BASE_USERS_URL + "/control-book")
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
            get(BASE_USERS_URL + "/control-book")
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
}
