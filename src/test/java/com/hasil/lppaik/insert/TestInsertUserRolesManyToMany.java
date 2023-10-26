package com.hasil.lppaik.insert;

import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.repository.CertificateRepository;
import com.hasil.lppaik.repository.MajorRepository;
import com.hasil.lppaik.repository.RoleRepository;
import com.hasil.lppaik.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
public class TestInsertUserRolesManyToMany {

  private final RoleRepository roleRepo;

  private final UserRepository userRepo;
  private final MajorRepository majorRepo;

  private final CertificateRepository certificateRepo;

  @Autowired
  public TestInsertUserRolesManyToMany(RoleRepository roleRepo, UserRepository userRepo, MajorRepository majorRepo, CertificateRepository certificateRepo) {
    this.roleRepo = roleRepo;
    this.userRepo = userRepo;
    this.majorRepo = majorRepo;
    this.certificateRepo = certificateRepo;
  }

  @BeforeEach
  void setUp() {
    userRepo.deleteAll();
    roleRepo.findAll() // this operation only support if @ManyToMany have property fetch = FetchType.EAGER
            .forEach(role -> {
              role.getUsers().forEach(u -> u.getRoles().remove(role));
              userRepo.saveAll(role.getUsers());

              roleRepo.delete(role);
            });
    roleRepo.deleteAll();
    majorRepo.deleteAll();

    Role role = new Role();
    role.setId("R001");
    role.setName(RoleEnum.ADMIN);
    roleRepo.save(role);

    Major pti = new Major();
    pti.setId("J01");
    pti.setName("PTI");

    Major hukum = new Major();
    hukum.setId("J02");
    hukum.setName("HUKUM");

    User user = new User();
    user.setName("budi");
    user.setPassword("rahasia");
    user.setEmail("budi@gmail.com");
    user.setUsername("2191100");


    userRepo.save(user);
    majorRepo.saveAll(List.of(pti, hukum));

  }

  @Test
  void testInsertUserAndRole() {

    Role admin = roleRepo.findById("ROO1").orElse(null);

    Role mahasiswa = new Role();
    mahasiswa.setId("R002");
    mahasiswa.setName(RoleEnum.MAHASISWA);

    Role kating = new Role();
    kating.setId("R003");
    kating.setName(RoleEnum.DOSEN);

    Set<Role> roles = new HashSet<>();

    roles.add(mahasiswa);
    roles.add(kating);

    User user = new User();
    user.setName("otong");
    user.setPassword("rahasia");
    user.setEmail("otong@gmail.com");
    user.setUsername("020901");
    user.setRoles(roles);

    User user2 = new User();
    user2.setName("jamal");
    user2.setPassword("rahasia");
    user2.setEmail("jamal@gmail.com");
    user2.setUsername("020902");
    user2.setRoles(Set.of(mahasiswa));


    User user3 = new User();
    user3.setName("abilal");
    user3.setPassword("rahasia");
    user3.setEmail("abilal@gmail.com");
    user3.setUsername("020903");
    user3.setRoles(Set.of(admin));

    roleRepo.saveAll(roles);
    userRepo.saveAll(List.of(user, user2));

    Assertions.assertNotNull(admin);
  }

  @Test
  void testRole() {

    Role role = roleRepo.findByName(RoleEnum.MAHASISWA)
            .orElseThrow();

    System.out.println(role.getName());
    System.out.println(role.getId());
  }

  @Test
  void testMajorAndUsers() {
    Major major = majorRepo.findById("J01").orElse(null);

    User user = new User();
    user.setName("otong");
    user.setPassword("rahasia");
    user.setEmail("otong@gmail.com");
    user.setUsername("020901");
    user.setMajor(major);

    userRepo.save(user);

    Assertions.assertNotNull(major);
  }

  @Test
  void testCertificatesAndUsers() {
    User user = userRepo.findById("2191100").orElse(null);


    Certificate certificate = new Certificate();
    certificate.setId(UUID.randomUUID().toString());
    certificate.setUser(user);


    certificateRepo.save(certificate);
    Assertions.assertNotNull(user);
  }
}
