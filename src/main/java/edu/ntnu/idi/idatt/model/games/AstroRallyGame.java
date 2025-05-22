package edu.ntnu.idi.idatt.model.games;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.strategy.AstroRallyStrategy;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.Objects;

/**
 * Concrete implementation of {@link BoardGame} for the Astro Rally game variant.
 *
 * <p>Uses an {@link AstroRallyStrategy} to manage turn execution, win
 * conditions, and game initialization logic. Handles player movement,
 * lap counting, and special tile effects unique to Astro Rally.</p>
 *
 * @see AstroRallyStrategy
 * @see BoardGame
 */
public class AstroRallyGame extends BoardGame {

  /**
   * Creates a new AstroRallyGame instance.
   *
   * @param board    the game board to use; must not be null
   * @param dice     the dice mechanism; must not be null
   * @param strategy the strategy implementing Astro Rally rules;
   *                 must be an instance of {@link AstroRallyStrategy}
   * @throws IllegalArgumentException if {@code strategy} is not an AstroRallyStrategy
   */
  public AstroRallyGame(Board board, Dice dice, GameStrategy strategy) {
    super(board, dice, strategy);
    if (!(strategy instanceof AstroRallyStrategy)) {
      throw new IllegalArgumentException(
          "AstroRallyGame requires an AstroRallyStrategy instance.");
    }
  }

  @Override
  protected void handlePlayerTurn(Player player) {
    Objects.requireNonNull(player, "player cannot be null for handlePlayerTurn");
    Tile oldTile = player.getCurrentTile();
    if (oldTile == null) {
      return;
    }

    gameEngine.playTurn(player);
    Tile newTile = player.getCurrentTile();
    if (newTile == null) {
      player.setOnCurrentTile(oldTile);
      newTile = oldTile;
    }

    boolean moved = oldTile.getTileId() != newTile.getTileId();
    if (moved) {
      oldTile.leavePlayer(player);
      newTile.landPlayer(player);
    }

    notifyPlayerMoved(player, oldTile, newTile);
    handleLapCompletion(player, oldTile, newTile, moved);

    if (newTile.isActionTile()) {
      handleSpecialTileAction(player, newTile);
    }
  }

  private void handleLapCompletion(
      Player player,
      Tile oldTile,
      Tile newTile,
      boolean moved) {
    if (!moved || board.getBoardSize() <= 0) {
      return;
    }

    int size = board.getBoardSize();
    boolean crossedMidToLow =
        oldTile.getTileId() > size / 2 && newTile.getTileId() < size / 2;
    boolean idDecreased = oldTile.getTileId() > newTile.getTileId();

    if ((crossedMidToLow && idDecreased)
        || (!crossedMidToLow && idDecreased && newTile.getTileId() == 1)) {
      player.incrementLapsCompleted();
    }
  }

  @Override
  protected void handleSpecialTileAction(Player player, Tile triggerTile) {
    ExceptionHandling.requireNonNull(player, "player cannot be null");
    ExceptionHandling.requireNonNull(triggerTile, "triggerTile cannot be null");

    TileAction action = triggerTile.getLandAction();
    if (action.getActionType() == ActionType.NO_OP) {
      notifyActionTileEffect(player, triggerTile, triggerTile);
      return;
    }

    Tile before = player.getCurrentTile();
    action.perform(player);
    Tile after = player.getCurrentTile();

    if (after != null && after.getTileId() != before.getTileId()) {
      before.leavePlayer(player);
      after.landPlayer(player);
      notifyPlayerMoved(player, before, after);
      notifyActionTileEffect(player, triggerTile, after);
    } else {
      notifyActionTileEffect(player, triggerTile, triggerTile);
    }
  }

  @Override
  protected boolean checkWinCondition(Player player) {
    Objects.requireNonNull(player, "player cannot be null for checkWinCondition");
    boolean winner = gameEngine.isWinner(player);
    if (winner) {
      notifyOnGameWon(player);
    }
    return winner;
  }

  @Override
  protected void initializeGameState() {
    if (!players.isEmpty()) {
      gameEngine.initializeGame(players);
    }
  }

  @Override
  public GameType getGameType() {
    return GameType.ASTRO_RALLY;
  }
}
