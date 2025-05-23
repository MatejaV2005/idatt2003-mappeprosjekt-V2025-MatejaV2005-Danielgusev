package edu.ntnu.idi.idatt.utils;

import edu.ntnu.idi.idatt.model.core.Player;

/**
 * Represents the data associated with a player's token on a game board.
 *
 * <p>This class encapsulates a {@link Player} object and the ID of the tile
 * that the player's token currently occupies. It provides methods to access
 * and update the current tile ID.
 * </p>
 */
public class PlayerTokenData {
  private final Player player;
  private int currentTileId;

  /**
   * Constructs a new {@code PlayerTokenData} instance.
   *
   * @param player The player associated with this token. Must not be {@code null}.
   * @param currentTileId The initial tile ID where the player's token is located.
   *                      Typically, this should be a valid tile ID on the game board.
   *
   * @throws NullPointerException if the {@code player} argument is {@code null}.
   */
  public PlayerTokenData(Player player, int currentTileId) {
    if (player == null) {
      throw new NullPointerException("Player cannot be null.");
    }
    this.player = player;
    this.setCurrentTileId(currentTileId);
  }

  /**
   * Gets the player associated with this token data.
   *
   * @return The {@link Player} object. This will never be {@code null}.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Gets the current tile ID where the player's token is located.
   *
   * @return The ID of the current tile.
   */
  public int getCurrentTileId() {
    return currentTileId;
  }

  /**
   * Sets the current tile ID for the player's token.
   *
   * @param tileId The new tile ID. This value should correspond to a valid
   *               tile on the game board.
   */
  public void setCurrentTileId(int tileId) {
    this.currentTileId = tileId;
  }
}
