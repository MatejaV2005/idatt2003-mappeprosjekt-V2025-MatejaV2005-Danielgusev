package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import edu.ntnu.idi.idatt.utils.AlertHelper;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller responsible for handling user interactions within the main game screen
 * ({@link GenericBoardGameView}). It acts as the intermediary between the game view and the game
 * model ({@link BoardGame}), translating user actions (like button clicks) into calls on the game
 * model. It also coordinates with the {@link NavigationController} for screen transitions away from
 * the game screen.
 */
public class BoardGameController {

  private static final Logger LOGGER = Logger.getLogger(BoardGameController.class.getName());

  private final BoardGame boardGame;
  private final NavigationController navigationController;

  /**
   * Constructs a new BoardGameController. Establishes connections between the view, model, and
   * navigation logic.
   *
   * @param view The associated {@link GenericBoardGameView}. Must not be null.
   * @param boardGame The {@link BoardGame} model instance. Must not be null.
   * @param navigationController The {@link NavigationController} for handling screen changes.
   *                             Must not be null.
   * @throws NullPointerException if any argument is null.
   */
  public BoardGameController(
      final GenericBoardGameView view,
      final BoardGame boardGame,
      final NavigationController navigationController) {
    GenericBoardGameView view1 = Objects.requireNonNull(view, "View cannot be null");
    this.boardGame = Objects.requireNonNull(boardGame, "BoardGame cannot be null");
    this.navigationController =
        Objects.requireNonNull(navigationController, "NavigationController cannot be null");

    view1.setController(this);
    initializeGameView();
  }

  /**
   * Performs any initial setup needed when the controller and view are linked. Currently, logs the
   * initialization and the initial game state.
   */
  protected void initializeGameView() {
    LOGGER.info("BoardGameController initialized and linked with view.");
    logCurrentGameState();
  }

  /**
   * Handles the user action of initiating the next turn, typically triggered by a button click. It
   * instructs the {@link BoardGame} model to advance the game state by one turn. Includes basic
   * error handling if the turn cannot be played.
   */
  public void onPlayTurn() {
    LOGGER.fine("onPlayTurn() called.");
    try {
      if (boardGame.isGameOver()) {
        LOGGER.warning("Attempted to play turn, but game is already over.");
        return;
      }
      boardGame.playNextTurn();
      logCurrentGameState();
    } catch (IllegalStateException e) {
      LOGGER.log(Level.SEVERE, "Error executing playNextTurn.", e);
      AlertHelper.showErrorAlert("Game Error", "Could not play turn: " + e.getMessage());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error during onPlayTurn.", e);
      AlertHelper.showErrorAlert(
          "Unexpected Error", "An unexpected error occurred: " + e.getMessage());
    }
  }

  /**
   * Logs the current state of the game model for debugging purposes. Includes game over status,
   * current player, player positions, and round count.
   */

  //TODO, hjelp fra KI modell fått her
  protected void logCurrentGameState() {
    try {
      LOGGER.info("--- Current Game State ---");
      LOGGER.log(Level.INFO, "Game Over: {0}", boardGame.isGameOver());

      Player currentPlayer = boardGame.getCurrentPlayer();
      LOGGER.log(
          Level.INFO, "Current Player: {0}",
          (currentPlayer != null ? currentPlayer.getName() : "None"));

      if (boardGame.getPlayers() != null) {
        for (Player p : boardGame.getPlayers()) {
          if (p != null) {
            LOGGER.log(
                Level.INFO,
                "  - {0} at tile {1}",
                new Object[] {
                    p.getName(),
                    (p.getCurrentTile() != null ? p.getCurrentTile().getTileId() : "unknown")
                });
          }
        }
      } else {
        LOGGER.warning("Player list is null in boardGame model.");
      }
      LOGGER.log(Level.INFO, "Round: {0}", boardGame.getRoundCount());
      LOGGER.info("--------------------------");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error logging game state.", e);
    }
  }

  /**
   * Provides access to the underlying {@link BoardGame} model instance. This allows other parts of
   * the application (like the view, although discouraged for direct model manipulation) or
   * potentially other controllers to query the game state.
   *
   * @return The {@link BoardGame} instance being controlled.
   *         Never null after successful construction.
   */
  public BoardGame getBoardGame() {
    return boardGame;
  }

  /**
   * Handles a request to navigate away from the current game screen and return to the
   * game selection screen. The navigation is facilitated by the {@link NavigationController},
   * specifically by calling its {@code navigateToGameSelection()} method.
   */
  public void requestGoToGameSelection() {
    LOGGER.info("BoardGameController: Go to Game Selection requested.");
    navigationController.navigateToGameSelection();
  }

  /**
   * Handles a request to navigate away from the current game screen and return to the
   * main menu or title screen of the application. The navigation is managed
   * by the {@link NavigationController} through its {@code navigateToTitleScreen()} method.
   */
  public void requestGoToMainMenu() {
    LOGGER.info("BoardGameController: Go to Main Menu requested.");
    navigationController.navigateToTitleScreen();
  }
}