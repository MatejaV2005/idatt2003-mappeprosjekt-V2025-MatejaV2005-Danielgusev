package edu.ntnu.idi.idatt.exceptions;

public class FileLoadException extends FileHandlingException {

  /**
   * Constructs a new FileLoadException with the specified detail message.
   *
   * @param message the detail message
   */
  public FileLoadException(String message) {
    super(message);
  }

  /**
   * Constructs a new FileLoadException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public FileLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
