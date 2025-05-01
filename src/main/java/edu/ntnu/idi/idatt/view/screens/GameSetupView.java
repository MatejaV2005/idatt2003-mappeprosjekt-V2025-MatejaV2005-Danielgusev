package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.GameSetupController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.components.gameSelection.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.decorator.ButtonDecorator;
import edu.ntnu.idi.idatt.view.decorator.HoverEffectDecorator;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View for the game setup screen where players can select difficulty, manage players,
 * and view board information before starting a game.
 */
public class GameSetupView {
  private static final Logger LOGGER = Logger.getLogger(GameSetupView.class.getName());
  private GameSetupController controller;
  private static final String BACKGROUND_PATH = "/edu/ntnu/idi/idatt/view/resources/GameScreen/Background.png";
  private static final String CSS_PATH = "/edu/ntnu/idi/idatt/view/resources/GameSetup/gameSetupStyle.css";

  private final StackPane stackRoot;
  private final Scene scene;
  private final BorderPane root;

  private final PlayerManagementPanel playerManagementPanel;
  private final GameInfoPanel gameInfoPanel;

  // Difficulty buttons (now standalone)
  private final Button easyDifficultyButton;
  private final Button normalDifficultyButton;
  private final Button hardDifficultyButton;
  private final Button uploadBoardButton;

  private final Button startGameButton;
  private final Button backButton;
  private final HBox bottomActionBar;
  private final Label statusLabel;

  /**
   * Constructs a new GameSetupView with responsive layout.
   */
  public GameSetupView() {
    root = new BorderPane();
    root.setPadding(new Insets(20));

    stackRoot = new StackPane();
    setupBackground();
    stackRoot.getChildren().add(root);

    // Create the main header label without the box
    Label gameModeLabel = new Label("Snakes & Ladders - Setup");
    gameModeLabel.getStyleClass().add("game-mode-title");

    HBox headerBox = new HBox(gameModeLabel);
    headerBox.setAlignment(Pos.CENTER);
    headerBox.setPadding(new Insets(10, 0, 20, 0));
    // Removed the header-box style class to eliminate the box

    root.setTop(headerBox);

    // Status label for feedback
    statusLabel = new Label();
    statusLabel.getStyleClass().add("status-label");

    // Create components using factories and decorators
    ButtonFactory buttonFactory = new ButtonFactory();
    ButtonDecorator decorator = new HoverEffectDecorator();

    // Create difficulty buttons directly without the panel
    easyDifficultyButton = decorator.decorate(buttonFactory.createStandardButton("Easy"));
    normalDifficultyButton = decorator.decorate(buttonFactory.createStandardButton("Normal"));
    hardDifficultyButton = decorator.decorate(buttonFactory.createStandardButton("Hard"));
    uploadBoardButton = decorator.decorate(buttonFactory.createStandardButton("Upload Board"));

    // Style difficulty buttons
    easyDifficultyButton.getStyleClass().add("difficulty-button");
    normalDifficultyButton.getStyleClass().add("difficulty-button");
    hardDifficultyButton.getStyleClass().add("difficulty-button");
    uploadBoardButton.getStyleClass().add("difficulty-button");

    playerManagementPanel = new PlayerManagementPanel(buttonFactory);
    gameInfoPanel = new GameInfoPanel();

    // Create action buttons with decorator
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

    scene = new Scene(stackRoot, 1280, 720);
    applyStylesheets();
  }

  private void setupBackground() {
    try {
      Image bg = ResourceLoader.loadImage(BACKGROUND_PATH);
      ImageView iv = new ImageView(bg);
      iv.setPreserveRatio(false);
      // make the BG fill the view at all times
      iv.fitWidthProperty().bind(stackRoot.widthProperty());
      iv.fitHeightProperty().bind(stackRoot.heightProperty());
      // add it *first*, so everything else is on top
      stackRoot.getChildren().add(iv);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING, "Could not load background, using solid color", e);
      stackRoot.setStyle("-fx-background-color: #1a1a2e;");
    }
  }

  /**
   * Sets up the responsive layout for the main components.
   * Now with difficulty buttons directly in the layout and game info directly included.
   */
  private void setupResponsiveLayout() {
    // Create difficulty header
    Label difficultyHeaderLabel = new Label("Game Difficulty");
    difficultyHeaderLabel.getStyleClass().add("game-mode-title");

    // Create difficulty buttons container
    VBox difficultyButtonsContainer = new VBox(15);
    difficultyButtonsContainer.getStyleClass().add("difficulty-buttons-container");
    difficultyButtonsContainer.getChildren().addAll(
        difficultyHeaderLabel,
        easyDifficultyButton,
        normalDifficultyButton,
        hardDifficultyButton,
        uploadBoardButton
    );
    difficultyButtonsContainer.setAlignment(Pos.CENTER);
    difficultyButtonsContainer.setPadding(new Insets(20));

    // Create the right side content with game info (no panel wrapper)
    VBox gameInfoContent = new VBox(15);
    gameInfoContent.getChildren().addAll(gameInfoPanel);
    gameInfoContent.setAlignment(Pos.CENTER);
    gameInfoContent.setPadding(new Insets(20));

    // Main content layout
    HBox mainContent = new HBox(20);
    mainContent.setPadding(new Insets(10));

    StackPane leftWrapper = new StackPane(difficultyButtonsContainer);
    StackPane centerWrapper = new StackPane(playerManagementPanel);
    StackPane rightWrapper = new StackPane(gameInfoContent);

    // Apply styling to wrappers
    leftWrapper.getStyleClass().add("panel-wrapper");
    centerWrapper.getStyleClass().add("panel-wrapper");
    // Removed panel-wrapper class from rightWrapper to eliminate the border

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
   * Gets the easy difficulty button.
   *
   * @return The easy difficulty button
   */
  public Button getEasyDifficultyButton() {
    return easyDifficultyButton;
  }

  /**
   * Gets the normal difficulty button.
   *
   * @return The normal difficulty button
   */
  public Button getNormalDifficultyButton() {
    return normalDifficultyButton;
  }

  /**
   * Gets the hard difficulty button.
   *
   * @return The hard difficulty button
   */
  public Button getHardDifficultyButton() {
    return hardDifficultyButton;
  }

  /**
   * Gets the upload board button.
   *
   * @return The upload board button
   */
  public Button getUploadBoardButton() {
    return uploadBoardButton;
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

    // Difficulty buttons
    easyDifficultyButton.setOnAction(e -> controller.onDifficultySelected("Easy"));
    normalDifficultyButton.setOnAction(e -> controller.onDifficultySelected("Normal"));
    hardDifficultyButton.setOnAction(e -> controller.onDifficultySelected("Hard"));
    uploadBoardButton.setOnAction(e -> controller.onUploadBoard());

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