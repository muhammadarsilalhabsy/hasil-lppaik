package com.hasil.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "control_book_details")
public class ControlBookDetail {

  @Id
  private String id;

  private String lesson;

  private String description;

  private LocalDate date;

  @ManyToOne
  @JoinColumn(name = "user_username", referencedColumnName = "username")
  private User user;

  @ManyToOne
  @JoinColumn(name = "tutor", referencedColumnName = "username")
  private User tutor;

}
