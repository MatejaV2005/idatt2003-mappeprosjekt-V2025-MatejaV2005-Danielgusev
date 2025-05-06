package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.games.SnakesAndLaddersGame;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.model.strategy.SnakesAndLaddersStrategy;

public class BoardGameFactory {
  private final BoardFactory boardFactory = new BoardFactory();

  public BoardGame createGame(GameType gameType, String difficulty) {
    switch (gameType) {
      case SNAKES_AND_LADDERS:
        return createSnakesAndLaddersGame(difficulty);
      case MONOPOLY:
        throw new UnsupportedOperationException("Monopoly game not yet implemented");
      case LUDO:
        throw new UnsupportedOperationException("Ludo game not yet implemented");
      default:
        throw new IllegalArgumentException("Unsupported game type: " + gameType);
    }
  }

  public BoardGame createSnakesAndLaddersGame(String difficulty) {
    Board board = switch (difficulty.toLowerCase()) {
      case "easy" -> boardFactory.createEasyBoard();
      case "normal" -> boardFactory.createNormalBoard();
      case "hard" -> boardFactory.createHardBoard();
      default -> boardFactory.createNormalBoard();
    };

    Dice dice = new Dice(2);
    GameStrategy strategy = new SnakesAndLaddersStrategy(board, dice);
    return new SnakesAndLaddersGame(board, dice, strategy);
  }

  // New overload:
  public BoardGame createSnakesAndLaddersGame(Board customBoard) {
    Dice dice = new Dice(2);
    GameStrategy strategy = new SnakesAndLaddersStrategy(customBoard, dice);
    return new SnakesAndLaddersGame(customBoard, dice, strategy);
  }
}
