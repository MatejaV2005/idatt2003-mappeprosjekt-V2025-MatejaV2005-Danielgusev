package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.games.SnakesAndLaddersGame;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.model.strategy.SnakesAndLaddersStrategy;

public class BoardGameFactory {
  private final BoardFactory boardFactory = new BoardFactory();


  public BoardGame createSnakesAndLaddersGame() {
    Board board = boardFactory.createNormalBoard();
    Dice dice = new Dice(2);
    GameStrategy strategy = new SnakesAndLaddersStrategy(board, dice);

    return new SnakesAndLaddersGame(board, dice, strategy);
  }

}
