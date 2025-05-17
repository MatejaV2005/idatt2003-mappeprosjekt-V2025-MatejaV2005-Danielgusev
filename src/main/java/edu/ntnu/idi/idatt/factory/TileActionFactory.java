package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Board; // Board is crucial here
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.LadderAction;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.core.actions.SpecialAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.playertype.Player;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Factory class for creating instances of {@link TileAction}.
 * This class centralizes the logic for instantiating different types of tile actions,
 * including standard actions like ladders and snakes, as well as various special actions.
 */
public class TileActionFactory {
  private static final Logger LOGGER = Logger.getLogger(TileActionFactory.class.getName());

  /**
   * Creates a {@link LadderAction} that moves a player to a destination tile.
   *
   * @param destinationTile The actual Tile object the player will move to. Cannot be null.
   * @param description A description of the ladder action.
   * @return A new LadderAction instance.
   */
  public static TileAction createLadderAction(Tile destinationTile, String description) {
    Objects.requireNonNull(destinationTile, "Destination tile cannot be null for LadderAction");
    return new LadderAction(destinationTile, description);
  }

  /**
   * Creates a {@link LadderAction} that moves a player to a destination tile ID.
   *
   * @param destinationTileId The ID of the tile the player will move to.
   * @param description A description of the ladder action.
   * @return A new LadderAction instance.
   */
  public static TileAction createLadderAction(int destinationTileId, String description) {
    return new LadderAction(destinationTileId, description);
  }

  /**
   * Creates a {@link SnakeAction} that moves a player to a destination tile.
   *
   * @param destinationTile The actual Tile object the player will move to. Cannot be null.
   * @param description A description of the snake action.
   * @return A new SnakeAction instance.
   */
  public static TileAction createSnakeAction(Tile destinationTile, String description) {
    Objects.requireNonNull(destinationTile, "Destination tile cannot be null for SnakeAction");
    return new SnakeAction(destinationTile, description);
  }

  /**
   * Creates a {@link SnakeAction} that moves a player to a destination tile ID.
   *
   * @param destinationTileId The ID of the tile the player will move to.
   * @param description A description of the snake action.
   * @return A new SnakeAction instance.
   */
  public static TileAction createSnakeAction(int destinationTileId, String description) {
    return new SnakeAction(destinationTileId, description);
  }

  /**
   * Creates a generic {@link SpecialAction} with a custom description and logic.
   * The {@code specificOriginalType} for this action will be {@link ActionType#SPECIAL}.
   *
   * @param description A description of the special action.
   * @param actionLogic The {@link Consumer} defining the action's behavior.
   * @return A new SpecialAction instance.
   */
  public static TileAction createGenericSpecialAction(String description, Consumer<Player> actionLogic)  {
    return new SpecialAction(description, actionLogic, ActionType.SPECIAL);
  }

  /**
   * Creates a {@link SpecialAction} for {@link ActionType#RETURN_TO_START}.
   * The player will be moved to the provided {@code specificStartingTile}.
   * If {@code specificStartingTile} is null, the action will log a warning and do nothing.
   *
   * @param specificStartingTile The specific tile to return to. Can be null.
   * @return A new SpecialAction instance for returning to start.
   */
  public static TileAction createReturnToStartAction(Tile specificStartingTile) {
    String desc = ActionType.RETURN_TO_START.getDefaultDescription(); // Assuming ActionType has getDefaultDescription()

    Consumer<Player> logic = player -> {
      Objects.requireNonNull(player, "Player cannot be null for RETURN_TO_START logic");
      Tile targetStartTile = specificStartingTile; // This is 'targetTile' passed to createReturnToStartAction

      if (targetStartTile != null) {
        Tile currentTile = player.getCurrentTile(); // Get current tile before moving
        if (currentTile != null) {
          currentTile.leavePlayer(player);
        }
        player.setOnCurrentTile(targetStartTile);
        targetStartTile.landPlayer(player); // Assumes targetStartTile is not null
        LOGGER.fine(player.getName() + " " + desc + " to tile " + targetStartTile.getTileId());
      } else {
        LOGGER.warning("Could not execute RETURN_TO_START for " + player.getName() + ": specificStartingTile was null. " +
            "The action requires a non-null starting tile to be passed or the factory method " +
            "needs Board context to default to Tile 1.");
      }
    };
    // The SpecialAction is created with the specific ActionType.RETURN_TO_START
    return new SpecialAction(desc, logic, ActionType.RETURN_TO_START);
  }

  /**
   * Creates a {@link SpecialAction} for {@link ActionType#SKIP_TURN}.
   *
   * @return A new SpecialAction instance for skipping a turn.
   */
  public static TileAction createSkipTurnAction() {
    String desc = ActionType.SKIP_TURN.getDefaultDescription(); // Assuming ActionType has getDefaultDescription()
    Consumer<Player> logic = player -> {
      Objects.requireNonNull(player, "Player cannot be null for SKIP_TURN logic");
      player.setSkipTurn(true);
      LOGGER.fine(player.getName() + " " + desc);
    };
    // The SpecialAction is created with the specific ActionType.SKIP_TURN
    return new SpecialAction(desc, logic, ActionType.SKIP_TURN);
  }

  /**
   * Creates a {@link TileAction} based on a given {@link ActionType}, destination ID,
   * and description. This version does not take a Board context.
   * For RETURN_TO_START, it will create an action that requires a specific starting tile ID
   * to be defined in the DTO (as destinationTileId) if it's to function correctly.
   *
   * @param type The {@link ActionType} enum value.
   * @param destinationTileId The destination tile ID (relevant for LADDER, SNAKE, or as the ID of the start tile for RETURN_TO_START).
   * @param description A description of the action. If null or empty, the default description from ActionType will be used.
   * @return The created TileAction, or {@link NoOperationAction#INSTANCE} if type is null or unknown.
   */
  public static TileAction createActionFromType(ActionType type, int destinationTileId, String description) {
    if (type == null) {
      LOGGER.warning("Null ActionType provided to factory, returning NoOperationAction.");
      return NoOperationAction.INSTANCE;
    }

    String descToUse = (description != null && !description.trim().isEmpty()) ? description : type.getDefaultDescription();

    return switch (type) {
      case LADDER -> createLadderAction(destinationTileId, descToUse);
      case SNAKE -> createSnakeAction(destinationTileId, descToUse);
      case SKIP_TURN -> createSkipTurnAction();
      case RETURN_TO_START -> {
        // This will create a ReturnToStartAction that expects destinationTileId
        // to be the ID of the tile to return to. If destinationTileId is -1 (or invalid),
        // the action will effectively do nothing unless the Tile object for ID 1 is
        // somehow resolved and passed as 'specificStartingTile' by the caller (ActionConverter).
        // ActionConverter currently passes null if destinationTileId is -1.
        LOGGER.fine("Creating RETURN_TO_START action. If destinationTileId is -1, specific starting tile must be resolved by caller of this factory method.");
        yield createReturnToStartAction(null); // Will only work if JSON provides a valid destinationTileId for "ReturnToStart" that ActionConverter can resolve to a Tile
      }
      case SPECIAL -> {
        LOGGER.warning("Factory creating generic SpecialAction from ActionType.SPECIAL. " +
            "Description: '" + descToUse + "'. Ensure lambda logic is defined elsewhere if specific behavior is needed.");
        yield createGenericSpecialAction(descToUse, p -> { /* Placeholder: Generic special actions need their logic defined by calling code */ });
      }
      case NO_OP -> NoOperationAction.INSTANCE;
      default -> {
        LOGGER.severe("Unhandled action type in factory: " + type + ", defaulting to NO_OP.");
        yield NoOperationAction.INSTANCE;
      }
    };
  }

  /**
   * Overloaded factory method for creating actions when a destination {@link Tile} object is known.
   * This is the method currently used by {@code ActionConverter}.
   *
   * @param type The {@link ActionType} enum value.
   * @param targetTile The target Tile object. For LADDER/SNAKE, it's the destination.
   * For RETURN_TO_START, it should be the specific tile to return to.
   * @param description A description of the action. If null/empty, default is used.
   * @return The created TileAction.
   */
  public static TileAction createActionFromType(ActionType type, Tile targetTile, String description) {
    if (type == null) {
      LOGGER.warning("Null ActionType provided to factory, returning NoOperationAction.");
      return NoOperationAction.INSTANCE;
    }

    String descToUse = (description != null && !description.trim().isEmpty()) ? description : type.getDefaultDescription();

    return switch (type) {
      case LADDER -> createLadderAction(Objects.requireNonNull(targetTile, "Target tile for LADDER cannot be null"), descToUse);
      case SNAKE -> createSnakeAction(Objects.requireNonNull(targetTile, "Target tile for SNAKE cannot be null"), descToUse);
      case SKIP_TURN -> createSkipTurnAction();
      case RETURN_TO_START -> createReturnToStartAction(targetTile); // targetTile here IS the specificStartingTile
      case SPECIAL -> {
        LOGGER.warning("Factory creating generic SpecialAction from ActionType.SPECIAL with a Tile object. " +
            "Description: '" + descToUse + "'. Ensure lambda logic is defined elsewhere.");
        yield createGenericSpecialAction(descToUse, p -> { /* Placeholder */ });
      }
      case NO_OP -> NoOperationAction.INSTANCE;
      default -> {
        LOGGER.severe("Unhandled action type in factory: " + type + ", defaulting to NO_OP.");
        yield NoOperationAction.INSTANCE;
      }
    };
  }
}
