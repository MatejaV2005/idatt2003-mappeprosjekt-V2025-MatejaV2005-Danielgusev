package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.screens.BoardGameView;
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

  protected final BoardGameView view;
  protected final BoardGame boardGame;
  protected final NavigationController navigationController;

  /**
   * Constructor for GameScreenController.
   *
   * @param view The associated GameScreenView.
   * @param boardGame The board game model.
   * @param navigationController The controller for screen navigation.
   */
  public BoardGameController(BoardGameView view, BoardGame boardGame, NavigationController navigationController) {
    if (view == null || boardGame == null || navigationController == null) {
      throw new IllegalArgumentException("View, BoardGame, and NavigationController cannot be null.");
    }
    this.view = view;
    this.boardGame = boardGame;
    this.navigationController = navigationController;

    // Connect this controller to the view's action handlers
    this.view.setController(this);

    // Perform initial setup
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
    LOGGER.info("Play Turn button clicked.");

    if (boardGame.isGameOver()) {
      LOGGER.info("Game is already over. No action taken.");
      handleGameOver();
      return;
    }

    // Execute the logic for a single turn
    executePlayerTurn();

    // Log the new game state
    LOGGER.info("Turn executed. Updating view with new state.");
    logCurrentGameState();

    // Check if the game ended after this turn
    if (boardGame.isGameOver()) {
      Platform.runLater(this::handleGameOver);
    } else {
      Player nextPlayer = boardGame.getCurrentPlayer();
      if (nextPlayer != null) {
        LOGGER.info("Next turn: " + nextPlayer.getName());
      }
    }
  }

  /**
   * Executes the core logic for a single player's turn.
   */
  protected void executePlayerTurn() {
    Player currentPlayer = boardGame.getCurrentPlayer();
    if (currentPlayer == null) {
      LOGGER.warning("Error: Could not get current player.");
      return;
    }

    LOGGER.info("Executing turn for: " + currentPlayer.getName());

    try {
      // Play the next turn using the boardGame's logic
      boardGame.playNextTurn();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error during player turn execution", e);
    }
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
    Optional<Player> winner = boardGame.getWinner();
    String message = winner.isPresent() ?
        "Game Over! Winner: " + winner.get().getName() :
        "Game Over! It's a draw or winner unknown.";
    LOGGER.info(message);

    // Here you would update the view with the game over state
  }

  /**
   * Navigates the user back to the game selection screen.
   */
  public void onBackToMenu() {
    LOGGER.info("Navigating back to game selection.");
    navigationController.navigateToGameSelection();
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