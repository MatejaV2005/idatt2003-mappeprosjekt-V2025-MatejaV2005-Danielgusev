package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.TitleScreenController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.decorator.ButtonDecorator;
import edu.ntnu.idi.idatt.view.decorator.HoverEffectDecorator;
import edu.ntnu.idi.idatt.utils.ResourceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Represents the view component for the application's title screen.
 *
 * <p>This class is responsible for constructing and managing the user interface
 * of the title screen, including background, logo, and navigation buttons.
 * It follows the Model-View-Controller (MVC) pattern, with its primary focus
 * on presentation and delegating user actions to a {@link TitleScreenController}.
 * </p>
 */
public class TitleScreenView {

  private static final Logger LOGGER = Logger.getLogger(TitleScreenView.class.getName());

  // Resource paths
  private static final String BACKGROUND_PATH =
      "/edu/ntnu/idi/idatt/view/resources/TitleScreen/Background_titleScreen.png";
  private static final String LOGO_PATH =
      "/edu/ntnu/idi/idatt/view/resources/TitleScreen/logo.png";
  private static final String CSS_PATH =
      "/edu/ntnu/idi/idatt/view/resources/TitleScreen/TitleScreen-Styles.css";

  // Layout constants
  private static final double SCENE_WIDTH = 1280;
  private static final double SCENE_HEIGHT = 720;
  private static final double CONTENT_LAYER_SPACING = 30;
  private static final double CONTENT_LAYER_MAX_WIDTH = 500;
  private static final double LOGO_FIT_WIDTH = 400;
  private static final double BUTTONS_BOX_SPACING = 20;
  private static final double BUTTONS_BOX_MAX_WIDTH = 300;

  // UI Text constants
  private static final String BUTTON_TEXT_CHOOSE_GAMEMODE = "Choose Gamemode";
  private static final String BUTTON_TEXT_QUIT_GAME = "Quit Game";

  private final Scene scene;
  private final StackPane root;
  private final Pane backgroundLayer;
  private final VBox contentLayer;

  private Button btnChooseGamemode;
  private Button btnQuitGame;
  private TitleScreenController controller;

  /**
   * Constructs the {@code TitleScreenView} and initializes all its UI components.
   *
   * <p>This involves setting up the root pane, background layer, and content layer which
   * includes the game logo and navigation buttons. Stylesheets are also applied.
   * </p>
   */
  public TitleScreenView() {
    this.root = new StackPane();
    this.backgroundLayer = new Pane();
    this.contentLayer = new VBox(CONTENT_LAYER_SPACING);

    setupBackgroundLayer();
    setupContentLayer();

    root.getChildren().addAll(backgroundLayer, contentLayer);

    this.scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    applyStylesheets();
  }

  /**
   * Sets the controller responsible for handling user interactions on this title screen.
   *
   * <p>After setting the controller, event handlers for UI elements like buttons are bound to the
   * appropriate methods in the controller.
   * </p>
   *
   * @param controller The {@link TitleScreenController} for this view. Must not be {@code null}.
   * @throws NullPointerException if the provided controller is {@code null}.
   */
  public void setController(TitleScreenController controller) {
    if (controller == null) {
      throw new NullPointerException("Controller cannot be null.");
    }
    this.controller = controller;
    bindEventHandlers();
  }

  /**
   * Returns the {@link Scene} associated with this title screen.
   *
   * @return The main scene for the title screen.
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Initializes and configures the background layer of the title screen.
   *
   * <p>Loads the background image and sets it to fill the entire root pane. If the image fails to
   * load, an error is logged.
   * </p>
   */
  private void setupBackgroundLayer() {
    try {
      Image bgImage = ResourceLoader.loadImage(BACKGROUND_PATH);
      ImageView bgImageView = new ImageView(bgImage);
      bgImageView.setPreserveRatio(false); // Allow stretching to fill

      // Bind image view size to the root pane's size
      bgImageView.fitWidthProperty().bind(root.widthProperty());
      bgImageView.fitHeightProperty().bind(root.heightProperty());

      backgroundLayer.getChildren().add(bgImageView);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load background image: " + BACKGROUND_PATH, e);
    }
  }

  /**
   * Initializes and configures the content layer of the title screen.
   *
   * <p>This layer contains the game logo and navigation buttons, centered within the screen.
   * </p>
   */
  private void setupContentLayer() {
    contentLayer.setAlignment(Pos.CENTER);
    contentLayer.setMaxWidth(CONTENT_LAYER_MAX_WIDTH);

    setupLogo();
    setupButtons();
  }

  /**
   * Initializes and adds the game logo to the content layer.
   *
   * <p>If the logo image fails to load, an error is logged.
   * </p>
   */
  private void setupLogo() {
    try {
      Image logoImage = ResourceLoader.loadImage(LOGO_PATH);
      ImageView logoImageView = new ImageView(logoImage);

      logoImageView.setFitWidth(LOGO_FIT_WIDTH);
      logoImageView.setPreserveRatio(true);

      contentLayer.getChildren().add(logoImageView);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING, "Failed to load logo image: " + LOGO_PATH, e);
    }
  }

  /**
   * Initializes, styles, and adds navigation buttons to the content layer.
   *
   * <p>Buttons are created using a {@link ButtonFactory} and decorated with hover effects using a
   * {@link ButtonDecorator}.
   * </p>
   */
  private void setupButtons() {
    ButtonFactory buttonFactory = new ButtonFactory();

    btnChooseGamemode = buttonFactory.createStandardButton(BUTTON_TEXT_CHOOSE_GAMEMODE);
    btnQuitGame = buttonFactory.createStandardButton(BUTTON_TEXT_QUIT_GAME);

    ButtonDecorator decorator = new HoverEffectDecorator();
    btnChooseGamemode = decorator.decorate(btnChooseGamemode);
    btnQuitGame = decorator.decorate(btnQuitGame);

    VBox buttonsBox = new VBox(BUTTONS_BOX_SPACING, btnChooseGamemode, btnQuitGame);
    buttonsBox.setAlignment(Pos.CENTER);
    buttonsBox.setMaxWidth(BUTTONS_BOX_MAX_WIDTH);

    contentLayer.getChildren().add(buttonsBox);
  }

  /**
   * Applies the CSS stylesheet to the scene for styling UI elements.
   *
   * <p>If the CSS file fails to load, an error is logged.
   * </p>
   */
  private void applyStylesheets() {
    try {
      String cssUrl = ResourceLoader.loadCssResource(CSS_PATH);
      if (cssUrl != null) {
        scene.getStylesheets().add(cssUrl);
      } else {
        LOGGER.log(Level.WARNING, "CSS resource not found: " + CSS_PATH);
      }
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load CSS stylesheet: " + CSS_PATH, e);
    }
  }

  /**
   * Binds event handlers from the controller to the UI buttons.
   *
   * <p>This method should only be called after the controller has been set.
   * </p>
   *
   * @throws IllegalStateException if the controller has not been set prior to calling this method.
   */
  private void bindEventHandlers() {
    if (controller == null) {
      throw new IllegalStateException("Controller must be set before binding event handlers.");
    }

    btnChooseGamemode.setOnAction(event -> controller.onChooseGameMode());
    btnQuitGame.setOnAction(event -> controller.onQuitGame());
  }
}