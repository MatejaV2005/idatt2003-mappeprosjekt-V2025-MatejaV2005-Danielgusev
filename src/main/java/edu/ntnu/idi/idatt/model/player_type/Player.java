package edu.ntnu.idi.idatt.model.player_type;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public abstract class Player {
  private String name;
  private Board board;
  private Tile currentTile;

  protected Player (String name, BoardGame game) {
    setName(name);
    initalizePlayer(game);
  }


  private void initalizePlayer(BoardGame game) {
    //TODO: Add exception-handling
    ExceptionHandling.requireNonNull(game, "Game must not be null");//Er dette nødvending? Game kan i utgangspunktet ikke være null uansett
    this.board = game.getBoard();
    this.currentTile = board.getTileById(1);
  }

  private void setName (String name) {
    ExceptionHandling.requireNonNull(name, name);
    this.name = name;
  }

  // Get-methods
  public String getName() {
    return name;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }



  // METHOD OVERLOADING
  /*public void placeOnTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "tile ");

    currentTile.leavePlayer(this); //removes player from the currently occupied tile
    this.currentTile = newTile; // determines the new current tile the player is to be placed
    currentTile.landPlayer(this); // lands player and initiates an action if there is any on that tile
  }*/

  //METHOD OVERLOADING
 /* public void placeOnTile(int tileID) {
    ExceptionHandling.requirePositive(tileID, "steps");

    // retrieves tile form board through getTileByID method
    currentTile.leavePlayer(this);
    this.currentTile = board.getTileById(tileID);
    currentTile.landPlayer(this);
  }*/

  /*public void basicMove(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");

    for (int i = 0; i < steps; i++) {
      currentTile = currentTile.getNextTile();
    }

    placeOnTile(currentTile);
  }*/

  public abstract void move(int steps);

}
