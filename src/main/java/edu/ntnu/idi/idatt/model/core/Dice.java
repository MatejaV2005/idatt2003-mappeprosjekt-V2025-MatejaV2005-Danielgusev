package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a collection of one or more {@link Die} objects that can be
 * rolled together as a set.
 *
 * <p>Provides methods to roll all dice at once, inspect the total or
 * individual die values from the last roll, and query the number of dice.</p>
 *
 * @see Die
 * @since 1.0
 */
public class Dice {

  private final List<Die> diceCollection;

  /**
   * Constructs a new {@code Dice} set containing the specified number of six-sided dice.
   *
   * @param numberOfDice the number of dice to include; must be strictly positive
   * @throws IllegalArgumentException if {@code numberOfDice} is not strictly positive
   */
  public Dice(int numberOfDice) {
    ExceptionHandling.requireStrictlyPositive(numberOfDice, "Number of dice");
    this.diceCollection = new ArrayList<>();
    initializeDice(numberOfDice);
  }

  /**
   * Initializes this {@code Dice} set by creating and adding the given number
   * of {@link Die} instances to the collection.
   *
   * @param numberOfDice the number of dice to create and add
   */
  public void initializeDice(int numberOfDice) {
    for (int i = 0; i < numberOfDice; i++) {
      diceCollection.add(new Die());
    }
  }

  /**
   * Rolls all dice in this set, generating new random values (1–6) on each die.
   *
   * @return the sum of the values rolled on all dice
   */
  public int roll() {
    int sum = 0;
    for (Die die : diceCollection) {
      sum += die.roll();
    }
    return sum;
  }

  /**
   * Retrieves the last rolled value of a specific die in the set.
   *
   * @param dieNumber the zero-based index of the die; must be between 0
   *                  and {@link #getNumberOfDice()} − 1
   * @return the last rolled value of the specified die
   * @throws IllegalArgumentException if {@code dieNumber} is out of range
   */
  public int getDieValue(int dieNumber) {
    ExceptionHandling.requireIndexRange(
        dieNumber, 0, diceCollection.size() - 1, "Die index");
    return diceCollection.get(dieNumber).getLastRolledValue();
  }

  /**
   * Calculates the total value of all dice based on their last roll.
   *
   * @return the sum of the last rolled values of all dice
   */
  public int getTotalDiceValue() {
    int total = 0;
    for (Die die : diceCollection) {
      total += die.getLastRolledValue();
    }
    return total;
  }

  /**
   * Returns the number of dice in this set.
   *
   * @return the size of the dice collection
   */
  public int getNumberOfDice() {
    return diceCollection.size();
  }

  /**
   * Returns an unmodifiable view of the dice in this set.
   * Modifications to the returned list are not allowed.
   *
   * @return an unmodifiable list of {@link Die} objects
   */
  public List<Die> getDiceCollection() {
    return Collections.unmodifiableList(diceCollection);
  }
}
