package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * Action representing sliding down a snake on a game tile.
 *
 * <p>Moves the player immediately to the specified destination tile,
 * typically at a lower tile ID.</p>
 *
 * @see TileAction
 * @since 1.0
 */
public class SnakeAction implements TileAction {

  private final Tile destinationTile;
  private final String description;
  private final int destinationTileId;

  /**
   * Creates a SnakeAction.
   *
   * @param destinationTile the tile to move the player to; may be null, in which case
   *                        {@link #getDestinationTileId()} returns -1
   * @param description     a non-blank description of the action; must not be null or blank
   * @throws IllegalArgumentException if {@code description} is null or blank
   */
  public SnakeAction(Tile destinationTile, String description) {
    ExceptionHandling.requireNonNullOrBlank(description, "description");

    this.destinationTile = destinationTile;
    this.destinationTileId = destinationTile != null
        ? destinationTile.getTileId()
        : -1;
    this.description = description;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Moves the player to this snake’s destination tile.</p>
   *
   * @param player the player to move; must not be null
   * @throws IllegalArgumentException if {@code player} is null
   */
  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "player");
    player.setOnCurrentTile(destinationTile);
  }

  /** {@inheritDoc} */
  @Override
  public ActionType getActionType() {
    return ActionType.SNAKE;
  }

  /** {@inheritDoc} */
  @Override
  public int getDestinationTileId() {
    return destinationTileId;
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return description;
  }
}
