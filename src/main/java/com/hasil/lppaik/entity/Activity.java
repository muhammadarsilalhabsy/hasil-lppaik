package com.hasil.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "activities")
public class Activity {

  @Id
  private String id;

  private String title;

  private Boolean mandatory = false;
  private LocalDate date = LocalDate.now();

  private String location;

  @Lob
  private String description;

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_time")
  private LocalTime endTime;

  @ManyToMany(mappedBy = "activities", fetch = FetchType.EAGER)
  private Set<User> users = new HashSet<>();

  @OneToMany(mappedBy = "activity")
  private List<ActivityImage> images = new ArrayList<>();

}
