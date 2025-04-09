package edu.ntnu.idi.idatt.model.playertype;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public abstract class Player {
  private final String name;
  private Tile currentTile;
  private final String pieceType;
  private boolean skipTurn;

  protected Player (String name, String pieceType) {
    this.name = name;
    this.skipTurn = false;
    this.pieceType = pieceType;
  }




  // Get-methods
  public String getName() {
    return name;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }

  public String getPieceType() {
    return pieceType;
  }

  public boolean shouldSkipTurn() {
    if (skipTurn) {
      skipTurn = false;
      return true;
    }
    return false;
  }

  // set-methods

  public void setSkipTurn(boolean skipTurn) {
    this.skipTurn = skipTurn;
  }

  public void setOnCurrentTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "tile ");
    this.currentTile = newTile;
  }

  // abstract method
  public abstract void move(int steps);


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
