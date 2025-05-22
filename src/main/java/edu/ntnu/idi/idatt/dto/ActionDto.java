package edu.ntnu.idi.idatt.dto;

/**
 * Data transfer object for a tile action.
 *
 * <p>Encapsulates the action type (as its JSON value), the ID of the
 * destination tile (if any), and a human-readable description.
 */
public class ActionDto {

  private final String actionType;
  private final int destinationTileId;
  private final String description;

  /**
   * Constructs an ActionDto with the given details.
   *
   * @param actionType the JSON value of the action type; must not be null
   * @param destinationTileId the ID of the tile this action points to, or 0/negative if none
   * @param description a brief description of the action; may be null or empty
   */
  public ActionDto(String actionType, int destinationTileId, String description) {
    this.actionType = actionType;
    this.destinationTileId = destinationTileId;
    this.description = description;
  }

  /**
   * Returns the JSON string value representing the action type.
   *
   * @return the action type JSON value
   */
  public String getActionType() {
    return actionType;
  }

  /**
   * Returns the ID of the destination tile for this action.
   *
   * @return destination tile ID, or a non-positive value if no destination
   */
  public int getDestinationTileId() {
    return destinationTileId;
  }

  /**
   * Returns a human-readable description of this action.
   *
   * @return the action description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns a string representation of this DTO, including all fields.
   *
   * @return string in the form
   *     "Action {actionType='…', destinationTileId=…, description='…'}"
   */
  @Override
  public String toString() {
    return "Action {"
        + "actionType='" + actionType + '\''
        + ", destinationTileId=" + destinationTileId
        + ", description='" + description + '\''
        + '}';
  }
}
