package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.strategy.AstroRallyStrategy;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.model.strategy.SnakesAndLaddersStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

import java.util.logging.Logger;

/**
 * Factory class for creating instances of {@link GameStrategy}.
 * This factory provides a centralized way to obtain strategy objects
 * for different game types, ensuring that the correct strategy implementation
 * is used based on the specified {@link GameType}.
 */
public class StrategyFactory {

  private static final Logger LOGGER = Logger.getLogger(StrategyFactory.class.getName());

  /**
   * Private constructor to prevent instantiation of this factory class. All methods are static.
   */
  private StrategyFactory() {
  }

  /**
   * Creates and returns a {@link GameStrategy} suitable for the given game type, configured with
   * the provided board and dice.
   *
   * @param gameType The type of game for which to create a strategy. Cannot be null.
   * @param board    The game {@link Board} that the strategy will operate on. Cannot be null.
   * @param dice     The {@link Dice} to be used by the strategy. Cannot be null.
   * @return A {@link GameStrategy} instance appropriate for the specified game type.
   * @throws IllegalArgumentException if any of the parameters are null, or if the {@code gameType}
   *                                  is unsupported or unknown.
   */
  public static GameStrategy createStrategy(GameType gameType, Board board, Dice dice) {
    ExceptionHandling.requireNonNull(gameType, "GameType for strategy creation");
    ExceptionHandling.requireNonNull(board, "Board for strategy creation");
    ExceptionHandling.requireNonNull(dice, "Dice for strategy creation");

    LOGGER.info("Creating strategy for game type: " + gameType);

    switch (gameType) {
      case SNAKES_AND_LADDERS:
        return new SnakesAndLaddersStrategy(dice);
      case ASTRO_RALLY:
        return new AstroRallyStrategy(board, dice);
      default:
        LOGGER.severe("Unsupported game type: " + gameType);
        throw new IllegalArgumentException("Unsupported game type: " + gameType);
    }
  }
}
