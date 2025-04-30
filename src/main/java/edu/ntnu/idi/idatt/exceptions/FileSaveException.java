package edu.ntnu.idi.idatt.exceptions;

public class FileSaveException extends FileHandlingException {

  /**
   * Constructs a new FileSaveException with the specified detail message.
   *
   * @param message the detail message
   */
  public FileSaveException(String message) {
    super(message);
  }

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


