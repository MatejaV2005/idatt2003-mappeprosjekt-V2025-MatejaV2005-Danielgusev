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

  public void PlaceOnTile(Tile newTile) {
    // NOT ITERATIVE, this method will be responsible for moving player instantly to
    // a new tile if player e.g. lands on a ladder or a teleporitng tile
    this.currentTile = newTile;
  }

  public abstract void move(int steps);

}
