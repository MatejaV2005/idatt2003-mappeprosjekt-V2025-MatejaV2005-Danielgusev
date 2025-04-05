package edu.ntnu.idi.idatt.model;

public enum ActionType {
  LADDER("Ladder"),
  SNAKE("Snake"),
  SPECIAL("Special"),
  RETURN_TO_START("ReturnToStart"),
  SKIP_TURN("SkipTurn"),
  NO_OP("NoOp");


  private final String jsonValue;

  ActionType(String jsonValue) {
    this.jsonValue = jsonValue;
  }

  public String getJsonValue() {
    return jsonValue;
  }

  public static ActionType fromJsonValue(String value) {
    if (value == null) {
      return NO_OP;
    }

    for (ActionType type : values()) {
      if (type.jsonValue.equalsIgnoreCase(value)) {
        return type;
      }
    }

    System.out.println("Unknown action type: " + value + ", defaulting to NO_OP");
    return NO_OP;
  }
}