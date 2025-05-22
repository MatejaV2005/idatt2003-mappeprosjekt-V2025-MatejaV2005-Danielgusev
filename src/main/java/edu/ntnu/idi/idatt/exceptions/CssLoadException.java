package edu.ntnu.idi.idatt.exceptions;

/**
 * Thrown when the application fails to load the CSS resource for the game view.
 *
 * <p>This exception indicates that the stylesheet could not be located,
 * read, or applied to the UI scene—typically due to a missing file,
 * incorrect path, or I/O error.</p>
 *
 */
public class CssLoadException extends RuntimeException {

  /**
   * Constructs a new CssLoadException with the specified detail message and cause.
   *
   * @param message the detail message explaining why the CSS failed to load
   * @param cause the underlying exception that triggered this failure
   */
  public CssLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
