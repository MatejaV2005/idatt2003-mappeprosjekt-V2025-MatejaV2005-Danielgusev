package edu.ntnu.idi.idatt.view.components.gameSelection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.util.logging.Logger;

/**
 * A panel displaying board information like board preview, difficulty, and rules for the game
 * setup screen. It can be dynamically updated to show information for different game types.
 */
public class GameInfoPanel extends VBox {

  private static final Logger LOGGER = Logger.getLogger(GameInfoPanel.class.getName());

  private final Label titleLabel;
  private final ImageView boardPreviewImageView;
  private final Text gameTitleInfoText;
  private final Text gameModeInfoText;
  private final Text featuresInfoText;
  private final Text rulesDescriptionText;

  public GameInfoPanel() {
    super(10);
    this.getStyleClass().add("info-panel");
    this.setAlignment(Pos.TOP_CENTER);
    this.setPadding(new Insets(15));

    titleLabel = new Label("Game Details");
    titleLabel.getStyleClass().add("panel-header");

    boardPreviewImageView = new ImageView();
    boardPreviewImageView.setFitWidth(180);
    boardPreviewImageView.setFitHeight(120);
    boardPreviewImageView.setPreserveRatio(true);
    boardPreviewImageView.setVisible(false);
    boardPreviewImageView.setManaged(false);

    gameTitleInfoText = new Text();
    gameModeInfoText = new Text();
    featuresInfoText = new Text();
    rulesDescriptionText = new Text();

    String infoTextStyleClass = "info-panel-text";
    gameTitleInfoText.getStyleClass().add(infoTextStyleClass);
    gameModeInfoText.getStyleClass().add(infoTextStyleClass);
    featuresInfoText.getStyleClass().add(infoTextStyleClass);

    String ruleTextStyleClass = "info-panel-rules";
    rulesDescriptionText.getStyleClass().add(ruleTextStyleClass);
    rulesDescriptionText.setWrappingWidth(280);
    rulesDescriptionText.setTextAlignment(TextAlignment.LEFT);

    this.getChildren()
        .addAll(
            titleLabel,
            boardPreviewImageView,
            gameTitleInfoText,
            gameModeInfoText,
            featuresInfoText,
            rulesDescriptionText);

    setNormalModeInfo();
    LOGGER.fine("GameInfoPanel (for setup screen) constructed.");
  }

  /**
   * Sets the board preview image.
   *
   * @param image The image to display. If null, the ImageView will be hidden.
   */
  public void setBoardPreviewImage(Image image) {
    boardPreviewImageView.setImage(image);
    boardPreviewImageView.setVisible(image != null);
    boardPreviewImageView.setManaged(image != null);
  }

  /**
   * Updates the game information displayed in the panel.
   *
   * @param title The title of the game or board variant (e.g., "Classic Board (Normal)").
   * @param mode The mode or difficulty (e.g., "Normal Difficulty", "Race Mode").
   * @param features A brief description of key features (e.g., "Balanced traps & ladders").
   * @param rulesDescription The rules for this game/mode.
   */
  public void updateGameInfo(
      String title, String mode, String features, String rulesDescription) {
    gameTitleInfoText.setText("Board: " + (title != null && !title.isEmpty() ? title : "N/A"));
    gameModeInfoText.setText("Mode: " + (mode != null && !mode.isEmpty() ? mode : "N/A"));
    featuresInfoText.setText(
        "Features: " + (features != null && !features.isEmpty() ? features : "N/A"));
    rulesDescriptionText.setText(
        rulesDescription != null && !rulesDescription.isEmpty()
            ? rulesDescription
            : "No rules provided.");
    LOGGER.fine("GameInfoPanel updated for: " + title);
  }

  public void setEasyModeInfo() {
    updateGameInfo(
        "Classic Board (Easy)",
        "Easy Difficulty",
        "60 Tiles, Few traps, many ladders",
        "• Roll 2 dice to move.\n"
            + "• Land on a Ladder to climb up!\n"
            + "• Land on a Snake to slide down.\n"
            + "• First to reach tile 60 wins!");
  }

  public void setNormalModeInfo() {
    updateGameInfo(
        "Classic Board (Normal)",
        "Normal Difficulty",
        "90 Tiles, Balanced traps & ladders",
        "• Roll 2 dice to move.\n"
            + "• Land on a Ladder to climb up!\n"
            + "• Land on a Snake to slide down.\n"
            + "• Some tiles have special effects!\n"
            + "• First to reach tile 90 wins!");
  }

  public void setHardModeInfo() {
    updateGameInfo(
        "Classic Board (Hard)",
        "Hard Difficulty",
        "120 Tiles, Many traps, few ladders",
        "• Roll 2 dice to move.\n"
            + "• Land on a Ladder to climb up!\n"
            + "• Land on a Snake to slide down.\n"
            + "• Beware of tricky special tiles!\n"
            + "• First to reach tile 120 wins!");
  }

  public void setUploadModeInfo() {
    updateGameInfo(
        "Custom Board (JSON)",
        "User Defined",
        "Varies by file (Snakes & Ladders)",
        "• Upload your own board in JSON format.\n"
            + "• The board file defines tile count, snakes, ladders, and special actions.\n"
            + "• Play by the rules of your custom creation!");
  }

  public void setAstroRallyInfo() {
    updateGameInfo(
        "Astro Rally Circuit",
        "Race Mode",
        "30 Tiles (example), Ship Abilities, 3 Laps",
        "• Choose your Rally Ship, each with unique abilities!\n"
            + "• Roll 2 dice to navigate the cosmic circuit.\n"
            + "• Watch out for Asteroid Fields and Nebula Clouds.\n"
            + "• Utilize Boost Gates and strategic path choices.\n"
            + "• First to complete 3 laps wins the Astro Rally!");
  }
}
