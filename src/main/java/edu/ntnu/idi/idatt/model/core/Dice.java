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
  private final List<Die> diceCollection;

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

    this.diceCollection = new ArrayList<>();
    initializeDice(numberOfDice);
  }

  /**
   * Initializes and adds the specified number of {@link Die} instances
   * to this {@code Dice} collection. This method is called by the constructor.
   *
   * @param numberOfDice The number of dice to create and add.
   */
  private void initializeDice(int numberOfDice) {
    for (int i = 0; i < numberOfDice; i++) {
      this.diceCollection.add(new Die());
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
    if (this.diceCollection.isEmpty()) {
      return 0;
    }
    for (Die die : this.diceCollection) {
      sum += die.roll();
    }
    return sum;
  }

  /**
   * Gets the last rolled value of a specific die in the collection.
   * The dice are 0-indexed.
   *
   * @param dieIndex The 0-based index of the die whose value is requested.
   * @return The last value rolled on the specified die.
   * @throws IllegalArgumentException if dieIndex is out of bounds for this dice collection.
   *
   */
  public int getDieValue(int dieIndex) {
    if (this.diceCollection.isEmpty() && dieIndex == 0) { // Spesiell case for tom liste
      throw new IllegalArgumentException("Die index (0) is out of bounds for an empty dice collection.");
    }
    ExceptionHandling.requireIndexRange(dieIndex, 0, this.diceCollection.size() - 1, "Die index");
    return this.diceCollection.get(dieIndex).getLastRolledValue();
  }

  /**
   * Gets the total sum of the last rolled values of all dice in this collection.
   * This method does not re-roll the dice; it sums their current face values.
   *
   * @return The sum of the current values of all dice. Returns 0 if there are no dice.
   */
  public int getTotalDiceValue() {
    int totalValue = 0;
    if (this.diceCollection.isEmpty()) {
      return 0;
    }
    for (Die die : this.diceCollection) {
      totalValue += die.getLastRolledValue();
    }
    return totalValue;
  }

  /**
   * Gets the number of dice in this collection.
   *
   * @return The count of dice.
   */
  public int getNumberOfDice() {
    return this.diceCollection.size();
  }

  /**
   * Returns an unmodifiable list of the {@link Die} objects in this collection.
   * This allows inspection of individual dice but prevents external modification
   * of the dice collection itself.
   *
   * @return An unmodifiable list of dice.
   */
  public List<Die> getDice() {
    return Collections.unmodifiableList(this.diceCollection);
  }


  /**
   * Returns a string representation of this Dice collection, typically indicating
   * the number of dice it contains and possibly their current values.
   *
   * @return A string representation of this Dice object.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Dice{count=").append(diceCollection.size()).append(", values=[");
    for (int i = 0; i < diceCollection.size(); i++) {
      sb.append(diceCollection.get(i).getLastRolledValue());
      if (i < diceCollection.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append("]}");
    return sb.toString();
  }

}
