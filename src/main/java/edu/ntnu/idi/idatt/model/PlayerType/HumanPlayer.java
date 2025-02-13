package edu.ntnu.idi.idatt.model.PlayerType;

public class HumanPlayer extends Player {

  public HumanPlayer(String name, BoardGame game) {
    super(name, game);
  }

  @Override
  public void move(int steps) {
    System.out.println(getName() + " moves " + steps + " steps");
    //LOGIC:
    //IN MOVE, need to implement methods from tile (leaveTile, LandTile)
    //USE FOR LOOP to iterate through the tiles on the board
  }
}
