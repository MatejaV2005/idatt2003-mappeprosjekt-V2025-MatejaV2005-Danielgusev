package edu.ntnu.idi.idatt.view.components.gameSelection;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A panel displaying board information like board preview, difficulty, and rules.
 */
public class GameInfoPanel extends VBox {

  private final Label titleLabel;
  private final ImageView boardPreview;
  private final Text tilesInfo;
  private final Text difficultyInfo;
  private final Text specialTilesInfo;
  private final Text rules;

  public GameInfoPanel() {
    super(10); // spacing between elements

    this.getStyleClass().add("info-panel");

    // Initialize components
    titleLabel = new Label("Board Summary");
    boardPreview = new ImageView(); // Empty by default; set image later

    tilesInfo = new Text("Tiles: 90");
    difficultyInfo = new Text("Difficulty: Normal");
    specialTilesInfo = new Text("Special Tiles: 5");

    rules = new Text("Roll 2 dice to move \nLand on Ladder to climb!  \nland on trap and fall!");

    String infoTextStyle = "-fx-font-size: 16px; -fx-font-weight: normal; -fx-fill: #e7c565;";
    tilesInfo.setStyle(infoTextStyle);
    difficultyInfo.setStyle(infoTextStyle);
    specialTilesInfo.setStyle(infoTextStyle);

    // Style for the rule texts
    String ruleTextStyle = "-fx-font-size: 14px; -fx-font-weight: normal; -fx-fill: white;";
    rules.setStyle(ruleTextStyle);

    // Optional: Style elements
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffe470");
    boardPreview.setFitWidth(150);
    boardPreview.setFitHeight(150);
    boardPreview.setPreserveRatio(false);

    this.setAlignment(Pos.TOP_CENTER);
    this.getChildren().addAll(
        titleLabel,
        boardPreview,
        tilesInfo,
        difficultyInfo,
        specialTilesInfo,
        rules
    );
  }

  /**
   * Sets the board preview image.
   */
  public void setBoardPreviewImage(ImageView image) {
    boardPreview.setImage(image.getImage());
  }

  /**
   * Updates game summary information dynamically.
   */
  public void updateGameInfo(int tiles, String difficulty, int specialTiles) {
    tilesInfo.setText("Tiles: " + tiles);
    difficultyInfo.setText("Difficulty: " + difficulty);
    specialTilesInfo.setText("Special Tiles: " + specialTiles);
  }

  /**
   * Preset info for Easy difficulty.
   */
  public void setEasyModeInfo() {
    updateGameInfo(60, "Easy", 3);
    rules.setText("Roll 2 dice to move \nFew traps, many ladders \nGoal: Reach tile 60");
  }

  /**
   * Preset info for Normal difficulty.
   */
  public void setNormalModeInfo() {
    updateGameInfo(90, "Normal", 5);
    rules.setText("Roll 2 dice to move \nBalanced traps and ladders \nGoal: Reach tile 90");
  }

  /**
   * Preset info for Hard difficulty.
   */
  public void setHardModeInfo() {
    updateGameInfo(120, "Hard", 10);
    rules.setText("Roll 2 dice to move \nMany traps, few ladders \nGoal: Reach tile 120");
  }

  /**
   * Info for uploading a custom board.
   */
  public void setUploadModeInfo() {
    updateGameInfo(0, "Custom", 0);
    rules.setText("upload youre own board in JSON format \nPlay custom rules!");
  }
}
