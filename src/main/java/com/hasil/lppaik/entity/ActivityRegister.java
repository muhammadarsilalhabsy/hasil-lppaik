package com.hasil.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "activity_registers")
public class ActivityRegister {

  @Id
  private String id;

  @ManyToOne
  @JoinColumn(name = "activity_id", referencedColumnName = "id")
  private Activity activity;

  @ManyToOne
  @JoinColumn(name = "user_username", referencedColumnName = "username")
  private User user;
}
