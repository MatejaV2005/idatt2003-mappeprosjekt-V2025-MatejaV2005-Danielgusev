package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.player_type.Player;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.List;

public class Tile {

 private int tileid;
 private Tile nextTile;
 private TileAction landaction;
 private List<Player> players;

  public Tile(int tileid) {
    ExceptionHandling.requirePositive(tileid, "tileid");

    this.tileid = tileid;
    this.players = new ArrayList<>();
  }


  // Get-methods
  public Tile getNextTile() {
    return nextTile;
  }

  public int getTileId() {
    return tileid;
  }

 public TileAction getLandaction() {
    return landaction;
 }

 // Set-methods
 public void setLandAction(TileAction action) {
    ExceptionHandling.requireNonNull(action, "TileAction");
    this.landaction = action;
 }

 public void setNextTile(Tile nextTile) {
    ExceptionHandling.requireNonNull(nextTile, "Tile");
    this.nextTile = nextTile;
 }

 public void landPlayer(Player player) {
    ExceptionHandling.requireNonNull(player, "Player");
    players.add(player);
//    landaction.perform(player);
 }

 public void leavePlayer(Player player) {
    ExceptionHandling.requireNonNull(player, "Player");
    players.remove(player);
 }
}






