package edu.ntnu.idi.idatt.exceptions;

/**
 * Thrown when an error occurs loading a file from persistent storage.
 *
 * <p>This exception indicates that a file could not be opened or read,
 * typically due to missing files, I/O errors, or data format issues.
 *
 */
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
