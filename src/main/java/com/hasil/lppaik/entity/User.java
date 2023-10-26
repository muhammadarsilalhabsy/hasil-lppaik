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
  private String username;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(unique = true, nullable = false)
  private String email;

  private String avatar;

  private String token;

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
          inverseJoinColumns = @JoinColumn(name = "activity_obj_id", referencedColumnName = "obj_id"))
  private Set<Activity> activities = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "major", referencedColumnName = "id")
  private Major major;

  @OneToOne(mappedBy = "user")
  private Certificate certificate;

  @OneToMany(mappedBy = "user")
  private List<ControlBookDetail> controlBookDetailUser;

  @OneToMany(mappedBy = "tutor")
  private List<ControlBookDetail> controlBookDetailTutor;



}
