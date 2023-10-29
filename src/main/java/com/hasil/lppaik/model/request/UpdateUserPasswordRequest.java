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
public class UpdateUserPasswordRequest {


  @Size(min = 5, max = 255, message = "minimum is {min} - {max} character")
  private String newPassword;

  private String confirmNewPassword;

}
