package edu.ntnu.idi.idatt.model.playertype;

import static edu.ntnu.idi.idatt.model.playertype.BotPlayer.LOGGER;

import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public class HumanPlayer extends Player {

  public HumanPlayer(String name, String pieceType) {
    super(name, pieceType);
  }

  @Override
  public void move(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");


    LOGGER.info(getName() + " moves " + steps + " steps");
    basicMove(steps);
  }
}

