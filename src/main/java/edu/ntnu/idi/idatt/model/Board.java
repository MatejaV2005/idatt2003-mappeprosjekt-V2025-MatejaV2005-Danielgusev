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

  public void assignTilesWithAction(Tile tile) {
    //TODO: implement when tileAction is done
    /*
    * how will this work? we need to assign already initialized tiles with an action
    * we could approach it this way:
    * we have a setLandAction method, so we should make an array of integers that will
    * serve as constant tiles that will contain a tileAction
    * but it should also be dynamic
    *
    * a problem is how to first of all add the TileActions, create them here?
    * MAYBE USE GENERICS?????
    * I ALSO THInk that i should refer to the script class from OVING_3
     */
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



