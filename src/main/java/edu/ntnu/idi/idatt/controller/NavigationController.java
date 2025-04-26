package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.view.screens.GameScreenView;
import edu.ntnu.idi.idatt.view.screens.GameSelectionView;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import edu.ntnu.idi.idatt.view.screens.TitleScreenView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationController {
  private Stage primaryStage;
  private Scene titleScene;
  private Scene gameSelectionScene;
  private Scene gameSetupScene;
  private Scene gameScreenScene;
  // More scenes...

  @SuppressWarnings("FieldCanBeLocal")
  private TitleScreenController titleController;

  @SuppressWarnings("FieldCanBeLocal")
  private GameSelectionController gameModeController;

  public NavigationController(Stage primaryStage) {
    this.primaryStage = primaryStage;
    initializeScenes();
  }

  private void initializeScenes() {
    // Create and initialize all scenes
    TitleScreenView titleScreenView = new TitleScreenView();
    this.titleScene = titleScreenView.getScene();

    GameSelectionView gameModeView = new GameSelectionView();
    this.gameSelectionScene = gameModeView.getScene();

    GameSetupView gameSetupView = new GameSetupView();
    this.gameSetupScene = gameSetupView.getScene();

    GameScreenView gameScreenView = new GameScreenView();
    this.gameScreenScene = gameScreenView.getScene();

    // Connect views with their controllers and pass this NavigationController
    titleController = new TitleScreenController(titleScreenView, this);
    gameModeController = new GameSelectionController(gameModeView, this);
  }

  public void navigateToTitleScreen() {
    primaryStage.setScene(titleScene);
  }

  public void navigateToGameSelection() {
    primaryStage.setScene(gameSelectionScene);
  }

  public void navigateToGameSetup() {
    primaryStage.setScene(gameSetupScene);
  }

  public void exitApp() {
    javafx.application.Platform.exit();
  }

  public void navigateToSettings() {
    System.out.println("navigated to settings");
  }
}
