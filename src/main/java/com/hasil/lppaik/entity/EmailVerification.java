package com.hasil.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_verification")
public class EmailVerification {

  @Id
  private String id;

  @OneToOne
  @JoinColumn(name = "user_username", referencedColumnName = "username")
  private User user;

  private Long expired;
}
