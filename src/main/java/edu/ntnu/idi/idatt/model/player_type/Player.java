package edu.ntnu.idi.idatt.model.player_type;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public abstract class Player {
  private String name;
  private Tile currentTile;
  private boolean shouldSkipTurn;

  protected Player (String name, Tile startingTile) {
    setName(name);
    this.currentTile = startingTile;
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

  public abstract void move(int steps);

  public void setSkipTurn(boolean shouldSkip) {
    this.shouldSkipTurn = shouldSkip;
  }

  public boolean shouldSkipTurn() {
    return shouldSkipTurn;
  }


  public void placeOnTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "tile ");
    this.currentTile = newTile;
  }

  public Tile basicMove(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");

    Tile newTile = currentTile;

    for (int i = 0; i < steps; i++) {
      if(newTile.getNextTile() != null) { //check for if the player is at the end of the board
        newTile = newTile.getNextTile();
      }
    }

    return newTile;
  }



}
