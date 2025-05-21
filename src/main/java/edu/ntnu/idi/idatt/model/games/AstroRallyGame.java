package edu.ntnu.idi.idatt.model.games;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.AstroRallyStrategy;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.Objects;

public class AstroRallyGame extends BoardGame {

  public AstroRallyGame(Board board, Dice dice, GameStrategy strategy) {
    super(board, dice, strategy);
    if (!(strategy instanceof AstroRallyStrategy)) {
      throw new IllegalArgumentException("AstroRallyGame requires an AstroRallyStrategy instance.");
    }
  }

  @Override
  protected void handlePlayerTurn(Player player) {
    Objects.requireNonNull(player, "Player cannot be null for handlePlayerTurn");
    Tile oldTile = player.getCurrentTile();
    if (oldTile == null) {
      return;
    }

    gameEngine.playTurn(player);

    Tile newTileAfterPrimaryMove = player.getCurrentTile();
    if (newTileAfterPrimaryMove == null) {
      player.setOnCurrentTile(oldTile);
      newTileAfterPrimaryMove = oldTile;
    }

    boolean playerPhysicallyMovedPrimary = oldTile.getTileId() != newTileAfterPrimaryMove.getTileId();

    if (playerPhysicallyMovedPrimary) {
      oldTile.leavePlayer(player);
      newTileAfterPrimaryMove.landPlayer(player);
    }

    notifyPlayerMoved(player, oldTile, newTileAfterPrimaryMove);
    handleLapCompletion(player, oldTile, newTileAfterPrimaryMove, playerPhysicallyMovedPrimary);

    if (newTileAfterPrimaryMove.isActionTile()) {
      handleSpecialTileAction(player, newTileAfterPrimaryMove);
    }
  }

  private void handleLapCompletion(
      Player player, Tile oldTile, Tile newTile, boolean playerMoved) {
    if (!playerMoved || this.board.getBoardSize() <= 0) {
      return;
    }

    boolean crossedMidPointToLow =
        oldTile.getTileId() > this.board.getBoardSize() / 2
            && newTile.getTileId() < this.board.getBoardSize() / 2;
    boolean idDecreased = oldTile.getTileId() > newTile.getTileId();

    if (crossedMidPointToLow && idDecreased) {
      player.incrementLapsCompleted();
    } else if (!crossedMidPointToLow && idDecreased && newTile.getTileId() == 1) {
      player.incrementLapsCompleted();
    }
  }

  @Override
  protected void handleSpecialTileAction(Player player, Tile triggerActionTile) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    ExceptionHandling.requireNonNull(triggerActionTile, "Action Tile cannot be null");

    TileAction landAction = triggerActionTile.getLandAction();
    if (landAction == null || landAction.getActionType() == ActionType.NO_OP) {
      notifyActionTileEffect(player, triggerActionTile, triggerActionTile);
      return;
    }

    Tile tileBeforeAction = player.getCurrentTile();

    landAction.perform(player);

    Tile tileAfterAction = player.getCurrentTile();

    if (tileAfterAction != null && tileAfterAction.getTileId() != tileBeforeAction.getTileId()) {
      tileBeforeAction.leavePlayer(player);
      tileAfterAction.landPlayer(player);

      notifyPlayerMoved(player, tileBeforeAction, tileAfterAction);
      notifyActionTileEffect(player, triggerActionTile, tileAfterAction);
    } else {
      notifyActionTileEffect(player, triggerActionTile, triggerActionTile);
    }
  }

  @Override
  protected boolean checkWinCondition(Player player) {
    Objects.requireNonNull(player, "Player cannot be null for checkWinCondition");
    boolean isWinner = gameEngine.isWinner(player);
    if (isWinner) {
      notifyOnGameWon(player);
    }
    return isWinner;
  }

  @Override
  protected void initializeGameState() {
    if (this.players != null && !this.players.isEmpty() && this.board != null && this.gameEngine != null) {
      this.gameEngine.initializeGame(this.players);
    }
  }

  @Override
  public GameType getGameType() {
    return GameType.ASTRO_RALLY;
  }
}