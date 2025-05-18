import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TileTest {

  private Tile tile;
  private Player mockPlayer;
  private TileAction mockAction;

  private static class TestLadderAction implements TileAction {
    @Override
    public void perform(Player player) {
    }

    @Override
    public ActionType getActionType() {
      return ActionType.LADDER;
    }

    @Override
    public int getDestinationTileId() { return 0; }

    @Override
    public String getDescription() {
      return "Test Ladder Action";
    }
  }

  @BeforeEach
  void setUp() {
    // Default tile for most tests
    tile = new Tile(1, 0, 0);
    mockPlayer = mock(Player.class);
    mockAction = mock(TileAction.class);

  }

  @Nested
  @DisplayName("Constructor and Initialization")
  class ConstructorAndInitializationTests {

    @Test
    void tileIsCorrectlyInitializedWithValidParameters() {
      assertNotNull(tile);
      assertEquals(1, tile.getTileId());
      assertEquals(0, tile.getRow());
      assertEquals(0, tile.getColumn());
      assertInstanceOf(NoOperationAction.class, tile.getLandAction());
      assertEquals(ActionType.NO_OP, tile.getActionType());
      assertEquals(-1, tile.getNextTileId());
      assertNotNull(tile.getPlayers());
      assertTrue(tile.getPlayers().isEmpty());
    }

    @Test
    void constructorAllowsZeroForRowAndColumn() {
      Tile zeroPositionTile = new Tile(2, 0, 0);
      assertEquals(0, zeroPositionTile.getRow());
      assertEquals(0, zeroPositionTile.getColumn());
    }

    @Test
    void constructorThrowsExceptionForZeroTileId() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> new Tile(0, 0, 0));
      assertEquals("tileId must be strictly positive (greater than 0).", exception.getMessage());
    }

    @Test
    void constructorThrowsExceptionForNegativeTileId() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> new Tile(-1, 0, 0));
      assertEquals("tileId must be strictly positive (greater than 0).", exception.getMessage());
    }

    @Test
    void constructorThrowsExceptionForNegativeRow() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> new Tile(1, -1, 0));
      assertEquals("row must be non-negative (0 or greater).", exception.getMessage());
    }

    @Test
    void constructorThrowsExceptionForNegativeColumn() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> new Tile(1, 0, -1));
      assertEquals("column must be non-negative (0 or greater).", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("Next Tile Logic")
  class NextTileLogicTests {

    @Test
    void setNextTileUpdatesNextTileAndItsId() {
      Tile next = new Tile(2, 0, 1);
      tile.setNextTile(next);
      assertEquals(next, tile.getNextTile());
      assertEquals(2, tile.getNextTileId());
    }

    @Test
    void setNextTileThrowsExceptionForNullArgument() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> tile.setNextTile(null));
      assertEquals("nextTile cannot be null.", exception.getMessage());
    }

    @Test
    void setNextTileIdUpdatesIdCorrectly() {
      tile.setNextTileId(5);
      assertEquals(5, tile.getNextTileId());
      tile.setNextTileId(-1);
      assertEquals(-1, tile.getNextTileId());
    }
  }

  @Nested
  @DisplayName("Land Action Logic")
  class LandActionLogicTests {

    @Test
    void setLandActionUpdatesAction() {
      tile.setLandAction(mockAction);
      assertEquals(mockAction, tile.getLandAction());
    }

    @Test
    void setLandActionDefaultsToNoOpActionForNullArgument() {
      tile.setLandAction(null);
      assertInstanceOf(NoOperationAction.class, tile.getLandAction());
      assertEquals(ActionType.NO_OP, tile.getActionType());
    }

    @Test
    void getActionTypeReturnsCorrectTypeFromSetAction() {
      when(mockAction.getActionType()).thenReturn(ActionType.SNAKE);
      tile.setLandAction(mockAction);
      assertEquals(ActionType.SNAKE, tile.getActionType());
    }

    @Test
    void isActionTileReturnsFalseForDefaultNoOpAction() {
      assertFalse(tile.isActionTile());
    }

    @Test
    void isActionTileReturnsTrueWhenSpecificActionIsSet() {
      tile.setLandAction(new TestLadderAction());
      assertTrue(tile.isActionTile());
    }
  }

  @Nested
  @DisplayName("Player Management on Tile")
  class PlayerManagementOnTileTests {

    @Test
    void landPlayerAddsPlayerAndPerformsAction() {
      tile.setLandAction(mockAction);
      tile.landPlayer(mockPlayer);

      assertTrue(tile.getPlayers().contains(mockPlayer));
      assertEquals(1, tile.getPlayers().size());
      verify(mockAction, times(1)).perform(mockPlayer);
    }


    @Test
    void landPlayerOnAlreadyOccupiedTilePerformsActionAgainWithoutAddingDuplicate() {
      tile.setLandAction(mockAction);
      tile.landPlayer(mockPlayer);
      tile.landPlayer(mockPlayer);

      assertEquals(1, tile.getPlayers().size());
      verify(mockAction, times(2)).perform(mockPlayer);
    }

    @Test
    void leavePlayerRemovesPlayerFromTile() {
      tile.landPlayer(mockPlayer);
      assertTrue(tile.getPlayers().contains(mockPlayer));

      tile.leavePlayer(mockPlayer);
      assertFalse(tile.getPlayers().contains(mockPlayer));
      assertTrue(tile.getPlayers().isEmpty());
    }


    @Test
    void leavePlayerDoesNothingIfPlayerIsNotOnTile() {
      Player anotherPlayer = mock(Player.class);
      tile.landPlayer(mockPlayer);

      tile.leavePlayer(anotherPlayer);

      assertEquals(1, tile.getPlayers().size());
      assertTrue(tile.getPlayers().contains(mockPlayer));
    }

    @Test
    void getPlayersReturnsUnmodifiableListToPreventExternalModification() {
      tile.landPlayer(mockPlayer);
      List<Player> playersOnTile = tile.getPlayers();

      assertThrows(UnsupportedOperationException.class, () -> playersOnTile.add(mock(Player.class)));
      assertThrows(UnsupportedOperationException.class, () -> playersOnTile.remove(0));
      assertThrows(UnsupportedOperationException.class, playersOnTile::clear);
    }
  }
}
