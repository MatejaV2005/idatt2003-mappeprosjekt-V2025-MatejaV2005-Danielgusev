package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.factory.TileFactory;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Core representation of a game board: a two-dimensional grid of {@link Tile} instances.
 *
 * <p>Manages board dimensions, tile creation, linking between adjacent tiles,
 * and provides lookup/access methods. Supports full lifecycle operations such
 * as initialization, reset, deserialization (via {@link #setTiles(Map)} and
 * {@link #relinkTiles()}), and runtime tile retrieval.</p>
 *
 * @see Tile
 * @see #initializeTiles()
 * @see #linkTiles()
 * @see #relinkTiles()
 *
 * */
public class Board {

  private Map<Integer, Tile> tiles;
  private int rows;
  private int columns;
  private TileFactory tileFactory;

  /**
   * Default constructor. Initializes the tile factory.
   * The board's dimensions and tiles must be set separately,
   * for example, through setters or by a deserialization process
   * that subsequently calls methods to populate the board.
   */
  public Board() {
    this.tileFactory = new TileFactory();
  }

  /**
   * Constructs a new game board with the specified number of rows and columns.
   * Tiles are automatically initialized and linked in a default sequential manner.
   * Both rows and columns must be strictly positive.
   *
   * @param rows    The number of rows on the board. Must be greater than 0.
   * @param columns The number of columns on the board. Must be greater than 0.
   * @throws IllegalArgumentException if rows or columns are not strictly positive.
   */
  public Board(int rows, int columns) {
    setRows(rows);
    setColumns(columns);
    this.tileFactory = new TileFactory();
    this.tiles = new HashMap<>();
    initializeTiles();
    linkTiles();
  }

  /**
   * Initializes all tiles on the board based on the current number of rows and columns.
   * Each tile is created with a unique ID, its row and column position.
   * This method is typically called by the constructor or when resetting the board.
   */
  private void initializeTiles() {
    if (this.tileFactory == null) {
      this.tileFactory = new TileFactory();
    }
    if (this.tiles == null) {
      this.tiles = new HashMap<>();
    } else {
      this.tiles.clear();
    }

    int tileId = 1;
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        this.tiles.put(tileId, this.tileFactory.createTile(tileId, i, j));
        tileId++;
      }
    }
  }

  /**
   * Links the initialized tiles in a sequential manner (e.g., tile 1 links to tile 2, etc.).
   * The last tile will have no next tile.
   * This method assumes {@link #initializeTiles()} has been called and {@code tiles} is populated.
   */
  private void linkTiles() {
    if (this.tiles == null || this.tiles.isEmpty()) {
      return;
    }
    int totalTiles = this.rows * this.columns;
    for (int i = 1; i < totalTiles; i++) {
      Tile current = this.tiles.get(i);
      Tile next = this.tiles.get(i + 1);
      if (current != null && next != null) {
        current.setNextTile(next);
      }
    }
  }

  /**
   * Re-establishes the {@code nextTile} runtime references for all tiles on the board
   * based on their stored {@code nextTileId} values.
   * This is typically used after deserializing a board to restore the object graph.
   * It assumes the {@code tiles} map is populated and all {@code nextTileId} values
   * refer to existing tile IDs within this board, or are -1.
   */
  public void relinkTiles() {
    if (this.tiles == null || this.tiles.isEmpty()) {
      return;
    }
    for (Tile tile : this.tiles.values()) {
      int nextTileIdToLink = tile.getNextTileId();
      if (nextTileIdToLink != -1) {
        Tile nextTileObject = this.tiles.get(nextTileIdToLink);
        tile.setNextTile(nextTileObject);
      } else {
        tile.setNextTile(null);
      }
    }
  }

  /**
   * Retrieves a specific {@link Tile} from the board by its unique ID.
   *
   * @param tileId The ID of the tile to retrieve. Must be strictly positive.
   * @return The {@link Tile} object corresponding to the given ID.
   * @throws IllegalArgumentException if tileId is not strictly positive,
   *                                  or if no tile exists with the given ID on this board.
   */
  public Tile getTileById(int tileId) {
    ExceptionHandling.requireStrictlyPositive(tileId, "tileId for getTileById");
    ExceptionHandling.requireNonNull(this.tiles, "Tiles map (internal board structure)");

    Tile tile = this.tiles.get(tileId);
    ExceptionHandling.requireNonNull(tile, "Tile with ID: " + tileId + " on this board");
    return tile;
  }

  /**
   * Gets the total number of tiles on this board.
   * This is typically {@code rows * columns}.
   *
   * @return The total number of tiles, or 0 if the board is not initialized.
   */
  public int getBoardSize() {
    return this.tiles != null ? this.tiles.size() : 0;
  }

  /**
   * Resets the board to its initial state by re-initializing and re-linking all tiles.
   * This uses the current {@code rows} and {@code columns} values.
   * Any existing player placements or tile actions on the old tiles will be lost.
   */
  public void resetBoard() {
    // Ensure rows and columns are valid before re-initializing
    ExceptionHandling.requireStrictlyPositive(this.rows, "Board rows for reset");
    ExceptionHandling.requireStrictlyPositive(this.columns, "Board columns for reset");

    initializeTiles();
    linkTiles();
  }

  /**
   * Gets the number of rows on this board.
   *
   * @return The number of rows.
   */
  public int getRows() {
    return rows;
  }

  /**
   * Sets the number of rows for this board.
   * This should typically be done before tiles are initialized or if the board is being resized
   * (which would require re-initialization of tiles).
   *
   * @param rows The number of rows. Must be strictly positive.
   * @throws IllegalArgumentException if rows is not strictly positive.
   */
  public void setRows(int rows) {
    ExceptionHandling.requireStrictlyPositive(rows, "Number of rows");
    this.rows = rows;
  }

  /**
   * Gets the number of columns on this board.
   *
   * @return The number of columns.
   */
  public int getColumns() {
    return columns;
  }

  /**
   * Sets the number of columns for this board.
   * This should typically be done before tiles are initialized or if the board is being resized.
   *
   * @param columns The number of columns. Must be strictly positive.
   * @throws IllegalArgumentException if columns is not strictly positive.
   */
  public void setColumns(int columns) {
    ExceptionHandling.requireStrictlyPositive(columns, "Number of columns");
    this.columns = columns;
  }

  /**
   * Returns an unmodifiable view of the map of tiles on this board.
   * The keys are tile IDs and values are the {@link Tile} objects.
   * Attempting to modify the returned map will result in an
   * {@link UnsupportedOperationException}.
   *
   * @return An unmodifiable map of tiles, or an unmodifiable empty map if
   *         the board has not been initialized with tiles.
   */
  public Map<Integer, Tile> getTiles() {
    return Collections.unmodifiableMap(Objects.requireNonNullElseGet(this.tiles, HashMap::new));
  }

  /**
   * Sets the entire collection of tiles for this board.
   * This method is typically used during deserialization or when constructing a board
   * with a pre-defined set of tiles. It's the caller's responsibility to ensure
   * the provided map is valid and consistent with the board's row/column dimensions
   * and that tiles are properly linked if {@link #relinkTiles()} is not called subsequently.
   *
   * @param tiles A map where keys are tile IDs and values are {@link Tile} objects.
   *              Cannot be null.
   *
   * @throws IllegalArgumentException if the provided tiles map is null.
   */
  public void setTiles(Map<Integer, Tile> tiles) {
    ExceptionHandling.requireNonNull(tiles, "Tiles map for setTiles");
    this.tiles = tiles;
  }



  /**
   * Compares this board to another object for equality.
   * Boards are considered equal if they have the same number of rows, columns,
   * and their tile maps are equal (deep equality of tiles is not checked here,
   * only if the maps contain the same tile IDs mapping to equal Tile objects
   * based on Tile's equals method).
   *
   * @param o The object to compare with this board.
   * @return {@code true} if the given object is a Board with the same dimensions and tiles,
   *                      {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Board otherBoard)) {
      return false;
    }
    return rows == otherBoard.rows
        && columns == otherBoard.columns
        && Objects.equals(tiles, otherBoard.tiles);
  }

  /**
   * Returns a hash code value for the board.
   * This implementation considers the rows, columns, and the tiles map.
   *
   * @return A hash code value for this board.
   */
  @Override
  public int hashCode() {
    return Objects.hash(rows, columns, tiles);
  }
}
