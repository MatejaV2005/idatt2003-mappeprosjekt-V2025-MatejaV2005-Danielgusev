package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.player_type.Player;

public class LadderAction implements TileAction {
  private final Tile destinationTile;
  private final String description;


  public LadderAction(Tile destinationTile, String description) {
    this.destinationTile = destinationTile;
    this.description = description;
  }

  @Override
  public void perform(Player player) {
    //TODO: add distinguishing features for
    System.out.println(player.getName() + " " + description);
    player.placeOnTile(destinationTile);
  }
}
