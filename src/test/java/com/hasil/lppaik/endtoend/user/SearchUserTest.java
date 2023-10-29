package com.hasil.lppaik.endtoend.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.entity.Gender;
import com.hasil.lppaik.entity.Major;
import com.hasil.lppaik.entity.User;

import com.hasil.lppaik.model.response.UserResponse;
import com.hasil.lppaik.model.response.WebResponseWithPaging;
import com.hasil.lppaik.repository.CertificateRepository;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;
import com.hasil.lppaik.security.BCrypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // tempatnya post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SearchUserTest extends AbstractUserTest{

  @Autowired
  public SearchUserTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository) {
    super(mapper, mvc, userRepository, majorRepository, roleRepository, certificateRepository);
  }



  @Test
  void testSearchWithoutAnyReqParam() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);

    mvc.perform(
            get(BASE_USERS_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      System.out.println(response.getMessage());

      assertEquals(10, response.getPagination().getSize()); // limit
      assertEquals(0, response.getPagination().getPage()); // alway 0 besause start with 0
      assertEquals(2, response.getPagination().getPageSize()); // 2 because data is more then 10
      assertEquals(10, response.getPagination().getTotalItems()); // total items must be 7

      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testSearchWithIdentity() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);
    Major major = majorRepository.findById("M00T").orElse(null);

    List<User> users = new ArrayList<>();
    for (int i = 0; i < 18; i++) {

      User uLoop = new User();
      uLoop.setGender(Gender.MALE);
      uLoop.setName("exampless " + i);
      uLoop.setUsername("99654321" + i);
      uLoop.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
      uLoop.setEmail("exampless" + i + "@gmail.com");
      uLoop.setMajor(major);
      users.add(uLoop);
    }
    userRepository.saveAll(users);

    mvc.perform(
            get(BASE_USERS_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .queryParam("identity", "example")
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      System.out.println(response.getMessage());

      assertEquals(10, response.getPagination().getSize()); // limit
      assertEquals(0, response.getPagination().getPage()); // always 0 because start with 0
      assertEquals(3, response.getPagination().getPageSize()); // 3 because data is 30
      assertEquals(10, response.getPagination().getTotalItems()); // total items must be 10

      assertNotNull(response);
    });
    assertNotNull(user);
    assertNotNull(major);
  }

  @Test
  void testSearchWithMajor() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);

    mvc.perform(
            get(BASE_USERS_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .queryParam("major", "IT")
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      System.out.println(response.getMessage());

      assertEquals(10, response.getPagination().getSize());
      assertEquals(0, response.getPagination().getPage());
      assertEquals(1, response.getPagination().getPageSize());
      assertEquals(3, response.getPagination().getTotalItems());

      assertNotNull(response);
    });
    assertNotNull(user);

  }

  @Test
  void testSearchWithPaging() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);

    Major major = majorRepository.findById("M00T").orElse(null);

    List<User> users = new ArrayList<>();
    for (int i = 0; i < 18; i++) {

      User uLoop = new User();
      uLoop.setGender(Gender.MALE);
      uLoop.setName("exampless " + i);
      uLoop.setUsername("99654321" + i);
      uLoop.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
      uLoop.setEmail("exampless" + i + "@gmail.com");
      uLoop.setMajor(major);
      users.add(uLoop);
    }
    userRepository.saveAll(users);

    mvc.perform(
            get(BASE_USERS_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .queryParam("page", "2")
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      System.out.println(response.getMessage());

      assertEquals(10, response.getPagination().getSize());
      assertEquals(2, response.getPagination().getPage()); // harus tambah 1
      assertEquals(3, response.getPagination().getPageSize());
      assertEquals(10, response.getPagination().getTotalItems());

      assertNotNull(response);
    });
    assertNotNull(user);
    assertNotNull(major);

  }

  @Test
  void testKatingSearchUserWithoutGettingUserHasRoleADMINandDOSEN() throws Exception {
    User user = userRepository.findById("99122211").orElse(null);

    mvc.perform(
            get(BASE_USERS_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())

    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponseWithPaging<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      System.out.println(response.getMessage());

      assertEquals(10, response.getPagination().getSize());
      assertEquals(0, response.getPagination().getPage()); // harus tambah 1
      assertEquals(2, response.getPagination().getPageSize());
      assertEquals(10, response.getPagination().getTotalItems()); // datanya 12, karenan di page ke 2 berarti datanya sisa 2

      System.out.println(response.getData());
      System.out.println(response.getData().size());
      assertNotNull(response);
    });
    assertNotNull(user);


  }
}
