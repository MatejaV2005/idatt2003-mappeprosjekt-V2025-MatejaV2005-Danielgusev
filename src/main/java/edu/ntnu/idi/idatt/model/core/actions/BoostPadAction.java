package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * {@code BoostPadAction} represents a tile action that grants
 * a player a temporary movement boost when landed upon.
 * <p>
 * After invoking {@link #perform(Player)}, call
 * {@link #consumeLastDeterminedMovementEffectSteps()} to retrieve
 * and clear the boost effect.
 * </p>
 */
public class BoostPadAction implements TileAction {

  /**
   * A brief description of this action for display or logging.
   */
  private final String description;

  /**
   * Stores the last determined boost in movement steps;
   * positive values indicate extra steps, zero if consumed or none.
   */
  private int lastDeterminedMovementEffectSteps;

  /**
   * Constructs a new {@code BoostPadAction} with the given description.
   *
   * @param description a non-null, non-blank description of the action
   * @throws IllegalArgumentException if {@code description} is null or blank
   */
  public BoostPadAction(String description) {
    ExceptionHandling.requireNonNullOrBlank(description, "description cannot be null or blank");
    this.description = description;
    this.lastDeterminedMovementEffectSteps = 0;
  }

  /**
   * Applies the boost effect to the specified player by setting
   * {@code lastDeterminedMovementEffectSteps} to +2.
   *
   * @param player the player receiving the boost; must not be null
   * @throws IllegalArgumentException if {@code player} is null
   */
  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null for BoostPadAction");
    this.lastDeterminedMovementEffectSteps = 2;
  }

  /**
   * Retrieves and clears the last determined movement boost.
   * Subsequent calls return zero until {@link #perform(Player)} is invoked again.
   *
   * @return the boost in movement steps, or zero if none/consumed
   */
  public int consumeLastDeterminedMovementEffectSteps() {
    int effect = this.lastDeterminedMovementEffectSteps;
    this.lastDeterminedMovementEffectSteps = 0;
    return effect;
  }

  /**
   * {@inheritDoc}
   * <p>
   * For {@code BoostPadAction}, always returns {@link ActionType#BOOST_PAD}.
   * </p>
   *
   * @return {@link ActionType#BOOST_PAD}
   */
  @Override
  public ActionType getActionType() {
    return ActionType.BOOST_PAD;
  }

  /**
   * {@inheritDoc}
   * <p>
   *   Boost pads do not relocate the player; this returns -1.
   * </p>
   *
   * @return -1 indicating no tile change
   */
  @Override
  public int getDestinationTileId() {
    return -1;
  }

  /**
   * {@inheritDoc}
   * <p>
   *   Returns the description provided at construction.
   * </p>
   *
   * @return the non-null, non-blank description
   */
  @Override
  public String getDescription() {
    return this.description;
  }
}
