package edu.ntnu.idi.idatt.model.games;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;

public class SnakesAndLaddersGame extends BoardGame {

  public SnakesAndLaddersGame(Board board, Dice dice, GameStrategy SnakesAndLaddersStrategy) {
    super(board, dice, SnakesAndLaddersStrategy);
    gameEngine.initializeGame(players);
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
    Tile destinationTile = null;

    if (actionTile.getLandAction() != null) {
      int destId = actionTile.getLandAction().getDestinationTileId();
      if (destId > 0) {
        destinationTile = board.getTileById(destId);
      }
    }

    if (destinationTile != null && !destinationTile.equals(actionTile)) {
      notifyActionTileEffect(player, actionTile, destinationTile);

      actionTile.leavePlayer(player);
      player.setOnCurrentTile(destinationTile);

      destinationTile.landPlayer(player);
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

  private void updatePlayerPosition(Player player, Tile destTile, Tile actionTile) {
    actionTile.leavePlayer(player);
    player.setOnCurrentTile(destTile);
    destTile.landPlayer(player);
  }

  @Override
  protected void initializeGameState() {

  }

  @Override
  public GameType getGameType() {
    return GameType.SNAKES_AND_LADDERS;
  }




}
