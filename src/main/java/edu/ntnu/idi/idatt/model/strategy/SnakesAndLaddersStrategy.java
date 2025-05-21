package edu.ntnu.idi.idatt.model.strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import java.util.List;

public class SnakesAndLaddersStrategy implements GameStrategy {
  private final Dice dice;

  public SnakesAndLaddersStrategy(Dice dice) {
    this.dice = dice;
  }

  @Override
  public boolean checkWinCondition(Player winner) {
    Tile currentTile = winner.getCurrentTile();
    if (currentTile.getNextTile() == null) {
      return true;
    }

    return false;
  }

  @Override
  public void executePlayerTurn(Player player) {
    int steps = dice.roll();
    Tile oldTile = player.getCurrentTile();
    Tile newTile = player.basicMove(steps);

    oldTile.leavePlayer(player);
    player.setOnCurrentTile(newTile);
  }

  @Override
  public Player determineWinner(List<Player> players) {
    for (Player p : players) {
      if (checkWinCondition(p)) {
        return p;
      }
    }
    return null;
  }

  @Override
  public void InitializeGame(Board board, List<Player> players) {
    for (Player p : players) {
      p.setOnCurrentTile(board.getTileById(1));
    }
  }

  private void updatePlayerPosition(Player player, Tile oldTile, Tile newTile) {
    oldTile.leavePlayer(player);
    player.setOnCurrentTile(newTile);
    newTile.landPlayer(player);
    
  }

}
