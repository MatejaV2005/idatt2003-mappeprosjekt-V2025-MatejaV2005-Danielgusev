package edu.ntnu.idi.idatt.model;

import java.util.HashMap;
import java.util.Map;

public class Board {
  private Map<Integer, Tile> tiles;

  public Board() {
    tiles = new HashMap<>();
  }

  public void addTiles(Tile tile) {
    tiles.put(tile.getTileId(), tile);
  }

  public void linkTiles(Tile previousTile, Tile currentTile) {

  }

  public Tile getTile(int tileId) {
    if (tileId < 1 || !tiles.containsKey(tileId)) {
      throw new IllegalArgumentException("tileID not found");
    }

    Tile tile = tiles.get(tileId);
    return tile;
  }



}



