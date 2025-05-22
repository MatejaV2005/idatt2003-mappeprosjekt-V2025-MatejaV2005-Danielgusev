package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * Represents a tile action where a player lands on a ladder.
 * This action immediately moves the player to the ladder's destination tile.
 */
public class LadderAction implements TileAction {
  private final Tile destinationTile;
  private final String description;
  private final int destinationTileId;

  /**
   * Constructs a LadderAction.
   *
   * @param destinationTile The destination tile the ladder leads to.
   * If null, {@link #getDestinationTileId()} will return -1,
   * and performing the action might lead to an exception
   * depending on the Player class's handling of setting a null current tile.
   * @param description A description of the ladder action (e.g., "Climbed up the ladder!").
   * Cannot be null or blank.
   * @throws IllegalArgumentException if {@code description} is null or blank.
   */
  public LadderAction(Tile destinationTile, String description) {
    ExceptionHandling.requireNonNullOrBlank(description, "Description for LadderAction");
    this.destinationTile = destinationTile;
    this.destinationTileId = destinationTile != null ? destinationTile.getTileId() : -1;
    this.description = description;
  }

  /**
   * Performs the ladder action on the given player.
   * The player's current tile is set to this ladder's {@code destinationTile}.
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
    ExceptionHandling.requireNonNull(player, "Player for LadderAction");
    System.out.println(player.getName() + " " + description);
    player.setOnCurrentTile(destinationTile);
  }

  /**
   * Gets the type of this action.
   *
   * @return {@link ActionType#LADDER}.
   */
  @Override
  public ActionType getActionType() {
    return ActionType.LADDER;
  }

  /**
   * Gets the destination tile ID for this ladder action.
   *
   * @return The ID of the destination tile, or -1 if the destination tile was null
   * during construction.
   */
  @Override
  public int getDestinationTileId() {
    return this.destinationTileId;
  }

  /**
   * Gets the description of this ladder action.
   *
   * @return The description string.
   */
  @Override
  public String getDescription() {
    return this.description;
  }
}
