package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

public class AsteroidFieldAction implements TileAction {

  private final String description;
  private int lastDeterminedMovementEffectSteps;

  public AsteroidFieldAction(String description) {
    if (description == null || description.trim().isEmpty()) {
      this.description = "Hit a dense asteroid field! Knocked back!";
    } else {
      this.description = description;
    }
    this.lastDeterminedMovementEffectSteps = 0;
  }

  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null for AsteroidFieldAction");
    this.lastDeterminedMovementEffectSteps = -2;
  }

  public int consumeLastDeterminedMovementEffectSteps() {
    int effect = this.lastDeterminedMovementEffectSteps;
    this.lastDeterminedMovementEffectSteps = 0;
    return effect;
  }

  @Override
  public ActionType getActionType() {
    return ActionType.ASTEROID_FIELD;
  }

  @Override
  public int getDestinationTileId() {
    return -1;
  }

  @Override
  public String getDescription() {
    return this.description;
  }


}