package edu.ntnu.idi.idatt.utils;

import java.util.Collection;

/**
 * Utility class for validating method arguments and program state.
 *
 * <p>This class provides precondition checks that throw informative
 * {@link IllegalArgumentException} or {@link IllegalStateException}
 * if violations are detected.
 * </p>
 *
 * <p>All methods are static and the class is non-instantiable.
 * </p>
 */
public final class ExceptionHandling {

  /**
   * Private constructor to prevent instantiation.
   */
  private ExceptionHandling() {}

  /**
   * Validates that the given string is not null or blank (i.e., empty or contains only whitespace).
   *
   * @param input     the string to validate
   * @param fieldName the name of the field (used in exception message)
   * @throws IllegalArgumentException if {@code input} is null or blank
   */
  public static void requireNonNullOrBlank(String input, String fieldName) {
    if (input == null || input.trim().isBlank()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or blank");
    }
  }

  /**
   * Validates that the given object is not null.
   *
   * @param input     the object to check
   * @param fieldName the name of the field (used in exception message)
   * @throws IllegalArgumentException if {@code input} is null
   */
  public static void requireNonNull(Object input, String fieldName) {
    if (input == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null.");
    }
  }

  /**
   * Validates that the given integer is positive (greater than zero).
   *
   * @param input     the number to check
   * @param fieldName the name of the field (used in exception message)
   * @throws IllegalArgumentException if {@code input} is negative
   */
  public static void requirePositive(int input, String fieldName) {
    if (input < 0) {
      throw new IllegalArgumentException(fieldName + " must be positive");
    }
  }

  /**
   * Validates that an index is within a specified range.
   *
   * @param index     the index to validate
   * @param min       minimum acceptable index (inclusive)
   * @param max       maximum acceptable index (inclusive)
   * @param fieldName the name of the field (used in exception message)
   * @throws IllegalArgumentException if {@code index} is outside the range
   */
  public static void requireIndexRange(int index, int min, int max, String fieldName) {
    if (index < min || index > max) {
      throw new IllegalArgumentException(
          fieldName
              + index + " is out of bounds. Must be between "
              + min + " and "
              + max + " (inclusive)."
      );
    }
  }

  /**
   * Validates that the number is non-negative (zero or greater).
   *
   * @param input     the number to check
   * @param fieldName the name of the field (used in exception message)
   * @throws IllegalArgumentException if {@code input} is negative
   */
  public static void requireNonNegative(int input, String fieldName) {
    if (input < 0) {
      throw new IllegalArgumentException(fieldName + " must be non-negative (0 or greater).");
    }
  }

  /**
   * Validates that the number is strictly positive (greater than zero).
   *
   * @param input     the number to check
   * @param fieldName the name of the field (used in exception message)
   * @throws IllegalArgumentException if {@code input} is less than or equal to zero
   */
  public static void requireStrictlyPositive(int input, String fieldName) {
    if (input <= 0) {
      throw new IllegalArgumentException(fieldName
          + " must be strictly positive (greater than 0).");
    }
  }

  /**
   * Validates that the collection is not null or empty.
   *
   * @param collection the collection to check
   * @param fieldName  the name of the field (used in exception message)
   * @throws IllegalArgumentException if {@code collection} is null or empty
   */
  public static void requireNotEmpty(Collection<?> collection, String fieldName) {
    if (collection == null || collection.isEmpty()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }

  /**
   * Validates that a specific condition (state) holds true.
   *
   * @param expression the boolean expression to check
   * @param message    the error message if the expression is false
   * @throws IllegalStateException if {@code expression} is false
   */
  public static void requireState(boolean expression, String message) {
    if (!expression) {
      throw new IllegalStateException(message);
    }
  }
}
