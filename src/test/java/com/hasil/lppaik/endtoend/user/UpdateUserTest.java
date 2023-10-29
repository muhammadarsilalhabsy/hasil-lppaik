package com.hasil.lppaik.endtoend.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // tempatnya post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hasil.lppaik.entity.Certificate;
import com.hasil.lppaik.entity.RoleEnum;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.request.UpdateUserPasswordRequest;
import com.hasil.lppaik.model.request.UpdateUserRequest;
import com.hasil.lppaik.model.response.ErrorResponse;
import com.hasil.lppaik.repository.CertificateRepository;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;
import com.hasil.lppaik.security.BCrypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.hasil.lppaik.model.response.WebResponse;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateUserTest extends AbstractUserTest {

  @Autowired
  public UpdateUserTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository) {
    super(mapper, mvc, userRepository, majorRepository, roleRepository, certificateRepository);
  }

  @Test
  void testUpdateUserWithIdSuccess() throws Exception {
    User admin = userRepository.findById("12345678").orElse(null);
    User mhs = userRepository.findById("87654321").orElse(null);

    UpdateUserRequest request = UpdateUserRequest.builder()
            .roles(Set.of(RoleEnum.KATING, RoleEnum.MAHASISWA))
            .email("new@gmail.com")
            .name("new simple name")
            .completed(true)
            .gender("FEMALE").build();

    mvc.perform(
            patch(BASE_USERS_URL + "/" + mhs.getUsername())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
                    .content(mapper.writeValueAsString(request)))
            .andExpectAll(status().isOk())
            .andExpectAll(result -> {
                WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                assertEquals("user with id " + mhs.getUsername() + " has been updated", response.getMessage());
                assertEquals("OK", response.getData());
                assertNotNull(response);
            }
    );
    assertNotNull(admin);
    assertNotNull(mhs);

    // comparing to database
    User candidateUserUpdate = userRepository.findById("87654321").orElse(null);

    assertNotNull(candidateUserUpdate);
    assertEquals(request.getName(), candidateUserUpdate.getName());
    assertEquals(request.getEmail(), candidateUserUpdate.getEmail());
    assertEquals(request.getCompleted(), candidateUserUpdate.getCompleted());
    assertEquals(request.getGender(), candidateUserUpdate.getGender().name());
    assertEquals(request.getRoles().size(), candidateUserUpdate.getRoles().size());

    // check if certificate created or not
    Certificate certificate = candidateUserUpdate.getCertificate();
    assertNotNull(certificate);

  }

  @Test
  void testUpdateUserWithIdForbidden() throws Exception {
    User mhs = userRepository.findById("87654321").orElse(null);

    UpdateUserRequest request = UpdateUserRequest.builder()
            .roles(Set.of(RoleEnum.KATING, RoleEnum.MAHASISWA))
            .email("new@gmail.com")
            .name("new simple name")
            .completed(true)
            .gender("FEMALE").build();

    mvc.perform(
                patch(BASE_USERS_URL + "/" + mhs.getUsername())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", mhs.getToken())
                        .content(mapper.writeValueAsString(request)))
        .andExpectAll(status().isForbidden())
        .andExpectAll(result -> {
                  ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                          new TypeReference<>() {});
                  assertEquals("This Operation is not support for you role!", response.getMessage());

                }
        );
    assertNotNull(mhs);
  }

  @Test
  void testUpdateUserWithIdUnauthorized() throws Exception {
    User mhs = userRepository.findById("87654321").orElse(null);

    UpdateUserRequest request = UpdateUserRequest.builder()
            .roles(Set.of(RoleEnum.KATING, RoleEnum.MAHASISWA))
            .email("new@gmail.com")
            .name("new simple name")
            .completed(true)
            .gender("FEMALE").build();

    mvc.perform(
        patch(BASE_USERS_URL + "/" + mhs.getUsername())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpectAll(status().isUnauthorized())
        .andExpectAll(result -> {
            ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertEquals("Unauthorized", response.getMessage());

          }
        );
    assertNotNull(mhs);
  }

  @Test
  void testUpdateCurrentUserPasswordUnauthorized() throws Exception {

    UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
            .newPassword("new pass")
            .confirmNewPassword("new pass")
            .build();

    mvc.perform(
        patch(BASE_USERS_URL + "/password")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpectAll(status().isUnauthorized())
        .andExpectAll(result -> {
            ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertEquals("Unauthorized", response.getMessage());
          }
    );

  }

  @Test
  void testUpdateCurrentUserPasswordBadRequest() throws Exception {
    User mhs = userRepository.findById("87654321").orElse(null);
    UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
            .newPassword("new pass")
            .confirmNewPassword("salah")
            .build();

    mvc.perform(
        patch(BASE_USERS_URL + "/password")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", mhs.getToken())
                .content(mapper.writeValueAsString(request)))
        .andExpectAll(status().isBadRequest())
        .andExpectAll(result -> {
            ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertEquals("password and confirm password not match!", response.getMessage());
          }
        );
    assertNotNull(mhs);
  }

  @Test
  void testUpdateCurrentUserPasswordSuccess() throws Exception {
    User mhs = userRepository.findById("87654321").orElse(null);
    UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
            .newPassword("new pass")
            .confirmNewPassword("new pass")
            .build();

    mvc.perform(
        patch(BASE_USERS_URL + "/password")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", mhs.getToken())
                .content(mapper.writeValueAsString(request)))
        .andExpectAll(status().isOk())
        .andExpectAll(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertEquals("password has been updated", response.getMessage());
          }
        );
    User fromDb = userRepository.findById("87654321").orElse(null);
    assertTrue(BCrypt.checkpw(request.getNewPassword(), fromDb.getPassword()));
    assertNotNull(mhs);
    assertNotNull(fromDb);
  }




}
