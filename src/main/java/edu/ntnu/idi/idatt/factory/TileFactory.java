package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

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
  public Tile createTile(int tileId) {
    ExceptionHandling.requirePositive(tileId, "tileId");
    return new Tile(tileId);
  }

}
