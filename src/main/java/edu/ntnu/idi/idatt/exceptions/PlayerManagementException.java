package edu.ntnu.idi.idatt.exceptions;

public class PlayerManagementException extends Exception {

  /**
   * Constructs a new PlayerManagementException with the specified detail message.
   *
   * @param message the detail message
   */
  public PlayerManagementException(String message) {
    super(message);
  }

  /**
   * Constructs a new PlayerManagementException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public PlayerManagementException(String message, Throwable cause) {
    super(message, cause);
  }


}
