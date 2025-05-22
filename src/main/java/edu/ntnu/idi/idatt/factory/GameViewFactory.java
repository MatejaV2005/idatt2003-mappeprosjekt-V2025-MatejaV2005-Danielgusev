package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.view.renderer.AstroRallyRenderer;
import edu.ntnu.idi.idatt.view.renderer.BoardRenderer;
import edu.ntnu.idi.idatt.view.renderer.SnakesAndLaddersRenderer;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import java.util.Objects;

public class GameViewFactory {

  public GenericBoardGameView createViewFor(GameType type) {
    Objects.requireNonNull(type, "GameType cannot be null in createViewFor");

    BoardRenderer renderer;
    switch (type) {
      case SNAKES_AND_LADDERS:
        renderer = new SnakesAndLaddersRenderer();
        break;
      case ASTRO_RALLY:
        renderer = new AstroRallyRenderer();
        break;
      default:
        throw new IllegalArgumentException("Unsupported or unknown game type: " + type);
    }
    return new GenericBoardGameView(renderer);
  }

}
