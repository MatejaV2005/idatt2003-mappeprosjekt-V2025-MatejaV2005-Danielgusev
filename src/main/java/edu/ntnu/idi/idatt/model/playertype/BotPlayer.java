package edu.ntnu.idi.idatt.model.playertype;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.logging.Logger;

public class BotPlayer extends Player {
  public static final Logger LOGGER = Logger.getLogger(BotPlayer.class.getName());

  public BotPlayer(String name, String pieceType) {
    super(name, pieceType);
  }

  @Override
  public void move(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");

    LOGGER.info("Bot moves " + steps + " steps"); //TODO Add custom Logging Handler for UI, to seperate debugging logger from UI
    basicMove(steps);
  }

}


