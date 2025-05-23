package edu.ntnu.idi.idatt.model.games;


import static edu.ntnu.idi.idatt.model.strategy.AstroRallyStrategy.TOTAL_LAPS_TO_WIN;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.strategy.AstroRallyStrategy;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * A specialized {@link BoardGame} implementation for the Astro Rally variant.
 *
 * <p>Enforces the use of an {@link AstroRallyStrategy}; passing any other strategy
 * implementation will result in an exception. Manages game flow according to
 * Astro Rally rules.
 * </p>
 */
public class AstroRallyGame extends BoardGame {
  private static final Logger LOGGER = Logger.getLogger(AstroRallyGame.class.getName());



  /**
   * Constructs a new {@code AstroRallyGame} with the given board, dice, and strategy.
   *
   * <p>The provided {@code strategy} must be an instance of {@link AstroRallyStrategy}.
   * </p>
   *
   * @param board    the {@link Board} on which the game will be played; must not be {@code null}
   * @param dice     the {@link Dice} instance used for die rolls; must not be {@code null}
   * @param strategy the {@link GameStrategy} governing Astro Rally rules; must be an instance
   *                 of {@link AstroRallyStrategy}
   * @throws IllegalArgumentException if {@code strategy} is not an instance of
   *                                  {@link AstroRallyStrategy}
   */
  public AstroRallyGame(Board board, Dice dice, GameStrategy strategy) {
    super(board, dice, strategy);
    if (!(strategy instanceof AstroRallyStrategy)) {
      throw new IllegalArgumentException(
          "AstroRallyGame requires an AstroRallyStrategy instance.");
    }
  }

  @Override
  protected void handlePlayerTurn(Player player) {
    Objects.requireNonNull(player, "player cannot be null for handlePlayerTurn");
    Tile oldTile = player.getCurrentTile();
    if (oldTile == null) {
      return;
    }

    gameEngine.playTurn(player);
    Tile newTile = player.getCurrentTile();

    if (newTile == null) {
      player.setOnCurrentTile(oldTile);
      newTile = oldTile;
    }

    boolean moved = oldTile.getTileId() != newTile.getTileId();
    if (moved) {
      oldTile.leavePlayer(player);
      newTile.landPlayer(player);
    }

    notifyPlayerMoved(player, oldTile, newTile);
    handleLapCompletion(player, oldTile, newTile, moved);

    if (newTile.isActionTile()) {
      handleSpecialTileAction(player, newTile);
    }
  }

  private void handleLapCompletion(
      Player player,
      Tile oldTile,
      Tile newTile,
      boolean moved) {
    if (!moved || board.getBoardSize() <= 0 || oldTile == null || newTile == null) {
      return;
    }

    int size = board.getBoardSize();

    // Only increment lap when crossing from the highest tile to tile 1
    // or when crossing from a high-numbered tile to a low-numbered tile
    boolean completedLap =
        (oldTile.getTileId() == size && newTile.getTileId() == 1)
            ||
            (oldTile.getTileId() > size / 2 && newTile.getTileId() < size / 2
                && oldTile.getTileId() > newTile.getTileId());

    if (completedLap) {
      player.incrementLapsCompleted();
      LOGGER.info("Player " + player.getName() + " completed lap "
          + player.getLapsCompleted());
    }
  }

  @Override
  protected void handleSpecialTileAction(Player player, Tile triggerTile) {
    ExceptionHandling.requireNonNull(player, "player cannot be null");
    ExceptionHandling.requireNonNull(triggerTile, "triggerTile cannot be null");

    TileAction action = triggerTile.getLandAction();

    if (action == null || action.getActionType() == ActionType.NO_OP) {
      notifyActionTileEffect(player, triggerTile, triggerTile);
      return;
    }

    Tile before = player.getCurrentTile();
    ExceptionHandling.requireNonNull(before, "Player's current tile before action cannot be null");

    action.perform(player);
    Tile after = player.getCurrentTile();
    ExceptionHandling.requireNonNull(after, "Player's current tile after action cannot be null");


    if (!Objects.equals(after, before)) {
      before.leavePlayer(player);
      after.landPlayer(player);

      notifyPlayerMoved(player, before, after);


      notifyActionTileEffect(player, triggerTile, after);
    } else {
      notifyActionTileEffect(player, triggerTile, triggerTile);
    }
  }

  @Override
  public boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "player");
    Tile tile = player.getCurrentTile();

    boolean isOnFirstTile = tile != null && tile.getTileId() == 1;
    boolean hasCompletedRequiredLaps = player.getLapsCompleted() >= TOTAL_LAPS_TO_WIN;

    return isOnFirstTile && hasCompletedRequiredLaps;
  }

  @Override
  protected void initializeGameState() {
    if (!players.isEmpty()) {
      gameEngine.initializeGame(players);
    }
  }

  @Override
  public GameType getGameType() {
    return GameType.ASTRO_RALLY;
  }
}