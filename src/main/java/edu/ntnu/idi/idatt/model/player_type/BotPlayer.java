package edu.ntnu.idi.idatt.model.player_type;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public class BotPlayer extends Player {

  public BotPlayer(String name, BoardGame game) {
    super(name, game);
  }

  @Override
  public void move(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");

    System.out.println("Bot moves " + steps + " steps");
    basicMove(steps);
  }

}


//LOGIC:
//IN MOVE, need to implement methods from tile (leaveTile, LandTile)
//USE FOR LOOP to iterate through the tiles on the board