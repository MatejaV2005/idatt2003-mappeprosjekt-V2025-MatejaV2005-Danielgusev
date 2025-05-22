package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Player;

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
