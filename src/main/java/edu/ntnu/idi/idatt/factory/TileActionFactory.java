package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.model.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.actions.SpecialAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.function.Consumer;

public class TileActionFactory {

  public static TileAction createLadderAction(Tile destinationTile, String description) {
    return new LadderAction(destinationTile, description);
  }

  public static TileAction createLadderAction(int destinationTileId, String description) {
    return new LadderAction(destinationTileId, description);
  }

  public static TileAction createSnakeAction(Tile destinationTile, String description) {
    return new SnakeAction(destinationTile, description);
  }

  public static TileAction createSnakeAction(int destinationTileId, String description) {
    return new SnakeAction(destinationTileId, description);
  }

  public static TileAction createSpecialAction(String description, Consumer<Player> action)  {
    return new SpecialAction(description, action);
  }

  public static TileAction createReturnToStartAction(Tile startingTile) {
    return new SpecialAction("returns to start",
        player -> player.setOnCurrentTile(startingTile));
  }

  public static TileAction createSkipTurnAction() {
    return new SpecialAction("skips turn",
        player -> player.setSkipTurn(true));
  }

  public static TileAction createActionFromType(ActionType type, int destinationTileId, String description) {
    if (type == null) {
      return NoOperationAction.INSTANCE;
    }

    return switch (type) {
      case LADDER -> createLadderAction(destinationTileId, description);
      case SNAKE -> createSnakeAction(destinationTileId, description);
      case SPECIAL -> {
        if (description != null && description.contains("skips turn")) {
          yield createSkipTurnAction();
        } else {
          yield createSpecialAction("special", p -> {
          });
        }
      }
      case RETURN_TO_START -> createReturnToStartAction(null);
      case SKIP_TURN -> createSkipTurnAction();
      case NO_OP -> NoOperationAction.INSTANCE;
    };
  }


  public static TileAction createActionFromType(ActionType type, Tile destinationTile, String description) {
    if (type == null) {
      return NoOperationAction.INSTANCE;
    }

    return switch (type) {
      case LADDER -> createLadderAction(destinationTile, description);
      case SNAKE -> createSnakeAction(destinationTile, description);

      case SPECIAL -> {
        if (description != null && description.contains("skips turn")) {
          yield createSkipTurnAction();
        } else {
          yield createSpecialAction("special", p -> {
          });
        }
      }

      case RETURN_TO_START -> createReturnToStartAction(destinationTile);
      case SKIP_TURN -> createSkipTurnAction();
      case NO_OP -> NoOperationAction.INSTANCE;
      default -> {
        System.out.println("Unknown action type: " + type);
        yield NoOperationAction.INSTANCE; //what does yield do?!?!?!
      }
    };
  }


}
