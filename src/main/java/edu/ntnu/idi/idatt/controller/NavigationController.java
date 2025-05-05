package edu.ntnu.idi.idatt.controller;

import static edu.ntnu.idi.idatt.view.utils.AlertHelper.showErrorAlert;

import edu.ntnu.idi.idatt.factory.BoardGameFactory;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import edu.ntnu.idi.idatt.view.screens.GameSelectionView;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import edu.ntnu.idi.idatt.view.screens.TitleScreenView;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Controller responsible for navigating between different screens of the application
 * and managing the game lifecycle.
 */
public class NavigationController {
  private static final Logger LOGGER = Logger.getLogger(NavigationController.class.getName());

  private final Stage primaryStage;
  private Scene titleScene;
  private Scene gameSelectionScene;
  private Scene gameSetupScene;
  private Scene boardGameScene;
  // More scenes...

  // Player manager for GameSetupController
  private final PlayerManager playerManager = PlayerManager.getInstance();
  private final BoardGameFactory boardGameFactory = new BoardGameFactory();
  private BoardGame currentBoardGame;

  @SuppressWarnings("FieldCanBeLocal")
  private TitleScreenController titleController;

  @SuppressWarnings("FieldCanBeLocal")
  private GameSelectionController gameModeController;

  @SuppressWarnings("FieldCanBeLocal")
  private GameSetupController gameSetupController;

  @SuppressWarnings("FieldCanBeLocal")
  private BoardGameController gameScreenController;

  /**
   * Creates a new NavigationController.
   *
   * @param primaryStage The primary stage of the application
   */
  public NavigationController(Stage primaryStage) {
    this.primaryStage = primaryStage;
    initializeScenes();
  }

  /**
   * Initializes all scenes and their controllers.
   */
  private void initializeScenes() {
    // Create and initialize all scenes
    TitleScreenView titleScreenView = new TitleScreenView();
    this.titleScene = titleScreenView.getScene();

    GameSelectionView gameModeView = new GameSelectionView();
    this.gameSelectionScene = gameModeView.getScene();

    GameSetupView gameSetupView = new GameSetupView();
    this.gameSetupScene = gameSetupView.getScene();

    GenericBoardGameView gameScreenView = new GenericBoardGameView();
    this.boardGameScene = gameScreenView.getScene();

    // Connect views with their controllers and pass this NavigationController
    titleController = new TitleScreenController(titleScreenView, this);
    gameModeController = new GameSelectionController(gameModeView, this);

    // Pass the primary stage to the GameSetupController for dialog display
    gameSetupController = new GameSetupController(gameSetupView, this, playerManager, primaryStage);
  }

  /**
   * Navigates to the title screen.
   */
  public void navigateToTitleScreen() {
    primaryStage.setScene(titleScene);
    primaryStage.setTitle("Board Game - Title");
  }

  /**
   * Navigates to the game selection screen.
   */
  public void navigateToGameSelection() {
    primaryStage.setScene(gameSelectionScene);
    primaryStage.setTitle("Board Game - Select Game");
  }

  /**
   * Navigates to the game setup screen.
   */
  public void navigateToGameSetup() {
    primaryStage.setScene(gameSetupScene);
    primaryStage.setTitle("Board Game - Setup");

    gameSetupController.refreshCurrentPlayersList();
    playerManager.clearCurrentPlayers();

  }

  /**
   * Starts a new game with the specified difficulty.
   *
   * @param difficulty The game difficulty
   */
  public void startNewGame(GameType game, String difficulty) {
    LOGGER.log(Level.INFO, "Starting new game with difficulty: {0}", difficulty);

    try {
      currentBoardGame = boardGameFactory.createGame(game, difficulty);


      playerManager.getPlayers().forEach(player -> {
        currentBoardGame.addPlayer(player);
        LOGGER.log(Level.INFO, "Added player: {0}", player.getName());
      });

      // Start the game
      currentBoardGame.startGame();

      // Initialize the game screen controller with the new board game
      GenericBoardGameView genericBoardGameView = new GenericBoardGameView();
      this.boardGameScene = genericBoardGameView.getScene();

      gameScreenController = new BoardGameController(
          genericBoardGameView,
          currentBoardGame,
          this
      );

      // Navigate to the game screen
      primaryStage.setScene(boardGameScene);
      primaryStage.setTitle("Board Game - Playing");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error starting new game", e);
      showErrorAlert("Game Start Error", "Could not start game: " + e.getMessage());
    }
  }

  /**
   * Starts a custom game with the specified board.
   *
   * @param customBoard The custom board to use
   */
  public void startCustomGame(Board customBoard) {
    LOGGER.log(Level.INFO, "Starting custom game with custom board");

    try {
      // Create a new board game with the custom board using the factory
      // Note: You'll need to add this method to your BoardGameFactory
      currentBoardGame = boardGameFactory.createSnakesAndLaddersGame();

      // Add players from the player manager to the board game
      playerManager.getPlayers().forEach(player -> {
        currentBoardGame.addPlayer(player);
        LOGGER.log(Level.INFO, "Added player: {0}", player.getName());
      });

      // Start the game
      currentBoardGame.startGame();

      // Initialize the game screen controller with the new board game
      GenericBoardGameView genericBoardGameView = new GenericBoardGameView();
      this.boardGameScene = genericBoardGameView.getScene();

      gameScreenController = new BoardGameController(
          genericBoardGameView,
          currentBoardGame,
          this
      );

      // Navigate to the game screen
      primaryStage.setScene(boardGameScene);
      primaryStage.setTitle("Board Game - Custom Game");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error starting custom game", e);
      showErrorAlert("Game Start Error", "Could not start custom game: " + e.getMessage());
    }
  }

  /**
   * Exits the application.
   */
  public void exitApp() {
    javafx.application.Platform.exit();
  }

  /**
   * Navigates to the settings screen.
   */
  public void navigateToSettings() {
    System.out.println("navigated to settings");
  }


}