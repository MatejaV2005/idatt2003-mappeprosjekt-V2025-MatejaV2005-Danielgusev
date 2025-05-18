package edu.ntnu.idi.idatt.utils;

import java.util.Collection;


public final class ExceptionHandling {


  private ExceptionHandling() {
  }


  public static void requireNonNullOrBlank(String input, String fieldName) {
    if (input == null || input.trim().isBlank()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or blank");
    }
  }


  public static void requireNonNull(Object input, String fieldName) {
    if (input == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null.");
    }
  }


  public static void requirePositive(int input, String fieldName) {
    if (input < 0) {
      throw new IllegalArgumentException(fieldName + " must be positive");
    }
  }


  public static void requireIndexRange(int index, int min, int max, String fieldName) {
    if (index < min || index > max) {
      throw new IllegalArgumentException(
          fieldName + " (" + index + ") is out of bounds. Must be between " + min + " and " + max + " (inclusive)."
      );
    }
  }

  public static void requireNonNegative(int input, String fieldName) {
    if (input < 0) {
      throw new IllegalArgumentException(fieldName + " must be non-negative (0 or greater).");
    }
  }


  public static void requireStrictlyPositive(int input, String fieldName) {
    if (input <= 0) {
      throw new IllegalArgumentException(fieldName + " must be strictly positive (greater than 0).");
    }
  }


  public static void requireNotEmpty(Collection<?> collection, String fieldName) {
    if (collection == null || collection.isEmpty()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }


  public static void requireLength(String input, int minLength, int maxLength, String fieldName) {
    if (minLength < 0) {
      throw new IllegalArgumentException("Precondition minLength cannot be negative for requireLength check.");
    }
    if (maxLength < minLength) {
      throw new IllegalArgumentException("Precondition maxLength cannot be less than minLength for requireLength check.");
    }

    if (input == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null when checking length. Use requireNonNull first.");
    }

    int length = input.length();
    if (length < minLength || length > maxLength) {
      if (minLength == maxLength) {
        throw new IllegalArgumentException(
            fieldName + " must be exactly " + minLength + " characters long. Actual: " + length
        );
      }
      throw new IllegalArgumentException(
          fieldName + " must be between " + minLength + " and " + maxLength + " characters long. Actual: " + length
      );
    }
  }


  public static void requireState(boolean expression, String message) {
    if (!expression) {
      throw new IllegalStateException(message);
    }
  }
}
