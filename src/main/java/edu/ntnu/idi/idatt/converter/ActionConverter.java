package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.ActionDto;
import edu.ntnu.idi.idatt.factory.TileActionFactory;
import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.model.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.actions.SpecialAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import java.util.Map;
import javax.swing.Action;

public class ActionConverter {

  public ActionDto toDto(TileAction action) {
    return new ActionDto(
        action.getActionType().getJsonValue(),
        action.getDestinationTileId(),
        action.getDescription());
  }

  public TileAction fromDto(ActionDto dto, Map<Integer, Tile> tileMap) {
    if (dto == null) {
      return NoOperationAction.INSTANCE;
    }

    ActionType type = ActionType.fromJsonValue(dto.getActionType());
    int destinationTileId = dto.getDestinationTileId();
    String description = dto.getDescription();

    Tile destinationTile = tileMap.get(destinationTileId);

    if (destinationTile == null && destinationTileId > 0) {
      System.err.println("Warning: destination tile with ID " + destinationTileId + " not found.");
    }

    // Use the generic factory method instead of multiple conditionals
    return TileActionFactory.createActionFromType(type, destinationTile, description);
  }

}
