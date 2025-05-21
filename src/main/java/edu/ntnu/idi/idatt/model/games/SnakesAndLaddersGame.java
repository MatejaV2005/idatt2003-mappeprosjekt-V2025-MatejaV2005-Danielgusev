package edu.ntnu.idi.idatt.model.games;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;

public class SnakesAndLaddersGame extends BoardGame {

  public SnakesAndLaddersGame(Board board, Dice dice, GameStrategy SnakesAndLaddersStrategy) {
    super(board, dice, SnakesAndLaddersStrategy);
  }

  @Override
  protected void handlePlayerTurn(Player player) {
    Tile oldTile = player.getCurrentTile();

    gameEngine.playTurn(player);

    Tile newTile = player.getCurrentTile();

    notifyPlayerMoved(player, oldTile, newTile);

    if (newTile.isActionTile()) {
      handleSpecialTileAction(player, newTile);
    }
  }

  @Override
  protected void handleSpecialTileAction(Player player, Tile actionTile) {
    TileAction landAction = actionTile.getLandAction();
    if (landAction == null) {
      return;
    }

    int destId = landAction.getDestinationTileId();

    if (destId > 0) {
      Tile destinationTile = board.getTileById(destId);
      if (destinationTile != null && !destinationTile.equals(actionTile)) {
        notifyActionTileEffect(player, actionTile, destinationTile);

        actionTile.leavePlayer(player);
        player.setOnCurrentTile(destinationTile);
        destinationTile.landPlayer(player);
      }
    } else {

      Tile tileBeforePerformingAction = player.getCurrentTile();
      landAction.perform(player);
      Tile tileAfterPerformingAction = player.getCurrentTile();

      if (!tileAfterPerformingAction.equals(tileBeforePerformingAction)) {
        notifyActionTileEffect(player, actionTile, tileAfterPerformingAction);
      } else {
        notifyActionTileEffect(player, actionTile, actionTile);
      }
    }
  }

  @Override
  protected boolean checkWinCondition(Player player) {
    boolean isWinner = gameEngine.isWinner(player);
    if (isWinner) {
      notifyOnGameWon(player);
    }
    return isWinner;
  }


  @Override
  protected void initializeGameState() {
    if (players != null && !players.isEmpty() && board != null && gameEngine != null) {
      gameEngine.initializeGame(players); // Ensures players are on the start tile
    }
  }

  @Override
  public GameType getGameType() {
    return GameType.SNAKES_AND_LADDERS;
  }
}