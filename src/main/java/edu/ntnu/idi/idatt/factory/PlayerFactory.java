package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.Player;

/**
 * Factory for creating {@link Player} instances.
 *
 * <p>Provides methods to instantiate players with or without an initial
 * tile placement. Suitable for initializing game sessions or loading
 * saved players.
 *
 * @see Player
 */
public class PlayerFactory {

  private PlayerFactory() {
  }


  /**
   * Creates a new player with the specified name and piece type, without setting a tile.
   *
   * <p>Use this overload when tile placement is not yet known or will be set later.
   *
   * @param name the player's unique name; must not be null or blank
   * @param pieceType the identifier or color of the player's piece; must not be null or blank
   * @return a new {@link Player} without initial tile placement
   */
  public static Player createPlayer(String name, String pieceType) {
    return new Player(name, pieceType);
  }
}
