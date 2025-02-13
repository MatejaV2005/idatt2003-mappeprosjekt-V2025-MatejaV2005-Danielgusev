package edu.ntnu.idi.idatt.model;

public class Player {

  String name;
  Tile currentTile;

  public Player (String name, BoardGame game) {
    this.name = name;
    this.currentTile = game.getBoard().getTile(1);
  }

  public String getName() {
    return name;
  }
  public Tile getCurrentTile() {
    return currentTile;
  }

  public void PlaceOnTile(Tile tile) {}
}
