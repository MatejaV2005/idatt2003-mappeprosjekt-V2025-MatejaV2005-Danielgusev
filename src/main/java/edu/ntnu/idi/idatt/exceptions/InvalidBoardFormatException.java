package edu.ntnu.idi.idatt.exceptions;

/**
 * Thrown when a board file’s format is invalid or cannot be parsed.
 *
 * <p>This exception indicates that the board data read from disk
 * does not conform to the expected structure, contains missing fields,
 * or references invalid tile IDs.
 *
 */
public class InvalidBoardFormatException extends FileLoadException {

  /**
   * Constructs a new InvalidBoardFormatException with the specified detail message.
   *
   * @param message the detail message explaining the format error
   */
  public InvalidBoardFormatException(String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidBoardFormatException with the specified detail message and cause.
   *
   * @param message the detail message explaining the format error
   * @param cause   the underlying exception that caused this failure
   */
  public InvalidBoardFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
