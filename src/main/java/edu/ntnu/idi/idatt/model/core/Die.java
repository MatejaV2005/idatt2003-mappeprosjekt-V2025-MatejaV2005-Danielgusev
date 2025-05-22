package edu.ntnu.idi.idatt.model.core;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a standard six-sided die.
 *
 * <p>Each die can be rolled to produce a random value between 1 and 6 inclusive.
 * The last rolled value is retained and accessible until the next roll. Before
 * the first roll, the value defaults to 0.</p>
 *
 * @see #roll()
 * @see #getLastRolledValue()
 * @since 1.0
 */
public class Die {
  private static final Logger LOGGER =
      Logger.getLogger(Die.class.getName());

  private final Random random;
  private int lastRolledValue;

  /**
   * Creates a new {@code Die}.
   *
   * <p>Initializes the underlying random number generator. The die’s
   * {@link #getLastRolledValue() last rolled value} will be 0 until
   * {@link #roll()} is first called.</p>
   */
  public Die() {
    this.random = new Random();
    LOGGER.log(Level.FINE, "Die instance created");
  }

  /**
   * Returns the result of the most recent roll.
   *
   * @return the last rolled value (1–6), or 0 if {@link #roll()} has not yet been called
   */
  public int getLastRolledValue() {
    return lastRolledValue;
  }

  /**
   * Rolls this die, generating a new random value between 1 and 6 inclusive.
   * Updates the stored {@code lastRolledValue} and logs the result at FINER level.
   *
   * @return the value of this roll (1–6)
   */
  public int roll() {
    lastRolledValue = random.nextInt(6) + 1;
    LOGGER.log(Level.FINER, "Die rolled: new value {0}", lastRolledValue);
    return lastRolledValue;
  }
}
