package edu.ntnu.idi.idatt.model.games;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

/**
 * Represents the specific game logic for a Snakes and Ladders game.
 * This class extends the abstract {@link BoardGame} and implements the
 * game-specific hooks for handling player turns, special tile actions,
 * win conditions, and game initialization.
 */
public class SnakesAndLaddersGame extends BoardGame {

  /**
   * Constructs a new Snakes and Ladders game.
   *
   * @param board    The game {@link Board} to be used. Cannot be null.
   * @param dice     The {@link Dice} to be used for rolling. Cannot be null.
   * @param strategy The {@link GameStrategy} defining the rules for this game.
   * Typically, an instance of {@code SnakesAndLaddersStrategy}. Cannot be null.
   * @throws IllegalArgumentException if board, dice, or strategy is null (handled by superclass).
   */
  public SnakesAndLaddersGame(Board board, Dice dice, GameStrategy strategy) {
    super(board, dice, strategy);
  }

  /**
   * Handles a single player's turn in the Snakes and Ladders game.
   * This involves the player rolling dice (via the game engine), moving on the board,
   * notifying observers of the move, and then checking if the new tile
   * has a special action to be performed.
   *
   * @param player The {@link Player} whose turn it is. Cannot be null.
   * @throws IllegalArgumentException if player is null.
   * @throws IllegalStateException if the player is not on a tile before the turn
   * (as expected by {@code gameEngine.playTurn}).
   */
  @Override
  protected void handlePlayerTurn(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for handlePlayerTurn");

    Tile oldTile = player.getCurrentTile();
    gameEngine.playTurn(player);
    Tile newTile = player.getCurrentTile();

    ExceptionHandling.requireNonNull(newTile, "Player's new tile after gameEngine.playTurn");

    notifyPlayerMoved(player, oldTile, newTile);

    if (newTile.isActionTile()) {
      handleSpecialTileAction(player, newTile);
    }
  }

  /**
   * Handles special actions when a player lands on a tile with an action
   * (e.g., a ladder, a snake, skip turn, return to start).
   *
   * @param player     The {@link Player} who landed on the action tile. Cannot be null.
   * @param actionTile The {@link Tile} with the special action. Cannot be null, and its
   * landAction is expected to be non-null (guaranteed by Tile class).
   * @throws IllegalArgumentException if player or actionTile is null.
   */
  @Override
  protected void handleSpecialTileAction(Player player, Tile actionTile) {
    ExceptionHandling.requireNonNull(player, "Player for handleSpecialTileAction");
    ExceptionHandling.requireNonNull(actionTile, "ActionTile for handleSpecialTileAction");

    TileAction landAction = actionTile.getLandAction();

    LOGGER_MODEL.info("Handling special action " + landAction.getActionType()
        + " for player " + player.getName() + " on tile " + actionTile.getTileId());

    int destId = landAction.getDestinationTileId();

    if (destId > 0) {
      Tile destinationTile = board.getTileById(destId);

      if (!destinationTile.equals(actionTile)) {
        LOGGER_MODEL.fine("Action moves player " + player.getName() + " from "
            + actionTile.getTileId() + " to " + destinationTile.getTileId());
        notifyActionTileEffect(player, actionTile, destinationTile);

        actionTile.leavePlayer(player);
        player.setOnCurrentTile(destinationTile);
        destinationTile.landPlayer(player);
      } else {
        notifyActionTileEffect(player, actionTile, actionTile);
      }
    } else {

      Tile tileBeforePerformingAction = player.getCurrentTile();

      ExceptionHandling.requireState(actionTile.equals(tileBeforePerformingAction),
          "Player's current tile " + (tileBeforePerformingAction != null ? tileBeforePerformingAction.getTileId() : "null") +
              " does not match actionTile " + actionTile.getTileId() + " before performing non-ID-based action.");

      landAction.perform(player);
      Tile tileAfterPerformingAction = player.getCurrentTile();

      if (!tileAfterPerformingAction.equals(tileBeforePerformingAction)) {
        notifyActionTileEffect(player, actionTile, tileAfterPerformingAction);
      } else {
        notifyActionTileEffect(player, actionTile, actionTile); // No change in tile, but action occurred
      }
    }
  }

  /**
   * Checks if the given player has met the win condition for Snakes and Ladders.
   * The win condition is typically reaching the last tile on the board.
   * If the player is a winner, observers are notified.
   *
   * @param player The {@link Player} to check. Cannot be null.
   * @return {@code true} if the player has won, {@code false} otherwise.
   * @throws IllegalArgumentException if player is null.
   */
  @Override
  protected boolean checkWinCondition(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for checkWinCondition");
    boolean isWinner = gameEngine.isWinner(player);
    if (isWinner) {
      LOGGER_MODEL.info("Player " + player.getName() + " has met win condition.");
    }
    return isWinner;
  }

  /**
   * Initializes the game state specifically for Snakes and Ladders.
   * This typically involves ensuring all players are placed on the starting tile of the board.
   * This method is called by {@link BoardGame#startGame()}.
   */
  @Override
  protected void initializeGameState() {
    if (this.players != null && !this.players.isEmpty() && this.gameEngine != null) {
      LOGGER_MODEL.info("Initializing game state for Snakes and Ladders: Placing players on start tile.");
      this.gameEngine.initializeGame(this.players);
    } else {
      LOGGER_MODEL.warning("Could not initialize game state: players list is empty or gameEngine is null.");
    }
  }

  /**
   * Gets the type of this game.
   *
   * @return Always {@link GameType#SNAKES_AND_LADDERS}.
   */
  @Override
  public GameType getGameType() {
    return GameType.SNAKES_AND_LADDERS;
  }
}
