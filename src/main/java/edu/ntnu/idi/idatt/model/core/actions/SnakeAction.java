package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;

public class SnakeAction implements TileAction{
  private final Tile destinationTile;
  private final String description;
  private final int destinationTileId;


  public SnakeAction(Tile destinationTile, String description) {
    this.destinationTile = destinationTile;
    this.destinationTileId = destinationTile != null ? destinationTile.getTileId() : -1;
    this.description = description;
  }


  @Override
  public void perform(Player player) {
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
