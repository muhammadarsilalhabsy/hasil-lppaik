package com.hasil.lppaik.endtoend.auth;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.model.request.LoginRequest;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.model.response.LoginResponse;
import com.hasil.lppaik.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // tempatnya post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
import com.hasil.lppaik.model.response.WebResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest extends AbstractSetupEndToEndTest {

  @Autowired
  public LoginTest(ObjectMapper mapper, MockMvc mvc, ActivityImageRepository activityImageRepository, ActivityRepository activityRepository, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, activityImageRepository, activityRepository, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
  }

  @Test
  void testLoginSuccess() throws Exception {

    LoginRequest request = new LoginRequest();
    request.setUsername("12345678");
    request.setPassword("secret");

    mvc.perform(
            post(BASE_URL_LOGIN)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<LoginResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("Logged in Successfully", response.getMessage());
    });


  }

  @Test
  void testLoginUnauthorizedWithErrorWrongUsername() throws Exception {

    LoginRequest request = new LoginRequest();
    request.setUsername("12345679");
    request.setPassword("secret");

    mvc.perform(
            post(BASE_URL_LOGIN)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("This username maybe not exist", response.getMessage());
    });
  }

  @Test
  void testLoginUnauthorizedWithErrorWrongPassword() throws Exception {

    LoginRequest request = new LoginRequest();
    request.setUsername("12345678");
    request.setPassword("happy");

    mvc.perform(
            post(BASE_URL_LOGIN)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("You send wrong password!", response.getMessage());
    });
  }





  // end-----------
}
