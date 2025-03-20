package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Board {
  private final Map<Integer, Tile> tiles;
  private int totalTiles;

  public Board(int totalTiles) {
    ExceptionHandling.requirePositive(totalTiles, "totalTiles");

    tiles = new HashMap<>();
    this.totalTiles = totalTiles;

    initializeTiles();
    linkTiles();
    assignTilesWithAction();
  }


  private void initializeTiles() {
    for (int i = 1; i <= totalTiles; i++) {
      tiles.put(i, new Tile(i));
    }
  }

  public void assignTilesWithAction() {
    //TODO: implement when tileAction is done, REMEMBER FACTORY DESIGN HERE
    Tile startTile = getTileById(8);
    Tile destinationTile = getTileById(80);

    startTile.setLandAction(Optional.of(new LadderAction(destinationTile, "climbs up to tile 80!")));

    Tile startTile2 = getTileById(50);
    Tile destinationTile2 = getTileById(5);

    startTile2.setLandAction(Optional.of(new LadderAction(destinationTile2, "climbs down to tile 5! buhu :(")));
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



