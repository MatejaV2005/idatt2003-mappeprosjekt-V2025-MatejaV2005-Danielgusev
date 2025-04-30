package edu.ntnu.idi.idatt.exceptions;

public class BoardManagementException extends Exception  {

  /**
   * Constructs a new BoardManagementException with the specified detail message.
   *
   * @param message the detail message
   */
  public BoardManagementException(String message) {
    super(message);
  }

  /**
   * Constructs a new BoardManagementException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public BoardManagementException(String message, Throwable cause) {
    super(message, cause);
  }
}
