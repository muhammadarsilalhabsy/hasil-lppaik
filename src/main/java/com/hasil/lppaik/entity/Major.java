package com.hasil.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "majors")
public class Major {
  @Id
  private String id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "major")
  private List<User> users;
}
