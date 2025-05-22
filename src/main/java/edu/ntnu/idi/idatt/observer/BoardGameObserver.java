package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;

/**
 * Observer interface for receiving events from a {@link Observable} board game.
 *
 * <p>Implementations of this interface can register with a {@link Observable}
 * to be notified of game events such as player movements, additions,
 * win notifications, action tile effects, and general state updates.</p>
 *
 */
public interface BoardGameObserver {

  /**
   * Invoked when a player moves from one tile to another.
   *
   * @param player the {@link Player} who moved; never null
   * @param from   the source {@link Tile}; never null
   * @param to     the destination {@link Tile}; never null
   */
  void onPlayerMoved(Player player, Tile from, Tile to);

  /**
   * Invoked when a new player is added to the game.
   *
   * @param player the {@link Player} added; never null
   */
  void onPlayerAdded(Player player);

  /**
   * Invoked when a player has won the game.
   *
   * @param player the winning {@link Player}; never null
   */
  void onGameWon(Player player);

  /**
   * Invoked after a player lands on a special tile and its action has been applied.
   *
   * @param player              the {@link Player} affected; never null
   * @param fromActionTile      the action-triggering {@link Tile}; never null
   * @param toDestinationTile   the tile where the player ends up after action; never null
   */
  void onActionTileEffect(Player player, Tile fromActionTile, Tile toDestinationTile);

  /**
   * Invoked when the overall game state is updated, e.g., after each turn.
   *
   * @param game the {@link BoardGame} whose state has changed; never null
   */
  void onGameStateUpdated(BoardGame game);
}
