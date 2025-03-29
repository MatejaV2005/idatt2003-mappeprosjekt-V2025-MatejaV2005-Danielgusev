package edu.ntnu.idi.idatt.DataTransfer;

public class TileDto {
  private int id;
  private int row;
  private int column;
  private int nextTileId;
  private String actionType;

  public TileDto(int id, int row, int column, int nextTileId, String actionType) {
    this.id = id;
    this.row = row;
    this.column = column;
    this.nextTileId = nextTileId;
    this.actionType = actionType;
  }

  public int getId() {
    return id;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public int getNextTileId() {
    return nextTileId;
  }

  public String getActionType() {
    return actionType;
  }
}
