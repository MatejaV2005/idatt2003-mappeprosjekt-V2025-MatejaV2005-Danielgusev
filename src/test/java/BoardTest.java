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
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Board}.
 * Tests the construction, configuration, and functionality of the Board class.
 */
class BoardTest {

  private Board board;

  /**
   * Sets up a basic board instance before each test.
   */
  @BeforeEach
  void setUp() {
    board = new Board();
  }

  /**
   * Tests that the default constructor initializes properties correctly.
   */
  @Test
  void defaultConstructor_InitializesBoardCorrectly() {
    assertNotNull(board.getTiles());
    assertTrue(board.getTiles().isEmpty());
    assertEquals(0, board.getRows());
    assertEquals(0, board.getColumns());
    assertEquals(0, board.getBoardSize());
  }

  /**
   * Tests that the setRows method validates input correctly.
   */
  @Test
  void setRows_RejectsNonPositiveValues() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> board.setRows(0)
    );
    assertEquals("Number of rows must be strictly positive (greater than 0).", exception.getMessage());

    assertThrows(IllegalArgumentException.class, () -> board.setRows(-1));
  }

  /**
   * Tests that the setColumns method validates input correctly.
   */
  @Test
  void setColumns_RejectsNonPositiveValues() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> board.setColumns(0)
    );
    assertEquals("Number of columns must be strictly positive (greater than 0).", exception.getMessage());

    assertThrows(IllegalArgumentException.class, () -> board.setColumns(-1));
  }

  /**
   * Tests that rows and columns can be set successfully.
   */
  @Test
  void setDimensions_UpdatesRowsAndColumns() {
    board.setRows(5);
    board.setColumns(10);

    assertEquals(5, board.getRows());
    assertEquals(10, board.getColumns());
  }

  /**
   * Tests that tiles can be set successfully.
   */
  @Test
  void setTiles_UpdatesTilesMap() {
    Map<Integer, Tile> newTiles = new HashMap<>();
    newTiles.put(1, new Tile(1, 0, 0));

    board.setTiles(newTiles);

    assertEquals(1, board.getBoardSize());
    assertEquals(newTiles.get(1), board.getTileById(1));
  }

  /**
   * Tests that setTiles rejects null input.
   */
  @Test
  void setTiles_RejectsNullMap() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> board.setTiles(null)
    );
    assertEquals("Tiles map for setTiles cannot be null.", exception.getMessage());
  }

  /**
   * Tests that the parameterized constructor initializes the board correctly.
   */
  @Test
  void parameterizedConstructor_InitializesBoardCorrectly() {
    board = new Board(3, 4);

    assertNotNull(board.getTiles());
    assertEquals(3, board.getRows());
    assertEquals(4, board.getColumns());
    assertEquals(12, board.getBoardSize());

    // Check that all tiles are created
    for (int i = 1; i <= 12; i++) {
      assertNotNull(board.getTileById(i));
      assertEquals(i, board.getTileById(i).getTileId());
    }
  }

  /**
   * Tests that tiles are linked sequentially after construction.
   */
  @Test
  void parameterizedConstructor_LinksTilesSequentially() {
    board = new Board(2, 2);

    assertEquals(board.getTileById(2), board.getTileById(1).getNextTile());
    assertEquals(board.getTileById(3), board.getTileById(2).getNextTile());
    assertEquals(board.getTileById(4), board.getTileById(3).getNextTile());
    assertNull(board.getTileById(4).getNextTile());
  }

  /**
   * Tests that parameterized constructor validates rows.
   */
  @Test
  void parameterizedConstructor_RejectsZeroRows() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new Board(0, 5)
    );
    assertEquals("Number of rows must be strictly positive (greater than 0).", exception.getMessage());
  }

  /**
   * Tests that parameterized constructor validates columns.
   */
  @Test
  void parameterizedConstructor_RejectsNegativeColumns() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new Board(5, -1)
    );
    assertEquals("Number of columns must be strictly positive (greater than 0).", exception.getMessage());
  }

  /**
   * Tests that getTileById returns the correct tile.
   */
  @Test
  void getTileById_ReturnsCorrectTile() {
    board = new Board(5, 5);

    Tile tile5 = board.getTileById(5);

    assertNotNull(tile5);
    assertEquals(5, tile5.getTileId());
  }

  /**
   * Tests that getTileById validates the tile ID.
   */
  @Test
  void getTileById_RejectsInvalidIds() {
    board = new Board(5, 5);

    // Test zero ID
    IllegalArgumentException zeroException = assertThrows(
        IllegalArgumentException.class,
        () -> board.getTileById(0)
    );
    assertEquals("tileId for getTileById must be strictly positive (greater than 0).",
        zeroException.getMessage());

    // Test negative ID
    IllegalArgumentException negativeException = assertThrows(
        IllegalArgumentException.class,
        () -> board.getTileById(-1)
    );
    assertEquals("tileId for getTileById must be strictly positive (greater than 0).",
        negativeException.getMessage());

    // Test non-existent ID
    IllegalArgumentException nonExistentException = assertThrows(
        IllegalArgumentException.class,
        () -> board.getTileById(26)
    );
    assertEquals("Tile with ID: 26 on this board cannot be null.",
        nonExistentException.getMessage());
  }

  /**
   * Tests that getBoardSize returns the correct number of tiles.
   */
  @Test
  void getBoardSize_ReturnsCorrectNumberOfTiles() {
    board = new Board(5, 5);
    assertEquals(25, board.getBoardSize());

    Board smallBoard = new Board(1, 1);
    assertEquals(1, smallBoard.getBoardSize());
  }

  /**
   * Tests that getTiles returns an unmodifiable map.
   */
  @Test
  void getTiles_ReturnsUnmodifiableMap() {
    board = new Board(2, 2);

    Map<Integer, Tile> tilesMap = board.getTiles();

    assertThrows(
        UnsupportedOperationException.class,
        () -> tilesMap.put(100, new Tile(100, 0, 0))
    );
  }

  /**
   * Tests that resetBoard re-initializes and relinks tiles.
   */
  @Test
  void resetBoard_ReInitializesAndRelinksTiles() {
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

  /**
   * Tests that relinkTiles correctly restores the next tile references.
   */
  @Test
  void relinkTiles_RestoresNextTileReferences() {
    board = new Board(1, 3);
    Map<Integer, Tile> modifiableTiles = new HashMap<>();
    Tile t1 = new Tile(1, 0, 0);
    Tile t2 = new Tile(2, 0, 1);
    Tile t3 = new Tile(3, 0, 2);

    // Set up next tile relationships
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
}