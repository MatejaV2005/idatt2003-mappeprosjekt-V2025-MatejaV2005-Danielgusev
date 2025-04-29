package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.GameSetupController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.observer.PlayerModelObserver;
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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View for the game setup screen where players can select difficulty, manage players,
 * and view board information before starting a game.
 */
public class GameSetupView {
  private static final Logger LOGGER = Logger.getLogger(GameSetupView.class.getName());
  private final Scene scene;
  private final BorderPane root;
  private GameSetupController controller;
  private static final String CSS_PATH = "/edu/ntnu/idi/idatt/view/resources/GameSetup/gameSetupStyle.css";

  private final DifficultySelectionPanel difficultySelectionPanel;
  private final PlayerManagementPanel playerManagementPanel;
  private final GameInfoPanel gameInfoPanel;

  private final Label gameModeLabel;
  private final Button startGameButton;
  private final Button backButton;
  private final HBox bottomActionBar;
  private final HBox headerBox;
  private final Label statusLabel;

  /**
   * Constructs a new GameSetupView with responsive layout.
   */
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

    // Status label for feedback
    statusLabel = new Label();
    statusLabel.getStyleClass().add("status-label");

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

    // Status message positioned above the action buttons
    HBox statusBox = new HBox(statusLabel);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(0, 0, 10, 0));

    bottomActionBar = new HBox(20, backButton, spacer, startGameButton);
    bottomActionBar.setAlignment(Pos.CENTER);
    bottomActionBar.setPadding(new Insets(10, 0, 0, 0));

    VBox bottomContainer = new VBox(10, statusBox, bottomActionBar);
    bottomContainer.setPadding(new Insets(10, 0, 0, 0));
    root.setBottom(bottomContainer);

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
   * Sets the controller for this view.
   *
   * @param controller The controller to set
   */
  public void setController(GameSetupController controller) {
    this.controller = controller;
    bindEventHandlers();
  }

  /**
   * Applies CSS stylesheets to the scene.
   */
  private void applyStylesheets() {
    try {
      scene.getStylesheets().add(ResourceLoader.loadCssResource(CSS_PATH));
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load CSS: " + e.getMessage(), e);
    }
  }

  /**
   * Updates the status message shown at the bottom of the screen.
   *
   * @param message The message to display
   * @param isError Whether the message is an error message
   */
  public void updateStatusMessage(String message, boolean isError) {
    statusLabel.setText(message);

    if (isError) {
      statusLabel.setStyle("-fx-text-fill: #ff6b6b;");
    } else {
      statusLabel.setStyle("-fx-text-fill: #89e075;");
    }
  }

  /**
   * Clears the status message.
   */
  public void clearStatusMessage() {
    statusLabel.setText("");
  }

  // --- Getters for Controller Access ---

  /**
   * Gets the main scene for this view.
   *
   * @return The scene
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Gets the difficulty selection panel.
   *
   * @return The difficulty selection panel
   */
  public DifficultySelectionPanel getDifficultySelectionPanel() {
    return difficultySelectionPanel;
  }

  /**
   * Gets the player management panel.
   *
   * @return The player management panel
   */
  public PlayerManagementPanel getPlayerManagementPanel() {
    return playerManagementPanel;
  }

  /**
   * Gets the game info panel.
   *
   * @return The game info panel
   */
  public GameInfoPanel getGameInfoPanel() {
    return gameInfoPanel;
  }

  /**
   * Gets the start game button.
   *
   * @return The start game button
   */
  public Button getStartGameButton() {
    return startGameButton;
  }

  /**
   * Gets the back button.
   *
   * @return The back button
   */
  public Button getBackButton() {
    return backButton;
  }

  /**
   * Binds event handlers to UI components.
   * This should be called after the controller is set.
   */
  private void bindEventHandlers() {
    if (controller == null) {
      throw new IllegalStateException("Controller must be set before binding event handlers");
    }

    // Main view buttons
    startGameButton.setOnAction(e -> controller.onGameStart());
    backButton.setOnAction(e -> controller.onBack());

    // Difficulty panel buttons
    difficultySelectionPanel.getEasyDifficultyButton().setOnAction(e -> controller.onDifficultySelected("Easy"));
    difficultySelectionPanel.getNormalDifficultyButton().setOnAction(e -> controller.onDifficultySelected("Normal"));
    difficultySelectionPanel.getHardDifficultyButton().setOnAction(e -> controller.onDifficultySelected("Hard"));
    difficultySelectionPanel.getUploadBoardButton().setOnAction(e -> controller.onUploadBoard());

    // Player management panel buttons
    playerManagementPanel.getAddPlayerButton().setOnAction(e -> controller.onAddPlayer());
    playerManagementPanel.getSavePlayerButton().setOnAction(e -> controller.onSavePlayer());
    playerManagementPanel.getRemovePlayerButton().setOnAction(e -> controller.onRemovePlayer());

    // Listen for tab changes
    playerManagementPanel.getPlayerTabs().getSelectionModel().selectedItemProperty().addListener(
        (observable, oldTab, newTab) -> {
          if (newTab == playerManagementPanel.getCurrentPlayersTab()) {
            controller.onCurrentPlayersTabSelected();
          } else if (newTab == playerManagementPanel.getSavedPlayersTab()) {
            controller.onSavedPlayersTabSelected();
          }
        }
    );
  }
}