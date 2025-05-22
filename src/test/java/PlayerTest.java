import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private Player player;
  private Tile initialTile;
  private Tile nextTile;
  private Tile finalTile;

  private final String validName = "TestPlayer";
  private final String validPieceType = "TestPiece";


  @BeforeEach
  void setUp() {
    player = new Player(validName, validPieceType);

    initialTile = new Tile(1, 0, 0);
    nextTile = new Tile(2, 0, 1);
    finalTile = new Tile(3, 0, 2);

    initialTile.setNextTile(nextTile);
    nextTile.setNextTile(finalTile);

    player.setOnCurrentTile(initialTile);
  }

  @Nested
  @DisplayName("Constructor and Initial State")
  class ConstructorTests {

    @Test
    void constructorSetsNameAndPieceTypeAndDefaults() {
      assertEquals(validName, player.getName());
      assertEquals(validPieceType, player.getPieceType());

      Player newPlayer = new Player("P2", "Hat");
      assertNull(newPlayer.getCurrentTile(), "New player should not have a currentTile set by constructor.");
      assertFalse(newPlayer.shouldSkipTurn(), "New player should not be set to skip turn by default.");
      assertEquals(0, newPlayer.getLapsCompleted(), "New player should have 0 laps completed.");
    }

    @Test
    void constructorTrimsNameAndPieceType() {
      Player p = new Player("  Padded Name  ", "  Padded Piece  ");
      assertEquals("Padded Name", p.getName());
      assertEquals("Padded Piece", p.getPieceType());
    }

    @Test
    void constructorThrowsForInvalidParameters() {
      assertThrows(IllegalArgumentException.class, () -> new Player(null, validPieceType),
          "Should throw when name is null");

      assertThrows(IllegalArgumentException.class, () -> new Player("   ", validPieceType),
          "Should throw when name is blank");

      assertThrows(IllegalArgumentException.class, () -> new Player(validName, null),
          "Should throw when pieceType is null");

      assertThrows(IllegalArgumentException.class, () -> new Player(validName, "   "),
          "Should throw when pieceType is blank");
    }
  }

  @Nested
  @DisplayName("Tile Management")
  class TileManagementTests {

    @Test
    void tileManagement() {
      assertEquals(initialTile, player.getCurrentTile(), "Player should be on initial tile");

      player.setOnCurrentTile(nextTile);
      assertEquals(nextTile, player.getCurrentTile(), "Player should be on next tile after change");

      assertThrows(IllegalArgumentException.class, () -> player.setOnCurrentTile(null),
          "Setting null tile should throw exception");
    }
  }

  @Nested
  @DisplayName("Lap Management")
  class LapManagementTests {

    @Test
    void lapCounterOperations() {
      assertEquals(0, player.getLapsCompleted(), "New player should have 0 laps completed");

      player.incrementLapsCompleted();
      assertEquals(1, player.getLapsCompleted(), "Lap counter should be incremented");

      player.incrementLapsCompleted();
      assertEquals(2, player.getLapsCompleted(), "Lap counter should be incremented again");

      player.resetLapsCompleted();
      assertEquals(0, player.getLapsCompleted(), "Lap counter should be reset to 0");
    }
  }

  @Nested
  @DisplayName("Skip Turn Functionality")
  class SkipTurnTests {

    @Test
    void skipTurnBehavior() {
      assertFalse(player.shouldSkipTurn(), "New player should not skip turn");

      player.setSkipTurn(true);
      assertTrue(player.shouldSkipTurn(), "Player should skip turn after setting flag");

      assertFalse(player.shouldSkipTurn(), "Flag should reset after being consumed");

      player.setSkipTurn(true);
      player.setSkipTurn(false);
      assertFalse(player.shouldSkipTurn(), "Setting to false should clear the flag");
    }
  }

  @Nested
  @DisplayName("Basic Movement Logic")
  class BasicMoveTests {

    @Test
    void basicMovementBehavior() {
      Tile result = player.basicMove(1);
      assertEquals(nextTile, result, "Should move one tile forward");

      result = player.basicMove(2);
      assertEquals(finalTile, result, "Should move two tiles forward");

      finalTile.setNextTile(null);
      result = player.basicMove(3);
      assertEquals(finalTile, result, "Should stop at final tile when moving beyond board end");
    }

    @Test
    void basicMoveEdgeCases() {
      assertThrows(IllegalArgumentException.class, () -> player.basicMove(0),
          "Should throw for zero steps");
      assertThrows(IllegalArgumentException.class, () -> player.basicMove(-1),
          "Should throw for negative steps");

      Player offBoardPlayer = new Player("Off Board", "Token");
      assertThrows(IllegalStateException.class, () -> offBoardPlayer.basicMove(1),
          "Should throw when player is not on a tile");

      initialTile.setNextTile(null);
      assertEquals(initialTile, player.basicMove(1),
          "Should return current tile when it's the final tile");
    }
  }

  @Nested
  @DisplayName("Equality and Hash Code")
  class EqualityTests {

    @Test
    void equalityBasedOnName() {
      Player sameNamePlayer = new Player(validName, "DifferentPiece");
      Player differentNamePlayer = new Player("OtherName", validPieceType);

      assertEquals(player, sameNamePlayer, "Players with same name should be equal");
      assertNotEquals(player, differentNamePlayer, "Players with different names should not be equal");
      assertEquals(player.hashCode(), sameNamePlayer.hashCode(),
          "Equal players should have same hash code");
    }
  }
}