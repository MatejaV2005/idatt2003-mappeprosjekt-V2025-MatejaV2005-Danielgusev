package edu.ntnu.idi.idatt.exceptions;

/**
 * Thrown when there is an error managing player entities, sessions, or persistence.
 *
 * <p>This exception is used when operations such as adding, removing,
 * loading, or saving players fail due to invalid data, duplicates,
 * or I/O issues.</p>
 *
 */
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
