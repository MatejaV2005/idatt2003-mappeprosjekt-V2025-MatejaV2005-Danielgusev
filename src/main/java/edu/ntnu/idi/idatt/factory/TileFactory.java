package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Tile;

public class TileFactory {

  public Tile createTileID(int id) {
    return new Tile(id);
  }



}
