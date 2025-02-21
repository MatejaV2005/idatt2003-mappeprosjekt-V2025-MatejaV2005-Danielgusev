package edu.ntnu.idi.idatt.model.PlayerType;
import edu.ntnu.idi.idatt.model.Tile;

public abstract class Player {
  private String name;
  private Tile currentTile;

  public Player (String name, BoardGame game) {
    this.name = name;
    this.currentTile = game.getBoard().getTile(1);
  }

  // Get-methods ------------------------------
  public String getName() {
    return name;
  }
  public Tile getCurrentTile() {
    return currentTile;
  }

  //--------------------------------------------

  public void placeOnTile(Tile newTile) {
    this.currentTile = newTile;
  }

  public void basicMove(int steps) {
    for (int i = 0; i < steps; i++) {
      currentTile = currentTile.getNextTile();
    }

    placeOnTile(currentTile);
  }


  public abstract void move(int steps);

}
