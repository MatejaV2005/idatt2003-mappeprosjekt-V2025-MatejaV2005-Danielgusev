package edu.ntnu.idi.idatt.exceptions;

/**
 * Thrown when the application fails to load the CSS resource for the game view.
 * Dette er en irreversibel konfigurasjonsfeil – den indikerer at noe er galt i distribusjonen,
 * ikke i klientkoden.
 */

public class CssLoadException extends RuntimeException {

  public CssLoadException(String message, Throwable cause) {
    super(message, cause);
  }

}
