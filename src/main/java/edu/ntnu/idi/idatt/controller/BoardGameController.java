package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.application.Platform;

/**
 * Controls the interactions on the game screen.
 * Connects the GameScreenView to the BoardGame and handles user input.
 */
public class BoardGameController {
  private static final Logger LOGGER = Logger.getLogger(BoardGameController.class.getName());

  protected final GenericBoardGameView view;
  protected final BoardGame boardGame;
  protected final NavigationController navigationController;

  /**
   * Constructor for GameScreenController.
   *
   * @param view The associated GameScreenView.
   * @param boardGame The board game model.
   * @param navigationController The controller for screen navigation.
   */
  public BoardGameController(GenericBoardGameView view, BoardGame boardGame, NavigationController navigationController) {
    if (view == null || boardGame == null || navigationController == null) {
      throw new IllegalArgumentException("View, BoardGame, and NavigationController cannot be null.");
    }
    this.view = view;
    this.boardGame = boardGame;
    this.navigationController = navigationController;

    this.view.setController(this);

    initializeGameView();
  }

  /**
   * Initializes the view or performs any setup needed when the game screen loads.
   */
  protected void initializeGameView() {
    LOGGER.info("GameScreenController initialized. Updating view with initial state.");
    logCurrentGameState();
  }

  /**
   * Handles the action triggered when the "Play Turn" button is clicked in the view.
   */
  public void onPlayTurn() {

  }

  /**
   * Executes the core logic for a single player's turn.
   */
  protected void executePlayerTurn() {

  }

  /**
   * Logs the current state of the game for debugging.
   */
  protected void logCurrentGameState() {
    LOGGER.info("--- Current Game State ---");
    LOGGER.info("Game Over: " + boardGame.isGameOver());

    Player currentPlayer = boardGame.getCurrentPlayer();
    LOGGER.info("Current Player: " + (currentPlayer != null ? currentPlayer.getName() : "None"));

    // Log player positions
    for (Player p : boardGame.getPlayers()) {
      LOGGER.info("  - " + p.getName() + " at tile " +
          (p.getCurrentTile() != null ? p.getCurrentTile().getTileId() : "unknown"));
    }

    LOGGER.info("Round: " + boardGame.getRoundCount());
    LOGGER.info("--------------------------");
  }

  /**
   * Handles the end of the game.
   */
  protected void handleGameOver() {

  }

  /**
   * Navigates the user back to the game selection screen.
   */
  public void onBackToMenu() {

  }

  /**
   * Provides access to the underlying BoardGame model.
   *
   * @return The BoardGame instance being controlled.
   */
  public BoardGame getBoardGame() {
    return boardGame;
  }
}