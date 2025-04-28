package edu.ntnu.idi.idatt.model.core.playertype;

import static edu.ntnu.idi.idatt.model.core.playertype.BotPlayer.LOGGER;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public class HumanPlayer extends Player {

  public HumanPlayer(String name, String pieceType) {
    super(name, pieceType);
  }

  @Override
  public Tile move(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");


    LOGGER.info(getName() + " moves " + steps + " steps");
    return basicMove(steps);
  }
}

