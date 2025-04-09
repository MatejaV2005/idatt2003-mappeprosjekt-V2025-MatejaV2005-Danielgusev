package edu.ntnu.idi.idatt.DataTransfer;

import java.util.Map;

public class BoardDto {
  private Map<Integer, TileDto> tiles;
  private final int rows;
  private final int columns;
//  private final String name;
//  private final String description;


  public BoardDto(Map<Integer, TileDto> tiles, int rows, int columns) {
    this.tiles = tiles;
    this.rows = rows;
    this.columns = columns;
  }

  public Map<Integer, TileDto> getTiles() {
    return tiles;
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

}
