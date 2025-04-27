package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.components.gameSelection.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.DifficultySelectionPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.decorator.ButtonDecorator;
import edu.ntnu.idi.idatt.view.decorator.HoverEffectDecorator;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * View for the game setup screen where players can select difficulty, manage players,
 * and view board information before starting a game.
 */
public class GameSetupView {

  private final Scene scene;
  private final BorderPane root;
  private static final String CSS_PATH = "/edu/ntnu/idi/idatt/view/resources/GameSetup/gameSetupStyle.css";

  private final DifficultySelectionPanel difficultySelectionPanel;
  private final PlayerManagementPanel playerManagementPanel;
  private final GameInfoPanel gameInfoPanel;

  private final Label gameModeLabel;
  private final Button startGameButton;
  private final Button backButton;
  private final HBox bottomActionBar;
  private final HBox headerBox;

  /**
   * Constructs a new GameSetupView with responsive layout.
   */
  public GameSetupView() {
    root = new BorderPane();
    root.setPadding(new Insets(20));

    gameModeLabel = new Label("Snakes & Ladders - Setup");
    gameModeLabel.getStyleClass().add("game-mode-title");

    headerBox = new HBox(gameModeLabel);
    headerBox.setAlignment(Pos.CENTER);
    headerBox.setPadding(new Insets(10, 0, 20, 0));
    headerBox.getStyleClass().add("header-box");
    root.setTop(headerBox);

    // Create components using factories and decorators
    ButtonFactory buttonFactory = new ButtonFactory();
    ButtonDecorator decorator = new HoverEffectDecorator();

    difficultySelectionPanel = new DifficultySelectionPanel();
    playerManagementPanel = new PlayerManagementPanel(buttonFactory);
    gameInfoPanel = new GameInfoPanel();

    // Create buttons with decorator
    startGameButton = decorator.decorate(buttonFactory.createStandardButton("Start Game"));
    startGameButton.getStyleClass().add("primary-button");

    backButton = decorator.decorate(buttonFactory.createStandardButton("Back"));
    backButton.getStyleClass().add("secondary-button");

    setupResponsiveLayout();

    // Create bottom action bar
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    bottomActionBar = new HBox(20, backButton, spacer, startGameButton);
    bottomActionBar.setAlignment(Pos.CENTER);
    bottomActionBar.setPadding(new Insets(20, 0, 0, 0));
    root.setBottom(bottomActionBar);

    scene = new Scene(root, 1280, 720);
    applyStylesheets();
  }

  /**
   * Sets up the responsive layout for the main components.
   * Uses proportional sizing (25%, 50%, 25%) for the three panels.
   */
  private void setupResponsiveLayout() {
    HBox mainContent = new HBox(20);
    mainContent.setPadding(new Insets(10));

    StackPane leftWrapper = new StackPane(difficultySelectionPanel);
    StackPane centerWrapper = new StackPane(playerManagementPanel);
    StackPane rightWrapper = new StackPane(gameInfoPanel);

    // Apply styling to wrappers
    leftWrapper.getStyleClass().add("panel-wrapper");
    centerWrapper.getStyleClass().add("panel-wrapper");
    rightWrapper.getStyleClass().add("panel-wrapper");

    mainContent.getChildren().addAll(leftWrapper, centerWrapper, rightWrapper);

    HBox.setHgrow(leftWrapper, Priority.ALWAYS);
    HBox.setHgrow(centerWrapper, Priority.ALWAYS);
    HBox.setHgrow(rightWrapper, Priority.ALWAYS);

    leftWrapper.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));
    centerWrapper.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.5));
    rightWrapper.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));

    leftWrapper.setMinWidth(200);
    centerWrapper.setMinWidth(400);
    rightWrapper.setMinWidth(200);

    difficultySelectionPanel.prefHeightProperty().bind(mainContent.heightProperty());
    playerManagementPanel.prefHeightProperty().bind(mainContent.heightProperty());
    gameInfoPanel.prefHeightProperty().bind(mainContent.heightProperty());

    // Add the main content to the root layout
    root.setCenter(mainContent);
  }

  /**
   * Updates the game mode label.
   *
   * @param gameMode The name of the selected game mode
   */
  public void updateGameMode(String gameMode) {
    gameModeLabel.setText(gameMode + " - Setup");
  }

  /**
   * Configures the stage for fullscreen display.
   *
   * @param stage The primary stage of the application
   */
  public void applyFullscreen(Stage stage) {
    stage.setMaximized(true);
  }

  /**
   * Applies CSS stylesheets to the scene.
   */
  private void applyStylesheets() {
    try {
      scene.getStylesheets().add(ResourceLoader.loadCssResource(CSS_PATH));
    } catch (BoardGameResourceException e) {
      System.err.println("Failed to load CSS: " + e.getMessage());
    }
  }

  // --- Getters for Controller Access ---

  public Scene getScene() {
    return scene;
  }

  public DifficultySelectionPanel getDifficultySelectionPanel() {
    return difficultySelectionPanel;
  }

  public PlayerManagementPanel getPlayerManagementPanel() {
    return playerManagementPanel;
  }

  public GameInfoPanel getGameInfoPanel() {
    return gameInfoPanel;
  }

  public Button getStartGameButton() {
    return startGameButton;
  }

  public Button getBackButton() {
    return backButton;
  }
}