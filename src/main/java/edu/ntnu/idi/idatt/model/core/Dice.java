package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a collection of one or more {@link Die} objects.
 * This class manages a set of dice, allowing them to be rolled together,
 * and provides methods to get the total value of the last roll or the value
 * of individual dice.
 */
public class Dice {

  /**
   * The list of Die objects managed by this Dice instance.
   * This list is final, but its contents (the Die objects) are mutable.
   */
  private final List<Die> dice;

  /**
   * Constructs a new {@code Dice} object containing a specified number of dice.
   * Each die is a standard six-sided die.
   *
   * @param numberOfDice The number of dice to include in this set.
   * Must be strictly positive (greater than 0).
   * @throws IllegalArgumentException if numberOfDice is not strictly positive.
   */
  public Dice(int numberOfDice) {
    ExceptionHandling.requireStrictlyPositive(numberOfDice, "Number of dice");

    this.dice = new ArrayList<>();
    initializeDice(numberOfDice);
  }

  /**
   * Initializes and adds the specified number of {@link Die} instances
   * to this {@code Dice} collection. This method is called by the constructor.
   *
   * @param numberOfDice The number of dice to create and add.
   */
  public void initializeDice(int numberOfDice) {
    for (int i = 0; i < numberOfDice; i++) {
      dice.add(new Die());
    }
  }

  /**
   * Rolls all dice in this collection.
   * Each die will generate a new random value between 1 and 6.
   *
   * @return The sum of the values rolled on all dice in this collection.
   */
  public int roll() {
    int sum = 0;
    if (this.dice.isEmpty()) {
      return 0;
    }
    for (Die die : this.dice) {
      sum += die.roll();
    }

    return sum;
  }

  public int getDieValue(int dieNumber) {
    ExceptionHandling.requireIndexRange(dieNumber, 0, dice.size() - 1, "Die index");

    return dice.get(dieNumber).getLastRolledValue();
  }

  public int getTotalDiceValue() {
    int totalValue = 0;
    for (Die die : dice) {
      totalValue += die.getLastRolledValue();
    }
    return totalValue;
  }

  public int getNumberOfDice() {
    return dice.size();
  }



  /**
   * Returns an unmodifiable list of the {@link Die} objects in this collection.
   * This allows inspection of individual dice but prevents external modification
   * of the dice collection itself.
   *
   * @return An unmodifiable list of dice.
   */
  public List<Die> getDice() {
    return Collections.unmodifiableList(this.dice);
  }




}
