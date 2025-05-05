package edu.ntnu.idi.idatt.view;

import edu.ntnu.idi.idatt.controller.NavigationController;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {

  @Override
  public void start(Stage primaryStage) {
    try {
      // Set up the primary stage
      primaryStage.setTitle("The Pimp Playground");
      primaryStage.setMinWidth(800);
      primaryStage.setMinHeight(600);
      primaryStage.setMaximized(false);

      // Create the navigation controller
      NavigationController navigationController = new NavigationController(primaryStage);

      // Navigate to the title screen to start the application
      navigationController.navigateToTitleScreen();

      // Show the stage
      primaryStage.show();

    } catch (Exception e) {
      System.err.println("Error starting application: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Main method to launch the application.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}

