package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;

/**
 * Defines the contract for actions performed when a player interacts with a tile.
 * Implementations define specific behaviors like movement or effects.
 */
public interface TileAction {

  /**
   * Performs the action on the specified player.
   *
   * @param player The player to perform the action on.
   */
  void perform(Player player);

  /**
   * Gets the type of this tile action.
   *
   * @return The {@link ActionType} of this action.
   */
  ActionType getActionType();

  /**
   * Gets the ID of a destination tile, if applicable.
   * Returns -1 if the action does not involve moving to a specific tile.
   *
   * @return The destination tile ID, or -1 if not applicable.
   */
  int getDestinationTileId();

  /**
   * Gets a human-readable description of the action.
   *
   * @return A string describing the action.
   */
  String getDescription();
}
