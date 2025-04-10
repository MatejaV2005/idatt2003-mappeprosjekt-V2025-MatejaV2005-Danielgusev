package edu.ntnu.idi.idatt.UserInterface.TitleScreen;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TitleScreen extends Application {

  @Override
  public void start(Stage primaryStage) {
    // Create the root StackPane to layer background and content
    StackPane root = new StackPane();

    // Load and display background image from file
    Image bgImage = new Image(getClass().getResourceAsStream("/edu/ntnu/idi/idatt/UserInterface/TitleScreen/Background_titleScreen.png"));
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setPreserveRatio(false); // Stretch image to fill entire screen
    bgImageView.setFitWidth(1280);
    bgImageView.setFitHeight(720);

    // VBox to hold logo and button layout, centered on screen
    VBox container = new VBox(30); // 30px vertical spacing
    container.setAlignment(Pos.CENTER);

    // Load and show logo from local file
    Image logoImage = new Image(getClass().getResourceAsStream("/edu/ntnu/idi/idatt/UserInterface/TitleScreen/logo.png"));
    ImageView logoImageView = new ImageView(logoImage);
    logoImageView.setFitWidth(400);
    logoImageView.setPreserveRatio(true);

    // Create a clipping rectangle with rounded corners for the logo
    Rectangle clip = new Rectangle();
    clip.setArcWidth(30);   // Adjust for desired roundness
    clip.setArcHeight(30);
    // Add a listener so that when layout bounds are computed or change, the clip is updated.
    logoImageView.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
      clip.setWidth(newBounds.getWidth());
      clip.setHeight(newBounds.getHeight());
    });
    // Optionally, set initial values (might be zero initially)
    clip.setWidth(logoImageView.getLayoutBounds().getWidth());
    clip.setHeight(logoImageView.getLayoutBounds().getHeight());
    // Apply the clip to the ImageView
    logoImageView.setClip(clip);

    // Create a decorator
    ButtonDecorator hoverDecorator = new HoverEffectDecorator();

    // Create buttons using the Builder pattern and apply the decorator
    Button btnChooseGamemode = new ButtonBuilder().withText("Choose Gamemode").build();
    btnChooseGamemode = hoverDecorator.decorate(btnChooseGamemode);

    Button btnSettings = new ButtonBuilder().withText("Settings").build();
    btnSettings = hoverDecorator.decorate(btnSettings);

    Button btnQuitGame = new ButtonBuilder().withText("Quit Game").build();
    btnQuitGame = hoverDecorator.decorate(btnQuitGame);

    // Example action for the Quit Game button
    btnQuitGame.setOnAction(e -> primaryStage.close());

    // Organize buttons into a VBox
    VBox buttonsBox = new VBox(20, btnChooseGamemode, btnSettings, btnQuitGame);
    buttonsBox.setAlignment(Pos.CENTER);
    buttonsBox.setMaxWidth(300);

    // Add logo and button box to the container
    container.getChildren().addAll(logoImageView, buttonsBox);

    // Add background and container to root layer
    root.getChildren().addAll(bgImageView, container);

    // Create the scene and load the external stylesheet.
    Scene scene = new Scene(root, 1280, 720);
    scene.getStylesheets().add(getClass().getResource("/edu/ntnu/idi/idatt/UserInterface/TitleScreen/TitleScreen-Styles.css").toExternalForm());

    // Bind the background image size to the scene dimensions.
    bgImageView.fitWidthProperty().bind(scene.widthProperty());
    bgImageView.fitHeightProperty().bind(scene.heightProperty());

    // Set up and show the main application window.
    primaryStage.setTitle("The Pimp Playground");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}