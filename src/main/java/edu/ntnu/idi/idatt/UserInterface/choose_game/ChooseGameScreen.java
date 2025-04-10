package edu.ntnu.idi.idatt.UserInterface.choose_game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import edu.ntnu.idi.idatt.UserInterface.TitleScreen.TitleScreen;

public class ChooseGameScreen {

  private Scene scene;
  private Stage primaryStage;

  public ChooseGameScreen(Stage stage) {
    this.primaryStage = stage;

    // 1. Create root pane (StackPane) to store background and content
    StackPane root = new StackPane();

    // 2. Load and display background image
    String imagePath = "/edu/ntnu/idi/idatt/UserInterface/ChooseGame/Background_choose_game.png";
    Image bgImage = new Image(getClass().getResourceAsStream(imagePath));
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setFitWidth(1280);
    bgImageView.setFitHeight(720);
    bgImageView.setPreserveRatio(false);
    root.getChildren().add(bgImageView);

    // 3. Create a VBox for all content (game modes and back button)
    VBox contentBox = new VBox(30);
    contentBox.setAlignment(Pos.CENTER);

    // 4. Create container for game mode panels
    HBox gameModesContainer = new HBox(40);
    gameModesContainer.setAlignment(Pos.CENTER);

    // 5. Create the individual game mode panels
    VBox panel1 = createGameModePanel("Snakes & Ladders");
    VBox panel2 = createGameModePanel("Other Gamemode");
    VBox panel3 = createGameModePanel("Other Gamemode");

    // 6. Add panels to the container
    gameModesContainer.getChildren().addAll(panel1, panel2, panel3);

    // 7. Create back button
    Button backButton = new Button("Back");
    backButton.getStyleClass().add("golden-button");
    backButton.setOnAction(e -> {
      // Go back to title screen
      TitleScreen titleScreen = new TitleScreen();
      titleScreen.start(primaryStage);
    });

    // 8. Add game modes and back button to content box
    contentBox.getChildren().addAll(gameModesContainer, backButton);

    // 9. Add content to root pane
    root.getChildren().add(contentBox);

    // 10. Create scene and add CSS
    scene = new Scene(root, 1280, 720);
    String cssPath = "/edu/ntnu/idi/idatt/UserInterface/ChooseGame/ChooseGameScreen_styles.css";
    scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
  }

  /**
   * Returns the created scene.
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Helper method to create a game mode panel with heading and white box.
   *
   * @param modeName Name of the game mode
   * @return A VBox panel with correct styling
   */
  private VBox createGameModePanel(String modeName) {
    // Create label with style class
    Label label = new Label(modeName);
    label.getStyleClass().add("game-mode-label");

    // Create white box with style class
    Pane whiteBox = new Pane();
    whiteBox.getStyleClass().add("white-box");
    whiteBox.setPrefSize(250, 400);

    // Create VBox panel and set alignment
    VBox vBox = new VBox(10, label, whiteBox);
    vBox.setAlignment(Pos.TOP_CENTER);

    // Make panels clickable
    vBox.getStyleClass().add("game-panel");
    vBox.setOnMouseClicked(e -> {
      System.out.println("Selected game mode: " + modeName);
      // Add code here to start the selected game
    });

    return vBox;
  }
}