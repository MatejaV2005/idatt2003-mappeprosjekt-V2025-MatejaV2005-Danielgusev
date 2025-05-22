package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.function.Consumer;

/**
 * Configurable tile action that executes a {@link java.util.function.Consumer}
 * on the {@link Player} when performed.
 *
 * <p>Enables arbitrary effects without defining a new class for each behavior.
 *
 * @see ActionType#SPECIAL
 * @since 1.0
 */
public class SpecialAction implements TileAction {
  private final Consumer<Player> action;
  private final String description;
  private final ActionType specificType;

  /**
   * Creates a SpecialAction.
   *
   * @param description  non-blank text describing the action
   * @param action       the logic to execute on the player; must not be null
   * @param specificType the action’s enum type; must not be null
   * @throws IllegalArgumentException if any argument is null or description is blank
   */
  public SpecialAction(
      String description,
      Consumer<Player> action,
      ActionType specificType) {
    ExceptionHandling.requireNonNullOrBlank(description, "description");
    ExceptionHandling.requireNonNull(action, "action logic");
    ExceptionHandling.requireNonNull(specificType, "action type");
    this.description = description;
    this.action = action;
    this.specificType = specificType;
  }

  /**
   * Executes the action logic on the given player.
   *
   * @param player the player to affect; must not be null
   * @throws IllegalArgumentException if {@code player} is null
   */
  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "player");
    action.accept(player);
  }

  /** {@inheritDoc} */
  @Override
  public ActionType getActionType() {
    return specificType;
  }

  /** {@inheritDoc} */
  @Override
  public int getDestinationTileId() {
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return description;
  }
}
