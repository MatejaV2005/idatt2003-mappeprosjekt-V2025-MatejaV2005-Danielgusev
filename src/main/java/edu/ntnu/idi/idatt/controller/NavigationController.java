package edu.ntnu.idi.idatt.controller;

import static edu.ntnu.idi.idatt.utils.AlertHelper.showErrorAlert;

import edu.ntnu.idi.idatt.factory.BoardGameFactory;
import edu.ntnu.idi.idatt.factory.GameViewFactory;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.service.PlayerManager;
import edu.ntnu.idi.idatt.view.screens.GameSelectionView;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import edu.ntnu.idi.idatt.view.screens.GenericBoardGameView;
import edu.ntnu.idi.idatt.view.screens.TitleScreenView;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Manages navigation between application screens and orchestrates game setup and launch.
 *
 * <p>Holds the primary {@link Stage}, pre-creates all Scenes (title, selection, setup),
 * and delegates view updates, logging, and error handling. Uses {@link BoardGameFactory}
 * and {@link GameViewFactory} to build and display games.
 */
public class NavigationController {

  private static final Logger LOG = Logger.getLogger(NavigationController.class.getName());

  private final Stage primaryStage;
  private Scene titleScene;
  private Scene gameSelectionScene;
  private Scene gameSetupScene;

  private final PlayerManager playerManager = PlayerManager.getInstance();
  private final BoardGameFactory gameFactory = new BoardGameFactory();
  private final GameViewFactory viewFactory = new GameViewFactory();

  private BoardGameController gameScreenController;
  private BoardGame currentBoardGame;
  private GameSetupController gameSetupControllerInstance;
  private GameSetupView gameSetupViewInstance;

  /**
   * Creates a NavigationController for the given primary stage.
   *
   * <p>Also initializes all core Scenes and their controllers.
   *
   * @param primaryStage the main application Stage; must not be null
   */
  public NavigationController(Stage primaryStage) {
    this.primaryStage = Objects.requireNonNull(
        primaryStage, "primaryStage must not be null");
    initializeScenes();
  }

  /**
   * Switches to the title screen.
   */
  public void navigateToTitleScreen() {
    primaryStage.setScene(titleScene);
    primaryStage.setTitle("Board Game • Title Screen");
    LOG.fine("Navigated to Title Screen.");
  }

  /**
   * Switches to the game selection screen.
   */
  public void navigateToGameSelection() {
    primaryStage.setScene(gameSelectionScene);
    primaryStage.setTitle("Board Game • Select Game");
    LOG.fine("Navigated to Game Selection Screen.");
  }

  /**
   * Prepares and navigates to the game setup screen for the specified type.
   *
   * <p>Validates that {@code gameType} is not null, clears current players,
   * and instructs the setup controller to configure the UI. If setup components
   * are missing, re-initializes scenes and falls back to the title screen on error.
   *
   * @param gameType the game type to set up; must not be null
   */
  public void navigateToGameSetup(GameType gameType) {
    Objects.requireNonNull(gameType, "gameType must not be null");
    LOG.log(Level.INFO, "Navigating to Game Setup for game type: {0}", gameType);

    if (gameSetupControllerInstance == null || gameSetupViewInstance == null) {
      LOG.severe("Setup components missing; reinitializing scenes.");
      initializeScenes();
      if (gameSetupControllerInstance == null || gameSetupViewInstance == null) {
        showErrorAlert("Critical Error",
            "Game setup components could not be initialized.");
        navigateToTitleScreen();
        return;
      }
    }

    gameSetupControllerInstance.prepareGuiForGameType(gameType);
    primaryStage.setScene(gameSetupScene);
    primaryStage.setTitle("Board Game • " + getGameTypeName(gameType) + " Setup");

    playerManager.clearCurrentPlayers();
    gameSetupControllerInstance.refreshPlayerListViews();

    LOG.log(Level.FINE, "Navigated to Game Setup Screen for {0}", gameType);
  }

  /**
   * Starts a new game with the given type and configuration.
   *
   * <p>Attempts to create a {@link BoardGame} via {@link BoardGameFactory}.
   * On success, sets up and displays the game screen; on failure, shows
   * an error alert and returns to setup.
   *
   * @param type the type of game to start; must not be null
   * @param difficultyOrConfig the chosen difficulty or config; must not be null
   */
  public void startNewGame(GameType type, String difficultyOrConfig) {
    Objects.requireNonNull(type, "type must not be null");
    Objects.requireNonNull(difficultyOrConfig, "difficultyOrConfig must not be null");

    LOG.log(Level.INFO,
        "Attempting to start new game: {0}, Config: {1}",
        new Object[] {type, difficultyOrConfig});
    try {
      currentBoardGame = gameFactory.createGame(type, difficultyOrConfig);
      if (currentBoardGame == null) {
        LOG.log(Level.SEVERE,
            "GameFactory returned null for type: {0}, config: {1}",
            new Object[] {type, difficultyOrConfig});
        showErrorAlert("Game Creation Error",
            "Failed to create the game. Please check the factory for " + type);
        navigateToGameSetup(type);
        return;
      }
      setupAndShowGameScreen();
      LOG.log(Level.INFO,
          "Successfully initiated new {0} game with config {1}",
          new Object[] {type, difficultyOrConfig});
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Could not start new game of type {0}", new Object[] {type});
      LOG.log(Level.SEVERE, "Exception on starting new game", e);
      showErrorAlert("Game Start Error",
          "Could not start new game: " + e.getMessage());
      navigateToGameSetup(type);
    }
  }

  /**
   * Starts a custom Snakes &amp; Ladders game with a provided board.
   *
   * @param customBoard the custom board to use; must not be null
   */
  public void startCustomGame(Board customBoard) {
    Objects.requireNonNull(customBoard, "customBoard must not be null");
    LOG.info("Attempting to start custom game.");
    try {
      currentBoardGame = gameFactory.createSnakesAndLaddersGame(customBoard);
      if (currentBoardGame == null) {
        LOG.severe("GameFactory returned null for custom board.");
        showErrorAlert("Game Creation Error",
            "Failed to create custom game model.");
        navigateToGameSetup(GameType.SNAKES_AND_LADDERS);
        return;
      }
      setupAndShowGameScreen();
      LOG.info("Successfully started custom game.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Could not start custom game.", e);
      showErrorAlert("Game Start Error",
          "Could not start custom game: " + e.getMessage());
      navigateToGameSetup(GameType.SNAKES_AND_LADDERS);
    }
  }

  /**
   * Exits the application by shutting down the JavaFX Platform.
   */
  public void exitApp() {
    LOG.info("Exiting application.");
    Platform.exit();
  }


  private void initializeScenes() {
    TitleScreenView titleView = new TitleScreenView();
    titleScene = titleView.getScene();
    new TitleScreenController(titleView, this);

    GameSelectionView selectView = new GameSelectionView();
    gameSelectionScene = selectView.getScene();
    new GameSelectionController(selectView, this);

    gameSetupViewInstance = new GameSetupView();
    gameSetupScene = gameSetupViewInstance.getScene();
    gameSetupControllerInstance =
        new GameSetupController(gameSetupViewInstance, this, playerManager, primaryStage);

    LOG.info("Core scenes initialized (Title, GameSelection, GameSetup).");
  }

  private void setupAndShowGameScreen() {
    if (currentBoardGame == null) {
      LOG.severe("Cannot setup game screen: model is null.");
      showErrorAlert("Internal Error",
          "Game model was not initialized by the factory.");
      navigateToGameSetup(
          gameSetupControllerInstance != null
              ? gameSetupControllerInstance.getCurrentGameType()
              : GameType.SNAKES_AND_LADDERS);
      return;
    }

    playerManager
        .getPlayers()
        .forEach(
            player -> {
              if (player != null) {
                currentBoardGame.addPlayer(player);
              } else {
                LOG.warning("Skipped null player during game start.");
              }
            });

    currentBoardGame.startGame();

    GenericBoardGameView gameView =
        viewFactory.createViewFor(currentBoardGame.getGameType());
    if (gameView == null) {
      showErrorAlert("UI Error",
          "Could not create the game screen. Check GameViewFactory.");
      navigateToGameSetup(currentBoardGame.getGameType());
      return;
    }
    Scene boardGameScene = gameView.getScene();
    gameScreenController =
        new BoardGameController(gameView, currentBoardGame, this);
    gameView.initializeView(currentBoardGame);

    primaryStage.setScene(boardGameScene);
    primaryStage.setTitle("Board Game • Playing " + getGameTypeName(
        currentBoardGame.getGameType()));
  }

  private String getGameTypeName(GameType gameType) {
    if (gameType == null) {
      return "Game";
    }
    return switch (gameType) {
      case SNAKES_AND_LADDERS -> "Snakes & Ladders";
      case ASTRO_RALLY -> "Astro Rally";
    };
  }
}
