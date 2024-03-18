package com.hasil.lppaik.endtoend.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.entity.Activity;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.SimpleActivityResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GetActivityTest extends AbstractSetupEndToEndTest {

  @Autowired
  public GetActivityTest(ObjectMapper mapper, MockMvc mvc, ActivityImageRepository activityImageRepository, ActivityRepository activityRepository, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, activityImageRepository, activityRepository, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
  }

  @Test
  void testGetOtherUserActivitiesSuccess() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User user = userRepository.findById("87654321").orElse(null);

    Activity first = activityRepository.findById("test-get-activity").orElse(null);
    Activity second = activityRepository.findById("activity11").orElse(null);

    user.getActivities().add(first);
    user.getActivities().add(second);

    userRepository.save(user);

    mvc.perform(
            get(BASE_USERS_URL + "/activities/" + user.getUsername())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<SimpleActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(2, response.getPagination().getTotalItems());
      assertEquals(0, response.getPagination().getPage());
      assertEquals(1, response.getPagination().getPageSize());
      assertEquals(10, response.getPagination().getSize());

      System.out.println(response.getData());
      System.out.println(response.getPagination());

      assertNotNull(response);
    });
    assertNotNull(user);
    assertNotNull(admin);
    assertNotNull(first);
    assertNotNull(second);
  }

  @Test
  void testGetUserCurrentActivitiesSuccess() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);
    Activity first = activityRepository.findById("test-get-activity").orElse(null);
    Activity second = activityRepository.findById("activity11").orElse(null);

    user.getActivities().add(first);
    user.getActivities().add(second);

    userRepository.save(user);

    mvc.perform(
            get(BASE_USERS_URL + "/activities")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<SimpleActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(2, response.getPagination().getTotalItems());
      assertEquals(0, response.getPagination().getPage());
      assertEquals(1, response.getPagination().getPageSize());
      assertEquals(10, response.getPagination().getSize());

      System.out.println(response.getData());
      System.out.println(response.getPagination());

      assertNotNull(response);
    });
    assertNotNull(user);
    assertNotNull(first);
    assertNotNull(second);
  }
  @Test
  void testGetUserCurrentActivities() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);


    mvc.perform(
            get(BASE_USERS_URL + "/activities")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<SimpleActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(0, response.getPagination().getTotalItems());
      assertEquals(0, response.getPagination().getPage());
      assertEquals(0, response.getPagination().getPageSize());
      assertEquals(10, response.getPagination().getSize());

      System.out.println(response.getData());
      System.out.println(response.getPagination());

      assertNotNull(response);
    });
    assertNotNull(user);
  }

}
