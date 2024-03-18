package com.hasil.lppaik.entity;

import javax.management.relation.Role;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum RoleEnum {
  MAHASISWA,
  KATING,
  ADMIN,
  DOSEN,
  TUTOR,
  REKTOR,
  KETUA;

  public static boolean isValidRoleEnum(final String role) {
    return Arrays.stream(RoleEnum.values())
            .map(RoleEnum::name)
            .collect(Collectors.toSet())
            .contains(role);
  }


}
