import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private Player player;
  private Tile mockInitialTile;
  private Tile mockNextTile;
  private Tile mockFinalTile;

  private final String validName = "TestPlayer";
  private final String validPieceType = "TestPiece";

  @BeforeEach
  void setUp() {
    player = new Player(validName, validPieceType);
    mockInitialTile = mock(Tile.class, "InitialTile");
    mockNextTile = mock(Tile.class, "NextTile");
    mockFinalTile = mock(Tile.class, "FinalTile");

    player.setOnCurrentTile(mockInitialTile);
  }

  @Nested
  @DisplayName("Constructor and Initial State")
  class ConstructorTests {

    @Test
    void constructorSetsNameAndPieceTypeAndDefaults() {
      assertEquals(validName, player.getName());
      assertEquals(validPieceType, player.getPieceType());
      assertNull(new Player("P2", "Hat").getCurrentTile(),
          "New player should not have a currentTile set by constructor.");
      assertFalse(player.shouldSkipTurn(),
          "New player should not be set to skip turn by default.");
    }

    @Test
    void constructorTrimsNameAndPieceType() {
      Player p = new Player("  Padded Name  ", "  Padded Piece  ");
      assertEquals("Padded Name", p.getName());
      assertEquals("Padded Piece", p.getPieceType());
    }

    @Test
    void constructorThrowsForNullName() {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> new Player(null, validPieceType));
      assertEquals("Player name cannot be null or blank", exception.getMessage());
    }

    @Test
    void constructorThrowsForBlankName() {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> new Player("   ", validPieceType));
      assertEquals("Player name cannot be null or blank", exception.getMessage());
    }

    @Test
    void constructorThrowsForNullPieceType() {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> new Player(validName, null));
      assertEquals("Player piece type cannot be null or blank", exception.getMessage());
    }

    @Test
    void constructorThrowsForBlankPieceType() {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> new Player(validName, "   "));
      assertEquals("Player piece type cannot be null or blank", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("Tile Management")
  class TileManagementTests {

    @Test
    void setOnCurrentTileUpdatesTileCorrectly() {
      assertEquals(mockInitialTile, player.getCurrentTile());
      player.setOnCurrentTile(mockNextTile);
      assertEquals(mockNextTile, player.getCurrentTile());
    }

    @Test
    void setOnCurrentTileThrowsForNullTile() {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> player.setOnCurrentTile(null));
      assertEquals("New tile for player cannot be null.", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("Skip Turn Functionality")
  class SkipTurnTests {

    @Test
    void setSkipTurnEnablesSkippingNextTurn() {
      player.setSkipTurn(true);
      assertTrue(player.shouldSkipTurn(), "Should skip turn after being set to true.");
    }

    @Test
    void shouldSkipTurnResetsFlagAfterReturningTrue() {
      player.setSkipTurn(true);
      player.shouldSkipTurn(); // First call consumes the flag
      assertFalse(player.shouldSkipTurn(), "Should not skip turn on subsequent call.");
    }

    @Test
    void shouldSkipTurnReturnsFalseIfNeverSetToSkip() {
      assertFalse(player.shouldSkipTurn());
    }

    @Test
    void setSkipTurnToFalseClearsSkipFlag() {
      player.setSkipTurn(true); // Set to skip
      player.setSkipTurn(false); // Then clear
      assertFalse(player.shouldSkipTurn());
    }
  }

  @Nested
  @DisplayName("Basic Movement Logic (basicMove)")
  class BasicMoveTests {

    @BeforeEach
    void setUpBasicMove() {
      // Player er allerede på mockInitialTile fra hoved-setUp
    }

    @Test
    void basicMoveReturnsCorrectDestinationForOneStep() {
      when(mockInitialTile.getNextTile()).thenReturn(mockNextTile);

      Tile resultTile = player.basicMove(1);
      assertEquals(mockNextTile, resultTile, "Should move to the next tile.");
    }

    @Test
    void basicMoveReturnsCorrectDestinationForMultipleSteps() {
      when(mockInitialTile.getNextTile()).thenReturn(mockNextTile);
      when(mockNextTile.getNextTile()).thenReturn(mockFinalTile);
      when(mockFinalTile.getNextTile()).thenReturn(null); // End of board path

      Tile resultTile = player.basicMove(2);
      assertEquals(mockFinalTile, resultTile, "Should move two tiles ahead.");
    }

    @Test
    void basicMoveStopsAtLastTileIfStepsExceedBoardEnd() {
      when(mockInitialTile.getNextTile()).thenReturn(mockNextTile);
      when(mockNextTile.getNextTile()).thenReturn(null); // mockNextTile is the last in this path

      Tile resultTile = player.basicMove(5); // Try to move 5 steps
      assertEquals(mockNextTile, resultTile, "Should stop at the last available tile.");
    }

    @Test
    void basicMoveThrowsIfStepsAreZero() {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> player.basicMove(0));
      assertEquals("Number of steps for basicMove must be strictly positive (greater than 0).",
          exception.getMessage());
    }

    @Test
    void basicMoveThrowsIfStepsAreNegative() {
      var exception = assertThrows(IllegalArgumentException.class,
          () -> player.basicMove(-2));
      assertEquals("Number of steps for basicMove must be strictly positive (greater than 0).",
          exception.getMessage());
    }

    @Test
    void basicMoveThrowsIfPlayerIsNotOnATile() {
      Player playerNotOnBoard = new Player("Off Roader", "Bike");

      var exception = assertThrows(IllegalStateException.class,
          () -> playerNotOnBoard.basicMove(1));
      assertEquals("Player must be on a tile to perform basicMove.", exception.getMessage());
    }

    @Test
    void basicMoveReturnsCurrentTileIfNoNextTileAndMovingOneStep() {
      when(mockInitialTile.getNextTile()).thenReturn(null); // Current tile is the last tile

      Tile resultTile = player.basicMove(1);
      assertEquals(mockInitialTile, resultTile, "Should stay on current tile if it's the last one.");
    }
  }
}
