package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.Optional;

/**
 * Creates tiles.
 */
public class TileFactory {
  /**
   * Creating a new tile with the tileId.
   * Can be upgraded to create special tiles in the future if needed.
   * @param tileId the unique tile identification.
   * @return new tile object
   * @throws IllegalArgumentException if tile is not positive
   */
  public Tile createTile(int tileId, int row, int column) {
    ExceptionHandling.requirePositive(tileId, "tileId");
    return new Tile(tileId, row, column);
  }

  public Tile createTileWithAction(int tileId, int rows, int columns, TileAction action) {
    ExceptionHandling.requirePositive(tileId, "tileId");
    Tile tile = new Tile(tileId, rows, columns);
    tile.setLandAction(action);
    return tile;
  }



}
