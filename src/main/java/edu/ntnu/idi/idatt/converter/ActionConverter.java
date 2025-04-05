package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.ActionDto;
import edu.ntnu.idi.idatt.factory.TileActionFactory;
import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.model.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.actions.SpecialAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import javax.swing.Action;

public class ActionConverter {

  public ActionDto toDto(TileAction action) {
    return new ActionDto(
        action.getActionType().getJsonValue(),
        action.getDestinationTileId(),
        action.getDescription());
  }

  public TileAction fromDto(ActionDto dto) {
    if (dto == null) {
      return NoOperationAction.INSTANCE;
    }

    ActionType type = ActionType.fromJsonValue(dto.getActionType());
    int destinationTileId = dto.getDestinationTileId();
    String description = dto.getDescription();

    if (type == ActionType.LADDER) {
      return TileActionFactory.createLadderAction(destinationTileId, description);
    } else if (type == ActionType.SNAKE) {
      return TileActionFactory.createSnakeAction(destinationTileId, description);
    } else if (type == ActionType.RETURN_TO_START) {
      return TileActionFactory.createReturnToStartAction(null);
    } else if (type == ActionType.SKIP_TURN) {
      return TileActionFactory.createSkipTurnAction();
    } else if (type == ActionType.SPECIAL) {
      return TileActionFactory.createSpecialAction(description, p -> {
      });
    } else {
      return NoOperationAction.INSTANCE;
    }
  }

}
