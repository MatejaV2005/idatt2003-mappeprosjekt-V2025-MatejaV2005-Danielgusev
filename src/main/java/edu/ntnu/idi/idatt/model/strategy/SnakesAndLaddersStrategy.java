package edu.ntnu.idi.idatt.model.strategy;


import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.List;
import java.util.logging.Logger;

/**
 * {@link GameStrategy} implementation for Snakes &amp; Ladders.
 *
 * <p>Manages turn execution (dice roll and basic movement),
 * win condition checking (landing on final tile), and initial
 * player placement on the starting tile.</p>
 *
 * @see GameStrategy
 * @since 1.0
 */
public class SnakesAndLaddersStrategy implements GameStrategy {

  private static final Logger LOGGER =
      Logger.getLogger(SnakesAndLaddersStrategy.class.getName());

  private final Dice dice;

  /**
   * Creates a new strategy using the specified dice.
   *
   * @param dice the {@link Dice} used for movement; must not be {@code null}
   * @throws IllegalArgumentException if {@code dice} is {@code null}
   */
  public SnakesAndLaddersStrategy(Dice dice) {
    ExceptionHandling.requireNonNull(dice, "Dice for SnakesAndLaddersStrategy");
    this.dice = dice;
  }

  /**
   * {@inheritDoc}
   *
   * <p>A player wins by landing on the final tile (with no next tile).
   * Throws if the player's current tile is not set.</p>
   */
  @Override
  public boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for checkWinCondition");
    Tile current = player.getCurrentTile();
    ExceptionHandling.requireState(
        current != null,
        "Player '" + player.getName() + "' must be on a tile to check win");
    return current.getNextTile() == null;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Rolls the dice and advances the player by the rolled amount.
   * Observers and special tile effects are handled by {@code BoardGame}.</p>
   */
  @Override
  public void executePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for executePlayerTurn");
    Tile origin = player.getCurrentTile();
    ExceptionHandling.requireState(
        origin != null,
        "Player '" + player.getName() + "' must be on a tile to take a turn");

    int steps = dice.roll();
    if (steps <= 0) {
      return;
    }

    Tile dest = player.basicMove(steps);
    ExceptionHandling.requireNonNull(dest, "Destination tile from basicMove");

    LOGGER.fine(
        "Player '" + player.getName() + "' moves from tile "
            + origin.getTileId() + " to " + dest.getTileId());

    origin.leavePlayer(player);
    player.setOnCurrentTile(dest);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Places all players on the starting tile (ID 1).
   * Resets each player's skip-turn status.</p>
   */
  @Override
  public void initializeGame(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "Board for initializeGame");
    ExceptionHandling.requireNotEmpty(
        players, "Players list for initializeGame");

    Tile start = board.getTileById(1);
    ExceptionHandling.requireState(
        start != null, "Starting tile (ID 1) not found on board");

    for (Player p : players) {
      ExceptionHandling.requireNonNull(p, "Player in list");
      p.setOnCurrentTile(start);
      p.setSkipTurn(false);
      LOGGER.finer(
          "Initialized player '" + p.getName() + "' on starting tile");
    }
  }
}
