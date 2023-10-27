package com.hasil.lppaik.endtoend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.entity.*;

import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;


import com.hasil.lppaik.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractAuthControllerTest {

  protected final String BASE_URL_REGISTER = "/api/v1/auth/register";
  protected final String BASE_URL_LOGIN = "/api/v1/auth/login";
  protected final String BASE_URL_LOGOUT = "/api/v1/auth/logout";
  protected final ObjectMapper mapper;

  protected final MockMvc mvc;

  protected final UserRepository userRepository;
  protected final MajorRepository majorRepository;

  protected final RoleRepository roleRepository;

  @Autowired
  public AbstractAuthControllerTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository) {
    this.mapper = mapper;
    this.mvc = mvc;
    this.userRepository = userRepository;
    this.majorRepository = majorRepository;
    this.roleRepository = roleRepository;
  }

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    roleRepository.findAll()
            .forEach(role -> {
              role.getUsers().forEach(u -> u.getRoles().remove(role));
              userRepository.saveAll(role.getUsers());
              roleRepository.delete(role);
            });
    roleRepository.deleteAll();
    majorRepository.deleteAll();

    // create roles
    Role admin = new Role();
    admin.setId("R001");
    admin.setName(RoleEnum.ADMIN);

    Role mhs = new Role();
    mhs.setId("R002");
    mhs.setName(RoleEnum.MAHASISWA);
    roleRepository.saveAll(List.of(admin, mhs));

    // create major
    Major lawyer = new Major();
    lawyer.setId("M00L");
    lawyer.setName("HUKUM");

    Major tech = new Major();
    tech.setId("M00T");
    tech.setName("IT");
    majorRepository.saveAll(List.of(lawyer,tech));

    // create user (ADMIN)
    User user1 = new User();
    user1.setGender(Gender.MALE);
    user1.setName("Otong");
    user1.setUsername("12345678");
    user1.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user1.setEmail("otong@gmail.com");
    user1.setRoles(Set.of(admin));
    user1.setMajor(lawyer);
    user1.setToken("token-admin");

    User user2 = new User();
    user2.setGender(Gender.MALE);
    user2.setName("Jamal");
    user2.setUsername("87654321");
    user2.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user2.setEmail("jamal@gmail.com");
    user2.setRoles(Set.of(mhs));
    user2.setMajor(lawyer);
    user2.setToken("token-mhs");
    userRepository.saveAll(List.of(user1, user2));
  }

}
