package com.hasil.lppaik.endtoend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasil.lppaik.entity.*;

import com.hasil.lppaik.repository.CertificateRepository;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;


import com.hasil.lppaik.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractSetupEndToEndTest {

  // urls
  public final String BASE_URL_REGISTER = "/api/v1/auth/register";
  public final String BASE_URL_LOGIN = "/api/v1/auth/login";
  public final String BASE_URL_LOGOUT = "/api/v1/auth/logout";
  public final String BASE_USERS_URL = "/api/v1/users";
  protected final String BASE_CERTIFICATE_URL = "/api/v1/certificate";
  protected final String BASE_MAJOR_URL = "/api/v1/majors";
  public final ObjectMapper mapper;

  public final MockMvc mvc;

  public final UserRepository userRepository;
  public final MajorRepository majorRepository;

  public final RoleRepository roleRepository;
  protected final CertificateRepository certificateRepository;

  @Autowired
  public AbstractSetupEndToEndTest(ObjectMapper mapper, MockMvc mvc, UserRepository userRepository, MajorRepository majorRepository, RoleRepository roleRepository, CertificateRepository certificateRepository) {
    this.mapper = mapper;
    this.mvc = mvc;
    this.userRepository = userRepository;
    this.majorRepository = majorRepository;
    this.roleRepository = roleRepository;
    this.certificateRepository = certificateRepository;
  }

  @BeforeEach
  void setUp() {
    certificateRepository.deleteAll();
    userRepository.deleteAll();

    // cari semua rolenya
    roleRepository.findAll()
            .forEach(role -> { // loopin setiap rolenya

              // ambil role dari usernya, lalu remove role dari usernya
              role.getUsers().forEach(u -> u.getRoles().remove(role));

              // setelah itu save user yang sudah di remove rolenya
              userRepository.saveAll(role.getUsers());

              // remove role yang sudah tidak di gunakan.
              roleRepository.delete(role);
            });

    // make sure rolenya sudah di delete semua
    roleRepository.deleteAll();
    majorRepository.deleteAll();

    // create roles
    Role admin = new Role();
    admin.setId("R001");
    admin.setName(RoleEnum.ADMIN);

    Role mhs = new Role();
    mhs.setId("R002");
    mhs.setName(RoleEnum.MAHASISWA);

    Role kating = new Role();
    kating.setId("R003");
    kating.setName(RoleEnum.KATING);

    Role dosen = new Role();
    dosen.setId("R004");
    dosen.setName(RoleEnum.DOSEN);

    Role tutor = new Role();
    tutor.setId("R004");
    tutor.setName(RoleEnum.TUTOR);

    roleRepository.saveAll(List.of(admin, mhs, kating, dosen, tutor));

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

    User user3 = new User();
    user3.setGender(Gender.MALE);
    user3.setName("Abiblal");
    user3.setUsername("99122211");
    user3.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user3.setEmail("abilal@gmail.com");
    user3.setRoles(Set.of(kating));
    user3.setMajor(lawyer);
    user3.setToken("token-kating");

    User user4 = new User();
    user4.setGender(Gender.MALE);
    user4.setName("Apon");
    user4.setUsername("84567112");
    user4.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user4.setEmail("apon@gmail.com");
    user4.setRoles(Set.of(tutor));
    user4.setMajor(lawyer);
    user4.setToken("token-tutor");

    User user5 = new User();
    user5.setGender(Gender.MALE);
    user5.setName("Zait");
    user5.setUsername("98456712");
    user5.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user5.setEmail("zait@gmail.com");
    user5.setRoles(Set.of(dosen));
    user5.setMajor(lawyer);
    user5.setCompleted(true);
    user5.setToken("token-dosen");
    userRepository.saveAll(List.of(user1, user2, user3, user4, user5));

    Certificate certificate = new Certificate();
    certificate.setId("id-certificate");
    certificate.setUser(user5);

    certificateRepository.save(certificate);

    // create example 7 users
    List<User> users = new ArrayList<>();
    for (int i = 0; i < 7; i++) {

      User uLoop = new User();
      uLoop.setGender(Gender.MALE);
      uLoop.setName("example " + i);
      uLoop.setUsername("87654321" + i);
      uLoop.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
      uLoop.setEmail("example" + i + "@gmail.com");
      if(i % 2 == 0 ){
        uLoop.setRoles(Set.of(mhs));
        uLoop.setMajor(lawyer);
      }else{
        uLoop.setMajor(tech);
        uLoop.setRoles(Set.of(kating));
      }
      users.add(uLoop);
    }
    userRepository.saveAll(users);
  }
}
