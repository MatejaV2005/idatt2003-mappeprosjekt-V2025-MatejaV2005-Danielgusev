package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.function.Consumer;

/**
 * Represents a generic "special" tile action defined by a {@link Consumer<Player>}.
 * Allows for flexible tile effects without needing a new class for each action.
 */
public class SpecialAction implements TileAction {
  private final Consumer<Player> action;
  private final String description;
  private final ActionType specificType;

  /**
   * Constructs a SpecialAction with a specific description, a consumer defining its logic,
   *    * and a specific action type. This allows for creating diverse actions without
   *    * requiring a new class for each unique behavior.
   *
   * @param description A description of the action. Cannot be null or blank.
   * @param action The {@link Consumer<Player>} defining the action's logic. Cannot be null.
   * @param specificType The specific {@link ActionType} for this action. Cannot be null.
   * @throws IllegalArgumentException if any parameter is null (or description is blank).
   */
  public SpecialAction(String description, Consumer<Player> action, ActionType specificType) {
    ExceptionHandling.requireNonNullOrBlank(description, "Description for SpecialAction");
    ExceptionHandling.requireNonNull(action, "Action (Consumer) for SpecialAction");
    ExceptionHandling.requireNonNull(specificType, "Specific ActionType for SpecialAction");

    this.description = description;
    this.action = action;
    this.specificType = specificType;
  }

  /**
   * Performs the special action on the player by executing the provided {@link Consumer<Player>}.
   *
   * @param player The player to perform the action on. Cannot be null.
   * @throws IllegalArgumentException if {@code player} is null.
   */
  @Override
  public void perform(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for SpecialAction");
    System.out.println(player.getName() + " " + description);
    action.accept(player);
  }

  /**
   * Gets the specific type of this special action.
   *
   * @return The {@link ActionType} set during construction.
   */
  @Override
  public ActionType getActionType() {
    return this.specificType;
  }

  /**
   * Gets the destination tile ID for this action.
   * Special actions typically do not have a fixed destination.
   *
   * @return Always -1.
   */
  @Override
  public int getDestinationTileId() {
    return -1;
  }

  /**
   * Gets the description of this special action.
   *
   * @return The description string.
   */
  @Override
  public String getDescription() {
    return this.description;
  }
}
