package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.factory.TileFactory;
import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Board {
  private final Map<Integer, Tile> tiles;
  private final int rows;
  private final int columns;


  private TileFactory tileFactory;

  public Board(int rows, int columns) {
    ExceptionHandling.requirePositive(rows, "rows");
    ExceptionHandling.requirePositive(columns, "columns");

    tiles = new HashMap<>();

    this.rows = rows;
    this.columns = columns;

    this.tileFactory = new TileFactory();

    initializeTiles();
    linkTiles();
  }


  private void initializeTiles() {
    int tileId = 1;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        tiles.put(tileId, tileFactory.createTile(tileId, i, j));
        tileId++;
      }
    }
  }

  private void linkTiles() {
    int totalTiles = rows * columns;
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



