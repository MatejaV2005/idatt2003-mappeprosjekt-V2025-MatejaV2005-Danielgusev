package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * Represents an asteroid field tile action that, when performed,
 * penalizes a player by reducing their movement steps.
 * <p>
 * Call {@link #consumeLastDeterminedMovementEffectSteps()} to
 * retrieve (and clear) the penalty after {@link #perform(Player)}.
 * </p>
 */
public class AsteroidFieldAction implements TileAction {

  private final String description;
  private int lastDeterminedMovementEffectSteps;

  /**
   * Constructs a new {@code AsteroidFieldAction} with the given description.
   *
   * @param description a non-null, non-blank description of this action
   * @throws IllegalArgumentException if {@code description} is {@code null} or blank
   */
  public AsteroidFieldAction(String description) {
    ExceptionHandling.requireNonNullOrBlank(description, "description cannot be null or blank.");
    this.description = description;
    this.lastDeterminedMovementEffectSteps = 0;
  }

  /**
   * Applies the asteroid field effect to the specified player. This method
   * determines the movement penalty (always –2 for an asteroid field) and
   * stores it internally until consumed.
   *
   * @param player the {@link Player} to apply this action to; must not be {@code null}
   * @throws IllegalArgumentException if {@code player} is {@code null}
   */
  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null for AsteroidFieldAction");
    this.lastDeterminedMovementEffectSteps = -2;
  }

  /**
   * Retrieves and clears the last computed movement penalty. After calling
   * this method, the stored effect resets to zero. Subsequent calls will
   * return zero until {@link #perform(Player)} is invoked again.
   *
   * @return the last determined movement‐effect steps (negative for penalty),
   *         or zero if already consumed or none set
   */
  public int consumeLastDeterminedMovementEffectSteps() {
    int effect = this.lastDeterminedMovementEffectSteps;
    this.lastDeterminedMovementEffectSteps = 0;
    return effect;
  }

  /**
   * {@inheritDoc}
   * <p>
   * For {@code AsteroidFieldAction}, this always returns
   * {@link ActionType#ASTEROID_FIELD}.
   * </p>
   *
   * @return {@link ActionType#ASTEROID_FIELD}
   */
  @Override
  public ActionType getActionType() {
    return ActionType.ASTEROID_FIELD;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Asteroid fields do not transport the player to a new tile,
   * so this method always returns –1.
   * </p>
   *
   * @return –1, indicating no change in tile location
   */
  @Override
  public int getDestinationTileId() {
    return -1;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Returns the description provided at construction time.
   * </p>
   *
   * @return the non-null, non-blank description of this action
   */
  @Override
  public String getDescription() {
    return this.description;
  }
}

