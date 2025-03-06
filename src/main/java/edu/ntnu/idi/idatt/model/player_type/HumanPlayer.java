package edu.ntnu.idi.idatt.model.player_type;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

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

