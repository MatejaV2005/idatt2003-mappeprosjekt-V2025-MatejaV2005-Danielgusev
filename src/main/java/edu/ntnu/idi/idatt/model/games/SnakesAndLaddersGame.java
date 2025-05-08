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

  }

  @Override
  protected boolean checkWinCondition(Player player) {
    boolean isWinner = gameEngine.isWinner(player);
    if (isWinner) {
      notifyOnGameWon(player);
    }
    return isWinner;
  }

  // Optional override methods:

  @Override
  protected void handleSpecialTileAction(Player player, Tile tile) {
    // Snakes and ladders specific tile handling
    // (for ladder movements, snake movements, etc.)
  }

  @Override
  protected void initializeGameState() {
    // Any special initialization for Snakes and Ladders
    // (setting up specific board configurations, etc.)
  }

  @Override
  public GameType getGameType() {
    return GameType.SNAKES_AND_LADDERS;
  }




}
