package edu.ntnu.idi.idatt.model.player_type;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public abstract class Player {
  private String name;
  private Tile currentTile;

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


  public void placeOnTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "tile ");
    this.currentTile = newTile;
  }

  public Tile basicMove(int steps) {
    ExceptionHandling.requirePositive(steps, "steps");

    Tile newTile = currentTile;

    for (int i = 0; i < steps; i++) {
      if(newTile.getNextTile() != null) {
        newTile = newTile.getNextTile();
      }
    }

    return newTile;
  }



}
