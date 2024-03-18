package com.hasil.lppaik.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

  @Id
  private String id;

  @Enumerated(EnumType.STRING)
  private RoleEnum name;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
  private Set<User> users = new HashSet<>();
}
