package com.hasil.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "activities")
public class Activity {

  @Id
  private Integer id;

  @Column(name = "obj_id")
  private String objId;

  private String title;

  private LocalDate date;

  private String location;

  @Lob
  private String description;

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_time")
  private LocalTime endTime;

  @ManyToMany(mappedBy = "activities", fetch = FetchType.EAGER)
  private Set<User> users = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "image_activities",
          joinColumns = @JoinColumn(name = "activity_obj_id", referencedColumnName = "obj_id"),
          inverseJoinColumns = @JoinColumn(name = "image_id", referencedColumnName = "id"))
  private Set<Image> images = new HashSet<>();

}
