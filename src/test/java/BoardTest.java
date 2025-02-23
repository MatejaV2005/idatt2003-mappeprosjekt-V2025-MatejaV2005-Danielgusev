import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BoardTest {
  private static Board board;

  @BeforeAll
  static void setUp() {
    board = new Board(100);
  }

  @Test
  void testBoardConstructor() {
      assertEquals(100, board.getBoardSize());
  }

  @Test
  void testTilesCorrectlyLinked() {
    for (int i = 1; i < board.getBoardSize(); i++) {
      Tile test = board.getTileById(i);
      assertEquals(i + 1, test.getNextTile().getTileId());
    }
  }

  @Test
  void testGetTileByIdWorksCorrect() {
    Tile test = board.getTileById(1);
    assertEquals(1, test.getTileId());
  }

  @Test
  @Disabled("Implement logic later")
  void testResetBoardWorksCorrect() {
    //TODO: fill in logic after youve added dynamic actions in BoardGame
  }

  // NEGATIVE TESTS ---------------------------------------

  @Test
  void testBoardInitializationWithNegativeTiles() {
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> new Board(-10),
        "Expected exception for negative totalTiles"
    );
    assertEquals("totalTiles must be positive", thrown.getMessage());
  }

  @Test
  void testBoardInitializationWithZeroTiles() {
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> new Board(0),
        "Expected exception for zero totalTiles"
    );
    assertEquals("totalTiles must be positive", thrown.getMessage());
  }

  @Test
  void testGetTileByIdWithNegativeId() {
    Board board1 = new Board(10);
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> board1.getTileById(-1),
        "Expected exception for negative tileId"
    );
    assertEquals("tileId must be positive", thrown.getMessage());
  }

  @Test
  void testGetTileByIdWithZeroId() {
    Board board1 = new Board(10);
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> board1.getTileById(0),
        "Expected exception for zero tileId"
    );
    assertEquals("tileId must be positive", thrown.getMessage());
  }

  @Test
  void testGetTileByIdWithNonExistentId() {
    Board board = new Board(10);
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> board.getTileById(100),
        "Expected exception for non-existent tileId"
    );
    assertEquals("Tile ID: 100 cannot be null or doesnt exist", thrown.getMessage());
  }
}
