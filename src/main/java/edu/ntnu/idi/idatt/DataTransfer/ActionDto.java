package edu.ntnu.idi.idatt.DataTransfer;


public class ActionDto {
  private final String actionType;
  private final int destinationTileId;
  private final String description;

  public ActionDto(String actionType, int destinationTileId, String description) {
    this.actionType = actionType;
    this.destinationTileId = destinationTileId;
    this.description = description;
  }

  public String getActionType() {
    return actionType;
  }

  public int getDestinationTileId() {
    return destinationTileId;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "Action {" +
        "actionType='" + actionType + '\'' +
        ", destinationTileId=" + destinationTileId +
        ", description='" + description + '\'' +
        '}';
  }



}
