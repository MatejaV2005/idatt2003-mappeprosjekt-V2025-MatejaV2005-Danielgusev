package edu.ntnu.idi.idatt.model.player_type;

import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.logging.Logger;

public class HumanPlayer extends Player {
  public static final Logger LOGGER = Logger.getLogger(HumanPlayer.class.getName());
  public HumanPlayer(String name, BoardGame game) {
    super(name, game);
  }

  @Override
  public void move(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");


    LOGGER.info(getName() + " moves " + steps + " steps");//TODO Add custom Logging Handler for UI, to seperate debugging logger from UI
    basicMove(steps);
  }
}

//LOGIC:
//IN MOVE, need to implement methods from tile (leaveTile, LandTile)
//USE FOR LOOP to iterate through the tiles on the board