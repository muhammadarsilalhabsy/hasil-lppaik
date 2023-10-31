package com.hasil.lppaik.endtoend.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutTest extends AbstractSetupEndToEndTest {

  @Autowired
  public LogoutTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
  }



  @Test
  void testLoginSuccess() throws Exception {
    User mhs = userRepository.findById("87654321").orElse(null);
    mvc.perform(
            delete(BASE_URL_LOGOUT)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", mhs.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("Logged out Successfully", response.getMessage());

      // get the user
      User userFromDb = userRepository.findById("87654321").orElse(null);
      assertNotNull(userFromDb);

      // make sure the token is null
      assertNull(userFromDb.getToken());
      assertNull(userFromDb.getTokenExpiredAt());

    });


  }
}
