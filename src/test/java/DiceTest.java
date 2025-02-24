import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.Dice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DiceTest {


  private Dice diceWithTwoDice;
  private Dice diceWithThreeDice;

  @BeforeEach
  void setUp() {
    diceWithTwoDice = new Dice(2); // Initialize a Dice instance with 2 dice
    diceWithThreeDice = new Dice(3); // Initialize a Dice instance with 3 dice
  }

  @Test
  void testRollSumPositive() {
    int result = diceWithTwoDice.roll();
    // Check if the result is within the valid range for 2 dice (2 to 12)
    assertTrue(result >= 2 && result <= 12, "The roll sum should be between 2 and 12 for 2 dice.");
  }

  @Test
  void testIndividualDieValuePositive() {
    diceWithThreeDice.roll(); // Roll the dice
    int value = diceWithThreeDice.getDieValue(1); // Get the value of the second die
    // Check if the value is within the range of a single die (1 to 6)
    assertTrue(value >= 1 && value <= 6, "The die value should be between 1 and 6.");
  }





  //Negative tests--------------------------------------------------

  @Test
  void testDiceCreationWithNegativeDiceNumber() {
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> new Dice(-1),
        "Expected exception for negative number of dice"
    );
    assertEquals("numberOfDice must be positive", thrown.getMessage());
  }

  @Test
  void testDiceCreationWithZeroDiceNumber() {
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> new Dice(0),
        "Expected exception for zero number of dice"
    );
    assertEquals("numberOfDice must be positive", thrown.getMessage());
  }

  @Test
  void testGetDieValueWithIndexOutOfBounds() {
    Dice dice = new Dice(2);
    dice.roll();
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> dice.getDieValue(3),
        "Expected exception for dice index out of bounds"
    );

    assertEquals("number of dice must be between 0 and 2", thrown.getMessage());
  }

}
