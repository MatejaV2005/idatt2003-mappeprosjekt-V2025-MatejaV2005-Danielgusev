package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.model.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;

import java.util.List;

public class GameEngine {
  private final GameStrategy strategy;
  private final Board board;

  public GameEngine(Board board, GameStrategy strategy) {
    this.board = board;
    this.strategy = strategy;
  }

  public Tile getStartingTile() {
    return board.getTileById(1);
  }

  public void playTurn(Player currentPlayer) {
    strategy.executePlayerTurn(currentPlayer);
  }

  public boolean isWinner(Player player) {
    return strategy.checkWinCondition(player);
  }

  public Player determineWinner(List<Player> players) {
    return strategy.determineWinner(players);
  }

  public void initializeGame(List<Player> players) {
    strategy.InitializeGame(board, players);
  }
}
