package edu.ntnu.idi.idatt.model;

import java.util.ArrayList;
import java.util.List;

public class Dice {
  private final List<Die> dice = new ArrayList<>();

  public Dice(int numberOfDice) {
    for (int i = 0; i < numberOfDice; i++) {
      dice.add(new Die());
    }
  }

  public int roll() {
    int sum = 0;
    for (Die die : dice) {
      sum += die.roll();
    }

    return sum;
  }

  public int getDieValue(int dieNumber) {
    if (dieNumber < 0 || dieNumber >= dice.size()) {
      throw new IndexOutOfBoundsException("Invalid die number: " + dieNumber + ". Must be between 0 and " + (dice.size() - 1) + ".");

    }

    return dice.get(dieNumber).getLastRolledValue();
  }







}
