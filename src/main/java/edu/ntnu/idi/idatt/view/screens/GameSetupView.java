package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.GameSetupController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.components.gameselection.DifficultySelectionPanel;
import edu.ntnu.idi.idatt.view.components.gameselection.InfoPanel;
import edu.ntnu.idi.idatt.view.components.gameselection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.decorator.ButtonDecorator;
import edu.ntnu.idi.idatt.view.decorator.HoverEffectDecorator;
import edu.ntnu.idi.idatt.utils.ResourceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
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


/**
 * View for the game setup screen where players can select difficulty, manage players, and view
 * board information before starting a game. This view is designed to be dynamically configured by
 * its controller based on the selected game type.
 */
public class GameSetupView {
  private static final Logger LOGGER = Logger.getLogger(GameSetupView.class.getName());
  private static final String BACKGROUND_PATH =
      "/edu/ntnu/idi/idatt/view/resources/GameScreen/Background.png";
  private static final String CSS_PATH =
      "/edu/ntnu/idi/idatt/view/resources/GameSetup/gameSetupStyle.css";

  private GameSetupController controller;

  private final StackPane stackRoot;
  private final Scene scene;
  private final BorderPane root;

  private final Label screenTitleLabel;
  private final PlayerManagementPanel playerManagementPanel;
  private final InfoPanel infoPanel;
  private final DifficultySelectionPanel difficultySelectionPanel;
  private final VBox difficultyButtonsContainer;

  private final Button startGameButton;
  private final Button backButton;
  private final Label statusLabel;


  /**
   * Constructs the game setup screen, initializing layout, background, and
   * UI components for difficulty selection, player management, and board info.
   * Loads and applies CSS stylesheets.
   */
  public GameSetupView() {
    root = new BorderPane();
    root.setPadding(new Insets(20));

    stackRoot = new StackPane();
    setupBackground();
    stackRoot.getChildren().add(root);



    screenTitleLabel = new Label("Game Setup");
    screenTitleLabel.getStyleClass().add("game-mode-title");
    HBox headerBox = new HBox(screenTitleLabel);
    headerBox.setAlignment(Pos.CENTER);
    headerBox.setPadding(new Insets(10, 0, 20, 0));
    root.setTop(headerBox);

    statusLabel = new Label();
    statusLabel.getStyleClass().add("status-label");



    difficultySelectionPanel = new DifficultySelectionPanel();
    difficultyButtonsContainer = new VBox(15);
    difficultyButtonsContainer.getStyleClass().add("difficulty-buttons-container");
    Label difficultyHeaderLabel = new Label("Game Difficulty / Board");
    difficultyHeaderLabel.getStyleClass().add("panel-header");
    difficultyButtonsContainer
        .getChildren()
        .addAll(
            difficultyHeaderLabel,
            difficultySelectionPanel.getEasyDifficultyButton(),
            difficultySelectionPanel.getNormalDifficultyButton(),
            difficultySelectionPanel.getHardDifficultyButton(),
            difficultySelectionPanel.getUploadBoardButton());
    difficultyButtonsContainer.setAlignment(Pos.TOP_CENTER);
    difficultyButtonsContainer.setPadding(new Insets(10));

    ButtonFactory buttonFactory = new ButtonFactory();
    ButtonDecorator decorator = new HoverEffectDecorator();

    playerManagementPanel = new PlayerManagementPanel(buttonFactory);
    infoPanel = new InfoPanel();

    startGameButton = decorator.decorate(buttonFactory.createStandardButton("Start Game"));
    startGameButton.getStyleClass().add("primary-button");

    backButton = decorator.decorate(buttonFactory.createStandardButton("Back"));
    backButton.getStyleClass().add("secondary-button");

    setupResponsiveLayout();

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox statusBox = new HBox(statusLabel);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(0, 0, 10, 0));

    HBox bottomActionBar = new HBox(20, backButton, spacer, startGameButton);
    bottomActionBar.setAlignment(Pos.CENTER);

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
      iv.fitWidthProperty().bind(stackRoot.widthProperty());
      iv.fitHeightProperty().bind(stackRoot.heightProperty());
      stackRoot.getChildren().add(iv);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING, "Could not load background, using solid color", e);
      stackRoot.setStyle("-fx-background-color: #1a1a2e;");
    }
  }


  private void setupResponsiveLayout() {
    VBox gameInfoContent = new VBox(15);
    gameInfoContent.getChildren().add(infoPanel);
    gameInfoContent.setAlignment(Pos.CENTER);
    gameInfoContent.setPadding(new Insets(20));

    HBox mainContent = new HBox(20);
    mainContent.setPadding(new Insets(10));
    mainContent.setAlignment(Pos.CENTER);

    StackPane leftWrapper = new StackPane(difficultyButtonsContainer);
    StackPane centerWrapper = new StackPane(playerManagementPanel);
    StackPane rightWrapper = new StackPane(gameInfoContent);

    leftWrapper.getStyleClass().add("panel-wrapper");
    centerWrapper.getStyleClass().add("panel-wrapper");
    rightWrapper.getStyleClass().add("panel-wrapper");

    mainContent.getChildren().addAll(leftWrapper, centerWrapper, rightWrapper);

    HBox.setHgrow(leftWrapper, Priority.ALWAYS);
    HBox.setHgrow(centerWrapper, Priority.ALWAYS);
    HBox.setHgrow(rightWrapper, Priority.ALWAYS);

    leftWrapper.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));
    centerWrapper.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.50));
    rightWrapper.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.25));

    leftWrapper.maxWidthProperty().bind(leftWrapper.prefWidthProperty());
    centerWrapper.maxWidthProperty().bind(centerWrapper.prefWidthProperty());
    rightWrapper.maxWidthProperty().bind(rightWrapper.prefWidthProperty());

    playerManagementPanel.maxWidthProperty().bind(centerWrapper.widthProperty());


    leftWrapper.setMinWidth(Region.USE_PREF_SIZE);
    centerWrapper.setMinWidth(0);
    rightWrapper.setMinWidth(Region.USE_PREF_SIZE);

    difficultyButtonsContainer.prefHeightProperty().bind(leftWrapper.heightProperty());
    playerManagementPanel.prefHeightProperty().bind(centerWrapper.heightProperty());
    infoPanel.prefHeightProperty().bind(rightWrapper.heightProperty());

    root.setCenter(mainContent);
  }

  /**
   * Sets the controller for this view.
   *
   * @param controller The controller to set.
   */
  public void setController(GameSetupController controller) {
    this.controller = controller;
    bindEventHandlers();
  }

  private void applyStylesheets() {
    try {
      String cssUrl = ResourceLoader.loadCssResource(CSS_PATH);
      if (cssUrl != null) {
        scene.getStylesheets().add(cssUrl);
      } else {
        LOGGER.log(Level.WARNING, "CSS Stylesheet not found: {0}", CSS_PATH);
      }
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load CSS: " + e.getMessage(), e);
    }
  }

  /**
   * Updates the main title of the game setup screen.
   *
   * @param title The new title to display.
   */
  public void setScreenTitle(String title) {
    if (title != null) {
      screenTitleLabel.setText(title);
    }
  }

  /**
   * Controls the visibility of the difficulty selection panel's container.
   *
   * @param visible True to show the panel, false to hide it.
   */
  public void setDifficultyPanelVisible(boolean visible) {
    difficultyButtonsContainer.setVisible(visible);
    difficultyButtonsContainer.setManaged(visible);
    LOGGER.fine(() -> String.format("Difficulty panel container visibility set to: %s", visible));
  }

  /**
   * Displays a temporary status message at the bottom of the screen.
   *
   * <p>The message style will be set to error if {@code isError} is true,
   * otherwise to informational style.
   * </p>
   *
   * @param message the status text to display; may be empty or null
   * @param isError  whether to style the message as an error
   */
  public void updateStatusMessage(String message, boolean isError) {
    statusLabel.setText(message);
    statusLabel.getStyleClass().removeAll("status-info", "status-error", "status-warning");
    if (isError) {
      statusLabel.getStyleClass().add("status-error");
    } else {
      statusLabel.getStyleClass().add("status-info");
    }
  }

  /**
   * Clears any status message currently displayed.
   *
   * <p>The status label text is set to an empty string.
   * </p>
   */
  public void clearStatusMessage() {
    statusLabel.setText("");
  }

  public Scene getScene() {
    return scene;
  }

  public PlayerManagementPanel getPlayerManagementPanel() {
    return playerManagementPanel;
  }

  public InfoPanel getGameInfoPanel() {
    return infoPanel;
  }

  private void bindEventHandlers() {
    if (controller == null) {
      throw new IllegalStateException("Controller must be set before binding event handlers.");
    }

    startGameButton.setOnAction(e -> controller.onGameStart());
    backButton.setOnAction(e -> controller.onBack());

    difficultySelectionPanel
        .getEasyDifficultyButton()
        .setOnAction(e -> controller.onDifficultySelected("Easy"));
    difficultySelectionPanel
        .getNormalDifficultyButton()
        .setOnAction(e -> controller.onDifficultySelected("Normal"));
    difficultySelectionPanel
        .getHardDifficultyButton()
        .setOnAction(e -> controller.onDifficultySelected("Hard"));
    difficultySelectionPanel
        .getUploadBoardButton()
        .setOnAction(e -> controller.onUploadBoard());

  }
}
