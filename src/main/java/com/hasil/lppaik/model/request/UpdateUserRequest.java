package com.hasil.lppaik.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hasil.lppaik.entity.Gender;
import com.hasil.lppaik.entity.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {


  @JsonIgnore
  @NotBlank
  private String username;

  @Size(min = 3, max = 255, message = "minimum is {min} - {max} character")
  private String name;

  @Email(message = "please provide well email format")
  private String email;

  private String gender;

  private String major;

  private Boolean completed;

  @Size(min = 3, max = 255, message = "minimum password length is {min} - {max} character")
  private String password;

  Set<RoleEnum> roles;
}
