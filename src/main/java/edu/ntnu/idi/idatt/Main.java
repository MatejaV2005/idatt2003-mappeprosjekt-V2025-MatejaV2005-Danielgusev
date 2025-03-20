package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.GameEngine;
import edu.ntnu.idi.idatt.model.player_type.HumanPlayer;
import edu.ntnu.idi.idatt.model.player_type.Player;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    BoardGame board = new BoardGame(90, 2);


    List<Player> players = List.of(
        new HumanPlayer("player 1", board.getStartingTile()),
        new HumanPlayer("player 2", board.getStartingTile()),
        new HumanPlayer("player 3", board.getStartingTile())
    );

    for (Player player : players) {
      board.addPlayer(player);
    }

    board.play();



  }
}