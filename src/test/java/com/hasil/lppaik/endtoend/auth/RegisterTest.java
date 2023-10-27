package com.hasil.lppaik.endtoend.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.model.request.RegisterUserRequest;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // tempatnya post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTest extends AbstractAuthControllerTest {


  @Autowired
  public RegisterTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository) {
    super(mapper, mvc, userRepository, majorRepository, roleRepository);
  }

  @Test
  void testRegisterUsersSuccess() throws Exception {
    // find a user with role admin
    User user = userRepository.findById("12345678").orElse(null);

    // create 2 users
    RegisterUserRequest request1 = RegisterUserRequest.builder()
            .name("user1")
            .email("user1@gmail.com")
            .username("111111112")
            .major("M00T")
            .gender(Gender.MALE)
            .build();
    RegisterUserRequest request2 = RegisterUserRequest.builder()
            .name("user2")
            .email("user2@gmail.com")
            .username("222222222")
            .major("M00L")
            .gender(Gender.FEMALE)
            .build();

    // insert user into list
    List<RegisterUserRequest> requestList = new ArrayList<>();
    requestList.add(request1);
    requestList.add(request2);

    // send a request
    mvc.perform(
            post(BASE_URL_REGISTER)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(requestList))
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("OK",response.getData());
    });

    assertNotNull(user.getToken());
    assertNotNull(user);
  }

  @Test
  void testRegisterUsersForbiddenBecauseRole() throws Exception{

    // user with didn't have role admin
    User user = userRepository.findById("87654321").orElse(null);

    // create 2 users
    RegisterUserRequest request1 = RegisterUserRequest.builder()
            .name("user1")
            .email("user1@gmail.com")
            .username("111111111")
            .major("M00T")
            .gender(Gender.MALE)
            .build();
    RegisterUserRequest request2 = RegisterUserRequest.builder()
            .name("user2")
            .email("user2@gmail.com")
            .username("222221111")
            .major("M00L")
            .gender(Gender.FEMALE)
            .build();

    // insert user into list
    List<RegisterUserRequest> requestList = new ArrayList<>();
    requestList.add(request1);
    requestList.add(request2);

    mvc.perform(
            post(BASE_URL_REGISTER)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(requestList))
    ).andExpectAll(
            status().isForbidden()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("This Operation is not support for you role!",response.getMessage());

    });
    assertNotNull(user.getToken());
    assertNotNull(user);
  }

  @Test
  void testRegisterUsersUnauthorized() throws Exception{

    // create 2 users
    RegisterUserRequest request1 = RegisterUserRequest.builder()
            .name("user1")
            .email("user1@gmail.com")
            .username("111111111")
            .major("M00T")
            .gender(Gender.MALE)
            .build();
    RegisterUserRequest request2 = RegisterUserRequest.builder()
            .name("user2")
            .email("user2@gmail.com")
            .username("222221111")
            .major("M00L")
            .gender(Gender.FEMALE)
            .build();

    // insert user into list
    List<RegisterUserRequest> requestList = new ArrayList<>();
    requestList.add(request1);
    requestList.add(request2);

    mvc.perform(
            post(BASE_URL_REGISTER)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "wrong-token")
                    .content(mapper.writeValueAsString(requestList))
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Unauthorized",response.getMessage());

    });
  }

  @Test
  void testRegisterUsersBadRequest() throws Exception {
    // find a user with role admin
    User user = userRepository.findById("12345678").orElse(null);

    // create 2 users
    RegisterUserRequest request1 = RegisterUserRequest.builder()
            .name("u")
            .email("user1@gmail.com")
            .username("111111111")
            .major("M00T")
            .gender(Gender.MALE)
            .build();
    RegisterUserRequest request2 = RegisterUserRequest.builder()
            .name("user2")
            .email("user2@gmail.com")
            .username("222221111")
            .major("M00L")
            .gender(Gender.FEMALE)
            .build();

    // insert user into list
    List<RegisterUserRequest> requestList = new ArrayList<>();
    requestList.add(request1);
    requestList.add(request2);

    // send a request
    mvc.perform(
            post(BASE_URL_REGISTER)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(requestList))
    ).andExpectAll(
            status().isBadRequest()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getMessage()); // registerUsers.requestList[0].name: size for the name is 3 - 255 character
    });
  }


}
