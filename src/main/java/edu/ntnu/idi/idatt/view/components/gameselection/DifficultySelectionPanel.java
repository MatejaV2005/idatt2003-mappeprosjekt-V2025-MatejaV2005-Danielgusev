package edu.ntnu.idi.idatt.view.components.gameselection;

import edu.ntnu.idi.idatt.factory.ButtonFactory;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * A vertical panel presenting difficulty options and a custom board upload action.
 *
 * <p>Contains four buttons:
 * <ul>
 *   <li><b>Easy:</b> selects easy difficulty</li>
 *   <li><b>Normal:</b> selects normal difficulty</li>
 *   <li><b>Hard:</b> selects hard difficulty</li>
 *   <li><b>Upload Board:</b> opens a file chooser to upload a custom board layout</li>
 * </ul>
 * and a label indicating the panel’s purpose.
 * </p>
 *
 * <p>Uses {@link ButtonFactory} to create uniformly styled buttons.
 * </p>
 */
public class DifficultySelectionPanel extends VBox {

  private final Button easyDifficultyButton;
  private final Button normalDifficultyButton;
  private final Button hardDifficultyButton;
  private final Button uploadBoardButton;

  /**
   * Constructs a new {@code DifficultySelectionPanel}.
   *
   * <p>Initializes the label and four buttons with spacing of 20px,
   * centers them within the panel, and applies CSS style classes.
   * </p>
   */
  public DifficultySelectionPanel() {
    super(20);

    getStyleClass().add("difficulty-panel");
    setAlignment(Pos.TOP_CENTER);

    ButtonFactory buttonFactory = new ButtonFactory();

    Label difficultyLabel = new Label("Difficulty");
    difficultyLabel.getStyleClass().add("difficulty-label");

    easyDifficultyButton = buttonFactory.createSmallButton("Easy");
    normalDifficultyButton = buttonFactory.createSmallButton("Normal");
    hardDifficultyButton = buttonFactory.createSmallButton("Hard");
    uploadBoardButton   = buttonFactory.createSmallButton("Upload Board");

    easyDifficultyButton.getStyleClass().add("difficulty-button");
    normalDifficultyButton.getStyleClass().add("difficulty-button");
    hardDifficultyButton.getStyleClass().add("difficulty-button");
    uploadBoardButton.getStyleClass().add("difficulty-button");

    getChildren().addAll(
        difficultyLabel,
        easyDifficultyButton,
        normalDifficultyButton,
        hardDifficultyButton,
        uploadBoardButton
    );
  }

  /**
   * Returns the button that selects easy difficulty.
   *
   * @return the {@code Easy} difficulty button
   */
  public Button getEasyDifficultyButton() {
    return easyDifficultyButton;
  }

  /**
   * Returns the button that selects normal difficulty.
   *
   * @return the {@code Normal} difficulty button
   */
  public Button getNormalDifficultyButton() {
    return normalDifficultyButton;
  }

  /**
   * Returns the button that selects hard difficulty.
   *
   * @return the {@code Hard} difficulty button
   */
  public Button getHardDifficultyButton() {
    return hardDifficultyButton;
  }

  /**
   * Returns the button that opens the custom board upload dialog.
   *
   * @return the {@code Upload Board} button
   */
  public Button getUploadBoardButton() {
    return uploadBoardButton;
  }
}
