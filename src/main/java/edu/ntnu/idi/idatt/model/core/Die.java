package edu.ntnu.idi.idatt.model.core;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a single, standard six-sided die.
 * This class allows for rolling the die to generate a random value between 1 and 6 (inclusive)
 * and retrieving the last rolled value.
 */
public class Die {
  private int lastRolledValue;

  private final Random random;

  private static final Logger LOGGER = Logger.getLogger(Die.class.getName());

  /**
   * Constructs a new {@code Die} object.
   * Initializes the random number generator. The die has not been "rolled"
   * initially, so {@link #getLastRolledValue()} will return its default
   * initialized value (e.g., 0) until {@link #roll()} is called.
   *
   *
   * Consider calling {@link #roll()} within the constructor if a die should
   * always have a valid initial face value (1-6) upon creation.
   * For this implementation, an explicit roll is required to set the first value.
   *
   */
  public Die() {
    this.random = new Random();
    LOGGER.log(Level.FINE, "Die instance created.");
  }

  /**
   * Gets the value of the last roll performed on this die.
   * If {@link #roll()} has not been called yet after construction,
   * this will return the initial default value (e.g., 0).
   *
   * @return The last value rolled, an integer between 1 and 6 if rolled,
   * or its initial default if not yet rolled.
   */
  public int getLastRolledValue() {
    return this.lastRolledValue;
  }

  /**
   * Simulates rolling the die, generating a new random value between 1 and 6 (inclusive).
   * The new value is stored and can be retrieved using {@link #getLastRolledValue()}.
   *
   * @return The new value rolled, an integer between 1 and 6.
   */
  public int roll() {
    this.lastRolledValue = this.random.nextInt(6) + 1;

    LOGGER.log(Level.FINER, "Die rolled. New value: {0}", this.lastRolledValue);

    return this.lastRolledValue;
  }

}
