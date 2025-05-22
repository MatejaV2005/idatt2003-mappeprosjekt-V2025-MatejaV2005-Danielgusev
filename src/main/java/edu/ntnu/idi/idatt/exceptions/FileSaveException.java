package edu.ntnu.idi.idatt.exceptions;

/**
 * Thrown when an error occurs saving data to a file.
 *
 * <p>This exception indicates that a file could not be written to or saved,
 * typically due to disk I/O errors, permission issues, or serialization failures.
 *
 */
public class FileSaveException extends FileHandlingException {

  /**
   * Constructs a new FileSaveException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public FileSaveException(String message, Throwable cause) {
    super(message, cause);
  }



}


