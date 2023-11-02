//package com.hasil.lppaik.insert;
//
//import com.hasil.lppaik.entity.*;
//import com.hasil.lppaik.repository.*;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//@SpringBootTest
//public class TestInsert {
//
//  private final RoleRepository roleRepo;
//
//  private final UserRepository userRepo;
//  private final MajorRepository majorRepo;
//
//  private final CertificateRepository certificateRepo;
//
//  private final ControlBookDetailRepository cbdRepo;
//
//  private final ActivityRepository activityRepo;
//
//  private final ImageRepository imgRepo;
//
//
//  @Autowired
//  public TestInsert(RoleRepository roleRepo, UserRepository userRepo, MajorRepository majorRepo, CertificateRepository certificateRepo, ControlBookDetailRepository cbdRepo, ActivityRepository activityRepo, ImageRepository imgRepo) {
//    this.roleRepo = roleRepo;
//    this.userRepo = userRepo;
//    this.majorRepo = majorRepo;
//    this.certificateRepo = certificateRepo;
//    this.cbdRepo = cbdRepo;
//    this.activityRepo = activityRepo;
//    this.imgRepo = imgRepo;
//  }
//
//  @BeforeEach
//  void setUp() {
//    imgRepo.findAll()
//            .forEach(img -> {
//              img.getActivities().forEach(u -> u.getImages().remove(img));
//              activityRepo.saveAll(img.getActivities());
//              imgRepo.delete(img);
//            });
//    imgRepo.deleteAll();
//    activityRepo.findAll()
//            .forEach(activity -> {
//              activity.getUsers().forEach(u -> u.getActivities().remove(activity));
//              userRepo.saveAll(activity.getUsers());
//              activityRepo.delete(activity);
//            });
//    activityRepo.deleteAll();
//    certificateRepo.deleteAll();
//    cbdRepo.deleteAll();
//    userRepo.deleteAll();
//    roleRepo.findAll() // this operation only support if @ManyToMany have property fetch = FetchType.EAGER
//            .forEach(role -> {
//              role.getUsers().forEach(u -> u.getRoles().remove(role));
//              userRepo.saveAll(role.getUsers());
//
//              roleRepo.delete(role);
//            });
//    roleRepo.deleteAll();
//    majorRepo.deleteAll();
//
//    Role role = new Role();
//    role.setId("test");
//    role.setName(RoleEnum.ADMIN);
//    roleRepo.save(role);
//
//
//    Major pti = new Major();
//    pti.setId("J01");
//    pti.setName("PTI");
//
//    Major hukum = new Major();
//    hukum.setId("J02");
//    hukum.setName("HUKUM");
//
//    User user = new User();
//    user.setName("budi");
//    user.setPassword("rahasia");
//    user.setEmail("budi@gmail.com");
//    user.setUsername("2191100");
//
//    User user1 = new User();
//    user1.setName("apon");
//    user1.setPassword("rahasia");
//    user1.setEmail("apom@gmail.com");
//    user1.setUsername("219999");
//    user1.setCompleted(true);
//
//    Activity activity = new Activity();
//    activity.setId(1);
//    activity.setDate(LocalDate.now());
//    activity.setEndTime(LocalTime.now());
//    activity.setStartTime(LocalTime.now());
//    activity.setLocation("UMK");
//    activity.setTitle("activity 1");
//    activity.setDescription("simple activity desc");
//    activity.setObjId("obj-id-1");
//
//
//    activityRepo.save(activity);
//    userRepo.saveAll(List.of(user, user1));
//    majorRepo.saveAll(List.of(pti, hukum));
//
//  }
//
//  @Test
//  void testIsCompleted() {
//    User apon = userRepo.findById("219999").orElse(null);
//    User budi = userRepo.findById("2191100").orElse(null);
//
//    Assertions.assertNotNull(apon);
//    Assertions.assertNotNull(budi);
//    Assertions.assertTrue(apon.getCompleted());
//    Assertions.assertFalse(budi.getCompleted());
//
//
//  }
//
//  @Test
//  @Disabled
//  void testInsertImageToActivites() {
//    Activity activity = activityRepo.findByObjId("obj-id-1").orElse(null);
//    Image image = new Image();
//    image.setPath("/gambar/activity1.png");
//    image.setType(".png");
//    image.setId(1L);
//    Image image2 = new Image();
//    image2.setPath("/gambar/activity1-1.png");
//    image2.setType(".png");
//    image2.setId(2L);
//
//    imgRepo.saveAll(List.of(image, image2));
//
//    activity.getImages().add(image);
//    activity.getImages().add(image2);
//
//    activityRepo.save(activity);
//  }
//
//  @Test
//  void testInsertActivityToUser() {
//    Activity activity = activityRepo.findByObjId("obj-id-1").orElse(null);
//    User user = userRepo.findById("219999").orElse(null);
//    user.getActivities().add(activity);
//
//    userRepo.save(user);
//    Assertions.assertNotNull(activity);
//    Assertions.assertNotNull(user);
//  }
//
//  @Test
//  void testInsertBTQDetailsToUser() {
//    User user = userRepo.findById("2191100").orElse(null);
//    User tutor = userRepo.findById("219999").orElse(null);
//
//    ControlBookDetail cbd1 = new ControlBookDetail();
//    cbd1.setDate(LocalDate.now());
//    cbd1.setId(UUID.randomUUID().toString());
//    cbd1.setDescription("simple desc 1");
//    cbd1.setLesson("simple lesson 1");
//    cbd1.setUser(user);
//    cbd1.setTutor(tutor);
//
//    ControlBookDetail cbd2 = new ControlBookDetail();
//    cbd2.setDate(LocalDate.now());
//    cbd2.setId(UUID.randomUUID().toString());
//    cbd2.setDescription("simple desc 2");
//    cbd2.setLesson("simple lesson 2");
//    cbd2.setUser(user);
//    cbd2.setTutor(tutor);
//
//    cbdRepo.saveAll(List.of(cbd1,cbd2));
//
//    Assertions.assertNotNull(user);
//    Assertions.assertNotNull(tutor);
//  }
//
//  @Test
//  void testInsertUserAndRole() {
//
//    Role admin = roleRepo.findById("test").orElse(null);
//
//    Role mahasiswa = new Role();
//    mahasiswa.setId("R002");
//    mahasiswa.setName(RoleEnum.MAHASISWA);
//
//    Role kating = new Role();
//    kating.setId("R003");
//    kating.setName(RoleEnum.DOSEN);
//
//    Set<Role> roles = new HashSet<>();
//
//    roles.add(mahasiswa);
//    roles.add(kating);
//
//    User user = new User();
//    user.setName("otong");
//    user.setPassword("rahasia");
//    user.setEmail("otong@gmail.com");
//    user.setUsername("020901");
//    user.setRoles(roles);
//    user.setCompleted(false);
//
//    User user2 = new User();
//    user2.setName("jamal");
//    user2.setPassword("rahasia");
//    user2.setEmail("jamal@gmail.com");
//    user2.setUsername("020902");
//    user2.setRoles(Set.of(mahasiswa));
//    user2.setCompleted(true);
//
//
//    User user3 = new User();
//    user3.setName("abilal");
//    user3.setPassword("rahasia");
//    user3.setEmail("abilal@gmail.com");
//    user3.setUsername("020903");
//    user3.setRoles(Set.of(admin));
//    user3.setCompleted(false);
//
//    roleRepo.saveAll(roles);
//    userRepo.saveAll(List.of(user, user2));
//
//    Assertions.assertNotNull(admin);
//  }
//
//  @Test
//  void testRole() {
//
//    Role role = roleRepo.findByName(RoleEnum.ADMIN)
//            .orElseThrow();
//
//    System.out.println(role.getName());
//    System.out.println(role.getId());
//  }
//
//  @Test
//  void testMajorAndUsers() {
//    Major major = majorRepo.findById("J01").orElse(null);
//
//    User user = new User();
//    user.setName("otong");
//    user.setPassword("rahasia");
//    user.setEmail("otong@gmail.com");
//    user.setUsername("020901");
//    user.setMajor(major);
//
//    userRepo.save(user);
//
//    Assertions.assertNotNull(major);
//  }
//
//  @Test
//  void testCertificatesAndUsers() {
//    User user = userRepo.findById("2191100").orElse(null);
//
//
//    Certificate certificate = new Certificate();
//    certificate.setId(UUID.randomUUID().toString());
//    certificate.setUser(user);
//
//
//    certificateRepo.save(certificate);
//    Assertions.assertNotNull(user);
//  }
//}
