package edu.ntnu.idi.idatt.model.strategy;


import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.List;

/**
 * Strategy implementation for the Astro Rally variant.
 *
 * <p>Encapsulates lap-based movement mechanics, defines turn execution
 * (dice rolling and navigation), enforces win conditions (required laps
 * plus return to finish), and handles game initialization (player placement
 * and state reset).</p>
 *
 * @see GameStrategy
 */
public class AstroRallyStrategy implements GameStrategy {

  /** Number of laps required to win (must land on tile 1 after this many laps). */
  public static final int TOTAL_LAPS_TO_WIN = 2;

  private final Board board;
  private final Dice dice;

  /**
   * Constructs the Astro Rally strategy.
   *
   * @param board the game board; must not be null
   * @param dice  the dice set; must not be null (if empty, initialized to 2 dice)
   * @throws IllegalArgumentException if {@code board} or {@code dice} is null
   */
  public AstroRallyStrategy(Board board, Dice dice) {
    ExceptionHandling.requireNonNull(board, "board");
    ExceptionHandling.requireNonNull(dice, "dice");
    this.board = board;
    this.dice = dice;
    if (this.dice.getNumberOfDice() == 0) {
      this.dice.initializeDice(2);
    }
  }

  /**
   * Calculates final tile after moving a given number of steps, accounting for laps.
   *
   * @param player            the player moving; must not be null
   * @param startTile         the starting tile; must not be null
   * @param totalMovementSteps non-negative number of steps to advance
   * @return the destination tile; never null if board is valid
   * @throws IllegalArgumentException if {@code player} or {@code startTile} is null,
   *                                  or if {@code totalMovementSteps} is negative
   * @throws IllegalStateException    if the board configuration is inconsistent
   */
  private Tile calculateFinalDestination(
      Player player,
      Tile startTile,
      int totalMovementSteps) {
    ExceptionHandling.requireNonNull(player, "player");
    ExceptionHandling.requireNonNull(startTile, "startTile");
    ExceptionHandling.requireNonNegative(totalMovementSteps, "movementSteps");

    if (totalMovementSteps == 0) {
      return startTile;
    }

    int currentTileId = startTile.getTileId();
    ExceptionHandling.requireStrictlyPositive(currentTileId, "startTile ID");

    boolean isOnFinalLap = player.getLapsCompleted() >= TOTAL_LAPS_TO_WIN - 1;

    for (int step = 0; step < totalMovementSteps; step++) {
      Tile tile = board.getTileById(currentTileId);
      ExceptionHandling.requireState(tile != null,
          "Tile ID " + currentTileId + " not found");

      int nextId;
      if (tile.getNextTile() != null) {
        nextId = tile.getNextTile().getTileId();
      } else if (currentTileId == board.getBoardSize()) {
        nextId = 1;
      } else {
        throw new IllegalStateException(
            "Tile " + currentTileId + " has no next and is not last");
      }

      // Lap detection when crossing tile 1 from elsewhere
      if (nextId == 1 && currentTileId != 1) {
        player.incrementLapsCompleted();
        if (player.getLapsCompleted() >= TOTAL_LAPS_TO_WIN && isOnFinalLap) {
          currentTileId = nextId;
          break;
        }
      }

      currentTileId = nextId;
    }

    Tile result = board.getTileById(currentTileId);
    ExceptionHandling.requireState(result != null,
        "Destination tile " + currentTileId + " not found");
    return result;
  }

  /**
   * Rolls dice and moves the player accordingly.
   *
   * @param player the player taking the turn; must not be null
   * @throws IllegalArgumentException if {@code player} is null
   * @throws IllegalStateException    if player's current tile is null
   */
  @Override
  public void executePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "player");
    Tile oldTile = player.getCurrentTile();
    ExceptionHandling.requireState(oldTile != null,
        "Player not on a tile at turn start");

    int steps = dice.roll();
    ExceptionHandling.requireNonNegative(steps, "dice result");
    if (steps == 0) {
      return;
    }

    Tile dest = calculateFinalDestination(player, oldTile, steps);
    if (dest.getTileId() != oldTile.getTileId()) {
      player.setOnCurrentTile(dest);
    }
  }

  /**
   * Determines if the player has won by completing required laps and landing on tile 1.
   *
   * @param player the player to check; must not be null
   * @return true if win conditions met, false otherwise
   * @throws IllegalArgumentException if {@code player} is null
   */
  @Override
  public boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "player");
    Tile tile = player.getCurrentTile();
    return tile != null
        && player.getLapsCompleted() >= TOTAL_LAPS_TO_WIN
        && tile.getTileId() == 1;
  }

  /**
   * Places each player at the starting tile (ID 1), resets laps, and clears skip flag.
   *
   * @param board   the board; must not be null or mismatch strategy's board
   * @param players the players to initialize; must not be null or empty
   * @throws IllegalArgumentException if args invalid
   * @throws IllegalStateException    if start tile missing
   */
  @Override
  public void initializeGame(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "board");
    ExceptionHandling.requireNotEmpty(players, "players");

    Tile start = this.board.getTileById(1);
    if (start == null) {
      throw new IllegalStateException("Missing start tile ID 1");
    }
    for (Player p : players) {
      ExceptionHandling.requireNonNull(p, "player in list");
      p.setOnCurrentTile(start);
      p.resetLapsCompleted();
      p.setSkipTurn(false);
    }
  }
}
