package edu.ntnu.idi.idatt.model;

import java.util.logging.Logger;
import java.util.Random;

public class Die {
  private int lastRolledValue;  // Renamed to follow Java conventions
  private final Random random;
  private static final Logger LOGGER = Logger.getLogger(Die.class.getName());

  /*
  * TODO: fill out javadoc
  * */
  public Die() {
    random = new Random();
    // roll(); //Initializes the die-instance with a value
  }

  // Returns the last rolled value
  public int getLastRolledValue() {
    return this.lastRolledValue;
  }

  /*
  * TODO: Fill out javadoc
  * */
  public int roll() {
    //LOGGER.info("Dice is rolling...");

    this.lastRolledValue = random.nextInt(6) + 1;  // Corrected range for a six-sided die
    //LOGGER.info("Rolled value is: " + lastRolledValue);

    return lastRolledValue;
  }
}
