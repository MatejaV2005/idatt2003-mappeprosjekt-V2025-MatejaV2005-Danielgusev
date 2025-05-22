package edu.ntnu.idi.idatt.model.strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;
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
  public void initializeGame(Board board, List<Player> players) {
    for (Player p : players) {
      p.setOnCurrentTile(board.getTileById(1));
    }
  }
}
