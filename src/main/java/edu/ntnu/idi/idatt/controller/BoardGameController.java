package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import edu.ntnu.idi.idatt.view.utils.AlertHelper;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Controller responsible for handling user interactions within the main game screen ({@link GenericBoardGameView}).
 * It acts as the intermediary between the game view and the game model ({@link BoardGame}),
 * translating user actions (like button clicks) into calls on the game model.
 * It also coordinates with the {@link NavigationController} for screen transitions away from the game screen.
 */
public class BoardGameController {
  private static final Logger LOGGER = Logger.getLogger(BoardGameController.class.getName());

  private final GenericBoardGameView view;
  private final BoardGame boardGame;
  private final NavigationController navigationController;

  /**
   * Constructs a new BoardGameController.
   * Establishes connections between the view, model, and navigation logic.
   *
   * @param view The associated {@link GenericBoardGameView}. Must not be null.
   * @param boardGame The {@link BoardGame} model instance. Must not be null.
   * @param navigationController The {@link NavigationController} for handling screen changes. Must not be null.
   * @throws NullPointerException if any argument is null.
   */
  public BoardGameController(GenericBoardGameView view, BoardGame boardGame, NavigationController navigationController) {
    this.view = Objects.requireNonNull(view, "View cannot be null in BoardGameController constructor.");
    this.boardGame = Objects.requireNonNull(boardGame, "BoardGame cannot be null in BoardGameController constructor.");
    this.navigationController = Objects.requireNonNull(navigationController, "NavigationController cannot be null in BoardGameController constructor.");

    // Set this controller instance on the view so the view can call methods like onPlayTurn()
    this.view.setController(this);

    initializeGameView(); // Perform any initial setup related to the controller/view link
  }

  /**
   * Performs any initial setup needed when the controller and view are linked.
   * Currently logs the initialization and the initial game state.
   */
  protected void initializeGameView() {
    LOGGER.info("BoardGameController initialized and linked with view.");
    logCurrentGameState(); // Log the state when the game screen is first shown
  }

  /**
   * Handles the user action of initiating the next turn, typically triggered by a button click.
   * It instructs the {@link BoardGame} model to advance the game state by one turn.
   * Includes basic error handling if the turn cannot be played.
   */
  public void onPlayTurn() {
    LOGGER.fine("onPlayTurn() called.");
    try {
      if (boardGame != null && !boardGame.isGameOver()) {
        boardGame.playNextTurn();
        logCurrentGameState();
      } else if (boardGame != null && boardGame.isGameOver()) {
        LOGGER.warning("Attempted to play turn, but game is already over.");

      } else {
        LOGGER.severe("Cannot play turn: boardGame model is null.");
      }
    } catch (IllegalStateException e) {
      LOGGER.log(Level.SEVERE, "Error executing playNextTurn: " + e.getMessage(), e);
      AlertHelper.showErrorAlert("Game Error", "Could not play turn: " + e.getMessage());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error during onPlayTurn", e);
      AlertHelper.showErrorAlert("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
    }
  }

  // executePlayerTurn() is removed as the logic is within boardGame.playNextTurn()

  /**
   * Logs the current state of the game model for debugging purposes.
   * Includes game over status, current player, player positions, and round count.
   */
  protected void logCurrentGameState() {

  }


  /**
   * Handles the user action to navigate away from the game screen, typically back
   * to the game selection or main menu. Delegates the navigation task to the
   * {@link NavigationController}.
   */
  public void onBackToMenu() {

  }


  public BoardGame getBoardGame() {

  }
}
