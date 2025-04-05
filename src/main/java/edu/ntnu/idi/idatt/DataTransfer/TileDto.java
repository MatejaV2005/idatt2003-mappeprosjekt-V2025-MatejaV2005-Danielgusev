package edu.ntnu.idi.idatt.DataTransfer;

import edu.ntnu.idi.idatt.model.ActionType;

public class TileDto {
  private final int id;
  private final int row;
  private final int column;
  private final int nextTileId;
  private ActionDto action;

  public TileDto(int id, int row, int column, int nextTileId, ActionDto action) {
    this.id = id;
    this.row = row;
    this.column = column;
    this.nextTileId = nextTileId;
    this.action = action;
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


  public ActionDto getAction() {
    return action;
  }
}
