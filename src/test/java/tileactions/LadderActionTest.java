package tileactions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.LadderAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LadderActionTest {

  private Player player;
  private Tile destinationTile;

  private final String testDescription = "Climb the ladder!";
  private final int testDestinationTileId = 5;
  private final int defaultTileX = 0;
  private final int defaultTileY = 0;


  @BeforeEach
  void setUp() {
    player = new Player("TestPlayer", "TestPiece");
    destinationTile = new Tile(testDestinationTileId, defaultTileX, defaultTileY);
  }

  @Test
  @DisplayName("Constructor should set properties correctly with a valid destination tile")
  void constructor_withValidTile_setsPropertiesCorrectly() {
    LadderAction ladderAction = new LadderAction(destinationTile, testDescription);
    assertEquals(testDescription, ladderAction.getDescription());
    assertEquals(testDestinationTileId, ladderAction.getDestinationTileId());
    assertEquals(ActionType.LADDER, ladderAction.getActionType());
  }

  @Test
  @DisplayName("Constructor should handle null destination tile gracefully for ID")
  void constructor_withNullTile_setsDestinationIdToMinusOne() {
    LadderAction ladderAction = new LadderAction(null, testDescription);
    assertEquals(testDescription, ladderAction.getDescription());
    assertEquals(-1, ladderAction.getDestinationTileId());
    assertEquals(ActionType.LADDER, ladderAction.getActionType());
  }

  @Test
  @DisplayName("perform() should set player's current tile to the destination tile")
  void perform_setsPlayerOnDestinationTile() {
    LadderAction ladderAction = new LadderAction(destinationTile, testDescription);
    // Optionally, set player to a different initial tile first
    Tile initialTile = new Tile(1, defaultTileX, defaultTileY);
    player.setOnCurrentTile(initialTile);

    ladderAction.perform(player);

    assertSame(destinationTile, player.getCurrentTile(),
        "Player's current tile should be the ladder's destination tile.");
  }

  @Test
  @DisplayName("perform() with null destination should throw IllegalArgumentException from Player.setOnCurrentTile")
  void perform_withNullDestinationTile_throwsIllegalArgumentException() {
    LadderAction ladderAction = new LadderAction(null, testDescription);
    Tile initialTile = new Tile(1, defaultTileX, defaultTileY);
    player.setOnCurrentTile(initialTile);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      ladderAction.perform(player);
    }, "Performing action with null destination should throw IllegalArgumentException.");

    assertTrue(exception.getMessage().contains("New tile for player cannot be null"),
        "Exception message should indicate that new tile cannot be null.");

    assertSame(initialTile, player.getCurrentTile(),
        "Player's tile should not change if setting to null fails.");
  }


  @Test
  @DisplayName("getActionType() should return LADDER")
  void getActionType_returnsLadder() {
    LadderAction ladderAction = new LadderAction(destinationTile, testDescription);
    assertEquals(ActionType.LADDER, ladderAction.getActionType());
  }

  @Test
  @DisplayName("getDestinationTileId() should return correct ID when destination is not null")
  void getDestinationTileId_returnsCorrectId() {
    LadderAction ladderAction = new LadderAction(destinationTile, testDescription);
    assertEquals(testDestinationTileId, ladderAction.getDestinationTileId());
  }

  @Test
  @DisplayName("getDestinationTileId() should return -1 when destination is null")
  void getDestinationTileId_whenDestinationIsNull_returnsMinusOne() {
    LadderAction ladderAction = new LadderAction(null, testDescription);
    assertEquals(-1, ladderAction.getDestinationTileId());
  }

  @Test
  @DisplayName("getDescription() should return the correct description")
  void getDescription_returnsCorrectDescription() {
    LadderAction ladderAction = new LadderAction(destinationTile, testDescription);
    assertEquals(testDescription, ladderAction.getDescription());
  }
}
