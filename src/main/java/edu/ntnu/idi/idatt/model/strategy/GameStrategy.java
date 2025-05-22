package edu.ntnu.idi.idatt.model.strategy;

import java.util.List;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Player;

/**
 * Defines the contract for game-specific behavior in a board game engine.
 *
 * <p>Strategy implementations control:
 * <ul>
 *   <li>Win condition evaluation ({@link #checkWinCondition(Player)})</li>
 *   <li>Per-turn execution logic ({@link #executePlayerTurn(Player)})</li>
 *   <li>Initial game setup for players ({@link #initializeGame(Board, List)})</li>
 * </ul>
 * </p>
 *
 */
public interface GameStrategy {

  /**
   * Determines whether the specified player has met the victory conditions.
   *
   * @param player the player to evaluate; must not be {@code null}
   * @return {@code true} if the player has won, {@code false} otherwise
   */
  boolean checkWinCondition(Player player);

  /**
   * Executes a single turn for the given player, applying movement, tile effects,
   * and any other game-specific actions.
   *
   * @param player the player whose turn is executing; must not be {@code null}
   */
  void executePlayerTurn(Player player);

  /**
   * Prepares the initial state of the game by placing and resetting all players
   * on the provided board.
   *
   * @param board the game board; must not be {@code null}
   * @param player the list of players to initialize; must not be {@code null} or empty
   */
  void initializeGame(Board board, List<Player> player);
}
