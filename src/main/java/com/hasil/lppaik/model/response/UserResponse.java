package com.hasil.lppaik.model.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hasil.lppaik.entity.Gender;
import com.hasil.lppaik.entity.Major;
import com.hasil.lppaik.entity.Role;
import com.hasil.lppaik.entity.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

  private String username;
  private String name;
  private String email;
  private String avatar;
  private Gender gender;
  private Boolean completed;
  private String major;
  private String motto;

  private List<RoleEnum> roles;
}
