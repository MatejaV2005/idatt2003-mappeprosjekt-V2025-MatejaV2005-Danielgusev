package edu.ntnu.idi.idatt.UserInterface;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TitleScreen extends Application {

  @Override
  public void start(Stage primaryStage) {
    // Root StackPane to layer background image and foreground content
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: #FFF;"); // white background

    // Background Image (fills entire scene)
    Image bgImage = new Image("https://cdn.builder.io/api/v1/image/assets/TEMP/a12f26c75bda54824dcae548155d4a7b61bf25a6");
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setPreserveRatio(false); // stretch to fill the area

    // Foreground container (holds logo and buttons)
    VBox container = new VBox(20); // 20px gap between children
    container.setMaxWidth(484);
    container.setStyle("-fx-padding: 20;");
    container.setAlignment(Pos.CENTER);

    // Logo container with padding
    HBox logoContainer = new HBox();
    logoContainer.setAlignment(Pos.CENTER);
    logoContainer.setStyle("-fx-padding: 38;");

    // Logo image
    Image logoImage = new Image("https://cdn.builder.io/api/v1/image/assets/TEMP/47d82e27d393c8f3999d928a0cf001964415a05d");
    ImageView logoImageView = new ImageView(logoImage);
    logoImageView.setFitWidth(347);
    logoImageView.setFitHeight(323);
    // Clip to round the corners (arc width/height set to 46)
    Rectangle clip = new Rectangle(347, 323);
    clip.setArcWidth(46);
    clip.setArcHeight(46);
    logoImageView.setClip(clip);
    logoContainer.getChildren().add(logoImageView);

    // Buttons container for three actions
    VBox buttonsContainer = new VBox(19); // 19px gap between buttons
    buttonsContainer.setFillWidth(true);

    // Create buttons
    Button btnChooseGamemode = new Button("Choose Gamemode");
    Button btnSettings = new Button("Settings");
    Button btnQuitGame = new Button("Quit game");

    // Define a common style for buttons (using a vertical gradient and rounded corners)
    String buttonStyle = "-fx-pref-width: 100%; " +
        "-fx-pref-height: 72px; " +
        "-fx-background-radius: 12px; " +
        "-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #9B9011, #C4B659); " +
        "-fx-text-fill: #FFF; " +
        "-fx-font-size: 24px; " +
        "-fx-font-weight: bold;";

    btnChooseGamemode.setStyle(buttonStyle);
    btnSettings.setStyle(buttonStyle);
    btnQuitGame.setStyle(buttonStyle);

    // Ensure buttons expand to fill the container width
    btnChooseGamemode.setMaxWidth(Double.MAX_VALUE);
    btnSettings.setMaxWidth(Double.MAX_VALUE);
    btnQuitGame.setMaxWidth(Double.MAX_VALUE);

    buttonsContainer.getChildren().addAll(btnChooseGamemode, btnSettings, btnQuitGame);

    // Add the logo and buttons to the main container
    container.getChildren().addAll(logoContainer, buttonsContainer);

    // Center the container in the StackPane
    StackPane.setAlignment(container, Pos.CENTER);

    // Add both the background image and the container to the root StackPane
    root.getChildren().addAll(bgImageView, container);

    // Create the Scene and bind the background image to the scene dimensions
    Scene scene = new Scene(root, 800, 600);
    bgImageView.fitWidthProperty().bind(scene.widthProperty());
    bgImageView.fitHeightProperty().bind(scene.heightProperty());

    primaryStage.setTitle("The pimp playground");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}