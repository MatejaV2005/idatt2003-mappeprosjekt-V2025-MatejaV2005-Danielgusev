package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.games.AstroRallyGame;
import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.model.games.SnakesAndLaddersGame;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.model.strategy.SnakesAndLaddersStrategy;
import java.util.Objects;

/**
 * Factory for producing {@link BoardGame} instances for supported game types.
 *
 * <p>Delegates board creation to {@link BoardFactory} and composes each
 * {@code BoardGame} with its {@link Dice} and {@link GameStrategy}.
 *
 * @see BoardFactory
 */
public class BoardGameFactory {
  private final BoardFactory boardFactory = new BoardFactory();

  /**
   * Creates a game of the specified type and difficulty/configuration.
   *
   * <p>For Snakes &amp; Ladders, selects an easy, normal or hard preset board
   * based on the lowercase value of {@code difficulty}. For Astro Rally,
   * creates the loop board regardless of {@code difficulty}.
   *
   * @param gameType   the type of game to create; must not be null
   * @param difficulty a string indicating preset difficulty or config; must not be null
   * @return a new {@link BoardGame} ready to start
   * @throws NullPointerException if {@code gameType} or {@code difficulty} is null
   */
  public BoardGame createGame(GameType gameType, String difficulty) {
    Objects.requireNonNull(gameType, "gameType must not be null");
    Objects.requireNonNull(difficulty, "difficulty must not be null");
    return switch (gameType) {
      case SNAKES_AND_LADDERS -> createSnakesAndLaddersGame(difficulty);
      case ASTRO_RALLY       -> createAstroRallyGame();
    };
  }

  /**
   * Creates a Snakes &amp; Ladders game with a preset board.
   *
   * <p>Selects easy, normal, or hard board based on
   * {@code difficulty.toLowerCase()}, then composes it with a 2-sided {@link Dice}
   * and {@link SnakesAndLaddersStrategy}.
   *
   * @param difficulty the difficulty keyword ("easy", "hard", or others for normal)
   * @return a new {@link SnakesAndLaddersGame}
   */
  private BoardGame createSnakesAndLaddersGame(String difficulty) {
    Board board = switch (difficulty.toLowerCase()) {
      case "easy" -> boardFactory.createEasyBoard();
      case "hard" -> boardFactory.createHardBoard();
      default -> boardFactory.createNormalBoard();
    };
    Dice dice = new Dice(2);
    GameStrategy strategy = new SnakesAndLaddersStrategy(dice);
    return new SnakesAndLaddersGame(board, dice, strategy);
  }

  /**
   * Creates a Snakes &amp; Ladders game using a custom board.
   *
   * <p>Useful for loading a user-provided board at runtime.
   *
   * @param customBoard the board to use; must not be null
   * @return a new {@link SnakesAndLaddersGame}
   * @throws NullPointerException if {@code customBoard} is null
   */
  public BoardGame createSnakesAndLaddersGame(Board customBoard) {
    Objects.requireNonNull(customBoard, "customBoard must not be null");
    Dice dice = new Dice(2);
    GameStrategy strategy = new SnakesAndLaddersStrategy(dice);
    return new SnakesAndLaddersGame(customBoard, dice, strategy);
  }

  /**
   * Creates an Astro Rally game.
   *
   * <p>Builds the circular Astro Rally board, uses a 2-sided {@link Dice},
   * and obtains an appropriate strategy via {@code StrategyFactory}.
   *
   * @return a new {@link AstroRallyGame}
   */
  private BoardGame createAstroRallyGame() {
    Board board = boardFactory.createAstroRallyBoard();
    Dice dice = new Dice(2);
    GameStrategy strategy = StrategyFactory.createStrategy(
        GameType.ASTRO_RALLY, board, dice);
    return new AstroRallyGame(board, dice, strategy);
  }
}
