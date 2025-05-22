package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * Factory for creating {@link Tile} instances with validated identifiers and positions.
 *
 * <p>Ensures that every tile has a positive ID, row, and column. Can be extended
 * in the future to produce specialized tile types or apply additional initialization.
 *
 */
public class TileFactory {

  /**
   * Creates a new {@link Tile} with the given identifiers.
   *
   * <p>Validates that {@code tileId}, {@code row}, and {@code column} are all
   * positive values. Throws an exception if any value is invalid.
   *
   * @param tileId unique identifier for the tile; must be positive
   * @param row    the row index of the tile on the board; must be positive
   * @param column the column index of the tile on the board; must be positive
   * @return a new {@link Tile} positioned at the specified row and column
   * @throws IllegalArgumentException if {@code tileId}, {@code row}, or {@code column}
   *                                  is not positive
   */
  public Tile createTile(int tileId, int row, int column) {
    ExceptionHandling.requirePositive(tileId, "tileId");
    ExceptionHandling.requirePositive(row, "row");
    ExceptionHandling.requirePositive(column, "column");
    return new Tile(tileId, row, column);
  }
}
