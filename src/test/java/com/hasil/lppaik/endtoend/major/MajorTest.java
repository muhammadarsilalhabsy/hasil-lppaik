package com.hasil.lppaik.endtoend.major;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.endtoend.AbstractSetupEndToEndTest;
import com.hasil.lppaik.entity.Major;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.CreateMajorRequest;
import com.hasil.lppaik.model.request.UpdateMajorRequest;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.model.response.MajorResponse;
import com.hasil.lppaik.model.response.WebResponse;
import com.hasil.lppaik.repository.CertificateRepository;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MajorTest extends AbstractSetupEndToEndTest {

  @Autowired
  public MajorTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository) {
    super(mapper, mvc, userRepository, majorRepository, roleRepository, certificateRepository);
  }

  @Test
  void testDeleteMajorSuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);
    String pathId = "/test-major-id";

    Major major = new Major();
    major.setId("test-major-id");
    major.setName("tast-major-name");

    majorRepository.save(major);

    mvc.perform(
            delete(BASE_MAJOR_URL + pathId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken()))
    .andExpectAll(status().isOk())
    .andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("major has been deleted", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testDeleteMajorForbidden() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);
    String pathId = "/M00T";

    mvc.perform(
            delete(BASE_MAJOR_URL + pathId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken()))
    .andExpectAll(status().isForbidden())
    .andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("This Operation is not support for you role!", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testDeleteMajorUnauthorized() throws Exception {
    String pathId = "/M00T";

    mvc.perform(
            delete(BASE_MAJOR_URL + pathId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
    .andExpectAll(status().isUnauthorized())
    .andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Unauthorized", response.getMessage());
      assertNotNull(response);
    });

  }

  @Test
  void testUpdateMajorSuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);
    UpdateMajorRequest request = UpdateMajorRequest.builder()
            .name("PTI")
            .build();

    String pathId = "/M00T";
    mvc.perform(
            patch(BASE_MAJOR_URL + pathId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(request)))
    .andExpectAll(status().isOk())
    .andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("major has been updated", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testUpdateMajorForbidden() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);
    UpdateMajorRequest request = UpdateMajorRequest.builder()
            .name("PTI")
            .build();

    String pathId = "/M00T";
    mvc.perform(
            patch(BASE_MAJOR_URL + pathId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(request)))
    .andExpectAll(status().isForbidden())
    .andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("This Operation is not support for you role!", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testUpdateMajorUnauthorized() throws Exception {

    UpdateMajorRequest request = UpdateMajorRequest.builder()
            .name("PTI")
            .build();

    String pathId = "/M00T";
    mvc.perform(
            patch(BASE_MAJOR_URL + pathId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)))
    .andExpectAll(status().isUnauthorized())
    .andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Unauthorized", response.getMessage());
      assertNotNull(response);
    });
  }

  @Test
  void testCreateMajorSuccess() throws Exception {
    User user = userRepository.findById("12345678").orElse(null);
    CreateMajorRequest request = CreateMajorRequest.builder()
            .id("M00P")
            .name("PAI")
            .build();

    mvc.perform(
        post(BASE_MAJOR_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", user.getToken())
                .content(mapper.writeValueAsString(request)))
        .andExpectAll(status().isOk())
        .andExpectAll(result -> {
        WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
        });
      assertEquals("Success create new major", response.getMessage());
      assertNotNull(response);
    });
    assertNotNull(user);
  }

  @Test
  void testCreateMajorForbidden() throws Exception {
    User user = userRepository.findById("87654321").orElse(null);
    CreateMajorRequest request = CreateMajorRequest.builder()
            .id("M00P")
            .name("PAI")
            .build();

    mvc.perform(
            post(BASE_MAJOR_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
                    .content(mapper.writeValueAsString(request)))
    .andExpectAll(status().isForbidden())
    .andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("This Operation is not support for you role!", response.getMessage());
      assertNotNull(response);
            });
    assertNotNull(user);
  }

  @Test
  void testCreateMajorUnauthorized() throws Exception {
    CreateMajorRequest request = CreateMajorRequest.builder()
            .id("M00P")
            .name("PAI")
            .build();

    mvc.perform(
            post(BASE_MAJOR_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)))
    .andExpectAll(status().isUnauthorized())
    .andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Unauthorized", response.getMessage());
      assertNotNull(response);
    });
  }

  @Test
  void testGetMajors() throws Exception {

    mvc.perform(
            get(BASE_MAJOR_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
    .andExpectAll(status().isOk())
    .andExpectAll(result -> {
      WebResponse<List<MajorResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertEquals("Success get majors", response.getMessage());
      assertEquals(2, response.getData().size());
      assertNotNull(response);
    });
  }
}
