package edu.ntnu.idi.idatt.converter;


import edu.ntnu.idi.idatt.DataTransfer.ActionDto;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.factory.TileActionFactory;
import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import java.util.Map;
import java.util.logging.Logger;

public class ActionConverter {
  private static final Logger LOGGER = Logger.getLogger(ActionDto.class.getName());

  public ActionDto toDto(TileAction action) {
    return new ActionDto(
        action.getActionType().getJsonValue(),
        action.getDestinationTileId(),
        action.getDescription());
  }

  public TileAction fromDto(ActionDto dto, Map<Integer, Tile> tileMap) throws InvalidBoardFormatException {
    if (dto == null) {
      return NoOperationAction.INSTANCE;
    }

    ActionType type = ActionType.fromJsonValue(dto.getActionType());
    if (type == ActionType.NO_OP && !"NoOp".equalsIgnoreCase(dto.getActionType()) && dto.getActionType() != null && !dto.getActionType().isEmpty()) {
      throw new InvalidBoardFormatException("Unknown action type in JSON: '" + dto.getActionType() + "'");
    }

    int destinationTileId = dto.getDestinationTileId();
    String description = dto.getDescription();
    Tile destinationTile = null;

    if (destinationTileId > 0) {
      destinationTile = tileMap.get(destinationTileId);
      if (destinationTile == null) {
        if (type == ActionType.LADDER || type == ActionType.SNAKE) {
          throw new InvalidBoardFormatException("Action '" + type + "' on a tile references a non-existent destination tile ID: " + destinationTileId);
        }
        LOGGER.warning("Destination tile with ID " + destinationTileId + " not found for action type " + type + ". Action may not function as expected.");
      }
    } else if (type == ActionType.LADDER || type == ActionType.SNAKE) {
      throw new InvalidBoardFormatException("Action type '" + type + "' requires a valid positive destinationTileId, but got: " + destinationTileId);
    }


    try {
      return TileActionFactory.createActionFromType(type, destinationTile, description);
    } catch (NullPointerException | IllegalArgumentException e) { // Fang generelle feil fra factory
      throw new InvalidBoardFormatException("Failed to create action of type '" + type + "': " + e.getMessage(), e);
    }
  }

}
