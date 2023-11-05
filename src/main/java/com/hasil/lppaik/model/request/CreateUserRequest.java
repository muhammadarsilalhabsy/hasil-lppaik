package com.hasil.lppaik.model.request;

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

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {

  @NotBlank(message = "username must be not blank")
  @Size(min = 8, max = 10, message = "username must be {min} - {max} character")
  private String username;

  @NotBlank(message = "name must be not blank")
  @Size(min = 3, max = 255, message = "minimum name length is {min} - {max} character")
  private String name;

  @NotBlank(message = "email must be not blank")
  @Email(message = "please provide well email format")
  private String email;

  @NotNull(message = "gender must be not null")
  private String gender;

  @NotNull(message = "major must be not null")
  private String major;
  @Size(min = 1, message = "at least create {min} role")
  Set<RoleEnum> roles;

}
