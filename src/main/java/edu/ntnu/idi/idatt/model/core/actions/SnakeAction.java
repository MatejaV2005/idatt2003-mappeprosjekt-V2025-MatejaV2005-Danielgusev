package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;


/**
 * Represents a tile action where a player lands on a snake.
 * This action immediately moves the player to the snake's destination tile,
 * which is typically a tile with a lower ID.
 */
public class SnakeAction implements TileAction {
  private final Tile destinationTile;
  private final String description;
  private final int destinationTileId;

  /**
   * Constructs a SnakeAction.
   *
   * @param destinationTile The destination tile the snake leads to.
   * If null, {@link #getDestinationTileId()} will return -1,
   * and performing the action might lead to an exception
   * depending on the Player class's handling of setting a null current tile.
   * @param description A description of the snake action (e.g., "Slid down the snake!").
   * Cannot be null or blank.
   * @throws IllegalArgumentException if {@code description} is null or blank.
   */
  public SnakeAction(Tile destinationTile, String description) {
    ExceptionHandling.requireNonNullOrBlank(description, "Description for SnakeAction");
    this.destinationTile = destinationTile;
    this.destinationTileId = destinationTile != null ? destinationTile.getTileId() : -1;
    this.description = description;
  }


  /**
   * Performs the snake action on the given player.
   * The player's current tile is set to this snake's {@code destinationTile}.
   * A message indicating the action is printed to standard output.
   *
   * @param player The player performing the action. Cannot be null.
   * @throws IllegalArgumentException if {@code player} is null.
   * May also throw an exception from {@code player.setOnCurrentTile()}
   * if {@code destinationTile} was null during construction and
   * the Player class does not allow setting a null current tile.
   */
  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for SnakeAction");
    player.setOnCurrentTile(destinationTile);
  }

  /**
   * Gets the type of this action.
   *
   * @return {@link ActionType#SNAKE}.
   */
  @Override
  public ActionType getActionType() {
    return ActionType.SNAKE;
  }

  /**
   * Gets the destination tile ID for this snake action.
   *
   * @return The ID of the destination tile, or -1 if the destination tile was null
   * during construction.
   */
  @Override
  public int getDestinationTileId() {
    return this.destinationTileId;
  }

  /**
   * Gets the description of this snake action.
   *
   * @return The description string.
   */
  @Override
  public String getDescription() {
    return this.description;
  }
}
