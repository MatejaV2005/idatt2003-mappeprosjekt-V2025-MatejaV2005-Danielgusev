package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.player_type.Player;

public class LadderAction implements TileAction {
  private int tileDestinationID;
  private String description;


  public LadderAction(int tileDestinationID, String description) {
    this.tileDestinationID = tileDestinationID;
    this.description = description;
  }

  @Override
  public void perform(Player player) {
    //ADD LOGGER
    System.out.println(player.getName() + " " + description);
    player.placeOnTile(tileDestinationID);
  }
}
