package edu.ntnu.idi.idatt.exceptions;


/**
 * Thrown when required board game resources (layouts, assets, configs)
 * cannot be found, loaded, or parsed.
 *
 * <p>This typically indicates missing or malformed files needed to
 * initialize or run a game.</p>
 *
 */
public class BoardGameResourceException extends Exception {

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message The detail message
   */
  public BoardGameResourceException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message The detail message
   * @param cause The cause of the exception
   */
  public BoardGameResourceException(String message, Throwable cause) {
    super(message, cause);
  }


}
