package edu.ntnu.idi.idatt.model;

import java.util.HashMap;
import java.util.Map;


public class Board {
  private Map<Integer, Tile> tiles;
  private int totalTiles;

  public Board(int totalTiles) {
    tiles = new HashMap<>();
    this.totalTiles = totalTiles;
    initializeTiles();
    linkTiles();
  }


  public void initializeTiles() {
    for (int i = 1; i <= totalTiles; i++) {
      tiles.put(i, new Tile(i));
    }
  }

  public void addTilesWithAction(Tile tile) {
    //TODO: implement when tileAction is done
  }

  public void linkTiles() {
    for (int i = 1; i < totalTiles; i++) {
      tiles.get(i).setNextTile(tiles.get(i + 1));
    }
  }

  public Tile getTile(int tileId) {
    if (tileId < 1 || !tiles.containsKey(tileId)) {
      throw new IllegalArgumentException("tileID not found");
    }

    return tiles.get(tileId);
  }

  public void resetBoard() {
    tiles.clear();
    initializeTiles();
    linkTiles();
  }



}



