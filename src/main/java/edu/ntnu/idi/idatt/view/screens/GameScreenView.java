package edu.ntnu.idi.idatt.view.screens;

//import edu.ntnu.idi.idatt.controller.GameScreenController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * View component for the main game screen.
 * Builds the board, play-turn button, scoreboard and dice display.
 * Styling is entirely in GameScreen_styles.css.
 */
public class GameScreenView {

  private Scene scene;
  private final StackPane root;
  private final BorderPane layout;
  private final GridPane boardGrid;
  private final Button playTurnButton;
  private final VBox scoreboardBox;
  //private GameScreenController controller;

  // CSS and resource paths
  private static final String CSS_PATH =
      "/edu/ntnu/idi/idatt/view/resources/GameScreen/GameScreen_styles.css";
  private static final String[] DICE_IMAGE_PATHS = {
      "/edu/ntnu/idi/idatt/view/resources/GameScreen/dice-1.png",
      "/edu/ntnu/idi/idatt/view/resources/GameScreen/dice-2.png"
  };

  public GameScreenView() {
    // Root container
    root = new StackPane();
    layout = new BorderPane();
    root.getChildren().add(layout);

    // Build sub-components
    boardGrid      = createBoardGrid(9, 10);
    playTurnButton = createPlayTurnButton();
    scoreboardBox  = createScoreboardBox();

    // Place components in BorderPane
    StackPane boardContainer = new StackPane(boardGrid);
    boardContainer.getStyleClass().add("board");
    layout.setCenter(boardContainer);

    VBox leftBox = new VBox(playTurnButton);
    leftBox.setAlignment(Pos.BOTTOM_LEFT);
    leftBox.setPadding(new Insets(0, 0, 20, 20));
    layout.setLeft(leftBox);

    layout.setRight(scoreboardBox);

    // Scene + CSS
    scene = new Scene(root, 1280, 720);
    try {
      scene.getStylesheets().add(ResourceLoader.loadCssResource(CSS_PATH));
    } catch (BoardGameResourceException e) {
      System.err.println("Could not load game screen CSS: " + e.getMessage());
    }
  }

  /**
   * Wire-up the controller callbacks.
   */
//  public void setController(GameScreenController controller) {
//    this.controller = controller;
//    playTurnButton.setOnAction(e -> controller.onPlayTurn());
//  }

  public Scene getScene() {
    return scene;
  }

  // —— Helpers —— //

  /**
   * Creates a rows×cols checkerboard grid of Labels.
   * You should replace the numeric text with your actual tile numbering logic.
   */
  private GridPane createBoardGrid(int rows, int cols) {
    GridPane grid = new GridPane();
    grid.getStyleClass().add("board-grid");
    grid.setAlignment(Pos.CENTER);

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        int tileNumber = r * cols + c + 1;
        Label cell = new Label(String.valueOf(tileNumber));
        cell.getStyleClass().addAll(
            "cell",
            (tileNumber % 2 == 0) ? "even" : "odd"
        );
        grid.add(cell, c, rows - 1 - r); // flip so “1” ends up bottom-left
      }
    }
    return grid;
  }

  /**
   * “Play Turn” button with proper style class.
   */
  private Button createPlayTurnButton() {
    Button btn = new Button("Play Turn");
    btn.getStyleClass().add("button-play-turn");
    return btn;
  }

  /**
   * Builds the right-hand scoreboard: ordinals, player names,
   * a separator, “You rolled:”, and two dice images.
   */
  private VBox createScoreboardBox() {
    VBox box = new VBox(10);
    box.getStyleClass().add("scoreboard");
    box.setAlignment(Pos.TOP_CENTER);
    box.setPadding(new Insets(20));

    // Example player names — replace with your model data
    String[] players = { "Player 1", "Player 2", "Player 3", "Player 4" };

    for (int i = 0; i < players.length; i++) {
      Label ord = new Label((i+1) + ".");
      ord.getStyleClass().add("ordinal-" + (i+1));

      Label name = new Label(players[i]);
      name.getStyleClass().add("player-entry");

      HBox entry = new HBox(8, ord, name);
      entry.setAlignment(Pos.CENTER_LEFT);
      box.getChildren().add(entry);
    }

    // Separator line
    Region sep = new Region();
    sep.getStyleClass().add("separator");
    sep.setPrefHeight(1);
    sep.setMaxWidth(Double.MAX_VALUE);
    box.getChildren().add(sep);

    // “You rolled:” label
    Label youRolled = new Label("You rolled:");
    youRolled.getStyleClass().add("you-rolled");
    box.getChildren().add(youRolled);

    // Dice images
    HBox diceBox = new HBox(10);
    diceBox.setAlignment(Pos.CENTER);
    for (String path : DICE_IMAGE_PATHS) {
      try {
        ImageView dieView = new ImageView(ResourceLoader.loadImage(path));
        dieView.getStyleClass().add("dice-image");
        diceBox.getChildren().add(dieView);
      } catch (BoardGameResourceException ex) {
        System.err.println("Failed to load dice image " + path);
      }
    }
    box.getChildren().add(diceBox);

    return box;
  }
}

