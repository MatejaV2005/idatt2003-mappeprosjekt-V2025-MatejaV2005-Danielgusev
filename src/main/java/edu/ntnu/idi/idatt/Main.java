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
   BoardGameApp app = new BoardGameApp();
   app.start();
  }
}