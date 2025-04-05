package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.playertype.Player;

public interface TileAction {

  /**
   * Perform the action on the given player.
   *
   * @param player the player to perform the action on
   */
  void perform(Player player);
  ActionType getActionType();
  int getDestinationTileId();
  String getDescription();
}
