package com.hasil.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @Column(unique = true)
  private String username;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(unique = true, nullable = false)
  private String email;

  private String avatar;

  private String token;

  private Boolean completed = false;

  @Lob
  private String motto;

  @Enumerated(value = EnumType.STRING)
  private Gender gender;

  @Column(name = "token_expired_at")
  private Long tokenExpiredAt;

  // attribute with relation
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles",
          joinColumns = @JoinColumn(name = "user_username", referencedColumnName = "username"),
          inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Set<Role> roles = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_activities",
          joinColumns = @JoinColumn(name = "user_username", referencedColumnName = "username"),
          inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
  private Set<Activity> activities = new HashSet<>();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "major", referencedColumnName = "id")
  private Major major;

  @OneToOne(mappedBy = "user")
  private Certificate certificate;

  @OneToOne(mappedBy = "user")
  private EmailVerification emailVerification;

  @OneToMany(mappedBy = "user")
  private List<ControlBookDetail> controlBookDetailUser;

  @OneToMany(mappedBy = "tutor")
  private List<ControlBookDetail> controlBookDetailTutor;

  @OneToMany(mappedBy = "user")
  private Set<ActivityRegister> activityRegisters = new HashSet<>();


}
