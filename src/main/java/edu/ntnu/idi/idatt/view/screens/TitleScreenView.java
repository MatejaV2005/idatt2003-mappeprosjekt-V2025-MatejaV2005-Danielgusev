package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.NavigationController;
import edu.ntnu.idi.idatt.controller.TitleScreenController;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.decorator.ButtonDecorator;
import edu.ntnu.idi.idatt.view.decorator.HoverEffectDecorator;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * The view component for the title screen.
 * Follows MVC pattern by focusing solely on presentation.
 * This class is responsible for creating and structuring the UI elements.
 */
public class TitleScreenView {
  private Scene scene;
  private final StackPane root;
  private final VBox container;
  private Button btnChooseGamemode;
  private Button btnSettings;
  private Button btnQuitGame;
  private TitleScreenController controller;

  // Check and update these paths to match your actual file structure
  private static final String BACKGROUND_PATH = "/edu/ntnu/idi/idatt/view/resources/titleScreen/Background_titleScreen.png";
  private static final String LOGO_PATH = "/edu/ntnu/idi/idatt/view/resources/titleScreen/logo.png";
  private static final String CSS_PATH = "/edu/ntnu/idi/idatt/view/resources/titleScreen/TitleScreen-Styles.css";

  /**
   * Constructs the title screen view with all UI components.
   */
  public TitleScreenView() {
    // Initialize root container
    this.root = new StackPane();
    this.container = new VBox(30);

    // Set up UI components
    setupBackground();
    setupLogo();
    setupButtons();

    // Add container to root and create scene
    root.getChildren().add(container);

    // Create scene and apply CSS
    scene = new Scene(root, 1280, 720);
    try {
      scene.getStylesheets().add(ResourceLoader.loadCssResource(CSS_PATH));
    } catch (Exception e) {
      System.err.println("Failed to load CSS: " + e.getMessage());
      // Continue without CSS rather than crashing the application
    }
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
   * Sets up the background image for the title screen.
   */
  private void setupBackground() {
    try {
      // Load background image
      Image bgImage = ResourceLoader.loadImage(BACKGROUND_PATH);
      ImageView bgImageView = new ImageView(bgImage);
      bgImageView.setPreserveRatio(false);

      // Bind image dimensions to scene size for responsive layout
      bgImageView.fitWidthProperty().bind(root.widthProperty());
      bgImageView.fitHeightProperty().bind(root.heightProperty());

      // Add background as the bottom layer
      root.getChildren().add(bgImageView);
    } catch (Exception e) {
      System.err.println("Failed to load background image: " + e.getMessage());
      // Set a fallback background color
      root.setStyle("-fx-background-color: #1a1a2e;");
    }

    // Configure the main content container
    container.setAlignment(Pos.CENTER);
    container.setMaxWidth(500);
  }

  /**
   * Sets up the logo for the title screen.
   */
  private void setupLogo() {
    try {
      // Load logo image
      Image logoImage = ResourceLoader.loadImage(LOGO_PATH);
      ImageView logoImageView = new ImageView(logoImage);
      logoImageView.setFitWidth(400);
      logoImageView.setPreserveRatio(true);

      // Create rounded corners for the logo using a clip
      Rectangle clip = new Rectangle();
      clip.setArcWidth(30);
      clip.setArcHeight(30);

      // Ensure clip resizes with the image
      logoImageView.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
        clip.setWidth(newBounds.getWidth());
        clip.setHeight(newBounds.getHeight());
      });

      // Apply clip to logo
      logoImageView.setClip(clip);

      // Add logo to the container
      container.getChildren().add(logoImageView);
    } catch (Exception e) {
      System.err.println("Failed to load logo image: " + e.getMessage());
      // Continue without logo rather than crashing
    }
  }

  /**
   * Sets up the buttons for the title screen using Factory and Decorator patterns.
   */
  private void setupButtons() {
    // Use ButtonFactory (Factory pattern)
    ButtonFactory buttonFactory = new ButtonFactory();

    // Create the buttons
    btnChooseGamemode = buttonFactory.createStandardButton("Choose Gamemode");
    btnSettings = buttonFactory.createStandardButton("Settings");
    btnQuitGame = buttonFactory.createStandardButton("Quit Game");

    // Use decorator pattern to add effects
    ButtonDecorator decorator = new HoverEffectDecorator();
    btnChooseGamemode = decorator.decorate(btnChooseGamemode);
    btnSettings = decorator.decorate(btnSettings);
    btnQuitGame = decorator.decorate(btnQuitGame);

    // Organize buttons in a VBox
    VBox buttonsBox = new VBox(20, btnChooseGamemode, btnSettings, btnQuitGame);
    buttonsBox.setAlignment(Pos.CENTER);
    buttonsBox.setMaxWidth(300);

    // Add buttons to the main container
    container.getChildren().add(buttonsBox);
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
    btnSettings.setOnAction(e -> controller.onSettings());
    btnQuitGame.setOnAction(e -> controller.onQuitGame());
  }
}