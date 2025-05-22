package tileactions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.SnakeAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SnakeActionTest {

  private Player player;
  private Tile destinationTile;

  private final String testDescription = "Slid down the snake!";
  private final int testDestinationTileId = 2; // Snakes usually go to a lower ID
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
    SnakeAction snakeAction = new SnakeAction(destinationTile, testDescription);
    assertEquals(testDescription, snakeAction.getDescription());
    assertEquals(testDestinationTileId, snakeAction.getDestinationTileId());
    assertEquals(ActionType.SNAKE, snakeAction.getActionType());
  }

  @Test
  @DisplayName("Constructor should handle null destination tile gracefully for ID")
  void constructor_withNullTile_setsDestinationIdToMinusOne() {
    SnakeAction snakeAction = new SnakeAction(null, testDescription);
    assertEquals(testDescription, snakeAction.getDescription());
    assertEquals(-1, snakeAction.getDestinationTileId());
    assertEquals(ActionType.SNAKE, snakeAction.getActionType());
  }

  @Test
  @DisplayName("perform() should set player's current tile to the destination tile")
  void perform_setsPlayerOnDestinationTile() {
    SnakeAction snakeAction = new SnakeAction(destinationTile, testDescription);
    Tile initialTile = new Tile(10, defaultTileX, defaultTileY);
    player.setOnCurrentTile(initialTile);

    snakeAction.perform(player);

    assertSame(destinationTile, player.getCurrentTile(),
        "Player's current tile should be the snake's destination tile.");
  }

  @Test
  @DisplayName("perform() with null destination should throw IllegalArgumentException from Player.setOnCurrentTile")
  void perform_withNullDestinationTile_throwsIllegalArgumentException() {
    SnakeAction snakeAction = new SnakeAction(null, testDescription);
    Tile initialTile = new Tile(10, defaultTileX, defaultTileY);
    player.setOnCurrentTile(initialTile);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      snakeAction.perform(player);
    }, "Performing action with null destination should throw IllegalArgumentException.");

    assertTrue(exception.getMessage().contains("New tile for player cannot be null"),
        "Exception message should indicate that new tile cannot be null.");

    assertSame(initialTile, player.getCurrentTile(),
        "Player's tile should not change if setting to null fails.");
  }

  @Test
  @DisplayName("getActionType() should return SNAKE")
  void getActionType_returnsSnake() {
    SnakeAction snakeAction = new SnakeAction(destinationTile, testDescription);
    assertEquals(ActionType.SNAKE, snakeAction.getActionType());
  }

  @Test
  @DisplayName("getDestinationTileId() should return correct ID when destination is not null")
  void getDestinationTileId_returnsCorrectId() {
    SnakeAction snakeAction = new SnakeAction(destinationTile, testDescription);
    assertEquals(testDestinationTileId, snakeAction.getDestinationTileId());
  }

  @Test
  @DisplayName("getDestinationTileId() should return -1 when destination is null")
  void getDestinationTileId_whenDestinationIsNull_returnsMinusOne() {
    SnakeAction snakeAction = new SnakeAction(null, testDescription);
    assertEquals(-1, snakeAction.getDestinationTileId());
  }

  @Test
  @DisplayName("getDescription() should return the correct description")
  void getDescription_returnsCorrectDescription() {
    SnakeAction snakeAction = new SnakeAction(destinationTile, testDescription);
    assertEquals(testDescription, snakeAction.getDescription());
  }
}
