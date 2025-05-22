package edu.ntnu.idi.idatt.model.strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.List;

/**
 * Implements the game strategy for "Astro Rally".
 * This strategy defines how player turns are executed, how movement is calculated,
 * win conditions are checked, and how the game is initialized.
 * Astro Rally involves players completing a set number of laps around the board.
 */
public class AstroRallyStrategy implements GameStrategy {

  private final Board board;
  private final Dice dice;

  /**
   * The total number of laps a player must complete to be eligible to win.
   * The player must also be on the finish tile (tile ID 1) after completing these laps.
   */
  public static final int TOTAL_LAPS_TO_WIN = 1;

  /**
   * Constructs a new AstroRallyStrategy.
   *
   * @param board The game board. Must not be {@code null}.
   * @param dice The dice used for movement. Must not be {@code null}.
   * If the provided dice has zero dice, it will be initialized with 2 dice.
   * @throws IllegalArgumentException if {@code board} or {@code dice} is {@code null}.
   */
  public AstroRallyStrategy(Board board, Dice dice) {
    ExceptionHandling.requireNonNull(board, "Board cannot be null for AstroRallyStrategy");
    ExceptionHandling.requireNonNull(dice, "Dice cannot be null for AstroRallyStrategy");

    this.board = board;
    this.dice = dice;
    if (this.dice.getNumberOfDice() == 0) {
      this.dice.initializeDice(2);
    }
  }

  /**
   * Calculates the player's final destination tile after moving a certain number of steps.
   * This method handles lap counting by checking if the player passes the starting tile (ID 1).
   * If the player is on their winning lap, movement stops once they reach or pass tile ID 1.
   *
   * @param player The player who is moving. Must not be {@code null}.
   * @param startTile The tile from which the player starts their movement. Must not be {@code null}.
   * @param totalMovementSteps The total number of steps the player is to move. Must be non-negative.
   * @return The {@link Tile} object representing the player's final destination.
   * Returns the {@code startTile} if {@code totalMovementSteps} is 0.
   * Returns {@code null} if a tile in the path cannot be found (should not happen on a valid board).
   * @throws IllegalArgumentException if {@code player}, {@code startTile} is {@code null},
   * or if {@code totalMovementSteps} is negative.
   * @throws IllegalStateException if the {@code startTile} has no valid ID or
   * if a tile in the movement path is unexpectedly {@code null} or has no next tile
   * when one is expected (indicative of a board configuration issue).
   */
  private Tile calculateFinalDestination(Player player, Tile startTile, int totalMovementSteps) {
    ExceptionHandling.requireNonNull(player, "Player for calculateFinalDestination");
    ExceptionHandling.requireNonNull(startTile, "Start tile for calculateFinalDestination");
    ExceptionHandling.requireNonNegative(totalMovementSteps, "Total movement steps");

    if (totalMovementSteps == 0) {
      return startTile;
    }

    int currentTileId = startTile.getTileId();
    ExceptionHandling.requireStrictlyPositive(currentTileId, "Start tile ID");

    int previousTileIdInLoop;
    boolean isOnWinningLap = player.getLapsCompleted() == TOTAL_LAPS_TO_WIN - 1;

    for (int step = 0; step < totalMovementSteps; step++) {
      Tile loopCurrentTile = this.board.getTileById(currentTileId);
      ExceptionHandling.requireState(loopCurrentTile != null,
          "Current tile (ID: " + currentTileId + ") not found on board during movement calculation.");

      int nextTileIdCandidate;

      if (loopCurrentTile.getNextTile() != null) {
        nextTileIdCandidate = loopCurrentTile.getNextTile().getTileId();
        ExceptionHandling.requireStrictlyPositive(nextTileIdCandidate, "Next tile ID from tile " + currentTileId);
      } else if (currentTileId == this.board.getBoardSize()) {
        nextTileIdCandidate = 1;
      } else {
        throw new IllegalStateException(
            "Tile (ID: " + currentTileId + ") has no next tile and is not the last tile on the board.");
      }

      previousTileIdInLoop = currentTileId;
      currentTileId = nextTileIdCandidate;

      if (currentTileId == 1 && previousTileIdInLoop != 1 && previousTileIdInLoop != this.board.getBoardSize()) {
        player.incrementLapsCompleted();
        isOnWinningLap = player.getLapsCompleted() == TOTAL_LAPS_TO_WIN - 1;
      }

      if (isOnWinningLap && currentTileId == 1 && previousTileIdInLoop != 1) {
        break;
      }
    }
    Tile finalDestination = this.board.getTileById(currentTileId);
    ExceptionHandling.requireState(finalDestination != null,
        "Final destination tile (ID: " + currentTileId + ") not found on board.");
    return finalDestination;
  }

  /**
   * Executes a single turn for the given player.
   * The player rolls the dice, and their piece is moved on the board according to the
   * rolled value and the {@link #calculateFinalDestination(Player, Tile, int)} logic.
   * Lap counting is handled during movement.
   *
   * @param player The player whose turn it is. Must not be {@code null}.
   * @throws IllegalArgumentException if {@code player} is {@code null}.
   * @throws IllegalStateException if the player's current tile is {@code null} before the turn
   * (player should always be on a tile).
   */
  @Override
  public void executePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null for executePlayerTurn");

    Tile oldTile = player.getCurrentTile();
    ExceptionHandling.requireState(oldTile != null,
        "Player " + player.getName() + " is not on any tile at the start of their turn.");

    int movementSteps = dice.roll();
    ExceptionHandling.requireNonNegative(movementSteps, "Dice roll result (movement steps)");


    if (movementSteps <= 0) {
      return;
    }

    Tile destinationTile = calculateFinalDestination(player, oldTile, movementSteps);

    if (destinationTile.getTileId() != oldTile.getTileId()) {
      player.setOnCurrentTile(destinationTile);
    }
  }

  /**
   * Checks if the specified player has met the win condition for Astro Rally.
   * A player wins if they have completed the required number of laps ({@value #TOTAL_LAPS_TO_WIN})
   * AND are currently on the finish tile (tile ID 1).
   *
   * @param player The player to check. Must not be {@code null}.
   * @return {@code true} if the player has won, {@code false} otherwise.
   * @throws IllegalArgumentException if {@code player} is {@code null}.
   */
  @Override
  public boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null when checking win condition");

    Tile currentTile = player.getCurrentTile();
    if (currentTile == null) {
      return false;
    }

    boolean hasCompletedRequiredLaps = player.getLapsCompleted() >= TOTAL_LAPS_TO_WIN;
    boolean isOnFinishTile = currentTile.getTileId() == 1;

    return hasCompletedRequiredLaps && isOnFinishTile;
  }

  /**
   * Initializes the game state for all participating players.
   * Each player is placed on the starting tile (tile ID 1), their lap count is reset,
   * and any "skip turn" status is cleared.
   *
   * @param board The game board, which should be the same as the one this strategy was constructed with.
   * Must not be {@code null}.
   * @param players A list of players participating in the game. Must not be {@code null} or empty,
   * and must not contain {@code null} players.
   * @throws IllegalArgumentException if {@code board} or {@code players} list is {@code null} or empty,
   * or if any player in the list is {@code null}.
   * @throws IllegalStateException if the starting tile (ID 1) cannot be found on the board.
   */
  @Override
  public void initializeGame(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "Board cannot be null for InitializeGame");
    ExceptionHandling.requireNotEmpty(players, "Players list cannot be null or empty for InitializeGame");

    Tile startingTile = this.board.getTileById(1);
    if (startingTile == null) {
      throw new IllegalStateException("Astro Rally board is missing the starting tile (ID 1).");
    }

    for (Player player : players) {
      ExceptionHandling.requireNonNull(player, "Player in list cannot be null during game initialization");
      player.setOnCurrentTile(startingTile);
      player.resetLapsCompleted();
      player.setSkipTurn(false);
    }
  }
}
