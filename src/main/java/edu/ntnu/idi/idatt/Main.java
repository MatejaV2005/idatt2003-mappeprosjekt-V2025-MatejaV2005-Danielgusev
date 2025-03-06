package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.GameEngine;
import edu.ntnu.idi.idatt.model.player_type.HumanPlayer;
import edu.ntnu.idi.idatt.model.player_type.Player;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    BoardGame board = new BoardGame(90, 2);
    GameEngine gameEngine = board.getGameEngine();

    List<Player> players = List.of(
        new HumanPlayer("xXSugondeezXx", gameEngine.getStartingTile()),
        new HumanPlayer("xXLigmaFigmaXx", gameEngine.getStartingTile()),
        new HumanPlayer("xXAtleXx PUSSYCLAT slayer", gameEngine.getStartingTile())
    );

    for (Player player : players) {
      board.addPlayer(player);
    }

    board.play();



  }
}