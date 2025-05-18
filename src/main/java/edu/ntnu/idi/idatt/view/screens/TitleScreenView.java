package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.TitleScreenController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.decorator.ButtonDecorator;
import edu.ntnu.idi.idatt.view.decorator.HoverEffectDecorator;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The view component for the title screen.
 * Follows MVC pattern by focusing solely on presentation.
 * This class is responsible for creating and structuring the UI elements.
 */
public class TitleScreenView {
  private Scene scene;
  private final StackPane root;
  private final Pane backgroundLayer;
  private final VBox contentLayer;
  private Button btnChooseGamemode;
  private Button btnSettings;
  private Button btnQuitGame;
  private TitleScreenController controller;

  // Resource paths
  private static final String BACKGROUND_PATH = "/edu/ntnu/idi/idatt/view/resources/TitleScreen/Background_titleScreen.png";
  private static final String LOGO_PATH = "/edu/ntnu/idi/idatt/view/resources/TitleScreen/logo.png";
  private static final String CSS_PATH = "/edu/ntnu/idi/idatt/view/resources/TitleScreen/TitleScreen-Styles.css";

  /**
   * Constructs the title screen view with all UI components.
   */
  public TitleScreenView() {
    // Initialize layers using a clear hierarchy
    this.root = new StackPane();
    this.backgroundLayer = new Pane();
    this.contentLayer = new VBox(30);

    setupBackgroundLayer();
    setupContentLayer();

    root.getChildren().addAll(backgroundLayer, contentLayer);

    scene = new Scene(root, 1280, 720);
    applyStylesheets();
  }

  /**
   * Sets the controller for this view.
   * This follows the MVC pattern by separating view logic from controller logic.
   *
   * @param controller The controller for this view
   */
  public void setController(TitleScreenController controller) {
    this.controller = controller;
    bindEventHandlers();
  }

  /**
   * Returns the scene containing the title screen.
   *
   * @return The scene object
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Sets up the background layer with the background image.
   */
  private void setupBackgroundLayer() {
    try {
      Image bgImage = ResourceLoader.loadImage(BACKGROUND_PATH);
      ImageView bgImageView = new ImageView(bgImage);
      bgImageView.setPreserveRatio(false);

      bgImageView.fitWidthProperty().bind(root.widthProperty());
      bgImageView.fitHeightProperty().bind(root.heightProperty());

      backgroundLayer.getChildren().add(bgImageView);
    } catch (BoardGameResourceException e) {
      System.err.println("Failed to load background: " + e.getMessage());
    }
  }

  /**
   * Sets up the content layer with logo and buttons.
   */
  private void setupContentLayer() {
    contentLayer.setAlignment(Pos.CENTER);
    contentLayer.setMaxWidth(500);

    setupLogo();
    setupButtons();
  }

  /**
   * Sets up the logo for the title screen.
   */
  private void setupLogo() {
    try {
      Image logoImage = ResourceLoader.loadImage(LOGO_PATH);
      ImageView logoImageView = new ImageView(logoImage);

      logoImageView.setFitWidth(400);
      logoImageView.setPreserveRatio(true);

      // Add logo as first element in the content layer
      contentLayer.getChildren().add(logoImageView);
    } catch (BoardGameResourceException e) {
      System.err.println("Failed to load logo: " + e.getMessage());
    }
  }

  /**
   * Sets up the buttons for the title screen using Factory and Decorator patterns.
   */
  private void setupButtons() {
    ButtonFactory buttonFactory = new ButtonFactory();

    btnChooseGamemode = buttonFactory.createStandardButton("Choose Gamemode");
    btnSettings = buttonFactory.createStandardButton("Settings");
    btnQuitGame = buttonFactory.createStandardButton("Quit Game");

    ButtonDecorator decorator = new HoverEffectDecorator();
    btnChooseGamemode = decorator.decorate(btnChooseGamemode);
    btnSettings = decorator.decorate(btnSettings);
    btnQuitGame = decorator.decorate(btnQuitGame);

    VBox buttonsBox = new VBox(20, btnChooseGamemode, btnSettings, btnQuitGame);
    buttonsBox.setAlignment(Pos.CENTER);
    buttonsBox.setMaxWidth(300);

    contentLayer.getChildren().add(buttonsBox);
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

  /**
   * Binds event handlers to button actions.
   * This connects UI events to controller methods.
   */
  private void bindEventHandlers() {
    if (controller == null) {
      throw new IllegalStateException("Controller must be set before binding event handlers");
    }

    btnChooseGamemode.setOnAction(e -> controller.onChooseGameMode());
    btnQuitGame.setOnAction(e -> controller.onQuitGame());
  }
}