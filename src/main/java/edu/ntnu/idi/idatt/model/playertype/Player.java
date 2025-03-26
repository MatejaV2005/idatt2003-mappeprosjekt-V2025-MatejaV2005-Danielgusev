package edu.ntnu.idi.idatt.model.playertype;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public abstract class Player {
  private String name;
  private Tile currentTile;
  private boolean skipTurn;

  protected Player (String name, Tile startingTile) {
    setName(name);
    this.currentTile = startingTile;
    this.skipTurn = false;
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

  public boolean shouldSkipTurn() {
    if (skipTurn) {
      skipTurn = false;
      return true;
    }
    return false;
  }

  public void setSkipTurn(boolean skipTurn) {
    this.skipTurn = skipTurn;
  }



  public abstract void move(int steps);


  public void placeOnTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "tile ");
    this.currentTile = newTile;
  }

  public Tile basicMove(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");

    Tile newTile = currentTile;

    for (int i = 0; i < steps; i++) {
      if (newTile.getNextTile() != null) { //check for if the player is at the end of the board
        newTile = newTile.getNextTile();
      }
    }

    return newTile;
  }



}
