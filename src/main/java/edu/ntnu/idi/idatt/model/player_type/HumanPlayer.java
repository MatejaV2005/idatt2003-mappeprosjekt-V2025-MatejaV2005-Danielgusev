package edu.ntnu.idi.idatt.model.player_type;

import static edu.ntnu.idi.idatt.model.player_type.BotPlayer.LOGGER;

import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.logging.Logger;

public class HumanPlayer extends Player {

  public HumanPlayer(String name, Tile startingTile) {
    super(name, startingTile);
  }

  @Override
  public void move(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");


   //  LOGGER.info(getName() + " moves " + steps + " steps");
    basicMove(steps);
  }
}

