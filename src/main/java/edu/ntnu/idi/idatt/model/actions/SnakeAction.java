package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.playertype.Player;

public class SnakeAction implements TileAction{
  private final Tile destinationTile;
  private final String description;
  private final int destinationTileId;


  public SnakeAction(Tile destinationTile, String description) {
    this.destinationTile = destinationTile;
    this.destinationTileId = destinationTile != null ? destinationTile.getTileId() : -1;
    this.description = description;
  }

  // New constructor for when we only have an ID
  public SnakeAction(int destinationTileId, String description) {
    this.destinationTile = null;
    this.destinationTileId = destinationTileId;
    this.description = description;
  }

  @Override
  public void perform(Player player) {
    //TODO: Add distinguishing features for class
    System.out.println(player.getName() + " " + description);
    player.setOnCurrentTile(destinationTile);
  }

  @Override
  public ActionType getActionType() {
    return ActionType.SNAKE;
  }

  @Override
  public int getDestinationTileId() {
    return destinationTile != null ? destinationTile.getTileId() : -1;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
