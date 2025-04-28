package edu.ntnu.idi.idatt.view.components.gameSelection;

import edu.ntnu.idi.idatt.factory.ButtonFactory;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * A panel for selecting game difficulty or uploading a custom board.
 */
public class DifficultySelectionPanel extends VBox {

  private final Button easyDifficultyButton;
  private final Button normalDifficultyButton;
  private final Button hardDifficultyButton;
  private final Button uploadBoardButton;

  public DifficultySelectionPanel() {
    super(20); // spacing between buttons

    this.getStyleClass().add("difficulty-panel");

    ButtonFactory buttonFactory = new ButtonFactory();

    easyDifficultyButton = buttonFactory.createSmallButton("Easy");
    normalDifficultyButton = buttonFactory.createSmallButton("Normal");
    hardDifficultyButton = buttonFactory.createSmallButton("Hard");
    uploadBoardButton = buttonFactory.createSmallButton("Upload Board");

    easyDifficultyButton.getStyleClass().add("difficulty-button");
    normalDifficultyButton.getStyleClass().add("difficulty-button");
    hardDifficultyButton.getStyleClass().add("difficulty-button");
    uploadBoardButton.getStyleClass().add("difficulty-button");


    this.setAlignment(Pos.TOP_CENTER);

    this.getChildren().addAll(
        easyDifficultyButton,
        normalDifficultyButton,
        hardDifficultyButton,
        uploadBoardButton
    );
  }

  // Optionally, provide public getters if controller needs to bind event handlers
  public Button getEasyDifficultyButton() {
    return easyDifficultyButton;
  }

  public Button getNormalDifficultyButton() {
    return normalDifficultyButton;
  }

  public Button getHardDifficultyButton() {
    return hardDifficultyButton;
  }

  public Button getUploadBoardButton() {
    return uploadBoardButton;
  }
}
