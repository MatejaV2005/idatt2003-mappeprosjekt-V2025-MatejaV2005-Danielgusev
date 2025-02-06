package edu.ntnu.idi.idatt.model;

import java.util.Random;

public class Die {
  private int lastRolledValue;  // Renamed to follow Java conventions
  private final Random random;

  public Die() {
    random = new Random();
    roll();  // Roll the die once to initialize it with a random value
  }

  // Returns the last rolled value
  public int getLastRolledValue() {
    return this.lastRolledValue;
  }

  // Rolls the die and returns the new value
  public int roll() {
    this.lastRolledValue = random.nextInt(6) + 1;  // Corrected range for a six-sided die
    return lastRolledValue;
  }
}
