package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.GameEngine;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.model.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.actions.SpecialAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.function.Consumer;

public class TileActionFactory {

  public static TileAction createLadderAction(Tile destinationTile, String description) {
    return new LadderAction(destinationTile, description);
  }

  public static TileAction createSnakeAction(Tile destinationTile, String description) {
    return new SnakeAction(destinationTile, description);
  }

  public static TileAction createSpecialAction(String description, Consumer<Player> action)  {
    return new SpecialAction(description, action);
  }

  public static TileAction createReturnToStartAction(Tile startingTile) {
    return new SpecialAction("returns to start",
        player -> player.placeOnTile(startingTile));
  }

  public static TileAction createSkipTurnAction() {
    return new SpecialAction("skips turn",
        player -> player.setSkipTurn(true));
  }



}
