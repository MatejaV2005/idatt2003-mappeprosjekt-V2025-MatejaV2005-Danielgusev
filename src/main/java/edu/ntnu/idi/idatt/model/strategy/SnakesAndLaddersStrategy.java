package edu.ntnu.idi.idatt.model.strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

import java.util.List;
import java.util.logging.Logger;

/**
 * Implements the {@link GameStrategy} for a game of Snakes and Ladders.
 * This strategy defines how a player's turn is executed, how win conditions are checked,
 * and how the game is initialized. It relies on a {@link Dice} object for rolling.
 */
public class SnakesAndLaddersStrategy implements GameStrategy {

  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersStrategy.class.getName());
  private final Dice dice;

  /**
   * Constructs a new SnakesAndLaddersStrategy.
   *
   * @param dice The {@link Dice} to be used for player rolls. Cannot be null.
   * @throws IllegalArgumentException if dice is null.
   */
  public SnakesAndLaddersStrategy(Dice dice) {
    ExceptionHandling.requireNonNull(dice, "Dice for SnakesAndLaddersStrategy");
    this.dice = dice;
  }

  /**
   * Checks if the given player has met the win condition for Snakes and Ladders.
   * The win condition is typically met when the player is on the last tile of the board,
   * which is identified as a tile that has no next tile in sequence.
   *
   * @param player The {@link Player} to check. Cannot be null and must be on a tile.
   * @return {@code true} if the player is on the last tile, {@code false} otherwise.
   * @throws IllegalArgumentException if player is null.
   * @throws IllegalStateException if the player's current tile is null.
   */
  @Override
  public boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for checkWinCondition");
    ExceptionHandling.requireState(player.getCurrentTile() != null,
        "Player " + player.getName() + " must be on a tile to check win condition.");

    Tile currentTile = player.getCurrentTile();
    // The last tile on the board has no next tile.
    return currentTile.getNextTile() == null;
  }

  /**
   * Executes a single turn for the given player in a Snakes and Ladders game.
   * The player rolls the dice, and their piece is moved forward on the board
   * according to the number of steps rolled. The player leaves their old tile
   * and is set on the new tile.
   * <p>
   * Note: This method handles the basic move. Subsequent actions due to landing
   * on a special tile (like a snake or ladder) are typically handled by the
   * {@code BoardGame} class calling {@code Tile.landPlayer()} on the new tile,
   * which in turn triggers {@code TileAction.perform()}.
   * </p>
   *
   * @param player The {@link Player} whose turn it is. Cannot be null and must be on a tile.
   * @throws IllegalArgumentException if player is null.
   * @throws IllegalStateException if the player's current tile is null.
   */
  @Override
  public void executePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for executePlayerTurn");

    Tile oldTile = player.getCurrentTile();
    
    ExceptionHandling.requireState(oldTile != null,
        "Player " + player.getName() + " must be on a tile to execute turn.");

    int steps = dice.roll();

    Tile newTile = player.basicMove(steps);
    ExceptionHandling.requireNonNull(newTile, "New tile calculated by basicMove");


    LOGGER.fine("Player " + player.getName() + " moving from tile "
        + oldTile.getTileId() + " to tile " + newTile.getTileId());

    oldTile.leavePlayer(player);
    player.setOnCurrentTile(newTile);
  }

  /**
   * Initializes the game for Snakes and Ladders.
   * This involves placing all players on the starting tile of the board (tile ID 1).
   *
   * @param board   The {@link Board} to initialize players on. Cannot be null.
   * @param players The list of {@link Player}s participating in the game.
   * Cannot be null. The list itself can be empty, but typically
   * {@code BoardGame.startGame()} would prevent this.
   * @throws IllegalArgumentException if board or players list is null.
   * @throws IllegalStateException if the starting tile (ID 1) cannot be found on the board.
   */
  @Override
  public void initializeGame(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "Board for game initialization");
    ExceptionHandling.requireNonNull(players, "Players list for game initialization");
    ExceptionHandling.requireNotEmpty(players, "Players list for game initialization");

    Tile startingTile = board.getTileById(1);
    ExceptionHandling.requireState(startingTile != null,
        "Starting tile (ID 1) not found on board during game initialization.");

    for (Player p : players) {
      if (p != null) {
        p.setOnCurrentTile(startingTile);
        LOGGER.finer("Placed player " + p.getName() + " on starting tile: " + startingTile.getTileId());
      }
    }
  }
}