package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.GameSelectionController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * View component for the game selection screen. Allows players to select from different game
 * modes.
 */
public class GameSelectionView {

  private static final Logger LOGGER = Logger.getLogger(GameSelectionView.class.getName());
  private static final String BACKGROUND_PATH =
      "/edu/ntnu/idi/idatt/view/resources/GameScreen/Background.png";
  private static final String CSS_PATH =
      "/edu/ntnu/idi/idatt/view/resources/ChooseGame/ChooseGameScreen_styles.css";

  public static final String SNAKES_AND_LADDERS = "Snakes & Ladders";
  public static final String ASTRO_RALLY = "Astro Rally";

  private final Scene scene;
  private final StackPane root;
  private final VBox contentBox;
  private final HBox gameModesContainer;
  private Button backButton;
  private GameSelectionController controller;

  public GameSelectionView() {
    this.root = new StackPane();
    this.contentBox = new VBox(30);
    this.contentBox.setAlignment(Pos.CENTER);
    this.gameModesContainer = new HBox(60);
    this.gameModesContainer.setAlignment(Pos.CENTER);

    setupBackground();
    setupGameModePanels();
    setupBackButton();

    contentBox.getChildren().addAll(gameModesContainer, backButton);
    root.getChildren().add(contentBox);

    scene = new Scene(root, 1280, 720);
    try {
      String cssUrl = ResourceLoader.loadCssResource(CSS_PATH);
      if (cssUrl != null) {
        scene.getStylesheets().add(cssUrl);
      } else {
        LOGGER.log(Level.WARNING, "CSS resource not found at path: {0}", CSS_PATH);
      }
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load CSS: " + e.getMessage(), e);
    }
  }

  /**
   * Sets the controller for this view.
   *
   * @param controller The controller for this view. Must not be null.
   */
  public void setController(GameSelectionController controller) {
    this.controller = Objects.requireNonNull(controller, "Controller cannot be null.");
    bindEventHandlers();
  }

  /**
   * Returns the scene containing the game selection interface.
   *
   * @return The scene object.
   */
  public Scene getScene() {
    return scene;
  }

  private void setupBackground() {
    try {
      Image bgImage = ResourceLoader.loadImage(BACKGROUND_PATH);
      ImageView bgImageView = new ImageView(bgImage);
      bgImageView.setPreserveRatio(false);

      bgImageView.fitWidthProperty().bind(root.widthProperty());
      bgImageView.fitHeightProperty().bind(root.heightProperty());

      root.getChildren().add(bgImageView);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING, "Failed to load background image: " + e.getMessage(), e);
      root.setStyle("-fx-background-color: #1a1a2e;");
    }
  }

  private void setupGameModePanels() {
    VBox snakesAndLaddersPanel = createGameModePanel(SNAKES_AND_LADDERS);
    VBox astroRallyPanel = createGameModePanel(ASTRO_RALLY);

    gameModesContainer.getChildren().addAll(snakesAndLaddersPanel, astroRallyPanel);
  }

  private void setupBackButton() {
    ButtonFactory buttonFactory = new ButtonFactory();
    backButton = buttonFactory.createStandardButton("Back");
    backButton.getStyleClass().add("golden-button");
  }

  private void bindEventHandlers() {
    if (controller == null) {
      throw new IllegalStateException("Controller must be set before binding event handlers.");
    }

    backButton.setOnAction(e -> controller.onBackButtonClicked());

    for (Node node : gameModesContainer.getChildren()) {
      if (node instanceof VBox panel) {
        Node firstChild = panel.getChildren().get(0);
        if (firstChild instanceof Label label) {
          String gameMode = label.getText();
          final String selectedGameMode = gameMode;
          panel.setOnMouseClicked(event -> controller.onGameModeSelected(selectedGameMode));
        } else {
          LOGGER.log(
              Level.WARNING,
              "Expected first child of game mode panel to be a Label, but found: {0}",
              firstChild.getClass().getName());
        }
      }
    }
  }

  /**
   * Creates a game mode panel with a heading and a content area.
   *
   * @param modeName Name of the game mode.
   * @return A styled VBox panel representing a game mode.
   */
  private VBox createGameModePanel(String modeName) {
    Label label = new Label(modeName);
    label.getStyleClass().add("game-mode-label");

    Pane contentArea = new Pane();
    contentArea.getStyleClass().add("white-box");
    contentArea.setPrefSize(300, 450);

    VBox panel = new VBox(15, label, contentArea);
    panel.setAlignment(Pos.TOP_CENTER);
    panel.getStyleClass().add("game-panel");


    return panel;
  }
}