package edu.ntnu.idi.idatt.model.core.actions;

/**
 * Enum representing the different types of actions that can occur when a player
 * lands on a tile. Each action type has a JSON value for serialization
 * and a default description.
 */
public enum ActionType {
  LADDER("Ladder", "Climb the ladder!"),
  SNAKE("Snake", "Slide down the snake!"),
  SPECIAL("Special", "A special event occurred!"), // Generic special
  RETURN_TO_START("ReturnToStart", "Oh no! Back to the start."),
  SKIP_TURN("SkipTurn", "Miss a turn!"),

  BOOST_PAD("BoostPad", "Speed boost activated for next turn!"),
  ASTEROID_FIELD("AsteroidField", "Navigating an asteroid field!"),

  NO_OP("NoOp", "");

  private final String jsonValue;
  private final String defaultDescription;

  /**
   * Constructor for ActionType.
   * @param jsonValue The string representation used in JSON serialization.
   * @param defaultDescription A default user-friendly description for this action type.
   */
  ActionType(String jsonValue, String defaultDescription) {
    this.jsonValue = jsonValue;
    this.defaultDescription = defaultDescription;
  }

  /**
   * Gets the string value used for JSON serialization.
   * @return The JSON string value.
   */
  public String getJsonValue() {
    return jsonValue;
  }

  /**
   * Gets the default user-friendly description for this action type.
   * This can be used if a more specific description is not provided.
   * @return The default description.
   */
  public String getDefaultDescription() {
    return defaultDescription;
  }

  /**
   * Converts a JSON string value back to its corresponding ActionType enum constant.
   * If the value is null or not recognized, it defaults to NO_OP.
   *
   * @param value The JSON string value to parse.
   * @return The corresponding ActionType, or NO_OP if not found or value is null.
   */
  public static ActionType fromJsonValue(String value) {
    if (value == null) {
      return NO_OP;
    }

    for (ActionType type : values()) {
      if (type.jsonValue.equalsIgnoreCase(value)) {
        return type;
      }
    }

    return NO_OP;
  }
}
