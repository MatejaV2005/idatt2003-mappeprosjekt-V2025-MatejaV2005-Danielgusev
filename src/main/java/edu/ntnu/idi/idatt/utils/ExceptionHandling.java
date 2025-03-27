package edu.ntnu.idi.idatt.utils;


public class ExceptionHandling {

  public static void requireNonNull(String input, String fieldname) {
    if (input == null || input.trim().isBlank()) {
      throw new IllegalArgumentException(fieldname + " cannot be null or blank");
    }
  }

  public static void requireNonNull(Object input, String fieldname) {
    if (input == null) {
      throw new IllegalArgumentException(fieldname + " cannot be null or doesnt exist");
    }
  }

  public static void requirePositive(int input, String fieldname) {
    if (input < 0) {
      throw new IllegalArgumentException(fieldname + " must be positive");
    }
  }

  public static void requireIndexRange(int index, int min, int max, String fieldname) {
    if (index < min || index > max) {
      throw new IllegalArgumentException(fieldname + " must be between " + min + " and " + max);
    }
  }




}
