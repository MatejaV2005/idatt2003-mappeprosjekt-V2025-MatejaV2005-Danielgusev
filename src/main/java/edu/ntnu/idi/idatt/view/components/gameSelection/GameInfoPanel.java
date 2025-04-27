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
  private final Text rule1;
  private final Text rule2;
  private final Text rule3;

  public GameInfoPanel() {
    super(10); // spacing between elements

    this.getStyleClass().add("info-panel");

    // Initialize components
    titleLabel = new Label("Board Summary");
    boardPreview = new ImageView(); // Empty by default; set image later

    tilesInfo = new Text("Tiles: 90");
    difficultyInfo = new Text("Difficulty: Normal");
    specialTilesInfo = new Text("Special Tiles: 5");

    rule1 = new Text("- Roll 2 dice to move");
    rule2 = new Text("- Land on ladder: climb!");
    rule3 = new Text("- Land on trap: fall");

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
        rule1,
        rule2,
        rule3
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
}
