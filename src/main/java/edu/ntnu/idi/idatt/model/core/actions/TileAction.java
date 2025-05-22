package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;

/**
 * Contract for any action that can occur when a player lands on or interacts
 * with a game tile.
 *
 * <p>Implementations encapsulate specific behaviors such as moving the player
 * to another tile (ladders and snakes), applying status effects
 * (skip turn, boost pad), or executing custom logic via a {@link SpecialAction}.</p>
 *
 * @see ActionType
 * @see LadderAction
 * @see SnakeAction
 * @see SpecialAction
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
