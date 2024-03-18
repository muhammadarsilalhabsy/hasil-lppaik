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
@Table(name = "activity_images")
public class ActivityImage {

  @Id
  private String id;

  @Lob
  private String image;

  @ManyToOne
  @JoinColumn(name = "activity_id", referencedColumnName = "id")
  private Activity activity;
}
