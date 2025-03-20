package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.player_type.Player;

public class SnakeAction implements TileAction{
  private final Tile destinationTile;
  private final String description;


  public SnakeAction(Tile destinationTile, String description) {
    this.destinationTile = destinationTile;
    this.description = description;
  }

  @Override
  public void perform(Player player) {
    //TODO: Add distinguishing features for class
    System.out.println(player.getName() + " " + description);
    player.placeOnTile(destinationTile);
  }

}
