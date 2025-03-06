import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.idatt.model.player_type.Player;

import org.junit.jupiter.api.Test;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * JUnit test class for Player
 */
public class PlayerTest {

  // --- Imitation Implementations for Testing -----------------

  /**
   * ImitationTile simulates a Tile.
   */
  private static class ImitationTile extends Tile {
    private final int id;
    private ImitationTile next;
    public boolean leaveCalled = false;
    public boolean landCalled = false;

    public ImitationTile(int id) {
      super(id);
      this.id = id;
    }

    public void setNextTile(ImitationTile next) {
      this.next = next;
    }

    @Override
    public Tile getNextTile() {
      return next;
    }

    @Override
    public void leavePlayer(Player player) {
      leaveCalled = true;
    }

    @Override
    public void landPlayer(Player player) {
      landCalled = true;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      ImitationTile other = (ImitationTile) obj;
      return id == other.id;
    }

    @Override
    public int hashCode() {
      return id;
    }
  }

  /**
   * ImitationBoard simulates a Board that stores ImitationTile instances.
   * It creates a simple cyclic chain: 1 -> 2 -> 3 -> 1.
   */
  private static class ImitationBoard extends Board {
    public ImitationBoard() {
      // First, call the Board constructor to create a board with 3 tiles.
      super(3);
      // Board.initializeTiles() and Board.linkTiles() have been called.
      // By default, Board.linkTiles() sets tile 1 -> tile 2 and tile 2 -> tile 3,
      // but it doesn't set tile 3's next tile. We'll set tile 3's next tile to tile 1 to complete the cycle.
      Tile tile1 = getTileById(1);
      Tile tile3 = getTileById(3);
      tile3.setNextTile(tile1);
    }
  }



  /**
   * DummyBoardGame is a minimal implementation of BoardGame that returns an ImitationBoard.
   * This dummy implementation is used to bypass the unfinished parts of BoardGame.
   */
  private static class DummyBoardGame {
    public DummyBoardGame() {
      // Call the parent constructor with a new ImitationBoard instance.
      super(new ImitationBoard());
    }
  }


  /**
   * TestPlayer is a concrete subclass of Player used for testing.
   */
  private static class TestPlayer extends Player {
    public TestPlayer(String name, BoardGame game) {
      super(name, game);
    }

    @Override
    public void move(int steps) {
      ExceptionHandling.requirePositive(steps, "steps");
      basicMove(steps);
    }
  }

  // --- Test Methods --------------------------

  // Tests that the constructor initializes the player's name and starting tile correctly.
  @Test
  void testConstructorAndGetters() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Ole", game);
    assertEquals("Alice", player.getName());
    Tile initialTile = game.getBoard().getTileById(1);
    assertEquals(initialTile, player.getCurrentTile());
  }

  // Tests that constructing a player with a null name throws NullPointerException.
  @Test
  void testConstructorNullName() {
    DummyBoardGame game = new DummyBoardGame();
    assertThrows(NullPointerException.class, () -> new TestPlayer(null, game));
  }

  // Tests that constructing a player with a null game throws NullPointerException.
  @Test
  void testConstructorNullGame() {
    assertThrows(NullPointerException.class, () -> new TestPlayer("Hedda", null));
  }

  // Tests that placeOnTile(Tile) properly changes the player's current tile.
  @Test
  void testPlaceOnTileObject() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Jens", game);
    ImitationTile initialTile = (ImitationTile) game.getBoard().getTileById(1);
    ImitationTile newTile = (ImitationTile) game.getBoard().getTileById(2);

    // Reset flags to verify method calls.
    initialTile.leaveCalled = false;
    newTile.landCalled = false;

    player.placeOnTile(newTile);
    assertEquals(newTile, player.getCurrentTile());
    assertTrue(initialTile.leaveCalled);
    assertTrue(newTile.landCalled);
  }

  // Tests that placeOnTile(int) properly changes the player's current tile.
  @Test
  void testPlaceOnTileById() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Lisa", game);
    ImitationTile initialTile = (ImitationTile) game.getBoard().getTileById(1);
    ImitationTile newTile = (ImitationTile) game.getBoard().getTileById(3);

    // Reset flags to verify method calls.
    initialTile.leaveCalled = false;
    newTile.landCalled = false;

    player.placeOnTile(3);
    assertEquals(newTile, player.getCurrentTile());
    assertTrue(initialTile.leaveCalled);
    assertTrue(newTile.landCalled);
  }

  // Test that calling placeOnTile(Tile) with null throws NullPointerException.
  @Test
  void testPlaceOnTileNull() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Mateja", game);
    assertThrows(NullPointerException.class, () -> player.placeOnTile((Tile) null));
  }

  // Test that calling placeOnTile(int) with a non-positive tile ID throws IllegalArgumentException.
  @Test
  void testPlaceOnTileWithNonPositiveId() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Eva", game);
    assertThrows(IllegalArgumentException.class, () -> player.placeOnTile(0));
    assertThrows(IllegalArgumentException.class, () -> player.placeOnTile(-1));
  }

  // Tests that basicMove moves the player correctly through the tile chain.
  @Test
  void testBasicMove() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Fredrik", game);
    // Starting at tile 1, moving 2 steps should land on tile 3 (chain: 1 -> 2 -> 3).
    player.basicMove(2);
    ImitationTile expectedTile = (ImitationTile) game.getBoard().getTileById(3);
    assertEquals(expectedTile, player.getCurrentTile());
  }

  // Tests that basicMove with non-positive steps throws IllegalArgumentException.
  @Test
  void testBasicMoveNonPositiveSteps() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Gro", game);
    assertThrows(IllegalArgumentException.class, () -> player.basicMove(0));
    assertThrows(IllegalArgumentException.class, () -> player.basicMove(-3));
  }

  // Tests that the move method (which calls basicMove) throws an exception for non-positive steps.
  @Test
  void testMoveNonPositiveSteps() {
    DummyBoardGame game = new DummyBoardGame();
    TestPlayer player = new TestPlayer("Henrik", game);
    assertThrows(IllegalArgumentException.class, () -> player.move(0));
    assertThrows(IllegalArgumentException.class, () -> player.move(-2));
  }
}
