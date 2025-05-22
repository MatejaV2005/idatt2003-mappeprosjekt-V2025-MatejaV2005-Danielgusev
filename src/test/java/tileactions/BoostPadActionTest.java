package tileactions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.BoostPadAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoostPadActionTest {

  private Player player;
  private final String validDescription = "Hit a Boost Pad! Zoom forward!";
  // Default description from the class is no longer used if constructor throws exception for null/blank

  @BeforeEach
  void setUp() {
    player = new Player("TestPlayer", "TestPiece");
  }

  @Test
  @DisplayName("Constructor should set valid description correctly")
  void constructor_withValidDescription_setsDescription() {
    BoostPadAction action = new BoostPadAction(validDescription);
    assertEquals(validDescription, action.getDescription());
    assertEquals(0, action.consumeLastDeterminedMovementEffectSteps(), "Initial effect should be 0 before perform");
  }

  @Test
  @DisplayName("Constructor should throw IllegalArgumentException if null description is provided")
  void constructor_withNullDescription_throwsIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new BoostPadAction(null);
    });
    assertEquals("description cannot be null or blank", exception.getMessage());
  }

  @Test
  @DisplayName("Constructor should throw IllegalArgumentException if empty string is provided for description")
  void constructor_withEmptyDescription_throwsIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new BoostPadAction("");
    });
    assertEquals("description cannot be null or blank", exception.getMessage());
  }

  @Test
  @DisplayName("Constructor should throw IllegalArgumentException if blank string is provided for description")
  void constructor_withBlankDescription_throwsIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new BoostPadAction("   ");
    });
    assertEquals("description cannot be null or blank", exception.getMessage());
  }

  @Test
  @DisplayName("perform() should set movement effect to 2")
  void perform_setsMovementEffectToTwo() {
    BoostPadAction action = new BoostPadAction(validDescription);
    action.perform(player);
    // We check the effect by consuming it
    assertEquals(2, action.consumeLastDeterminedMovementEffectSteps());
  }

  @Test
  @DisplayName("perform() with null player should throw IllegalArgumentException")
  void perform_withNullPlayer_throwsIllegalArgumentException() {
    BoostPadAction action = new BoostPadAction(validDescription);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      action.perform(null);
    });
    assertEquals("Player cannot be null for BoostPadAction cannot be null.", exception.getMessage());
  }

  @Test
  @DisplayName("consumeLastDeterminedMovementEffectSteps() should return effect and reset it to 0")
  void consumeLastDeterminedMovementEffectSteps_returnsEffectAndResets() {
    BoostPadAction action = new BoostPadAction(validDescription);
    action.perform(player); // Sets effect to 2

    assertEquals(2, action.consumeLastDeterminedMovementEffectSteps(), "First consumption should return 2.");
    assertEquals(0, action.consumeLastDeterminedMovementEffectSteps(), "Second consumption should return 0 as effect is reset.");
  }

  @Test
  @DisplayName("consumeLastDeterminedMovementEffectSteps() should return 0 if perform was not called")
  void consumeLastDeterminedMovementEffectSteps_withoutPerform_returnsZero() {
    BoostPadAction action = new BoostPadAction(validDescription);
    assertEquals(0, action.consumeLastDeterminedMovementEffectSteps());
  }

  @Test
  @DisplayName("getActionType() should return BOOST_PAD")
  void getActionType_returnsBoostPad() {
    BoostPadAction action = new BoostPadAction(validDescription);
    assertEquals(ActionType.BOOST_PAD, action.getActionType());
  }

  @Test
  @DisplayName("getDestinationTileId() should return -1")
  void getDestinationTileId_returnsMinusOne() {
    BoostPadAction action = new BoostPadAction(validDescription);
    assertEquals(-1, action.getDestinationTileId());
  }

  @Test
  @DisplayName("getDescription() should return the correct description")
  void getDescription_returnsCorrectDescription() {
    BoostPadAction actionWithValidDesc = new BoostPadAction(validDescription);
    assertEquals(validDescription, actionWithValidDesc.getDescription());
  }
}
