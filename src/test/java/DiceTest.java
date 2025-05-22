import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Die;

import java.util.List;
import org.junit.jupiter.api.Test;

class DiceTest {


  @Test
  void constructor_WithPositiveNumber_CreatesCorrectNumberOfDice() {
    Dice dice = new Dice(2);

    assertNotNull(dice);
    assertEquals(2, dice.getNumberOfDice());
    assertNotNull(dice.getDiceCollection());
    assertEquals(2, dice.getDiceCollection().size());

    for (Die die : dice.getDiceCollection()) {
      assertNotNull(die);
    }
  }

  @Test
  void constructor_WithZeroDice_ThrowsException() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new Dice(0)
    );

    assertEquals("Number of dice must be strictly positive (greater than 0).",
        exception.getMessage());
  }


  @Test
  void constructor_WithNegativeDice_ThrowsException() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new Dice(-1)
    );

    assertEquals("Number of dice must be strictly positive (greater than 0).",
        exception.getMessage());
  }

  @Test
  void roll_WithSingleDie_ReturnsSumWithinValidRange() {
    Dice dice = new Dice(1);

    for (int i = 0; i < 100; i++) {
      int sum = dice.roll();
      assertTrue(sum >= 1 && sum <= 6,
          "Roll sum for 1 die should be between 1 and 6, was: " + sum);
    }
  }


  @Test
  void roll_WithMultipleDice_ReturnsSumWithinValidRange() {
    int numberOfDice = 3;
    Dice dice = new Dice(numberOfDice);

    for (int i = 0; i < 100; i++) {
      int sum = dice.roll();
      int minPossible = numberOfDice;
      int maxPossible = numberOfDice * 6;

      assertTrue(sum >= minPossible && sum <= maxPossible,
          "Roll sum for " + numberOfDice + " dice should be between "
              + minPossible + " and " + maxPossible + ", was: " + sum);
    }
  }


  @Test
  void roll_UpdatesLastRolledValuesOfIndividualDice() {
    Dice dice = new Dice(2);
    dice.roll();

    int die1Value = dice.getDieValue(0);
    int die2Value = dice.getDieValue(1);

    assertTrue(die1Value >= 1 && die1Value <= 6);
    assertTrue(die2Value >= 1 && die2Value <= 6);
    assertEquals(die1Value + die2Value, dice.getTotalDiceValue());
  }

  @Test
  void getDieValue_AfterRoll_ReturnsCorrectValue() {
    Dice dice = new Dice(1);
    dice.roll();

    int value = dice.getDieValue(0);

    assertTrue(value >= 1 && value <= 6);
    assertEquals(value, dice.getTotalDiceValue());
  }


  @Test
  void getDieValue_WithNegativeIndex_ThrowsException() {
    Dice dice = new Dice(2);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> dice.getDieValue(-1)
    );

    assertEquals("Die index (-1) is out of bounds. Must be between 0 and 1 (inclusive).",
        exception.getMessage());
  }


  @Test
  void getDieValue_WithIndexEqualToSize_ThrowsException() {
    Dice dice = new Dice(2);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> dice.getDieValue(2)
    );

    assertEquals("Die index (2) is out of bounds. Must be between 0 and 1 (inclusive).",
        exception.getMessage());
  }


  @Test
  void getTotalDiceValue_ReturnsSumOfIndividualDice() {
    Dice dice = new Dice(3);
    dice.roll();

    int die1 = dice.getDieValue(0);
    int die2 = dice.getDieValue(1);
    int die3 = dice.getDieValue(2);

    assertEquals(die1 + die2 + die3, dice.getTotalDiceValue());
  }

  @Test
  void getTotalDiceValue_BeforeFirstRoll_ReturnsZero() {
    Dice dice = new Dice(2);

    assertEquals(0, dice.getTotalDiceValue(),
        "Total should be 0 before any roll if Die initializes lastRolledValue to 0.");
  }


  @Test
  void getTotalDiceValue_AfterMultipleRolls_ReflectsLastRoll() {
    Dice dice = new Dice(2);

    dice.roll();
    int sumAfterFirstRoll = dice.getTotalDiceValue();
    assertTrue(sumAfterFirstRoll >= 2 && sumAfterFirstRoll <= 12);

    dice.roll();
    int sumAfterSecondRoll = dice.getTotalDiceValue();
    assertTrue(sumAfterSecondRoll >= 2 && sumAfterSecondRoll <= 12);

    int die1 = dice.getDieValue(0);
    int die2 = dice.getDieValue(1);
    assertEquals(die1 + die2, sumAfterSecondRoll);
  }


  @Test
  void getDice_ReturnsUnmodifiableList() {
    Dice dice = new Dice(2);
    List<Die> dieList = dice.getDiceCollection();

    assertNotNull(dieList);
    assertEquals(2, dieList.size());
    assertThrows(UnsupportedOperationException.class, () -> dieList.remove(0));
  }
}