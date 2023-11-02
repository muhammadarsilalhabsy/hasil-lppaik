//package com.hasil.lppaik.logic;
//
//import com.hasil.lppaik.entity.*;
//import com.hasil.lppaik.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class TestLogic {
//  private final RoleRepository roleRepo;
//
//  private final UserRepository userRepo;
//  private final MajorRepository majorRepo;
//
//  @Autowired
//  public TestLogic(RoleRepository roleRepo, UserRepository userRepo, MajorRepository majorRepo) {
//    this.roleRepo = roleRepo;
//    this.userRepo = userRepo;
//    this.majorRepo = majorRepo;
//  }
//
//  @BeforeEach
//  void setUp() {
//    userRepo.deleteAll();
//    roleRepo.findAll() // this operation only support if @ManyToMany have property fetch = FetchType.EAGER
//            .forEach(role -> {
//              role.getUsers().forEach(u -> u.getRoles().remove(role));
//              userRepo.saveAll(role.getUsers());
//
//              roleRepo.delete(role);
//            });
//    majorRepo.deleteAll();
//
//    Role role1 = new Role();
//    role1.setId("R001");
//    role1.setName(RoleEnum.ADMIN);
//
//    Role role2 = new Role();
//    role2.setId("R002");
//    role2.setName(RoleEnum.MAHASISWA);
//
//    roleRepo.saveAll(List.of(role1, role2));
//
//    Major pti = new Major();
//    pti.setId("J01");
//    pti.setName("PTI");
//    majorRepo.save(pti);
//
//    User user1 = new User();
//    user1.setName("budi");
//    user1.setPassword("rahasia");
//    user1.setEmail("budi@gmail.com");
//    user1.setUsername("87654321");
//    user1.setRoles(Set.of(role1, role2));
//
//    User user2 = new User();
//    user2.setName("otong");
//    user2.setPassword("rahasia");
//    user2.setEmail("otong@gmail.com");
//    user2.setUsername("12345678");
//    user2.setRoles(Set.of(role2));
//
//    userRepo.saveAll(List.of(user1, user2));
//
//  }
//
//  @Test
//  @Disabled
//  void testAnyMatchUserHasRoleAdmin() {
//
//    User user = userRepo.findById("2191100").orElse(null);
//
//    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));
//    assertNotNull(user);
//    assertTrue(isAdmin);
//
//  }
//
//  @Test
//  @Disabled
//  void testAnyMatchUserHasNotRoleAdmin() {
//
//    User user = userRepo.findById("919191").orElse(null);
//
//    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));
//    boolean isMahasiswa = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.MAHASISWA));
//    assertNotNull(user);
//    assertFalse(isAdmin);
//    assertTrue(isMahasiswa);
//
//  }
//
//  @Test
//  @Disabled
//  void testAnyMatchUserMustHaveRoleKETINGorADMINorTUTORIsAllowFlase() {
//
//    User user = userRepo.findById("12345678").orElse(null);
//
//    boolean isAllow = user.getRoles().stream()
//            .anyMatch(
//                    role -> role.getName().equals(RoleEnum.ADMIN)
//                            || role.getName().equals(RoleEnum.KATING)
//                            || role.getName().equals(RoleEnum.DOSEN));
//    assertNotNull(user);
//    assertFalse(isAllow);
//
//  }
//
//  @Test
//  @Disabled
//  void testAnyMatchUserMustHaveRoleKETINGorADMINorTUTORIsAllowTrue() {
//
//    User user = userRepo.findById("87654321").orElse(null);
//
//    boolean isAllow = user.getRoles().stream()
//            .anyMatch(
//                    role -> role.getName().equals(RoleEnum.ADMIN)
//                            || role.getName().equals(RoleEnum.KATING)
//                            || role.getName().equals(RoleEnum.DOSEN));
//    assertNotNull(user);
//    assertTrue(isAllow);
//  }
//
//
//}
