package edu.ntnu.idi.idatt.model.strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.List;
import java.util.Objects;

public class AstroRallyStrategy implements GameStrategy {

  private final Board board;
  private final Dice dice;
  public static final int TOTAL_LAPS_TO_WIN = 1;

  public AstroRallyStrategy(Board board, Dice dice) {
    ExceptionHandling.requireNonNull(board, "Board cannot be null for AstroRallyStrategy");
    ExceptionHandling.requireNonNull(dice, "Dice cannot be null for AstroRallyStrategy");

    this.board = board;
    this.dice = dice;
    if (this.dice.getNumberOfDice() == 0) {
      this.dice.addDice(2);
    }
  }


  private Tile calculateFinalDestination(Player player, Tile startTile, int totalMovementSteps) {
    int currentTileId = startTile.getTileId();
    int previousTileIdInLoop;
    boolean isOnWinningLap = player.getLapsCompleted() == TOTAL_LAPS_TO_WIN - 1;

    for (int step = 0; step < totalMovementSteps; step++) {
      Tile loopCurrentTile = this.board.getTileById(currentTileId);
      int nextTileIdCandidate;

      if (loopCurrentTile != null && loopCurrentTile.getNextTile() != null) {
        nextTileIdCandidate = loopCurrentTile.getNextTile().getTileId();
      } else if (loopCurrentTile != null && currentTileId == this.board.getBoardSize()) {
        nextTileIdCandidate = 1;
      } else {
        break;
      }

      previousTileIdInLoop = currentTileId;
      currentTileId = nextTileIdCandidate;

      if (isOnWinningLap && currentTileId == 1 && previousTileIdInLoop != 1) {
        break;
      }
    }
    return this.board.getTileById(currentTileId);
  }

  @Override
  public void executePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null for executePlayerTurn");

    Tile oldTile = player.getCurrentTile();
    if (oldTile == null) {
      return;
    }

    int movementSteps = dice.roll();

    if (movementSteps <= 0) {
      return;
    }

    Tile destinationTile = calculateFinalDestination(player, oldTile, movementSteps);

    if (destinationTile != null && destinationTile.getTileId() != oldTile.getTileId()) {
      player.setOnCurrentTile(destinationTile);
    }
  }

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

  @Override
  public Player determineWinner(List<Player> players) {
    Objects.requireNonNull(players, "Players list cannot be null for determineWinner");
    for (Player player : players) {
      if (checkWinCondition(player)) {
        return player;
      }
    }
    return null;
  }

  @Override
  public void InitializeGame(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "Board cannot be null for InitializeGame");
    ExceptionHandling.requireNonNull(players, "Players list cannot be null for InitializeGame");

    Tile startingTile = this.board.getTileById(1);
    if (startingTile == null) {
      throw new IllegalStateException("Astro Rally board is missing the starting tile (ID 1).");
    }

    for (Player player : players) {
      player.setOnCurrentTile(startingTile);
      player.resetLapsCompleted();
      player.setSkipTurn(false);
    }
  }
}