package edu.ntnu.idi.idatt.model.player_type;

public class BotPlayer extends Player {

  public BotPlayer(String name, BoardGame game) {
    super(name, game);
  }

  @Override
  public void move(int steps) {
    System.out.println("Bot moves " + steps + " steps");
    basicMove(steps);
  }

}


//LOGIC:
//IN MOVE, need to implement methods from tile (leaveTile, LandTile)
//USE FOR LOOP to iterate through the tiles on the board