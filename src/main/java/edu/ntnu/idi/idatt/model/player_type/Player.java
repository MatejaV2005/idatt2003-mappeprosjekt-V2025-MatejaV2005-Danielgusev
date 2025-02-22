package edu.ntnu.idi.idatt.model.player_type;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Tile;

public abstract class Player {
  private String name;
  private Board board;
  private Tile currentTile;

  protected Player (String name, BoardGame game) {
    this.name = name;
    initalizePlayer(game);
  }


  private void initalizePlayer(BoardGame game) {
    //TODO: Add exception-handling
    this.board = game.getBoard();
    this.currentTile = board.getTile(1);
  }

  // Get-methods
  public String getName() {
    return name;
  }
  public Tile getCurrentTile() {
    return currentTile;
  }



  // METHOD OVERLOADING
  public void placeOnTile(Tile newTile) {
    //TODO: Add exception-handling
    this.currentTile = newTile; // determines and places the player on the new current tile
    currentTile.landPlayer(this); // lands player and initiates an action if there is any on that tile
  }

  //METHOD OVERLOADING
  public void placeOnTile(int tileID) {
    //TODO: Add exception-handling
    Tile newTile = board.getTileById(tileID); // retrieves tile form board through getTileByID method
    this.currentTile = newTile;
    currentTile.landPlayer(this);
  }

  public void basicMove(int steps) {
    //TODO: Add exception-handling
    for (int i = 0; i < steps; i++) {
      currentTile = currentTile.getNextTile();
    }

    placeOnTile(currentTile);
  }

  public abstract void move(int steps);

}
