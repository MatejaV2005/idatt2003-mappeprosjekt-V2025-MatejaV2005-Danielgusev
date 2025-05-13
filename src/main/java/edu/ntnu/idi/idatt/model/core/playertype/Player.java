package edu.ntnu.idi.idatt.model.core.playertype;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.observer.Observable;
import edu.ntnu.idi.idatt.observer.PlayerModelObserver;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.List;


public abstract class Player implements Observable<PlayerModelObserver> {
  private final String name;
  private Tile currentTile;
  private final String pieceType;
  private boolean skipTurn;
  private final List<PlayerModelObserver> observers;

  protected Player (String name, String pieceType) {
    this.name = name;
    this.skipTurn = false;
    this.pieceType = pieceType;
    observers = new ArrayList<>();
  }

  @Override
  public void addObserver(PlayerModelObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(PlayerModelObserver observer) {
    observers.remove(observer);
  }

  protected void notifyPlayerAdded(Player player) {
    for (PlayerModelObserver observer : observers) {
      observer.onPlayerAdded(player);
    }
  }

  protected void notifyPlayerRemoved(Player player) {
    for (PlayerModelObserver observer : observers) {
      observer.onPlayerRemoved(player);
    }
  }

  protected void notifyPlayersLoaded(List<Player> players) {
    for (PlayerModelObserver observer : observers) {
      observer.onPlayersLoaded(players);
    }
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
  public abstract Tile move(int steps);


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
