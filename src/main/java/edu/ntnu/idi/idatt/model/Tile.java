package edu.ntnu.idi.idatt.model;

public class Tile {

 private int tileid;
 private Tile nexttile;
 //private TileAction landAction;

  public Tile(int tileid) {
    this.tileid = tileid;
  }

  public int getTileid() {
    return tileid;
  }

  public Tile getNexttile() {
    return nexttile;
  }


}
