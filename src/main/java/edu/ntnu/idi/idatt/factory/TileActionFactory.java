package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.model.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;

public class TileActionFactory {

  public TileAction createLadderAction(Tile destinationTile, String description) {
    return new LadderAction(destinationTile, description);
  }

  public TileAction createSnakeAction(Tile destinationTile, String description) {
    return new SnakeAction(destinationTile, description);
  }



}
