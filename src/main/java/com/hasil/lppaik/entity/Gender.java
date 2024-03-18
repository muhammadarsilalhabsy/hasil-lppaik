package com.hasil.lppaik.entity;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Gender {
  MALE, FEMALE;

  public static boolean isValidGender(final String gender) {
    return Arrays.stream(Gender.values())
            .map(Gender::name)
            .collect(Collectors.toSet())
            .contains(gender);
  }
}
