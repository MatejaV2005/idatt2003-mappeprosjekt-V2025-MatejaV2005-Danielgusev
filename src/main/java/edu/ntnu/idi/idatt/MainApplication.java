package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.config.AppInitializer;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main JavaFX application class.
 *
 * <p>This class serves as the entry point for the JavaFX runtime.
 * It overrides the {@link #start(Stage)} method to initialize and display
 * the primary stage of the application, delegating the setup logic to
 * an {@link AppInitializer}.
 * </p>
 */
public class MainApplication extends Application {

  private static final Logger LOGGER = Logger.getLogger(MainApplication.class.getName());

  /**
   * The main entry point for all JavaFX applications.
   * The {@code start} method is called after the {@code init} method has returned,
   * and after the system is ready for the application to begin running.
   * <p>
   * This implementation delegates the initialization of the application's
   * UI and core components to an {@link AppInitializer}.
   * </p>
   *
   * @param primaryStage The primary stage for this application, onto which
   * the application scene can be set. Must not be {@code null}.
   * @throws NullPointerException if {@code primaryStage} is {@code null}.
   */
  @Override
  public void start(Stage primaryStage) {
    Objects.requireNonNull(primaryStage, "PrimaryStage cannot be null in start method.");
    try {
      AppInitializer appInitializer = new AppInitializer();
      appInitializer.initialize(primaryStage);
    } catch (Exception e) {
      // Log any critical errors during initialization
      LOGGER.log(Level.SEVERE, "A critical error occurred during application startup", e);
      // Optionally, show an alert to the user here if appropriate
      // For example: AlertHelper.showErrorAlert("Critical Error", "Application failed to start: " + e.getMessage());
    }
  }

  /**
   * The main method is ignored in correctly deployed JavaFX application.
   * Main() serves only as fallback in case the application can not be
   * launched through deployment artifacts, e.g., in IDEs with limited FX
   * support. NetBeans ignores main().
   * <p>
   * This method launches the JavaFX application.
   * </p>
   *
   * @param args Command line arguments passed to the application.
   */
  public static void main(String[] args) {
    launch(args);
  }
}