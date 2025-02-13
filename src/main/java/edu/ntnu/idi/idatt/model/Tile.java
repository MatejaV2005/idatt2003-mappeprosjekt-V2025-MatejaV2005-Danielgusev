package edu.ntnu.idi.idatt.model;

public class Tile {

 private int tileid;
 private Tile nexttile;
 private TileAction landaction;

  public Tile(int tileid) {
    this.tileid = tileid;
  }

  // Get methods ------------------------------------
  public Tile getNextTile() {
    return nexttile;
  }

  public int getTileId() {
   return tileid;
  }

 public TileAction getLandaction() {
  return landaction;
 }

 // Set methods --------------------------------------



  public void setNextTile(Tile tile) {
  }
}


/*
* how move, PlaceOnTile and LandPLayer correspond

Move will calculate the destination tile based on the steps its given. it will iterate through the tiles until it has finished the loop and found which tile the player is supposed to go to.

PlaceOnTile Registers the actual tile that the player moved to, and saves the player position to that corresponding tile.

landPlayer tells the tile (if there is an action on it) to activate its action and perform it*/
