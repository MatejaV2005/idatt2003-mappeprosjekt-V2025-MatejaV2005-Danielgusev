package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.dto.ActionDto;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.factory.TileActionFactory;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts between {@link TileAction} domain objects and their
 * JSON‐serializable {@link ActionDto} counterparts.
 *
 * <p>This converter handles mapping the action type, destination tile ID,
 * and description into a DTO for persistence or network transfer, and
 * recreates domain objects from a DTO plus a tile lookup map.
 *
 * <p>Invalid or missing data in the DTO will result in an
 * {@link InvalidBoardFormatException}.
 */
public class ActionConverter {

  private static final Logger LOGGER = Logger.getLogger(ActionConverter.class.getName());

  /**
   * Transforms a {@link TileAction} into an {@link ActionDto}.
   *
   * @param action the domain action; must not be null
   * @return a DTO containing the action type JSON value, destination tile ID,
   *         and description
   */
  public ActionDto toDto(TileAction action) {
    return new ActionDto(
        action.getActionType().getJsonValue(),
        action.getDestinationTileId(),
        action.getDescription());
  }

  /**
   * Recreates a {@link TileAction} from its {@link ActionDto} representation.
   *
   * <p>Looks up the destination tile in the provided {@code tileMap} if
   * the DTO specifies a positive ID. For ladder and snake actions, a
   * missing or invalid destination ID triggers an exception.
   * Unknown action types also result in an exception.
   *
   * @param dto     the DTO to convert; may be null to indicate no‐op
   * @param tileMap mapping from tile ID to {@link Tile} instances
   * @return the corresponding domain {@link TileAction}, or
   *         {@link NoOperationAction#INSTANCE} if {@code dto} is null
   * @throws InvalidBoardFormatException if the DTO contains invalid data
   */
  public TileAction fromDto(ActionDto dto, Map<Integer, Tile> tileMap)
      throws InvalidBoardFormatException {

    if (dto == null) {
      return NoOperationAction.INSTANCE;
    }

    ActionType type = ActionType.fromJsonValue(dto.getActionType());
    if (type == ActionType.NO_OP
        && dto.getActionType() != null
        && !dto.getActionType().equalsIgnoreCase("NoOp")
        && !dto.getActionType().isEmpty()) {
      throw new InvalidBoardFormatException(
          "Unknown action type in JSON: '" + dto.getActionType() + "'");
    }

    int destinationTileId = dto.getDestinationTileId();
    String description = dto.getDescription();
    Tile destinationTile = null;

    if (destinationTileId > 0) {
      destinationTile = tileMap.get(destinationTileId);
      if (destinationTile == null) {
        if (type == ActionType.LADDER || type == ActionType.SNAKE) {
          throw new InvalidBoardFormatException(
              "Action '" + type + "' references non-existent destination tile ID: "
                  + destinationTileId);
        }
        LOGGER.log(
            Level.WARNING,
            "Destination tile with ID {0} not found for action type {1}. "
                + "Action may not function as expected.",
            new Object[] {destinationTileId, type});
      }
    } else if (type == ActionType.LADDER || type == ActionType.SNAKE) {
      throw new InvalidBoardFormatException(
          "Action type '" + type
              + "' requires a positive destinationTileId, but got: " + destinationTileId);
    }

    try {
      return TileActionFactory.createActionFromType(type, destinationTile, description);
    } catch (NullPointerException | IllegalArgumentException e) {
      throw new InvalidBoardFormatException(
          "Failed to create action of type '" + type + "': " + e.getMessage(), e);
    }
  }
}
