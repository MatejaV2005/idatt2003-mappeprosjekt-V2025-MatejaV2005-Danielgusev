package tileactions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoOperationActionTest {

  private Player player;
  private Tile initialTile;

  @BeforeEach
  void setUp() {
    player = new Player("TestPlayer", "TestPiece");
    initialTile = new Tile(1, 0, 0);
    player.setOnCurrentTile(initialTile);
  }

  @Test
  @DisplayName("perform() should not change the player's state")
  void perform_doesNotChangePlayerState() {
    NoOperationAction noOpAction = NoOperationAction.INSTANCE;

    Tile tileBeforeAction = player.getCurrentTile();
    String nameBeforeAction = player.getName();

    noOpAction.perform(player);

    assertSame(tileBeforeAction, player.getCurrentTile(), "Player's tile should not change.");
    assertEquals(nameBeforeAction, player.getName(), "Player's name should not change.");
  }

  @Test
  @DisplayName("getActionType() should return NO_OP")
  void getActionType_returnsNoOp() {
    NoOperationAction noOpAction = NoOperationAction.INSTANCE;
    assertEquals(ActionType.NO_OP, noOpAction.getActionType());
  }

  @Test
  @DisplayName("getDestinationTileId() should return -1")
  void getDestinationTileId_returnsMinusOne() {
    NoOperationAction noOpAction = NoOperationAction.INSTANCE;
    assertEquals(-1, noOpAction.getDestinationTileId());
  }

  @Test
  @DisplayName("getDescription() should return the correct description")
  void getDescription_returnsCorrectDescription() {
    NoOperationAction noOpAction = NoOperationAction.INSTANCE;
    assertEquals("No operation performed", noOpAction.getDescription());
  }

  @Test
  @DisplayName("INSTANCE should provide a singleton instance")
  void instance_isSingleton() {
    NoOperationAction instance1 = NoOperationAction.INSTANCE;
    NoOperationAction instance2 = NoOperationAction.INSTANCE;
    assertSame(instance1, instance2, "INSTANCE should always return the same object (singleton).");
  }
}
