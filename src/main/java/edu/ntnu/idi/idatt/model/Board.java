package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.factory.TileFactory;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

import java.util.HashMap;
import java.util.Map;

public class Board {
  private Map<Integer, Tile> tiles;
  private int rows;
  private int columns;

  private Integer nextTileId;
  private transient TileFactory tileFactory;

  // No args constructor for deserializing
  public Board() {
    this.tileFactory = new TileFactory();
  }

  public Board(int rows, int columns) {
    ExceptionHandling.requirePositive(rows, "rows");
    ExceptionHandling.requirePositive(columns, "columns");

    this.rows = rows;
    this.columns = columns;
    this.tileFactory = new TileFactory();

    this.tiles = new HashMap<>();
    initializeTiles();
    linkTiles();
  }

  private void initializeTiles() {
    tiles = new HashMap<>();
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
      Tile current = tiles.get(i);
      Tile next = tiles.get(i + 1);
      if (current != null && next != null) {
        current.setNextTile(next);
      }
    }
  }

  public Tile getTileById(int tileId) {
    ExceptionHandling.requirePositive(tileId, "tileId");
    Tile tile = tiles.get(tileId);
    ExceptionHandling.requireNonNull(tile, "Tile ID: " + tileId);
    return tile;
  }

  public int getBoardSize() {
    return tiles != null ? tiles.size() : 0;
  }

  public void resetBoard() {
    initializeTiles();
    linkTiles();
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public Map<Integer, Tile> getTiles() {
    return tiles;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public void setTiles(Map<Integer, Tile> tiles) {
    this.tiles = tiles;
  }
}
