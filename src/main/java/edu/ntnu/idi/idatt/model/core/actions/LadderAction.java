package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * Action representing climbing a ladder on a game tile.
 *
 * <p>Moves the player immediately to the specified destination tile and
 * prints a description of the action.
 *
 * @see TileAction
 * @since 1.0
 */
public class LadderAction implements TileAction {

  private final Tile destinationTile;
  private final String description;
  private final int destinationTileId;

  /**
   * Creates a LadderAction.
   *
   * @param destinationTile the tile to move the player to; may be null.
   * @param description     a non-blank description of the action; must not be null or blank
   * @throws IllegalArgumentException if {@code description} is null or blank
   */
  public LadderAction(Tile destinationTile, String description) {
    ExceptionHandling.requireNonNullOrBlank(description, "description");
    this.destinationTile = destinationTile;
    this.destinationTileId = destinationTile != null ? destinationTile.getTileId() : -1;
    this.description = description;
  }

  /**
   * Executes the ladder action by moving the player.
   *
   * <p>Prints the action description to standard output and updates the player's current tile.
   *
   * @param player the player to move; must not be null
   * @throws IllegalArgumentException if {@code player} is null, or if the
   *         destination tile for this action is null.
   */
  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "player");

    if (this.destinationTile == null) {
      throw new IllegalArgumentException("New tile for player cannot be null");
    }
    player.setOnCurrentTile(this.destinationTile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActionType getActionType() {
    return ActionType.LADDER;
  }

  /**
   * {@inheritDoc}
   *
   * @return the ID of the destination tile, or -1 if none was provided
   */
  @Override
  public int getDestinationTileId() {
    return destinationTileId;
  }

  /**
   * {@inheritDoc}
   *
   * @return the description provided at construction
   */
  @Override
  public String getDescription() {
    return description;
  }
}
