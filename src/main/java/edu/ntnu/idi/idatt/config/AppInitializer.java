package edu.ntnu.idi.idatt.config;

import edu.ntnu.idi.idatt.controller.NavigationController;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * Handles the initialization of the main application components and setup of the primary stage.
 *
 * <p>This class is responsible for creating and configuring core controllers (like the
 * {@link NavigationController}) and setting up the initial view of the application.
 * It helps to keep the {@link edu.ntnu.idi.idatt.MainApplication#start(Stage)} method clean
 * and focused.
 * </p>
 */
public class AppInitializer {

  private static final Logger LOGGER = Logger.getLogger(AppInitializer.class.getName());

  private static final String APPLICATION_TITLE = "The Pimp Playground";
  private static final double MIN_STAGE_WIDTH = 800;
  private static final double MIN_STAGE_HEIGHT = 600;

  /**
   * Default constructor for {@code AppInitializer}.
   *
   * <p>This constructor can be used to set up any initial dependencies or configurations that the
   * AppInitializer itself might need before the {@link #initialize(Stage)} method is called.
   * </p>
   */
  public AppInitializer() {
    // Initialization of AppInitializer's own dependencies can happen here if needed.
  }

  /**
   * Initializes the primary stage of the application.
   *
   * <p>This method sets the title and minimum dimensions for the stage, creates the main
   * {@link NavigationController}, navigates to the initial screen (assumed to be the title screen),
   * and then shows the stage. It includes error handling for the initialization process.
   * </p>
   *
   * @param primaryStage The primary stage for this application, provided by the JavaFX runtime.
   *                     Must not be {@code null}.
   * @throws NullPointerException if {@code primaryStage} is {@code null}.
   * @throws RuntimeException     if there's a critical issue setting up the navigation or initial
   *                              screen, wrapping the original exception.
   */
  public void initialize(Stage primaryStage) {
    Objects.requireNonNull(primaryStage, "Primary stage cannot be null for initialization.");
    LOGGER.info("Initializing application...");

    try {
      primaryStage.setTitle(APPLICATION_TITLE);

      primaryStage.setFullScreen(true);
      primaryStage.setFullScreenExitHint("Trykk ESC for å avslutte fullskjerm");
      primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf("ESC"));

      primaryStage.setMinWidth(MIN_STAGE_WIDTH);
      primaryStage.setMinHeight(MIN_STAGE_HEIGHT);
      primaryStage.setMaximized(false);

      NavigationController navigationController = new NavigationController(primaryStage);
      navigationController.navigateToTitleScreen();

      primaryStage.show();
      LOGGER.info("Application initialized in fullscreen and primary stage shown successfully.");

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize the application UI.", e);
      throw new RuntimeException("Application initialization failed critically.", e);
    }
  }
}