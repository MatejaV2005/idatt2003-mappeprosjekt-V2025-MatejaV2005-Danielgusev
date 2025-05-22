package edu.ntnu.idi.idatt.model.games;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * Represents the Snakes and Ladders board game.
 * This class extends {@link BoardGame} and implements the specific logic
 * for handling player turns, special tile actions (snakes and ladders),
 * win conditions, and game initialization pertinent to Snakes and Ladders.
 */
public class SnakesAndLaddersGame extends BoardGame {

  /**
   * Constructs a new SnakesAndLaddersGame.
   *
   * @param board The game board. Must not be {@code null}.
   * @param dice The dice used in the game. Must not be {@code null}.
   * @param snakesAndLaddersStrategy The game strategy defining the rules for Snakes and Ladders.
   * Must not be {@code null}.
   * @throws IllegalArgumentException if board, dice, or snakesAndLaddersStrategy is {@code null}.
   */
  public SnakesAndLaddersGame(Board board, Dice dice, GameStrategy snakesAndLaddersStrategy) {
    super(board, dice, snakesAndLaddersStrategy);
  }

  /**
   * Handles a single turn for the given player.
   * This involves the player rolling dice (handled by the game engine via strategy),
   * moving on the board, notifying observers of the move, and then checking if the
   * new tile triggers a special action (like landing on a snake or ladder).
   *
   * @param player The player whose turn it is. Must not be {@code null}.
   * @throws IllegalArgumentException if {@code player} is {@code null},
   * or if the player's current tile is unexpectedly {@code null} after the game engine processes the turn.
   */
  @Override
  protected void handlePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for turn");
    Tile oldTile = player.getCurrentTile();

    gameEngine.playTurn(player);

    Tile newTile = player.getCurrentTile();
    ExceptionHandling.requireNonNull(newTile, "Player's new tile after turn");

    notifyPlayerMoved(player, oldTile, newTile);

    if (newTile.isActionTile()) {
      handleSpecialTileAction(player, newTile);
    }
  }

  /**
   * Handles actions for special tiles like snakes or ladders.
   * If the {@code actionTile} has a defined {@link TileAction}, this method executes it.
   * If the action involves moving to a specific destination tile (e.g., via {@code getDestinationTileId() > 0}),
   * the player is moved directly. Otherwise, the generic {@code perform} method of the action is called.
   * Observers are notified of the effect of the action tile.
   *
   * @param player The player who landed on the action tile. Must not be {@code null}.
   * @param actionTile The special tile the player landed on. Must not be {@code null}.
   * @throws IllegalArgumentException if {@code player} or {@code actionTile} is {@code null}.
   * @throws IllegalStateException if a tile action is expected to result in a new tile for the player,
   * but the player's tile remains {@code null} after the action.
   */
  @Override
  protected void handleSpecialTileAction(Player player, Tile actionTile) {
    ExceptionHandling.requireNonNull(player, "Player for special action");
    ExceptionHandling.requireNonNull(actionTile, "Action tile for special action");

    TileAction landAction = actionTile.getLandAction();
    if (landAction == null) {
      return;
    }

    int destId = landAction.getDestinationTileId();

    if (destId > 0) {
      Tile destinationTile = board.getTileById(destId);
      if (destinationTile != null && !destinationTile.equals(actionTile)) {
        notifyActionTileEffect(player, actionTile, destinationTile);

        actionTile.leavePlayer(player);
        player.setOnCurrentTile(destinationTile);
        destinationTile.landPlayer(player);
      }
    } else {
      Tile tileBeforePerformingAction = player.getCurrentTile();
      ExceptionHandling.requireNonNull(tileBeforePerformingAction, "Player's current tile before performing TileAction");

      landAction.perform(player);

      Tile tileAfterPerformingAction = player.getCurrentTile();
      ExceptionHandling.requireNonNull(tileAfterPerformingAction, "Player's current tile after performing TileAction");

      if (!tileAfterPerformingAction.equals(tileBeforePerformingAction)) {
        notifyActionTileEffect(player, actionTile, tileAfterPerformingAction);
      } else {
        notifyActionTileEffect(player, actionTile, actionTile);
      }
    }
  }

  /**
   * Checks if the given player has met the win condition for Snakes and Ladders.
   * Typically, this means reaching the final tile on the board.
   * If the player is a winner, observers are notified.
   *
   * @param player The player to check. Must not be {@code null}.
   * @return {@code true} if the player has won, {@code false} otherwise.
   * @throws IllegalArgumentException if {@code player} is {@code null}.
   */
  @Override
  protected boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for win condition check");
    boolean isWinner = gameEngine.isWinner(player);
    if (isWinner) {
      notifyOnGameWon(player);
    }
    return isWinner;
  }


  /**
   * Initializes the game state for Snakes and Ladders.
   * This typically involves placing all players on the starting tile of the board.
   * This method relies on the {@link GameStrategy} provided to the {@link edu.ntnu.idi.idatt.model.core.GameEngine}.
   * It will only proceed if there are players added to the game, and the board and game engine are properly set up.
   */
  @Override
  protected void initializeGameState() {
    if (!players.isEmpty()) {
      gameEngine.initializeGame(players);
    }
  }

  /**
   * Gets the type of this game.
   *
   * @return {@link GameType#SNAKES_AND_LADDERS}.
   */
  @Override
  public GameType getGameType() {
    return GameType.SNAKES_AND_LADDERS;
  }
}
