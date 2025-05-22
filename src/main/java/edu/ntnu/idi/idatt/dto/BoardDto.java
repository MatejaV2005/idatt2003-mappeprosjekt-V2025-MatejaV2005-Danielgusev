package edu.ntnu.idi.idatt.dto;

import java.util.Map;

/**
 * Data transfer object for a game board layout.
 *
 * <p>Holds a mapping of tile IDs to {@link TileDto} instances and the
 * board dimensions (rows and columns).
 */
public class BoardDto {

  private final Map<Integer, TileDto> tiles;
  private final int rows;
  private final int columns;

  /**
   * Constructs a BoardDto with the given tiles and dimensions.
   *
   * @param tiles   map from tile ID to {@link TileDto}; must not be null or empty
   * @param rows    number of rows in the board; must be positive
   * @param columns number of columns in the board; must be positive
   */
  public BoardDto(Map<Integer, TileDto> tiles, int rows, int columns) {
    this.tiles = tiles;
    this.rows = rows;
    this.columns = columns;
  }

  /**
   * Returns the mapping of tile IDs to tile DTOs.
   *
   * @return map of tile ID to {@link TileDto}
   */
  public Map<Integer, TileDto> getTiles() {
    return tiles;
  }

  /**
   * Returns the number of rows in the board.
   *
   * @return row count
   */
  public int getRows() {
    return rows;
  }

  /**
   * Returns the number of columns in the board.
   *
   * @return column count
   */
  public int getColumns() {
    return columns;
  }
}
