package edu.ntnu.idi.idatt.controller;

import static edu.ntnu.idi.idatt.view.utils.AlertHelper.showErrorAlert;

import edu.ntnu.idi.idatt.factory.BoardGameFactory;
import edu.ntnu.idi.idatt.factory.GameViewFactory;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.view.screens.GameSelectionView;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import edu.ntnu.idi.idatt.view.screens.TitleScreenView;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform; // For exitApp
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Manages navigation between different screens (Scenes) of the application,
 * such as the title screen, game selection, game setup, and the main game view.
 * It also orchestrates the initialization and launching of game sessions by
 * coordinating with model factories ({@link BoardGameFactory}) and view factories
 * ({@link GameViewFactory}).
 */
public class NavigationController {
  private static final Logger LOG = Logger.getLogger(NavigationController.class.getName());

  private final Stage primaryStage;
  private Scene titleScene;
  private Scene gameSelectionScene;
  private Scene gameSetupScene;
  private Scene boardGameScene;

  private final PlayerManager playerManager = PlayerManager.getInstance();
  private final BoardGameFactory gameFactory = new BoardGameFactory();
  private final GameViewFactory viewFactory = new GameViewFactory(); // Ny factory

  private BoardGameController gameScreenController; // Holder på den aktive spillkontrolleren
  private BoardGame currentBoardGame; // Holder på den aktive spillmodellen

  /**
   * Constructs a NavigationController.
   *
   * @param primaryStage The primary {@link Stage} of the application. Must not be null.
   * @throws NullPointerException if primaryStage is null.
   */
  public NavigationController(Stage primaryStage) {
    this.primaryStage = Objects.requireNonNull(primaryStage, "PrimaryStage cannot be null.");
    initializeScenes();
  }

  /**
   * Initializes all the scenes used by the application, except for the main
   * board game scene which is created dynamically when a game starts.
   * It creates instances of the different view controllers.
   */
  private void initializeScenes() {
    // Title Screen
    TitleScreenView titleView = new TitleScreenView(); // Assuming this class exists
    titleScene = titleView.getScene();
    new TitleScreenController(titleView, this); // Assuming this class exists

    // Game Selection Screen
    GameSelectionView selectView = new GameSelectionView(); // Assuming this class exists
    gameSelectionScene = selectView.getScene();
    new GameSelectionController(selectView, this); // Assuming this class exists

    // Game Setup Screen
    GameSetupView setupView = new GameSetupView(); // Assuming this class exists
    gameSetupScene = setupView.getScene();
    // GameSetupController needs the stage for FileChooser dialogs
    new GameSetupController(setupView, this, playerManager, primaryStage);

    LOG.info("Core scenes initialized.");
  }

  /**
   * Navigates the application to the title screen.
   */
  public void navigateToTitleScreen() {
    primaryStage.setScene(titleScene);
    primaryStage.setTitle("Board Game • Title Screen");
    LOG.fine("Navigated to Title Screen.");
  }

  /**
   * Navigates the application to the game selection screen.
   */
  public void navigateToGameSelection() {
    primaryStage.setScene(gameSelectionScene);
    primaryStage.setTitle("Board Game • Select Game");
    LOG.fine("Navigated to Game Selection Screen.");
  }

  /**
   * Navigates the application to the game setup screen.
   * Clears any previously selected players from the {@link PlayerManager}.
   */
  public void navigateToGameSetup() {
    primaryStage.setScene(gameSetupScene);
    primaryStage.setTitle("Board Game • Game Setup");
    if (playerManager != null) {
      playerManager.clearCurrentPlayers();
    }
    // The GameSetupController is responsible for refreshing its player list display.
    LOG.fine("Navigated to Game Setup Screen.");
  }

  /**
   * Starts a new game of the specified type and difficulty.
   * Creates the game model using {@link BoardGameFactory}, then sets up and shows the game view.
   *
   * @param type The {@link GameType} of the game to start.
   * @param difficulty A string representing the game difficulty (e.g., "Easy", "Normal", "Hard").
   */
  public void startNewGame(GameType type, String difficulty) {
    Objects.requireNonNull(type, "GameType cannot be null for starting a new game.");
    Objects.requireNonNull(difficulty, "Difficulty cannot be null for starting a new game.");

    LOG.info("Attempting to start new game: " + type + ", Difficulty: " + difficulty);
    try {
      currentBoardGame = gameFactory.createGame(type, difficulty);
      setupAndShowGameScreen();
      LOG.log(Level.INFO, "Successfully started new {0} game with difficulty {1}", new Object[]{type, difficulty});
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Could not start new game of type " + type, e);
      showErrorAlert("Game Start Error", "Could not start new game: " + e.getMessage());
    }
  }

  /**
   * Starts a new game using a custom {@link Board} configuration.
   * Creates the game model using {@link BoardGameFactory}, then sets up and shows the game view.
   *
   * @param customBoard The custom {@link Board} to use for the game.
   */
  public void startCustomGame(Board customBoard) {
    Objects.requireNonNull(customBoard, "CustomBoard cannot be null for starting a custom game.");
    LOG.info("Attempting to start custom game.");
    try {
      currentBoardGame = gameFactory.createSnakesAndLaddersGame(customBoard); // Adjusted to use a more specific factory method
      setupAndShowGameScreen();
      LOG.info("Successfully started custom game.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Could not start custom game.", e);
      showErrorAlert("Game Start Error", "Could not start custom game: " + e.getMessage());
    }
  }

  /**
   * Private helper method to set up and display the main game screen.
   * Adds players from {@link PlayerManager} to the {@code currentBoardGame},
   * starts the game model, creates the game view using {@link GameViewFactory},
   * initializes the view, creates the {@link BoardGameController}, and sets the scene.
   *
   * @throws IllegalStateException if {@code currentBoardGame} is null.
   */
  private void setupAndShowGameScreen() {
    if (currentBoardGame == null) {
      LOG.severe("Cannot setup and show game screen: currentBoardGame is null.");
      showErrorAlert("Internal Error", "Game model was not initialized.");
      navigateToGameSetup();
      return;
    }

    if (playerManager != null) {
      playerManager.getPlayers().forEach(player -> {
        if (player != null) {
          currentBoardGame.addPlayer(player);
        } else {
          LOG.warning("Attempted to add a null player from PlayerManager.");
        }
      });
    }

    currentBoardGame.startGame();

    GenericBoardGameView gameView = viewFactory.createViewFor(currentBoardGame.getGameType());
    this.boardGameScene = gameView.getScene();

    this.gameScreenController = new BoardGameController(gameView, currentBoardGame, this);
    gameView.initializeView(currentBoardGame);

    primaryStage.setScene(boardGameScene);
    primaryStage.setTitle("Board Game • Playing " + currentBoardGame.getGameType());
  }

  /**
   * Exits the application by closing the primary stage.
   */
  public void exitApp() {
    LOG.info("Exiting application.");
    Platform.exit();
  }


}