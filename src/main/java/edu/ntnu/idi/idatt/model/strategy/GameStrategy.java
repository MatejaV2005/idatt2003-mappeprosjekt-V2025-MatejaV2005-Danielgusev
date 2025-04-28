package edu.ntnu.idi.idatt.model.strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import java.util.List;

public interface GameStrategy {
  boolean checkWinCondition(Player player);
  void executePlayerTurn(Player player);
  Player determineWinner(List<Player> player);
  void InitializeGame(Board board, List<Player> player);

}
