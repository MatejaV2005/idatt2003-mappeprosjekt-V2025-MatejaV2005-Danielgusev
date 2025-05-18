package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling; // Importer din klasse

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single tile on the game board.
 * Each tile has a unique ID, a position (row and column), an optional action
 * that occurs when a player lands on it, and a reference to the next tile in sequence.
 * It also keeps track of players currently occupying the tile.
 */
public class Tile {

  private final int tileId;
  private final int row;
  private final int column;

  private Tile nextTile;
  private int nextTileId;
  private TileAction landAction;

  private final List<Player> playersOnTile = new ArrayList<>();

  /**
   * Constructs a new Tile with the given ID and position (row, column).
   * The tile ID must be strictly positive. Row and column indices must be non-negative.
   * Initially, the tile has no specific land action (defaults to NoOperationAction)
   * and no next tile ID set (defaults to -1, indicating it might be the last tile or unlinked).
   *
   * @param tileId The unique identifier for this tile. Must be strictly positive.
   * @param row    The zero-based row index of this tile on the board. Must be non-negative.
   * @param column The zero-based column index of this tile on the board. Must be non-negative.
   * @throws IllegalArgumentException if tileId is not strictly positive,
   * or if row or column is negative.
   */
  public Tile(int tileId, int row, int column) {
    ExceptionHandling.requireStrictlyPositive(tileId, "tileId");
    ExceptionHandling.requireNonNegative(row, "row");
    ExceptionHandling.requireNonNegative(column, "column");

    this.tileId = tileId;
    this.row = row;
    this.column = column;
    this.landAction = NoOperationAction.INSTANCE;
    this.nextTileId = -1;
  }

  /**
   * Gets the unique identifier of this tile.
   *
   * @return The tile ID.
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * Gets the row index of this tile on the board.
   *
   * @return The zero-based row index.
   */
  public int getRow() {
    return row;
  }

  /**
   * Gets the column index of this tile on the board.
   *
   * @return The zero-based column index.
   */
  public int getColumn() {
    return column;
  }

  /**
   * Gets the next tile in the default sequential path on the board.
   * This is a runtime reference and may be null if this is the last tile
   * or if tiles have not been linked yet.
   *
   * @return The next {@link Tile} object, or {@code null} if there is no next tile.
   */
  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * Sets the next tile in the default sequential path and updates the {@code nextTileId}.
   *
   * @param nextTile The {@link Tile} object that follows this one. Cannot be null.
   * @throws IllegalArgumentException if nextTile is null.
   */
  public void setNextTile(Tile nextTile) {
    ExceptionHandling.requireNonNull(nextTile, "nextTile");
    this.nextTile = nextTile;
    this.nextTileId = nextTile.getTileId();
  }

  /**
   * Gets the ID of the next tile in the default sequential path.
   * This is primarily used for serialization and linking tiles after loading.
   * A value of -1 typically indicates that no next tile ID is set
   * (e.g., for the last tile on the board).
   *
   * @return The ID of the next tile, or -1 if not set.
   */
  public int getNextTileId() {
    return nextTileId;
  }

  /**
   * Sets the ID of the next tile in the default sequential path.
   * This method is typically used during board setup or deserialization
   * before the actual {@link Tile} objects are linked.
   *
   * @param nextTileId The ID of the tile that should follow this one.
   */
  public void setNextTileId(int nextTileId) {
    this.nextTileId = nextTileId;
  }

  /**
   * Gets the action associated with landing on this tile.
   *
   * @return The {@link TileAction} to be performed. Will never be null.
   */
  public TileAction getLandAction() {
    return landAction;
  }

  /**
   * Sets the action to be performed when a player lands on this tile.
   * If a null action is provided, it defaults to {@link NoOperationAction#INSTANCE}.
   *
   * @param action The {@link TileAction} to associate with this tile.
   * Can be null, in which case a no-op action is used.
   */
  public void setLandAction(TileAction action) {
    this.landAction = (action != null) ? action : NoOperationAction.INSTANCE;
  }

  /**
   * Gets the {@link ActionType} of the action associated with this tile.
   *
   * @return The {@link ActionType} of the land action.
   */
  public ActionType getActionType() {
    return landAction.getActionType();
  }

  /**
   * Returns an unmodifiable view of the list of players currently on this tile.
   * Attempting to modify the returned list will result in an
   * {@link UnsupportedOperationException}.
   *
   * @return An unmodifiable list of {@link Player} objects.
   */
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(playersOnTile);
  }

  /**
   * Adds a player to this tile and triggers the tile's land action on that player.
   *
   * @param player The {@link Player} landing on this tile. Cannot be null.
   * @throws IllegalArgumentException if player is null.
   */
  public void landPlayer(Player player) {
    ExceptionHandling.requireNonNull(player, "player landing on tile");
    if (!playersOnTile.contains(player)) {
      playersOnTile.add(player);
    }
    landAction.perform(player);
  }

  /**
   * Removes a player from this tile.
   * Does nothing if the player is not on this tile.
   *
   * @param player The {@link Player} to remove from this tile. Cannot be null.
   * @throws IllegalArgumentException if player is null.
   */
  public void leavePlayer(Player player) {
    ExceptionHandling.requireNonNull(player, "player leaving tile");
    playersOnTile.remove(player);
  }

  /**
   * Checks if this tile has an action other than {@link NoOperationAction}.
   *
   * @return {@code true} if the tile has a specific action, {@code false} otherwise.
   */
  public boolean isActionTile() {
    return landAction.getActionType() != ActionType.NO_OP;
  }

  /**
   * Compares this tile to another object for equality.
   * Two tiles are considered equal if they have the same {@code tileId}.
   *
   * @param o The object to compare with this tile.
   * @return {@code true} if the given object is a Tile with the same ID,
   * {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tile)) {
      return false;
    }
    Tile otherTile = (Tile) o;
    return this.tileId == otherTile.tileId;
  }

  /**
   * Returns a hash code value for the tile, based on its {@code tileId}.
   *
   * @return A hash code value for this tile.
   */
  @Override
  public int hashCode() {
    return Objects.hash(tileId);
  }

  /**
   * Returns a string representation of the tile, including its ID, position,
   * next tile ID, number of players currently on it, and its action type.
   *
   * @return A string representation of this tile.
   */
  @Override
  public String toString() {
    return "Tile{"
        + "id=" + tileId
        + ", row=" + row
        + ", col=" + column
        + ", nextId=" + nextTileId
        + ", playersOn=" + playersOnTile.size()
        + ", action=" + getActionType()
        + '}';
  }
}
