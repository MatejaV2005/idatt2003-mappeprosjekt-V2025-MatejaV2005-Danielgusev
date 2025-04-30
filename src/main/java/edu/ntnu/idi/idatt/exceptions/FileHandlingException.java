package edu.ntnu.idi.idatt.exceptions;

/**
 * Base exception for file handling operations in the application.
 * Serves as a parent class for more specific file handling exceptions.
 */
public class FileHandlingException extends Exception {

  /**
   * Constructs a new FileHandlingException with the specified detail message.
   *
   * @param message the detail message
   */
  public FileHandlingException(String message) {
    super(message);
  }

  /**
   * Constructs a new FileHandlingException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public FileHandlingException(String message, Throwable cause) {
    super(message, cause);
  }
}
