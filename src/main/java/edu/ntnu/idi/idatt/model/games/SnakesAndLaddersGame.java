package edu.ntnu.idi.idatt.model.games;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.Objects;

/**
 * {@code BoardGame} implementation for the classic Snakes & Ladders game.
 *
 * <p>Defines turn sequencing, tile action handling, win condition checks,
 * and strategy-driven initialization specific to Snakes & Ladders.</p>
 *
 * <p>Key hooks:
 * <ul>
 *   <li>{@link #handlePlayerTurn(Player)} – dice roll, movement, and action triggers.</li>
 *   <li>{@link #handleSpecialTileAction(Player, Tile)} – snakes or ladders logic.</li>
 *   <li>{@link #checkWinCondition(Player)} – victory upon reaching final tile.</li>
 *   <li>{@link #initializeGameState()} – placing players on start.</li>
 * </ul>
 * </p>
 *
 * @see GameStrategy
 * @see GameType#SNAKES_AND_LADDERS
 */
public class SnakesAndLaddersGame extends BoardGame {

  /**
   * Creates a new Snakes & Ladders game instance.
   *
   * @param board    the game board; must not be {@code null}
   * @param dice     the dice set; must not be {@code null}
   * @param strategy the strategy implementing Snakes & Ladders rules; must not be {@code null}
   * @throws IllegalArgumentException if any argument is {@code null}
   */
  public SnakesAndLaddersGame(Board board, Dice dice, GameStrategy strategy) {
    super(board, dice, strategy);
  }

  /**
   * Executes one turn: rolls dice, moves player, notifies observers,
   * and applies any landing action (snake or ladder).
   *
   * @param player the current player; must not be {@code null}
   * @throws IllegalArgumentException if {@code player} is {@code null}
   */
  @Override
  protected void handlePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "player");

    Tile origin = player.getCurrentTile();
    gameEngine.playTurn(player);

    Tile destination = player.getCurrentTile();
    ExceptionHandling.requireNonNull(destination, "player's new tile");

    notifyPlayerMoved(player, origin, destination);

    if (destination.isActionTile()) {
      handleSpecialTileAction(player, destination);
    }
  }

  /**
   * Applies the tile's action when landing on a snake or ladder.
   * Moves the player if a destination is specified, otherwise performs the action.
   * Observers are notified of the effect.
   *
   * @param player the player who landed; must not be {@code null}
   * @param actionTile the tile with a special action; must not be {@code null}
   * @throws IllegalArgumentException if any argument is {@code null}
   */
  @Override
  protected void handleSpecialTileAction(Player player, Tile actionTile) {
    ExceptionHandling.requireNonNull(player, "player");
    ExceptionHandling.requireNonNull(actionTile, "actionTile");

    TileAction action = actionTile.getLandAction();

    if (action == null) {
      return;
    }

    int destId = action.getDestinationTileId();

    if (destId > 0) {
      Tile dest = board.getTileById(destId);
      if (dest != null && !Objects.equals(dest, actionTile)) {
        notifyActionTileEffect(player, actionTile, dest);
        actionTile.leavePlayer(player);
        player.setOnCurrentTile(dest);
        dest.landPlayer(player);
      }
    } else {
      Tile before = player.getCurrentTile();
      ExceptionHandling.requireNonNull(before, "player's current tile before action.perform");
      action.perform(player);
      Tile after = player.getCurrentTile();
      ExceptionHandling.requireNonNull(after, "player's current tile after action.perform");

      if (!Objects.equals(after, before)) {
        notifyActionTileEffect(player, actionTile, after);
      } else {
        notifyActionTileEffect(player, actionTile, actionTile);
      }
    }
  }

  /**
   * Determines if the player has reached the final tile and won the game.
   * Notifies observers upon victory.
   *
   * @param player the player to check; must not be {@code null}
   * @return {@code true} if the player has won, {@code false} otherwise
   * @throws IllegalArgumentException if {@code player} is {@code null}
   */
  @Override
  protected boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "player");
    boolean won = gameEngine.isWinner(player);
    if (won) {
      notifyOnGameWon(player);
    }
    return won;
  }

  /**
   * Places all players on the starting tile at game start.
   */
  @Override
  protected void initializeGameState() {
    if (!players.isEmpty()) {
      gameEngine.initializeGame(players);
    }
  }

  /**
   * Returns the game type identifier.
   *
   * @return {@link GameType#SNAKES_AND_LADDERS}
   */
  @Override
  public GameType getGameType() {
    return GameType.SNAKES_AND_LADDERS;
  }
}
