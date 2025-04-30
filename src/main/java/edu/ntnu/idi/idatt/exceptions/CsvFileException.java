package edu.ntnu.idi.idatt.exceptions;

public class CsvFileException extends FileHandlingException {

  /**
   * Constructs a new CsvFileException with the specified detail message.
   *
   * @param message the detail message
   */
  public CsvFileException(String message) {
    super(message);
  }

  /**
   * Constructs a new CsvFileException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public CsvFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
