package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;

public class LadderAction implements TileAction {
  private final Tile destinationTile;
  private final String description;
  private final int destinationTileId;

  // Existing constructor
  public LadderAction(Tile destinationTile, String description) {
    this.destinationTile = destinationTile;
    this.destinationTileId = destinationTile != null ? destinationTile.getTileId() : -1;
    this.description = description;
  }

  // New constructor for when we only have an ID
  public LadderAction(int destinationTileId, String description) {
    this.destinationTile = null;
    this.destinationTileId = destinationTileId;
    this.description = description;
  }

  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " " + description);
    player.setOnCurrentTile(destinationTile);
  }

  @Override
  public ActionType getActionType() {
    return ActionType.LADDER;
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
