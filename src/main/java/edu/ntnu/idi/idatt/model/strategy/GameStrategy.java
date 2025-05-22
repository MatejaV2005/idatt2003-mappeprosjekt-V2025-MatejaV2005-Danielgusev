package edu.ntnu.idi.idatt.model.strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Player;
import java.util.List;

public interface GameStrategy {
  boolean checkWinCondition(Player player);
  void executePlayerTurn(Player player);
  void initializeGame(Board board, List<Player> player);
}
