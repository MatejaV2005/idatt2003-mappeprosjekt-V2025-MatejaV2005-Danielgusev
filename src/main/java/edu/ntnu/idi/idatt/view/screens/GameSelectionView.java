package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.GameSelectionController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * View component for the game selection screen.
 * Allows players to select from different game modes.
 * Follows MVC pattern by focusing solely on UI presentation.
 */
public class GameSelectionView {
  private Scene scene;
  private final StackPane root;
  private final VBox contentBox;
  private final HBox gameModesContainer;
  private Button backButton;
  private GameSelectionController controller;

  private static final String BACKGROUND_PATH = "/edu/ntnu/idi/idatt/view/resources/ChooseGame/Background_choose_game.png";
  private static final String CSS_PATH = "/edu/ntnu/idi/idatt/view/resources/ChooseGame/ChooseGameScreen_styles.css";

  /**
   * Constructs the game selection view with all necessary UI components.
   */
  public GameSelectionView() {
    // Initialize layout containers
    this.root = new StackPane();
    this.contentBox = new VBox(30);
    this.contentBox.setAlignment(Pos.CENTER);
    this.gameModesContainer = new HBox(40);
    this.gameModesContainer.setAlignment(Pos.CENTER);

    // Set up UI components
    setupBackground();
    setupGameModePanels();
    setupBackButton();

    // Add components to layout
    contentBox.getChildren().addAll(gameModesContainer, backButton);
    root.getChildren().add(contentBox);

    // Create scene and apply CSS
    scene = new Scene(root, 1280, 720);
    try {
      scene.getStylesheets().add(ResourceLoader.loadCssResource(CSS_PATH));
    } catch (BoardGameResourceException e) {
      System.err.println("Failed to load CSS: " + e.getMessage());
      // Continue without CSS rather than crashing
    }
  }

  /**
   * Sets the controller for this view.
   *
   * @param controller The controller for this view
   */
  public void setController(GameSelectionController controller) {
    this.controller = controller;
    bindEventHandlers();
  }

  /**
   * Returns the scene containing the game selection interface.
   *
   * @return The scene object
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Sets up the background image for the game selection screen.
   */
  private void setupBackground() {
    try {
      // Load and set up background image
      Image bgImage = ResourceLoader.loadImage(BACKGROUND_PATH);
      ImageView bgImageView = new ImageView(bgImage);
      bgImageView.setFitWidth(1280);
      bgImageView.setFitHeight(720);
      bgImageView.setPreserveRatio(false);

      // Bind image size to scene size for responsive layout
      bgImageView.fitWidthProperty().bind(root.widthProperty());
      bgImageView.fitHeightProperty().bind(root.heightProperty());

      // Add background as the bottom layer
      root.getChildren().add(bgImageView);
    } catch (BoardGameResourceException e) {
      System.err.println("Failed to load background image: " + e.getMessage());
      // Set a fallback background color
      root.setStyle("-fx-background-color: #1a1a2e;");
    }
  }

  /**
   * Sets up the game mode panels for selection.
   */
  private void setupGameModePanels() {
    // Create the game mode panels
    VBox snakesAndLaddersPanel = createGameModePanel("Snakes & Ladders");
    VBox gameModePanel2 = createGameModePanel("Other Gamemode");
    VBox gameModePanel3 = createGameModePanel("Other Gamemode");

    // Add panels to container
    gameModesContainer.getChildren().addAll(
        snakesAndLaddersPanel,
        gameModePanel2,
        gameModePanel3
    );
  }

  /**
   * Sets up the back button for returning to the title screen.
   */
  private void setupBackButton() {
    // Create the back button using factory
    ButtonFactory buttonFactory = new ButtonFactory();
    backButton = buttonFactory.createStandardButton("Back");
    backButton.getStyleClass().add("golden-button");
  }

  /**
   * Binds event handlers to button actions using the controller.
   */
  private void bindEventHandlers() {
    if (controller == null) {
      throw new IllegalStateException("Controller must be set before binding event handlers");
    }

    // Bind back button action
    backButton.setOnAction(e -> controller.onBackButtonClicked());

    // Add click handlers to each panel in the container
    for (int i = 0; i < gameModesContainer.getChildren().size(); i++) {
      VBox panel = (VBox) gameModesContainer.getChildren().get(i);
      String gameMode = ((Label) panel.getChildren().get(0)).getText();

      final String selectedGameMode = gameMode;
      panel.setOnMouseClicked(e -> controller.onGameModeSelected(selectedGameMode));
    }
  }

  /**
   * Creates a game mode panel with heading and content area.
   *
   * @param modeName Name of the game mode
   * @return A styled VBox panel representing a game mode
   */
  private VBox createGameModePanel(String modeName) {
    // Create label with style class
    Label label = new Label(modeName);
    label.getStyleClass().add("game-mode-label");

    // Create content area with style class
    Pane contentArea = new Pane();
    contentArea.getStyleClass().add("white-box");
    contentArea.setPrefSize(250, 400);

    // Create panel and set alignment
    VBox panel = new VBox(10, label, contentArea);
    panel.setAlignment(Pos.TOP_CENTER);
    panel.getStyleClass().add("game-panel");

    return panel;
  }
}