package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.PlayerType.Player;
import java.util.ArrayList;
import java.util.List;

public class Tile {

 private int tileid;
 private Tile nextTile;
 private TileAction landaction;
 private List<Player> players;

  public Tile(int tileid) {
    this.tileid = tileid;
    this.players = new ArrayList<>();
  }


  public Tile getNextTile() {
    return nextTile;
  }

  public int getTileId() {
   return tileid;
  }


 public TileAction getLandaction() {
  return landaction;
 }

 public TileAction action setLandAction(TileAction action) {
  //TODO: fill out
 }

 public void landPlayer(Player player) {
   players.add(player);
   //TODO: fill out
 }

 public void leavePlayer(Player player) {
   players.remove(player);
   //TODO: fill out
 }

 public void setNextTile(Tile nextTile) {
  this.nextTile = nextTile;
 }




}






/*
* how move, PlaceOnTile and LandPLayer correspond

Move will calculate the destination tile based on the steps its given. it will iterate through the tiles until it has finished the loop and found which tile the player is supposed to go to.

PlaceOnTile Registers the actual tile that the player moved to, and saves the player position to that corresponding tile.

landPlayer tells the tile (if there is an action on it) to activate its action and perform it*/
