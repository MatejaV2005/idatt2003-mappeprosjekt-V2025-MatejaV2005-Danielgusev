package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.List;

public class Dice {
  private final List<Die> dice;

  public Dice(int numberOfDice) {
    ExceptionHandling.requirePositive(numberOfDice, "numberOfDice");

    dice = new ArrayList<>();
    addDice(numberOfDice);

  }

  private void addDice(int numberOfDice) {
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
    ExceptionHandling.requireIndexRange(dieNumber, 0, dice.size(), "number of dice");

    return dice.get(dieNumber).getLastRolledValue();
  }







}
