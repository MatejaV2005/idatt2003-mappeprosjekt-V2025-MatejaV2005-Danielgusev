package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Dice;
import edu.ntnu.idi.idatt.model.Die;
import edu.ntnu.idi.idatt.model.player_type.HumanPlayer;
import edu.ntnu.idi.idatt.model.player_type.Player;

public class Main {
  /**
   * Main method for testing the game.
   */
  public static void main(String[] args) {
    Board board = new Board(90);
    BoardGame game = new BoardGame(board);

    game.addPlayer(new HumanPlayer("xXMatejaV05Xx", game));
    game.addPlayer(new HumanPlayer("xXMajidSigmaBoyZz", game));
    game.addPlayer(new HumanPlayer("Atle", game));
    game.addPlayer(new HumanPlayer("GuzzyLuzzy2504", game));

    game.play();
    Player winner = game.getWinner();
    if (winner != null) {
      System.out.println("Winner is: " + winner.getName());
    }
  }
}