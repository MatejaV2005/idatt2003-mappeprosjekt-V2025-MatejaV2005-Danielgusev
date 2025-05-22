package edu.ntnu.idi.idatt.controller;

import static edu.ntnu.idi.idatt.view.utils.AlertHelper.showErrorAlert;

import edu.ntnu.idi.idatt.factory.BoardGameFactory;
import edu.ntnu.idi.idatt.factory.GameViewFactory;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
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
 * Manages navigation between different screens (Scenes) of the application. It also orchestrates
 * the initialization and launching of game sessions. This version is adapted to handle dynamic
 * GameType selection for the setup screen, based on the user's working S&L version.
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
  private final GameViewFactory viewFactory = new GameViewFactory();

  private BoardGameController gameScreenController;
  private BoardGame currentBoardGame;
  private GameSetupController gameSetupControllerInstance;
  private GameSetupView gameSetupViewInstance;


  /**
   * Constructs a NavigationController.
   *
   * @param primaryStage The primary {@link Stage} of the application. Must not be null.
   */
  public NavigationController(Stage primaryStage) {
    this.primaryStage = Objects.requireNonNull(primaryStage, "PrimaryStage cannot be null.");
    initializeScenes(); // Initializes all scenes, including GameSetupView
  }

  /**
   * Initializes all scenes. GameSetupView and its controller are created once here.
   * The GameSetupView will be reconfigured by GameSetupController when navigating to it.
   */
  private void initializeScenes() {
    TitleScreenView titleView = new TitleScreenView();
    titleScene = titleView.getScene();
    new TitleScreenController(titleView, this);

    GameSelectionView selectView = new GameSelectionView();
    gameSelectionScene = selectView.getScene();
    new GameSelectionController(selectView, this); // This will call the new navigateToGameSetup(GameType)

    this.gameSetupViewInstance = new GameSetupView();
    this.gameSetupScene = gameSetupViewInstance.getScene();

    this.gameSetupControllerInstance =
        new GameSetupController(gameSetupViewInstance, this, playerManager, primaryStage);

    LOG.info("Core scenes initialized (Title, GameSelection, GameSetup).");
  }

  /** Navigates the application to the title screen. */
  public void navigateToTitleScreen() {
    primaryStage.setScene(titleScene);
    primaryStage.setTitle("Board Game • Title Screen");
    LOG.fine("Navigated to Title Screen.");
  }

  /** Navigates the application to the game selection screen. */
  public void navigateToGameSelection() {
    primaryStage.setScene(gameSelectionScene);
    primaryStage.setTitle("Board Game • Select Game");
    LOG.fine("Navigated to Game Selection Screen.");
  }

  /**
   * Navigates to the Game Setup screen, preparing it for the specified game type.
   *
   * @param gameType The {@link GameType} to set up.
   */
  public void navigateToGameSetup(GameType gameType) {
    Objects.requireNonNull(gameType, "GameType cannot be null for game setup.");
    LOG.info("Navigating to Game Setup for game type: " + gameType);

    if (gameSetupControllerInstance == null || gameSetupViewInstance == null) {
      LOG.severe("GameSetupController or GameSetupView is null. Cannot navigate to game setup.");

      initializeScenes();
      if (gameSetupControllerInstance == null || gameSetupViewInstance == null) {
        showErrorAlert("Critical Error", "Game setup components could not be initialized.");
        navigateToTitleScreen();
        return;
      }
    }

    gameSetupControllerInstance.prepareGuiForGameType(gameType);

    primaryStage.setScene(gameSetupScene);
    primaryStage.setTitle("Board Game • " + getGameTypeName(gameType) + " Setup");

    if (playerManager != null) {
      playerManager.clearCurrentPlayers();
    }

    gameSetupControllerInstance.refreshPlayerListViews();

    LOG.fine("Navigated to Game Setup Screen for " + gameType);
  }


  /**
   * Starts a new game of the specified type and difficulty/configuration.
   *
   * @param type The {@link GameType} of the game to start.
   * @param difficultyOrConfig A string representing the game config (e.g., "Easy", "default").
   */
  public void startNewGame(GameType type, String difficultyOrConfig) {
    Objects.requireNonNull(type, "GameType cannot be null for starting a new game.");
    Objects.requireNonNull(
        difficultyOrConfig, "Difficulty/Config cannot be null for starting a new game.");

    LOG.info("Attempting to start new game: " + type + ", Config: " + difficultyOrConfig);
    try {
      currentBoardGame = gameFactory.createGame(type, difficultyOrConfig);

      if (currentBoardGame == null) {
        LOG.severe(
            "BoardGameFactory.createGame returned null for type: "
                + type
                + " and config: "
                + difficultyOrConfig);
        showErrorAlert(
            "Game Creation Error",
            "Failed to create the game model. Please check factory implementation for " + type);
        navigateToGameSetup(type);
        return;
      }
      setupAndShowGameScreen();
      LOG.log(
          Level.INFO,
          "Successfully initiated new {0} game with config {1}",
          new Object[] {type, difficultyOrConfig});
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Could not start new game of type " + type, e);
      showErrorAlert("Game Start Error", "Could not start new game: " + e.getMessage());
      navigateToGameSetup(type);
    }
  }

  /**
   * Starts a new game using a custom {@link Board} configuration.
   *
   * @param customBoard The custom {@link Board} to use for the game.
   */
  public void startCustomGame(Board customBoard) {
    Objects.requireNonNull(customBoard, "CustomBoard cannot be null for starting a custom game.");
    LOG.info("Attempting to start custom game.");
    try {
      currentBoardGame = gameFactory.createSnakesAndLaddersGame(customBoard);
      if (currentBoardGame == null) {
        LOG.severe("BoardGameFactory.createSnakesAndLaddersGame returned null for custom board.");
        showErrorAlert(
            "Game Creation Error",
            "Failed to create the custom game model from board factory.");
        navigateToGameSetup(GameType.SNAKES_AND_LADDERS);
        return;
      }
      setupAndShowGameScreen();
      LOG.info("Successfully started custom game.");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Could not start custom game.", e);
      showErrorAlert("Game Start Error", "Could not start custom game: " + e.getMessage());
      navigateToGameSetup(GameType.SNAKES_AND_LADDERS);
    }
  }

  /**
   * Private helper method to set up and display the main game screen. This method ensures that the
   * view is initialized *after* the controller is set up, preserving the working order from your
   * original version.
   */
  private void setupAndShowGameScreen() {
    if (currentBoardGame == null) {
      LOG.severe(
          "Cannot setup and show game screen: currentBoardGame is null. "
              + "This indicates a problem in game creation logic (e.g., BoardGameFactory).");
      showErrorAlert("Internal Error", "Game model was not initialized by the factory.");

      GameType typeToSetup = (gameSetupControllerInstance != null) ?
          gameSetupControllerInstance.getCurrentGameType() :
          GameType.SNAKES_AND_LADDERS;
      navigateToGameSetup(typeToSetup);
      return;
    }

    if (playerManager != null) {
      playerManager
          .getPlayers()
          .forEach(
              player -> {
                if (player != null) {
                  currentBoardGame.addPlayer(player);
                } else {
                  LOG.warning("Attempted to add a null player from PlayerManager.");
                }
              });
    }

    currentBoardGame.startGame();

    GenericBoardGameView gameView = viewFactory.createViewFor(currentBoardGame.getGameType());
    if (gameView == null) {
      LOG.severe("GameViewFactory returned null for game type: " + currentBoardGame.getGameType());
      showErrorAlert("UI Error", "Could not create the game screen. Check GameViewFactory.");
      navigateToGameSetup(currentBoardGame.getGameType());
      return;
    }
    this.boardGameScene = gameView.getScene();

    this.gameScreenController = new BoardGameController(gameView, currentBoardGame, this);

    gameView.initializeView(currentBoardGame);

    primaryStage.setScene(boardGameScene);
    primaryStage.setTitle("Board Game • Playing " + getGameTypeName(currentBoardGame.getGameType()));
  }

  /** Exits the application by closing the primary stage. */
  public void exitApp() {
    LOG.info("Exiting application.");
    Platform.exit();
  }

  /**
   * Helper to get a display-friendly name for a GameType.
   *
   * @param gameType The game type.
   * @return A string representation for UI titles.
   */
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
