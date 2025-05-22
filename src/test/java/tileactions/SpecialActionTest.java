package tileactions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.SpecialAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SpecialActionTest {

  private Player player;
  private final String testDescription = "A special event occurred!";
  private final ActionType testSpecificType = ActionType.SKIP_TURN; // Example specific type

  @BeforeEach
  void setUp() {
    player = new Player("TestPlayer", "TestPiece");
  }

  @Test
  @DisplayName("Constructor should set properties correctly")
  void constructor_setsPropertiesCorrectly() {
    Consumer<Player> dummyAction = p -> {}; // A non-null, do-nothing action
    SpecialAction specialAction = new SpecialAction(testDescription, dummyAction, testSpecificType);

    assertEquals(testDescription, specialAction.getDescription());
    assertEquals(testSpecificType, specialAction.getActionType());
    assertEquals(-1, specialAction.getDestinationTileId(), "SpecialAction should not have a destination tile ID.");
  }

  @Test
  @DisplayName("perform() should execute the provided consumer action")
  void perform_executesConsumerAction() {
    AtomicBoolean actionWasPerformed = new AtomicBoolean(false);
    Consumer<Player> testConsumer = p -> {
      assertSame(player, p, "Player passed to consumer should be the correct instance.");
      actionWasPerformed.set(true);
    };

    SpecialAction specialAction = new SpecialAction(testDescription, testConsumer, testSpecificType);
    specialAction.perform(player);

    assertTrue(actionWasPerformed.get(), "The consumer action should have been performed.");
  }

  @Test
  @DisplayName("perform() with null consumer action should throw NullPointerException when invoked")
  void perform_withNullConsumer_throwsNullPointerException() {
    SpecialAction specialAction = new SpecialAction(testDescription, null, testSpecificType);

    assertThrows(NullPointerException.class, () -> {
      specialAction.perform(player);
    }, "Performing action with a null consumer should throw NullPointerException.");
  }

  @Test
  @DisplayName("getActionType() should return the specific type")
  void getActionType_returnsSpecificType() {
    Consumer<Player> dummyAction = p -> {};
    SpecialAction specialAction = new SpecialAction(testDescription, dummyAction, testSpecificType);
    assertEquals(testSpecificType, specialAction.getActionType());
  }

  @Test
  @DisplayName("getDestinationTileId() should always return -1")
  void getDestinationTileId_returnsMinusOne() {
    Consumer<Player> dummyAction = p -> {};
    SpecialAction specialAction = new SpecialAction(testDescription, dummyAction, testSpecificType);
    assertEquals(-1, specialAction.getDestinationTileId());
  }

  @Test
  @DisplayName("getDescription() should return the correct description")
  void getDescription_returnsCorrectDescription() {
    Consumer<Player> dummyAction = p -> {};
    SpecialAction specialAction = new SpecialAction(testDescription, dummyAction, testSpecificType);
    assertEquals(testDescription, specialAction.getDescription());
  }
}
