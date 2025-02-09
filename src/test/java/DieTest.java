
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.Die;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DieTest {
  Die die;

  @BeforeEach
  void setUp() {
    die = new Die();
  }

  @Test
  void testRollDie() {
   for (int i = 0; i < 100; i++) {
     int value = die.roll();
     assertTrue(value <= 6 && value >= 1, "Dice value should be between 1 and 6, but was: " + value);
   }
  }

  @Test
  void testRollDieDifferentValues() {
    boolean changed = false;
    int initialValue = die.getLastRolledValue();

    for (int i = 0; i < 100; i++) {
      die.roll();

      if (die.getLastRolledValue() != initialValue) {
        changed = true;
      }
    }

    assertTrue(changed, "After multiple rolls, the value should vary");
  }
}
