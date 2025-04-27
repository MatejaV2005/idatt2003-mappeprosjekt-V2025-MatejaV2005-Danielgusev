//package edu.ntnu.idi.idatt.controller;
//
//import edu.ntnu.idi.idatt.model.Board;
//import edu.ntnu.idi.idatt.model.BoardGame;
//import edu.ntnu.idi.idatt.model.GameEngine;
//import edu.ntnu.idi.idatt.model.player_type.Player;
//import edu.ntnu.idi.idatt.view.screens.GameScreenView;
//import javafx.application.Platform;
//import java.util.Arrays; // Import Arrays for logging dice values
//
///**
// * Controls the interactions on the game screen.
// * Connects the GameScreenView to the GameEngine and handles user input.
// * Designed as a base class for different board game controllers.
// */
//public class GameScreenController {
//
//  protected final GameScreenView view;
//  protected final GameEngine gameEngine;
//  protected final NavigationController navigationController;
//
//  /**
//   * Constructor for GameScreenController.
//   *
//   * @param view               The associated GameScreenView.
//   * @param gameEngine         The game logic engine.
//   * @param navigationController The controller responsible for screen navigation.
//   */
//  public GameScreenController(GameScreenView view, GameEngine gameEngine,
//      NavigationController navigationController) {
//    if (view == null || gameEngine == null || navigationController == null) {
//      throw new IllegalArgumentException("View, GameEngine, and NavigationController cannot be null.");
//    }
//    this.view = view;
//    this.gameEngine = gameEngine;
//    this.navigationController = navigationController;
//
//    // Connect this controller to the view's action handlers
//    this.view.setController(this);
//
//    // Perform initial setup if needed (e.g., display initial state)
//    initializeGameView();
//  }
//
//  /**
//   * Initializes the view or performs any setup needed when the game screen loads.
//   * Currently, it triggers an initial state update log.
//   */
//  protected void initializeGameView() {
//    // Log initial state - Later, the view could observe this state
//    System.out.println("GameScreenController initialized. Initial state update needed in view.");
//    logCurrentGameState();
//  }
//
//  /**
//   * Handles the action triggered when the "Play Turn" button is clicked in the view.
//   * This is the primary method connected via view.setController().
//   */
//  public void onPlayTurn() {
//    System.out.println("Play Turn button clicked."); // Log button press
//
//    if (gameEngine.isGameOver()) {
//      System.out.println("Game is already over. No action taken.");
//      handleGameOver(); // Show game over info if needed again
//      return;
//    }
//
//    // Execute the logic for a single turn
//    executePlayerTurn();
//
//    // Log the new game state. The view would need to react to this state change.
//    System.out.println("Turn executed. Requesting view update based on new state.");
//    logCurrentGameState(); // Log state for debugging; view would read state
//
//    // Check if the game ended after this turn
//    if (gameEngine.isGameOver()) {
//      // Use Platform.runLater if game over handling involves UI updates from a non-FX thread
//      Platform.runLater(this::handleGameOver);
//    } else {
//      Player nextPlayer = gameEngine.getCurrentPlayer();
//      if (nextPlayer != null) {
//        System.out.println("Next turn: " + nextPlayer.getName());
//      }
//    }
//  }
//
//  /**
//   * Executes the core logic for a single player's turn using the GameEngine.
//   * This method can be overridden by subclasses for game-specific turn logic.
//   */
//  protected void executePlayerTurn() {
//    Player currentPlayer = gameEngine.getCurrentPlayer();
//    if (currentPlayer == null) {
//      System.err.println("Error: Could not get current player.");
//      return;
//    }
//    System.out.println("Executing turn for: " + currentPlayer.getName());
//
//    try {
//      // 1. Roll the dice
//      int roll = gameEngine.rollDice();
//      System.out.println(currentPlayer.getName() + " rolled: " + Arrays.toString(gameEngine.getLastDiceRollValues()) + " (Sum: " + roll + ")");
//
//      // 2. Move the player
//      gameEngine.movePlayer(currentPlayer, roll);
//      System.out.println(currentPlayer.getName() + " moved to tile: " + currentPlayer.getCurrentTileIndex());
//
//      // 3. Process actions on the landing tile (e.g., snakes, ladders)
//      System.out.println("Processing tile " + currentPlayer.getCurrentTileIndex() + " for " + currentPlayer.getName());
//      gameEngine.processCurrentTile(currentPlayer);
//      System.out.println(currentPlayer.getName() + " is now effectively on tile: " + currentPlayer.getCurrentTileIndex());
//
//
//      // 4. Switch to the next player (assuming GameEngine handles this)
//      gameEngine.nextTurn();
//
//    } catch (Exception e) {
//      System.err.println("Error during player turn execution: " + e.getMessage());
//      e.printStackTrace(); // Log stack trace for detailed debugging
//      // Optionally, handle specific game exceptions here
//    }
//  }
//
//  /**
//   * Logs the current state of the game for debugging.
//   * In a full implementation, the View would observe the GameEngine's state
//   * or the Controller would provide data transfer objects to the View.
//   */
//  protected void logCurrentGameState() {
//    System.out.println("--- Current Game State ---");
//    System.out.println("Game Over: " + gameEngine.isGameOver());
//    Player currentPlayer = gameEngine.getCurrentPlayer();
//    System.out.println("Current Player: " + (currentPlayer != null ? currentPlayer.getName() : "None"));
//    System.out.println("Last Dice Roll: " + Arrays.toString(gameEngine.getLastDiceRollValues()));
//    // Log player positions
//    if (gameEngine.getGame() != null && gameEngine.getGame().getPlayers() != null) {
//      for (Player p : gameEngine.getGame().getPlayers()) {
//        System.out.println("  - " + p.getName() + " at tile " + p.getCurrentTileIndex());
//      }
//    } else {
//      System.out.println("  (Could not retrieve player list)");
//    }
//    System.out.println("--------------------------");
//  }
//
//
//  /**
//   * Handles the end of the game. Currently logs the winner.
//   * Can be overridden for custom game-over screens or actions.
//   */
//  protected void handleGameOver() {
//    Player winner = gameEngine.getWinner();
//    String message = (winner != null) ? "Game Over! Winner: " + winner.getName() : "Game Over! It's a draw or winner unknown.";
//    System.out.println(message);
//    // Here you might trigger a UI update (e.g., show a dialog) if the view supports it,
//    // or navigate away.
//    // Example: showGameOverPopup(message); // Requires implementation in view or separate service
//  }
//
//  /**
//   * Navigates the user back to the game selection screen using the NavigationController.
//   * This could be connected to a 'Back' button in the GameScreenView if one existed.
//   */
//  public void onBackToMenu() {
//    System.out.println("Navigating back to game selection.");
//    navigationController.navigateToGameSelection();
//  }
//
//  /**
//   * Provides access to the underlying BoardGame model via the GameEngine.
//   *
//   * @return The BoardGame instance being controlled.
//   */
//  public BoardGame getGame() {
//    // Delegate to gameEngine to get the game instance
//    return gameEngine.getGame();
//  }
//}