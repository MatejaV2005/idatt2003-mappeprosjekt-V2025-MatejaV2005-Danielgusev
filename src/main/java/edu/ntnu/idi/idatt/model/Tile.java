package edu.ntnu.idi.idatt.model;


import edu.ntnu.idi.idatt.model.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.playertype.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

import java.util.*;

public class Tile {
  private final int tileId;
  private final int row;
  private final int column;

  private Tile nextTile;              // Runtime reference
  private int nextTileId;            // For serialization
  private TileAction landAction;     // Always set, never null

  private final List<Player> players = new ArrayList<>();

  /**
   * Constructs a new Tile with the given ID and position.
   *
   * @param tileId the unique ID of the tile
   * @param row the row position of the tile
   * @param column the column position of the tile
   * @throws IllegalArgumentException if tileId, row, or column is not positive
   */
  public Tile(int tileId, int row, int column) {
    ExceptionHandling.requirePositive(tileId, "tileId");
    ExceptionHandling.requirePositive(row, "row");
    ExceptionHandling.requirePositive(column, "column");

    this.tileId = tileId;
    this.row = row;
    this.column = column;
    this.landAction = NoOperationAction.INSTANCE;
    this.nextTileId = -1;
  }

  /**
   * Returns the tile's unique ID.
   *
   * @return the tile ID
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * Returns the row position of the tile.
   *
   * @return the row index
   */
  public int getRow() {
    return row;
  }

  /**
   * Returns the column position of the tile.
   *
   * @return the column index
   */
  public int getColumn() {
    return column;
  }

  /**
   * Returns the next tile in sequence.
   *
   * @return the next tile
   */
  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * Returns the ID of the next tile.
   *
   * @return the next tile ID
   */
  public int getNextTileId() {
    return nextTileId;
  }

  /**
   * Returns the type of the action performed when a player lands.
   *
   * @return the action type
   */
  public ActionType getActionType() {
    return landAction.getActionType();
  }

  /**
   * Returns the action assigned to this tile.
   *
   * @return the land action
   */
  public TileAction getLandAction() {
    return landAction;
  }

  /**
   * Returns an unmodifiable list of players currently on the tile.
   *
   * @return the list of players
   */
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  /**
   * Sets the next tile and updates the next tile ID accordingly.
   *
   * @param nextTile the next tile
   * @throws NullPointerException if nextTile is null
   */
  public void setNextTile(Tile nextTile) {
    this.nextTile = Objects.requireNonNull(nextTile);
    this.nextTileId = nextTile.getTileId();
  }

  /**
   * Sets the ID of the next tile. Used for serialization.
   *
   * @param nextTileId the ID of the next tile
   */
  public void setNextTileId(int nextTileId) {
    this.nextTileId = nextTileId;
  }

  /**
   * Sets the action to be performed when a player lands on this tile.
   * If null is passed, a no-operation action is used.
   *
   * @param action the tile action to set
   */
  public void setLandAction(TileAction action) {
    this.landAction = (action != null) ? action : NoOperationAction.INSTANCE;
  }

  /**
   * Adds a player to the tile and performs the land action.
   *
   * @param player the player landing on the tile
   * @throws NullPointerException if player is null
   */
  public void landPlayer(Player player) {
    Objects.requireNonNull(player);
    players.add(player);
    landAction.perform(player);
  }

  /**
   * Removes a player from the tile.
   *
   * @param player the player to remove
   * @throws NullPointerException if player is null
   */
  public void leavePlayer(Player player) {
    Objects.requireNonNull(player);
    players.remove(player);
  }

  /**
   * Compares this tile to another object for equality.
   * Tiles are considered equal if they have the same tile ID.
   *
   * @param o the object to compare
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tile)) {
      return false;
    }
    Tile tile = (Tile) o;
    return tileId == tile.tileId;
  }

  /**
   * Returns the hash code for this tile based on its ID.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(tileId);
  }

  /**
   * Returns a string representation of the tile.
   *
   * @return the string representation
   */
  @Override
  public String toString() {
    return "Tile{"
        + "id=" + tileId
        + ", row=" + row
        + ", column=" + column
        + ", nextTileId=" + nextTileId
        + ", players=" + players.size()
        + ", actionType=" + getActionType()
        + '}';
  }
}
