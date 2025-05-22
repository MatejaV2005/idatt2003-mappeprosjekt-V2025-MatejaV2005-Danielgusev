package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.AsteroidFieldAction;
import edu.ntnu.idi.idatt.model.core.actions.BoostPadAction;
import edu.ntnu.idi.idatt.model.core.actions.LadderAction;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.SnakeAction;
import edu.ntnu.idi.idatt.model.core.actions.SpecialAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Central factory for instantiating all {@link TileAction} types.
 *
 * <p>Provides named creation methods for each concrete action and a generic
 * {@link #createActionFromType(ActionType, Tile, String)} entry point based
 * on an {@link ActionType} enum.
 *
 * @see ActionType
 * @since 1.0
 */
public class TileActionFactory {

  /**
   private constructor to prevent instantiation.
   */
  private TileActionFactory() {
  }

  private static final Logger LOGGER =
      Logger.getLogger(TileActionFactory.class.getName());

  /**
   * Creates a ladder action that moves a player from one tile to another.
   *
   * @param destinationTile the tile to land on; must not be null
   * @param description     a description shown when the action occurs
   * @return a new {@link LadderAction} instance
   */
  public static TileAction createLadderAction(
      Tile destinationTile,
      String description) {
    Objects.requireNonNull(destinationTile,
        "Destination tile cannot be null for LadderAction");
    return new LadderAction(destinationTile, description);
  }

  /**
   * Creates a snake action that moves a player downward on the board.
   *
   * @param destinationTile the tile to land on; must not be null
   * @param description     a description shown when the action occurs
   * @return a new {@link SnakeAction} instance
   */
  public static TileAction createSnakeAction(
      Tile destinationTile,
      String description) {
    Objects.requireNonNull(destinationTile,
        "Destination tile cannot be null for SnakeAction");
    return new SnakeAction(destinationTile, description);
  }

  /**
   * Creates a custom special action with arbitrary logic.
   *
   * @param description a description shown when the action occurs
   * @param actionLogic a {@link Consumer} that implements the action on a player
   * @return a new {@link SpecialAction} instance with type SPECIAL
   */
  public static TileAction createGenericSpecialAction(
      String description,
      Consumer<Player> actionLogic) {
    return new SpecialAction(description, actionLogic, ActionType.SPECIAL);
  }

  /**
   * Creates a return-to-start action that moves a player back to a given tile.
   *
   * @param specificStartingTile the tile to return to; may be null to no-op
   * @return a new {@link SpecialAction} of type RETURN_TO_START
   */
  public static TileAction createReturnToStartAction(
      Tile specificStartingTile) {
    String desc = ActionType.RETURN_TO_START.getDefaultDescription();
    Consumer<Player> logic = player -> {
      Objects.requireNonNull(player,
          "Player cannot be null for RETURN_TO_START logic");
      if (specificStartingTile != null) {
        Tile currentTile = player.getCurrentTile();
        if (currentTile != null) {
          currentTile.leavePlayer(player);
        }
        player.setOnCurrentTile(specificStartingTile);
        specificStartingTile.landPlayer(player);
        LOGGER.log(Level.FINE,
            "{0} {1} to tile {2}",
            new Object[] {
                player.getName(),
                desc,
                specificStartingTile.getTileId()
            });
      } else {
        LOGGER.log(Level.WARNING,
            "Could not execute RETURN_TO_START for {0}: specificStartingTile was null.",
            player.getName());
      }
    };
    return new SpecialAction(desc, logic, ActionType.RETURN_TO_START);
  }

  /**
   * Creates a skip-turn action that prevents the player from moving next turn.
   *
   * @return a new {@link SpecialAction} of type SKIP_TURN
   */
  public static TileAction createSkipTurnAction() {
    String desc = ActionType.SKIP_TURN.getDefaultDescription();
    Consumer<Player> logic = player -> {
      Objects.requireNonNull(player,
          "Player cannot be null for SKIP_TURN logic");
      player.setSkipTurn(true);
      LOGGER.log(Level.FINE,
          "{0} will {1}",
          new Object[] {player.getName(), desc});
    };
    return new SpecialAction(desc, logic, ActionType.SKIP_TURN);
  }

  /**
   * Creates a boost-pad action that grants extra movement steps.
   *
   * @param description a description shown when the action occurs
   * @return a new {@link BoostPadAction} instance
   */
  public static TileAction createBoostPadAction(String description) {
    LOGGER.log(Level.FINER,
        "Creating BoostPadAction with description: {0}",
        description);
    return new BoostPadAction(description);
  }

  /**
   * Creates an asteroid-field action that reduces movement steps.
   *
   * @param description a description shown when the action occurs
   * @return a new {@link AsteroidFieldAction} instance
   */
  public static TileAction createAsteroidFieldAction(String description) {
    LOGGER.log(Level.FINER,
        "Creating AsteroidFieldAction with description: {0}",
        description);
    return new AsteroidFieldAction(description);
  }

  /**
   * Creates a {@link TileAction} based on an {@link ActionType}, target tile,
   * and optional description.
   *
   * <p>If {@code type} is null or unknown, returns {@link NoOperationAction#INSTANCE}.
   *
   * @param type        the action type; may be null
   * @param targetTile  the tile used by LADDER, SNAKE, RETURN_TO_START
   * @param description overrides the default description if non-empty
   * @return the corresponding {@link TileAction} or no-op instance
   */
  public static TileAction createActionFromType(
      ActionType type,
      Tile targetTile,
      String description) {
    if (type == null) {
      LOGGER.warning(
          "Null ActionType provided to factory, returning NoOperationAction.");
      return NoOperationAction.INSTANCE;
    }

    String descToUse = (description != null && !description.trim().isEmpty())
        ? description
        : type.getDefaultDescription();

    return switch (type) {
      case LADDER ->
          createLadderAction(
              Objects.requireNonNull(targetTile,
                  "Target tile for LADDER cannot be null"),
              descToUse);
      case SNAKE ->
          createSnakeAction(
              Objects.requireNonNull(targetTile,
                  "Target tile for SNAKE cannot be null"),
              descToUse);
      case SKIP_TURN -> createSkipTurnAction();
      case RETURN_TO_START -> createReturnToStartAction(targetTile);
      case BOOST_PAD -> createBoostPadAction(descToUse);
      case ASTEROID_FIELD -> createAsteroidFieldAction(descToUse);
      case SPECIAL -> createGenericSpecialAction(descToUse, p -> {
      });
      default -> {
        if (type != ActionType.NO_OP) {
          LOGGER.log(Level.SEVERE,
              "Unhandled action type in factory: {0}. Defaulting to NO_OP.",
              type);
        }
        yield NoOperationAction.INSTANCE;
      }
    };
  }
}
