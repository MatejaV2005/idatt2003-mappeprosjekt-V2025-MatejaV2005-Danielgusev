package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.controller.BoardLoader;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.filehandler.BoardJsonFileHandler;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Dice;
import edu.ntnu.idi.idatt.model.playertype.HumanPlayer;
import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    BoardJsonFileHandler fileHandler = new BoardJsonFileHandler();
    BoardLoader boardLoader = new BoardLoader();

    BoardFactory factory = new BoardFactory();

    Board defaultBoard = factory.createNormalBoard();
    Dice dice = new Dice(2);

    BoardGame board = new BoardGame(defaultBoard, dice);
    boardLoader.saveBoardToFile(defaultBoard, "Files/Boards/defaultBoard.JSON");


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