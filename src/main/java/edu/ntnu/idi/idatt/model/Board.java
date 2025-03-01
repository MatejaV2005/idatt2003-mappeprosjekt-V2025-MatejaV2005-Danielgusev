package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.HashMap;
import java.util.Map;


public class Board {
  private Map<Integer, Tile> tiles;
  private int totalTiles;

  public Board(int totalTiles) {
    ExceptionHandling.requirePositive(totalTiles, "totalTiles");

    tiles = new HashMap<>();
    this.totalTiles = totalTiles;

    initializeTiles();
    linkTiles();
  }


  private void initializeTiles() {
    for (int i = 1; i <= totalTiles; i++) {
      tiles.put(i, new Tile(i));
    }
  }

  public void assignTilesWithAction() {
    //TODO: implement when tileAction is done
    Tile startTile = getTileById(5);
    Tile destinationTile = getTileById(15);

    startTile.setLandAction(new LadderAction(destinationTile, "climbs up to tile 15!"));
  }

  private void linkTiles() {
    for (int i = 1; i < totalTiles; i++) {
      tiles.get(i).setNextTile(tiles.get(i + 1));
    }
  }

  public Tile getTileById(int tileId) {
    ExceptionHandling.requirePositive(tileId, "tileId");
    Tile tile = tiles.get(tileId);

    ExceptionHandling.requireNonNull(tile, "Tile ID: " + tileId);

    return tile;
  }

  public int getBoardSize() {
    return tiles.size();
  }

  public void resetBoard() {
    tiles.clear();
    initializeTiles();
    linkTiles();
  }
}



