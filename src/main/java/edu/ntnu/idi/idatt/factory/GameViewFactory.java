package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.view.renderer.AstroRallyRenderer;
import edu.ntnu.idi.idatt.view.renderer.BoardRenderer;
import edu.ntnu.idi.idatt.view.renderer.SnakesAndLaddersRenderer;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import java.util.Objects;

/**
 * Factory for creating {@link GenericBoardGameView} instances based on game type.
 *
 * <p>Selects and instantiates the appropriate {@link BoardRenderer}
 * implementation for the given {@link GameType} and wraps it in a
 * {@link GenericBoardGameView}.
 *
 * @see AstroRallyRenderer
 * @see SnakesAndLaddersRenderer
 *
 */
public class GameViewFactory {

  /**
   * Creates a {@link GenericBoardGameView} for the specified game type.
   *
   * <p>Validates that {@code type} is not null, then chooses the correct
   * {@link BoardRenderer}. Throws if the game type is unsupported.
   *
   * @param type the game type to render; must not be null
   * @return a new {@link GenericBoardGameView} configured for {@code type}
   * @throws NullPointerException     if {@code type} is null
   * @throws IllegalArgumentException if {@code type} is not supported
   */
  public GenericBoardGameView createViewFor(GameType type) {
    Objects.requireNonNull(type, "GameType cannot be null in createViewFor");

    BoardRenderer renderer = switch (type) {
      case SNAKES_AND_LADDERS -> new SnakesAndLaddersRenderer();
      case ASTRO_RALLY -> new AstroRallyRenderer();
    };

    return new GenericBoardGameView(renderer);
  }
}
