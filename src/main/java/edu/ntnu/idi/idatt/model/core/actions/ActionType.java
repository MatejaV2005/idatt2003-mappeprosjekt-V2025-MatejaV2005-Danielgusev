package edu.ntnu.idi.idatt.model.core.actions;

/**
 * Enumerates the types of actions triggered when a player lands on a tile.
 *
 * <p>Each action type carries a JSON value for (de)serialization and
 * a default description for UI display when no custom description is provided.</p>
 *
 * @see #getJsonValue()
 * @see #getDefaultDescription()
 */
public enum ActionType {
  LADDER("Ladder", "Climb the ladder!"),
  SNAKE("Snake", "Slide down the snake!"),
  SPECIAL("Special", "A special event occurred!"),
  RETURN_TO_START("ReturnToStart", "Oh no! Back to the start."),
  SKIP_TURN("SkipTurn", "Miss a turn!"),
  BOOST_PAD("BoostPad", "Speed boost activated for next turn!"),
  ASTEROID_FIELD("AsteroidField", "Navigating an asteroid field!"),
  NO_OP("NoOp", "");

  private final String jsonValue;
  private final String defaultDescription;

  /**
   * Constructs an ActionType.
   *
   * @param jsonValue         the value to use in JSON representations
   * @param defaultDescription the default human-readable description
   */
  ActionType(String jsonValue, String defaultDescription) {
    this.jsonValue = jsonValue;
    this.defaultDescription = defaultDescription;
  }

  /**
   * Returns the JSON string used to serialize this action type.
   *
   * @return the JSON value
   */
  public String getJsonValue() {
    return jsonValue;
  }

  /**
   * Returns the default description to show when this action occurs.
   *
   * <p>Use custom descriptions where needed; this serves as a fallback.</p>
   *
   * @return the default user-friendly description
   */
  public String getDefaultDescription() {
    return defaultDescription;
  }

  /**
   * Parses a JSON string back into its corresponding {@link ActionType}.
   *
   * <p>If {@code value} is null or does not match any enum constant
   * (case-insensitive), this returns {@link #NO_OP}.</p>
   *
   * @param value the JSON string to parse
   * @return the matching ActionType, or NO_OP if unrecognized
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
