import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BoardTest {

  private Board board;

  @Nested
  @DisplayName("Default Constructor and Basic Setters")
  class DefaultConstructorTests {

    @BeforeEach
    void setUp() {
      board = new Board();
    }

    @Test
    void defaultConstructorInitializesTileFactory() {
      assertNotNull(board.getTiles());
      assertTrue(board.getTiles().isEmpty());
      assertEquals(0, board.getRows());
      assertEquals(0, board.getColumns());
      assertEquals(0, board.getBoardSize());
    }

    @Test
    void setRowsThrowsForNonPositiveValue() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> board.setRows(0));
      assertEquals("Number of rows must be strictly positive (greater than 0).", e.getMessage());
      assertThrows(IllegalArgumentException.class, () -> board.setRows(-1));
    }

    @Test
    void setColumnsThrowsForNonPositiveValue() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> board.setColumns(0));
      assertEquals("Number of columns must be strictly positive (greater than 0).", e.getMessage());
      assertThrows(IllegalArgumentException.class, () -> board.setColumns(-1));
    }

    @Test
    void setRowsAndColumnsSuccessfullyUpdatesDimensions() {
      board.setRows(5);
      board.setColumns(10);
      assertEquals(5, board.getRows());
      assertEquals(10, board.getColumns());
    }

    @Test
    void setTilesSuccessfullyUpdatesTilesMap() {
      Map<Integer, Tile> newTiles = new HashMap<>();
      newTiles.put(1, new Tile(1,0,0));
      board.setTiles(newTiles);
      assertEquals(1, board.getBoardSize());
      assertEquals(newTiles.get(1), board.getTileById(1));
    }

    @Test
    void setTilesThrowsForNullMap() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> board.setTiles(null));
      assertEquals("Tiles map for setTiles cannot be null.", e.getMessage());
    }
  }

  @Nested
  @DisplayName("Parameterized Constructor (rows, columns)")
  class ParameterizedConstructorTests {

    @Test
    void constructorInitializesBoardCorrectlyWithValidDimensions() {
      board = new Board(3, 4);
      assertNotNull(board.getTiles());
      assertEquals(3, board.getRows());
      assertEquals(4, board.getColumns());
      assertEquals(12, board.getBoardSize());

      for (int i = 1; i <= 12; i++) {
        assertNotNull(board.getTileById(i));
        assertEquals(i, board.getTileById(i).getTileId());
      }
    }

    @Test
    void constructorLinksTilesSequentially() {
      board = new Board(2, 2);
      assertEquals(board.getTileById(2), board.getTileById(1).getNextTile());
      assertEquals(board.getTileById(3), board.getTileById(2).getNextTile());
      assertEquals(board.getTileById(4), board.getTileById(3).getNextTile());
      assertNull(board.getTileById(4).getNextTile());
    }

    @Test
    void constructorThrowsForZeroRows() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> new Board(0, 5));
      assertEquals("Number of rows must be strictly positive (greater than 0).", e.getMessage());
    }

    @Test
    void constructorThrowsForNegativeColumns() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> new Board(5, -1));
      assertEquals("Number of columns must be strictly positive (greater than 0).", e.getMessage());
    }
  }

  @Nested
  @DisplayName("Tile Access and Board Information")
  class TileAccessTests {

    @BeforeEach
    void setUp() {
      board = new Board(5, 5);
    }

    @Test
    void getTileByIdReturnsCorrectTile() {
      Tile tile5 = board.getTileById(5);
      assertNotNull(tile5);
      assertEquals(5, tile5.getTileId());
    }

    @Test
    void getTileByIdThrowsForZeroId() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> board.getTileById(0));
      assertEquals("tileId for getTileById must be strictly positive (greater than 0).", e.getMessage());
    }

    @Test
    void getTileByIdThrowsForNegativeId() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> board.getTileById(-1));
      assertEquals("tileId for getTileById must be strictly positive (greater than 0).", e.getMessage());
    }

    @Test
    void getTileByIdThrowsForNonExistentId() {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> board.getTileById(26));
      assertEquals("Tile with ID: 26 on this board cannot be null.", e.getMessage());
    }

    @Test
    void getTileByIdThrowsIfTilesMapIsNullInternally() {
      Board emptyBoard = new Board();
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> emptyBoard.getTileById(1));
      assertEquals("Tiles map (internal board structure) cannot be null.", e.getMessage());
    }


    @Test
    void getBoardSizeReturnsCorrectNumberOfTiles() {
      assertEquals(25, board.getBoardSize());
      Board smallBoard = new Board(1,1);
      assertEquals(1, smallBoard.getBoardSize());
    }

    @Test
    void getTilesReturnsUnmodifiableMap() {
      Map<Integer, Tile> tilesMap = board.getTiles();
      assertThrows(UnsupportedOperationException.class, () -> tilesMap.put(100, new Tile(100,0,0)));
    }

    @Test
    void getTilesReturnsCopyNotInternalReference() {
      Map<Integer, Tile> tiles1 = board.getTiles();
      Map<Integer, Tile> tiles2 = board.getTiles();
      assertNotSame(tiles1, tiles2, "getTiles() should return a new unmodifiable wrapper each time for safety.");
    }
  }

  @Nested
  @DisplayName("Board Manipulation and State")
  class BoardManipulationTests {

    @Test
    void resetBoardReInitializesAndRelinksTiles() {
      board = new Board(2, 2); // 4 tiles
      Tile originalTile1 = board.getTileById(1);
      Tile originalTile2 = board.getTileById(2);
      originalTile1.setNextTileId(99); // Mess up linking

      board.resetBoard();

      assertEquals(4, board.getBoardSize());
      Tile newTile1 = board.getTileById(1);
      Tile newTile2 = board.getTileById(2);

      assertNotSame(originalTile1, newTile1, "Tiles should be new instances after reset.");
      assertEquals(newTile2, newTile1.getNextTile(), "Tiles should be correctly relinked after reset.");
      assertEquals(2, newTile1.getNextTileId());
    }

    @Test
    void resetBoardThrowsIfDimensionsAreInvalid() {
      board = new Board();
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> board.resetBoard());
      assertTrue(e.getMessage().contains("Board rows for reset") || e.getMessage().contains("Board columns for reset"));
    }

    @Test
    void relinkTilesCorrectlyRestoresNextTileReferences() {
      board = new Board(1, 3);
      Map<Integer, Tile> modifiableTiles = new HashMap<>();
      Tile t1 = new Tile(1,0,0);
      Tile t2 = new Tile(2,0,1);
      Tile t3 = new Tile(3,0,2);

      t1.setNextTileId(2);
      t1.setNextTile(t2);

      t2.setNextTileId(3);
      t2.setNextTile(t3);

      t3.setNextTileId(-1);
      t3.setNextTile(null);

      modifiableTiles.put(1, t1);
      modifiableTiles.put(2, t2);
      modifiableTiles.put(3, t3);

      board.setTiles(modifiableTiles);
      board.relinkTiles();

      assertEquals(t2, board.getTileById(1).getNextTile());
      assertEquals(t3, board.getTileById(2).getNextTile());
      assertNull(board.getTileById(3).getNextTile());
    }

    @Test
    void relinkTilesHandlesNonExistentNextTileIdGracefully() {
      board = new Board(1, 2);
      Tile t1 = board.getTileById(1);
      t1.setNextTileId(99);
      t1.setNextTile(null);

      board.relinkTiles();
      assertNull(t1.getNextTile(), "NextTile should remain null if nextTileId is invalid.");
    }
  }
}
