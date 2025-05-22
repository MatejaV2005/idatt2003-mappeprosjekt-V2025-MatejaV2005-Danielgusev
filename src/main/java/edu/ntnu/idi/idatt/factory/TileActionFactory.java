package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.AsteroidFieldAction; // Importer ny handling
import edu.ntnu.idi.idatt.model.core.actions.BoostPadAction; // Importer ny handling
import edu.ntnu.idi.idatt.model.core.actions.LadderAction;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.core.actions.SpecialAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.Player; // Trengs for SpecialAction lambda
import java.util.Objects;
import java.util.function.Consumer; // Trengs for SpecialAction lambda
import java.util.logging.Logger;

/**
 * Factory class for creating instances of {@link TileAction}. This class centralizes the logic for
 * instantiating different types of tile actions.
 */
public class TileActionFactory {
  private static final Logger LOGGER = Logger.getLogger(TileActionFactory.class.getName());

  public static TileAction createLadderAction(Tile destinationTile, String description) {
    Objects.requireNonNull(destinationTile, "Destination tile cannot be null for LadderAction");
    return new LadderAction(destinationTile, description);
  }

  public static TileAction createSnakeAction(Tile destinationTile, String description) {
    Objects.requireNonNull(destinationTile, "Destination tile cannot be null for SnakeAction");
    return new SnakeAction(destinationTile, description);
  }

  public static TileAction createGenericSpecialAction(
      String description, Consumer<Player> actionLogic) {
    return new SpecialAction(description, actionLogic, ActionType.SPECIAL);
  }

  public static TileAction createReturnToStartAction(Tile specificStartingTile) {
    String desc = ActionType.RETURN_TO_START.getDefaultDescription();
    Consumer<Player> logic =
        player -> {
          Objects.requireNonNull(player, "Player cannot be null for RETURN_TO_START logic");
          if (specificStartingTile != null) {
            Tile currentTile = player.getCurrentTile();
            if (currentTile != null) {
              currentTile.leavePlayer(player);
            }
            player.setOnCurrentTile(specificStartingTile);
            specificStartingTile.landPlayer(player);
            LOGGER.fine(
                player.getName() + " " + desc + " to tile " + specificStartingTile.getTileId());
          } else {
            LOGGER.warning(
                "Could not execute RETURN_TO_START for "
                    + player.getName()
                    + ": specificStartingTile was null.");
          }
        };
    return new SpecialAction(desc, logic, ActionType.RETURN_TO_START);
  }

  public static TileAction createSkipTurnAction() {
    String desc = ActionType.SKIP_TURN.getDefaultDescription();
    Consumer<Player> logic =
        player -> {
          Objects.requireNonNull(player, "Player cannot be null for SKIP_TURN logic");
          player.setSkipTurn(true);
          LOGGER.fine(player.getName() + " will " + desc);
        };
    return new SpecialAction(desc, logic, ActionType.SKIP_TURN);
  }

  // Nye factory-metoder for Astro Rally actions
  /**
   * Creates a {@link BoostPadAction}.
   *
   * @param description A description of the boost pad effect.
   * @return A new BoostPadAction instance.
   */
  public static TileAction createBoostPadAction(String description) {
    LOGGER.finer("Creating BoostPadAction with description: " + description);
    return new BoostPadAction(description);
  }

  /**
   * Creates an {@link AsteroidFieldAction}.
   *
   * @param description A description of the asteroid field encounter.
   * @return A new AsteroidFieldAction instance.
   */
  public static TileAction createAsteroidFieldAction(String description) {
    LOGGER.finer("Creating AsteroidFieldAction with description: " + description);
    return new AsteroidFieldAction(description);
  }

  /**
   * Creates a {@link TileAction} based on a given {@link ActionType}, destination {@link Tile}, and
   * description. This is the primary factory method used by converters when a target Tile object is
   * resolved.
   *
   * @param type The {@link ActionType} enum value.
   * @param targetTile The target Tile object. For LADDER/SNAKE, it's the destination. For
   * RETURN_TO_START, it should be the specific tile to return to. For other actions like
   * BOOST_PAD or ASTEROID_FIELD, this is -1 since its destination is handled by strategy
   * @param description A description of the action. If null or empty, the default description from
   * ActionType will be used.
   * @return The created TileAction, or {@link NoOperationAction#INSTANCE} if type is null or
   * unknown.
   */
  public static TileAction createActionFromType(
      ActionType type, Tile targetTile, String description) {
    if (type == null) {
      LOGGER.warning("Null ActionType provided to factory, returning NoOperationAction.");
      return NoOperationAction.INSTANCE;
    }

    String descToUse =
        (description != null && !description.trim().isEmpty())
            ? description
            : type.getDefaultDescription();

    switch (type) {
      case LADDER:
        return createLadderAction(
            Objects.requireNonNull(targetTile, "Target tile for LADDER cannot be null"),
            descToUse);
      case SNAKE:
        return createSnakeAction(
            Objects.requireNonNull(targetTile, "Target tile for SNAKE cannot be null"), descToUse);
      case SKIP_TURN:
        return createSkipTurnAction();
      case RETURN_TO_START:
        return createReturnToStartAction(targetTile);
      case BOOST_PAD:
        return createBoostPadAction(descToUse);
      case ASTEROID_FIELD:
        return createAsteroidFieldAction(descToUse);
      case SPECIAL:
        return createGenericSpecialAction(descToUse, p -> {});
      case NO_OP:
      default:
        if (type != ActionType.NO_OP) {
          LOGGER.severe("Unhandled action type in factory: " + type + ". Defaulting to NO_OP.");
        }
        return NoOperationAction.INSTANCE;
    }
  }
}
