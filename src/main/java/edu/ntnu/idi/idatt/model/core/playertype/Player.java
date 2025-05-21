package edu.ntnu.idi.idatt.model.core.playertype;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;


public class Player {
  private final String name;
  private Tile currentTile;
  private final String pieceType;
  private boolean skipTurn;

  private int lapsCompleted = 0;

  public Player (String name, String pieceType) {
    this.name = name;
    this.skipTurn = false;
    this.pieceType = pieceType;
  }

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


  public void setSkipTurn(boolean skipTurn) {
    this.skipTurn = skipTurn;
  }



  public int getLapsCompleted() {
    return lapsCompleted;
  }

  public void incrementLapsCompleted() {
    this.lapsCompleted++;
  }

  public void resetLapsCompleted() {
    this.lapsCompleted = 0;
  }

  public void setOnCurrentTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "tile ");
    this.currentTile = newTile;
  }

  public Tile basicMove(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");

    Tile destinationTile = this.getCurrentTile();

    for (int i = 0; i < steps; i++) {
      if (destinationTile.getNextTile() != null) { //check for if the player is at the end of the board
        destinationTile = destinationTile.getNextTile();
      }
    }

    return destinationTile;
  }
}
