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
  void testInvalidDieNumberNegative() {
    diceWithTwoDice.roll(); // Roll the dice to ensure dice are initialized
    // Test invalid index (-1)
    Exception exception1 = assertThrows(IndexOutOfBoundsException.class, () -> {
      diceWithTwoDice.getDieValue(-1);
    });

    assertEquals("Invalid die number: -1. Must be between 0 and 1.", exception1.getMessage());

    // Test invalid index (greater than size)
    Exception exception2 = assertThrows(IndexOutOfBoundsException.class, () -> {
      diceWithTwoDice.getDieValue(2);
    });
    assertEquals("Invalid die number: 2. Must be between 0 and 1.", exception2.getMessage());
  }

}
