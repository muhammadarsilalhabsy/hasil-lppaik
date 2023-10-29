package com.hasil.lppaik.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hasil.lppaik.entity.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UpdateUserDetailRequest {

  @Email(message = "please provide well email format")
  private String email;

  private String motto;
}
