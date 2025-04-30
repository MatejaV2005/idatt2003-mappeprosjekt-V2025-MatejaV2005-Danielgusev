package edu.ntnu.idi.idatt.exceptions;

public class JsonFileException extends FileHandlingException {
  /**
   * Constructs a new JsonFileException with the specified detail message.
   *
   * @param message the detail message
   */
  public JsonFileException(String message) {
    super(message);
  }

  /**
   * Constructs a new JsonFileException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public JsonFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
