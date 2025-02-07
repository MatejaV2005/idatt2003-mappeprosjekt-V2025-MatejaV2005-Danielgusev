package edu.ntnu.idi.idatt.model;

import java.util.Random;
import java.util.logging.Logger;

public class Die {
  private int lastRolledValue;  // Renamed to follow Java conventions
  private final Random random;
  private static final Logger LOGGER = Logger.getLogger(Die.class.getName());
  public Die() {
    random = new Random();
    roll();  // Roll the die once to initialize it with a random value
  }

  
  public int getLastRolledValue() {
    return this.lastRolledValue;
  }

  // Rolls the die and returns the new value
  public int roll() {
    LOGGER.info("The dice is rolling...");

    this.lastRolledValue = random.nextInt(6) + 1;

    LOGGER.info("The dice has rolled " + lastRolledValue);
    return lastRolledValue;
  }
}
