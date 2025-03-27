package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.playertype.Player;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Represents a tile in a board game, with unique identification,
 * positional information, and potential actions.
 */
public class Tile {
  // Immutable properties
  private final int tileId;
  private final int row;
  private final int column;

  // Mutable properties
  private Tile nextTile;
  private TileAction landAction;
  private final List<Player> players;

  /**
   * Constructs a new Tile with required positional information.
   *
   * @param tileId Unique identifier for the tile
   * @param row Row position of the tile
   * @param column Column position of the tile
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public Tile(int tileId, int row, int column) {
    // Validate input parameters
    ExceptionHandling.requirePositive(tileId, "tileId");
    ExceptionHandling.requirePositive(row, "row");
    ExceptionHandling.requirePositive(column, "column");

    // Initialize immutable fields
    this.tileId = tileId;
    this.row = row;
    this.column = column;

    // Initialize mutable fields
    this.players = new ArrayList<>();
    this.landAction = TileAction.NO_OP; // Default no-op action
  }

  /**
   * Retrieves the next tile in the sequence.
   *
   * @return The next tile, or null if not set
   */
  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * Retrieves the unique tile identifier.
   *
   * @return The tile's unique ID
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * Retrieves the row position of the tile.
   *
   * @return The tile's row position
   */
  public int getRow() {
    return row;
  }

  /**
   * Retrieves the column position of the tile.
   *
   * @return The tile's column position
   */
  public int getColumn() {
    return column;
  }

  /**
   * Retrieves the land action for this tile.
   *
   * @return Optional containing the land action
   */
  public Optional<TileAction> getLandAction() {
    return landAction == TileAction.NO_OP ? Optional.empty() : Optional.of(landAction);
  }

  /**
   * Retrieves an unmodifiable list of players on this tile.
   *
   * @return List of players on the tile
   */
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  /**
   * Sets the land action for the tile.
   *
   * @param action The tile action to set
   * @throws NullPointerException if action is null
   */
  public void setLandAction(TileAction action) {
    this.landAction = action != null ? action : TileAction.NO_OP;
  }

  /**
   * Sets the next tile in the sequence.
   *
   * @param nextTile The next tile to set
   * @throws NullPointerException if nextTile is null
   */
  public void setNextTile(Tile nextTile) {
    this.nextTile = Objects.requireNonNull(nextTile, "Next tile cannot be null");
  }

  /**
   * Adds a player to the tile and performs the land action.
   *
   * @param player The player landing on the tile
   * @throws NullPointerException if player is null
   */
  public void landPlayer(Player player) {
    Objects.requireNonNull(player, "Player cannot be null");

    // Add player to the tile
    players.add(player);

    // Perform land action if exists
    landAction.perform(player);
  }

  /**
   * Removes a player from the tile.
   *
   * @param player The player leaving the tile
   * @throws NullPointerException if player is null
   */
  public void leavePlayer(Player player) {
    Objects.requireNonNull(player, "Player cannot be null");
    players.remove(player);
  }

  /**
   * Provides equality comparison based on tile ID.
   *
   * @param o The object to compare
   * @return true if tiles have the same ID, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tile tile = (Tile) o;
    return tileId == tile.tileId;
  }

  /**
   * Generates a hash code based on tile ID.
   *
   * @return The hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(tileId);
  }

  /**
   * Provides a string representation of the tile.
   *
   * @return A descriptive string about the tile
   */
  @Override
  public String toString() {
    return "Tile{" +
        "id=" + tileId +
        ", row=" + row +
        ", column=" + column +
        ", players=" + players.size() +
        '}';
  }
}