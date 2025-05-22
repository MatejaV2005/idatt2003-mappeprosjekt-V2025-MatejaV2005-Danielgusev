package edu.ntnu.idi.idatt.exceptions;

/**
 * Exception thrown when there is an error managing board game board states,
 * configurations, or operations.
 *
 * <p>This exception is typically used when board operations such as initialization,
 * tile management, player positioning, or board state transitions fail.</p>
 *
 */
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
