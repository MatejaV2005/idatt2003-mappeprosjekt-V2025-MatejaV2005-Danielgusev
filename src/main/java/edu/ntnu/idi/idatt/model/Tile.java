package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.player_type.Player;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Tile {

 private int tileid;
 private Tile nextTile;
 private Optional<TileAction> landaction;
 private List<Player> players;

  public Tile(int tileid) {
    ExceptionHandling.requirePositive(tileid, "tileid");

    this.tileid = tileid;
    this.players = new ArrayList<>();
    this.landaction = Optional.empty();
  }


  // Get-methods
  public Tile getNextTile() {
    return nextTile;
  }

  public int getTileId() {
    return tileid;
  }

 public Optional<TileAction> getLandaction() {
    return landaction;
 }

 public List<Player> getPlayers() {
    return Collections.unmodifiableList(players);
 }

 // Set-methods
 public void setLandAction(Optional<TileAction> action) {
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

    landaction.ifPresent(action -> action.perform(player));

 }

 public void leavePlayer(Player player) {
    ExceptionHandling.requireNonNull(player, "Player");
    players.remove(player);
 }
}






