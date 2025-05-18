import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Die;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class DiceTest {

  @Nested
  @DisplayName("Constructor and Initialization")
  class ConstructorTests {

    @Test
    void constructorSuccessfullyCreatesDiceWithPositiveNumber() {
      Dice dice = new Dice(2);
      assertNotNull(dice);
      assertEquals(2, dice.getNumberOfDice());
      assertNotNull(dice.getDice());
      assertEquals(2, dice.getDice().size());
      for (Die die : dice.getDice()) {
        assertNotNull(die);
      }
    }

    @Test
    void constructorThrowsExceptionForZeroDice() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> new Dice(0));
      assertEquals("Number of dice must be strictly positive (greater than 0).",
          exception.getMessage());
    }

    @Test
    void constructorThrowsExceptionForNegativeDice() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> new Dice(-1));
      assertEquals("Number of dice must be strictly positive (greater than 0).",
          exception.getMessage());
    }
  }

  @Nested
  @DisplayName("Rolling Dice")
  class RollingTests {

    @Test
    void rollReturnsSumWithinValidRangeForSingleDie() {
      Dice dice = new Dice(1);
      for (int i = 0; i < 100; i++) { // Roll multiple times
        int sum = dice.roll();
        assertTrue(sum >= 1 && sum <= 6,
            "Roll sum for 1 die should be between 1 and 6, was: " + sum);
      }
    }

    @Test
    void rollReturnsSumWithinValidRangeForMultipleDice() {
      int numberOfDice = 3;
      Dice dice = new Dice(numberOfDice);
      for (int i = 0; i < 100; i++) { // Roll multiple times
        int sum = dice.roll();
        assertTrue(sum >= numberOfDice && sum <= numberOfDice * 6,
            "Roll sum for " + numberOfDice + " dice should be between "
                + numberOfDice + " and " + (numberOfDice * 6) + ", was: " + sum);
      }
    }

    @Test
    void rollUpdatesLastRolledValuesOfIndividualDice() {
      Dice dice = new Dice(2);
      dice.roll();
      int die1Value = dice.getDieValue(0);
      int die2Value = dice.getDieValue(1);
      assertTrue(die1Value >= 1 && die1Value <= 6);
      assertTrue(die2Value >= 1 && die2Value <= 6);
      assertEquals(die1Value + die2Value, dice.getTotalDiceValue());
    }
  }

  @Nested
  @DisplayName("Getting Die Values")
  class DieValueTests {

    @Test
    void getDieValueReturnsCorrectValueAfterRoll() {
      Dice dice = new Dice(1);
      dice.roll(); // Sets lastRolledValue in the Die
      int value = dice.getDieValue(0);
      assertTrue(value >= 1 && value <= 6);
      assertEquals(value, dice.getTotalDiceValue());
    }

    @Test
    void getDieValueThrowsExceptionForNegativeIndex() {
      Dice dice = new Dice(2);
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> dice.getDieValue(-1));
      assertEquals("Die index (-1) is out of bounds. Must be between 0 and 1 (inclusive).",
          exception.getMessage());
    }

    @Test
    void getDieValueThrowsExceptionForIndexEqualToSize() {
      Dice dice = new Dice(2);
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> dice.getDieValue(2));
      assertEquals("Die index (2) is out of bounds. Must be between 0 and 1 (inclusive).",
          exception.getMessage());
    }

    @Test
    void getDieValueThrowsExceptionForIndexGreaterThanSize() {
      Dice dice = new Dice(1);
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> dice.getDieValue(1));
      assertEquals("Die index (1) is out of bounds. Must be between 0 and 0 (inclusive).",
          exception.getMessage());
    }

    @Test
    void getTotalDiceValueReturnsSumOfIndividualLastRolledValues() {
      Dice dice = new Dice(3);
      int expectedSum = 0;
      for (Die die : dice.getDice()) {
        expectedSum += die.roll();
      }
      dice.roll();
      int die1 = dice.getDieValue(0);
      int die2 = dice.getDieValue(1);
      int die3 = dice.getDieValue(2);
      assertEquals(die1 + die2 + die3, dice.getTotalDiceValue());
    }

    @Test
    void getTotalDiceValueReturnsZeroBeforeFirstRollIfDiceAreNotPreRolled() {
      Dice dice = new Dice(2);
      // Assuming Die constructor does not roll, lastRolledValue is 0
      assertEquals(0, dice.getTotalDiceValue(),
          "Total should be 0 before any roll if Die initializes lastRolledValue to 0.");
    }
    @Test
    void getTotalDiceValueAfterMultipleRollsReflectsLastRoll() {
      Dice dice = new Dice(2);
      dice.roll(); // First roll
      int sumAfterFirstRoll = dice.getTotalDiceValue();
      assertTrue(sumAfterFirstRoll >= 2 && sumAfterFirstRoll <= 12);

      dice.roll(); // Second roll
      int sumAfterSecondRoll = dice.getTotalDiceValue();
      assertTrue(sumAfterSecondRoll >= 2 && sumAfterSecondRoll <= 12);

      int die1 = dice.getDieValue(0);
      int die2 = dice.getDieValue(1);
      assertEquals(die1 + die2, sumAfterSecondRoll);
    }
  }

  @Nested
  @DisplayName("Utility Methods")
  class UtilityMethodTests {

    @Test
    void getDiceReturnsUnmodifiableList() {
      Dice dice = new Dice(2);
      List<Die> dieList = dice.getDice();
      assertNotNull(dieList);
      assertEquals(2, dieList.size());
      assertThrows(UnsupportedOperationException.class, () -> dieList.add(new Die()));
      assertThrows(UnsupportedOperationException.class, () -> dieList.remove(0));
    }
  }

  @Nested
  @DisplayName("ToString Method")
  class ToStringTests {
    @Test
    void toStringContainsRelevantInformation() {
      Dice diceOne = new Dice(1);
      diceOne.roll(); // Roll to get a value
      String strOne = diceOne.toString();
      assertTrue(strOne.startsWith("Dice{count=1, values=["));
      assertTrue(strOne.endsWith("]}"));
      assertTrue(strOne.contains(String.valueOf(diceOne.getDieValue(0))));

      Dice diceTwo = new Dice(2);
      String strTwoInitial = diceTwo.toString();
      assertEquals("Dice{count=2, values=[0, 0]}", strTwoInitial);

      diceTwo.roll();
      String strTwoRolled = diceTwo.toString();
      assertTrue(strTwoRolled.startsWith("Dice{count=2, values=["));
      assertTrue(strTwoRolled.contains(String.valueOf(diceTwo.getDieValue(0))));
      assertTrue(strTwoRolled.contains(String.valueOf(diceTwo.getDieValue(1))));
      assertTrue(strTwoRolled.contains(", "));
    }
  }
}
