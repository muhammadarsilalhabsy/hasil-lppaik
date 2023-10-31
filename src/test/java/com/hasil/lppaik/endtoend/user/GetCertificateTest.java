package com.hasil.lppaik.endtoend.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.CertificateResponse;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GetCertificateTest extends AbstractSetupEndToEndTest {

  @Autowired
  public GetCertificateTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository, ControlBookDetailRepository controlBookDetailRepository) {
    super(mapper, mvc, userRepository, majorRepository, roleRepository, certificateRepository, controlBookDetailRepository);
  }

  @Test
  void testGetCertificateWithId() throws Exception {

    User user = userRepository.findById("98456712").orElse(null);

    String certificate = user.getCertificate().getId();

    mvc.perform(
            get(BASE_CERTIFICATE_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("id", certificate)
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<CertificateResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("Success get certificate", response.getMessage());
      assertEquals(response.getData().getCertificate(), certificate);
      assertEquals(response.getData().getUser().getUsername(), user.getUsername());
    });
      assertNotNull(user);
  }

  @Test
  void testGetCertificateWithIdNotFound() throws Exception {

    String certificate = "salah";

    mvc.perform(
            get(BASE_CERTIFICATE_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("id", certificate)
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response);
      assertEquals("Certificate is NOT FOUND!", response.getMessage());

    });

  }
}
