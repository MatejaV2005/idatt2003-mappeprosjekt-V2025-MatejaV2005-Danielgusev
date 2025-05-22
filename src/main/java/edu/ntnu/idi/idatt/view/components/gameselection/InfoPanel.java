package edu.ntnu.idi.idatt.view.components.gameselection;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Represents a panel for displaying detailed information about a selected game or board.
 *
 * <p>This panel includes a title, an optional preview image of the game board,
 * and textual information about the game's title, mode, features, and rules.
 * It is designed to be dynamically updated based on user selections in a game setup screen.
 * The panel adheres to specific styling defined in an external CSS file using style classes
 * like "info-panel", "panel-header", "info-panel-general-detail", and "info-panel-rules".
 * </p>
 *
 * @see VBox
 * @see Label
 * @see ImageView
 * @see Text
 */
public class InfoPanel extends VBox {
  private static final Logger LOGGER = Logger.getLogger(InfoPanel.class.getName());

  private static final double DEFAULT_SPACING = 10.0;
  private static final double DEFAULT_PADDING = 15.0;
  private static final double IMAGE_PREVIEW_FIT_WIDTH = 180.0;
  private static final double IMAGE_PREVIEW_FIT_HEIGHT = 120.0;
  private static final double TEXT_WRAPPING_WIDTH = 280.0;
  private static final double SPACING_REGION_HEIGHT = 15.0;

  private static final String STYLE_CLASS_INFO_PANEL = "info-panel";
  private static final String STYLE_CLASS_PANEL_HEADER = "panel-header";
  private static final String STYLE_CLASS_GENERAL_DETAIL = "info-panel-general-detail";
  private static final String STYLE_CLASS_RULES = "info-panel-rules";

  private static final String NOT_AVAILABLE_TEXT = "N/A";
  private static final String NO_RULES_PROVIDED_TEXT = "No rules provided.";
  private final Text gameTitleInfoText;
  private final Text gameModeInfoText;
  private final Text featuresInfoText;
  private final Text rulesDescriptionText;

  /**
   * Constructs a new {@code InfoPanel}.
   *
   * <p>Initializes all UI components, sets their styles and properties,
   * and arranges them within this {@link VBox}. The panel is initially populated
   * with information for the "Normal Mode".
   * </p>
   */
  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  public InfoPanel() {
    super(DEFAULT_SPACING);
    this.getStyleClass().add(STYLE_CLASS_INFO_PANEL);
    this.setAlignment(Pos.TOP_CENTER);
    this.setPadding(new Insets(DEFAULT_PADDING));

    Label titleLabel = createStyledLabel();
    ImageView boardPreviewImageView = createBoardPreview();

    gameTitleInfoText = createConfiguredTextNode(STYLE_CLASS_GENERAL_DETAIL);
    gameModeInfoText = createConfiguredTextNode(STYLE_CLASS_GENERAL_DETAIL);
    featuresInfoText = createConfiguredTextNode(STYLE_CLASS_GENERAL_DETAIL);
    rulesDescriptionText = createConfiguredTextNode(STYLE_CLASS_RULES);

    Region spacingRegion = new Region();
    spacingRegion.setPrefHeight(SPACING_REGION_HEIGHT);

    this.getChildren()
        .addAll(
            titleLabel,
            boardPreviewImageView,
            gameTitleInfoText,
            gameModeInfoText,
            featuresInfoText,
            spacingRegion,
            rulesDescriptionText);

    setNormalModeInfo();
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.fine("InfoPanel constructed and initialized with Normal Mode info.");
    }
  }

  /**
   * Creates and configures a {@link Label} with the specified text and style class.
   *
   * @return A configured {@link Label} instance.
   */
  private Label createStyledLabel() {
    Label label = new Label("Game Details");
    label.getStyleClass().add(InfoPanel.STYLE_CLASS_PANEL_HEADER);
    return label;
  }

  /**
   * Creates and configures an {@link ImageView} for the board preview.
   * The image view is initially hidden and not managed by the layout.
   *
   * @return A configured {@link ImageView} instance.
   */
  private ImageView createBoardPreview() {
    ImageView imageView = new ImageView();
    imageView.setFitWidth(IMAGE_PREVIEW_FIT_WIDTH);
    imageView.setFitHeight(IMAGE_PREVIEW_FIT_HEIGHT);
    imageView.setPreserveRatio(true);
    imageView.setVisible(false);
    imageView.setManaged(false);
    return imageView;
  }

  /**
   * Creates and configures a {@link Text} node with a specific style class,
   * wrapping width, and text alignment.
   *
   * @param styleClass The CSS style class to apply.
   * @return A configured {@link Text} instance.
   */
  private Text createConfiguredTextNode(String styleClass) {
    Text textNode = new Text();
    textNode.getStyleClass().add(styleClass);
    textNode.setWrappingWidth(TEXT_WRAPPING_WIDTH);
    textNode.setTextAlignment(TextAlignment.LEFT);
    return textNode;
  }


  /**
   * Updates the displayed game information in the panel.
   *
   * <p>This method sets the text for the game title, mode, features, and rules.
   * If any of the provided strings are {@code null} or empty, a default placeholder
   * text (e.g., "N/A" or "No rules provided.") will be used.
   * </p>
   *
   * @param title The title of the game or board variant.
   * @param mode The mode or difficulty of the game.
   * @param features A brief description of the game's key features.
   * @param rulesDescription A detailed description of the game rules.
   */
  public void updateGameInfo(
      String title, String mode, String features, String rulesDescription) {
    gameTitleInfoText.setText("Board: " + getInfoOrDefault(title, NOT_AVAILABLE_TEXT));
    gameModeInfoText.setText("Mode: " + getInfoOrDefault(mode, NOT_AVAILABLE_TEXT));
    featuresInfoText.setText("Features: " + getInfoOrDefault(features, NOT_AVAILABLE_TEXT));
    rulesDescriptionText.setText(getInfoOrDefault(rulesDescription, NO_RULES_PROVIDED_TEXT));

    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.fine(() -> "InfoPanel updated for game: " + title);
    }
  }

  /**
   * Returns the provided information string or a default value if the info is null or empty.
   *
   * @param info The information string to check.
   * @param defaultValue The default value to return if info is invalid.
   * @return The original info string or the default value.
   */
  private String getInfoOrDefault(String info, String defaultValue) {
    return (info != null && !info.trim().isEmpty()) ? info : defaultValue;
  }

  /**
   * Sets the panel to display information for the "Easy Mode" of the classic game.
   */
  public void setEasyModeInfo() {
    updateGameInfo(
        "Classic Board (Easy)",
        "Easy Difficulty",
        "60 Tiles, Few traps, many ladders",
        """
        • Roll 2 dice to move.

        • Land on a Ladder to climb up!

        • Land on a Snake to slide down.

        • First to reach tile 60 wins!
        """);
  }

  /**
   * Sets the panel to display information for the "Normal Mode" of the classic game.
   * This is also the default information set upon construction.
   */
  public void setNormalModeInfo() {
    updateGameInfo(
        "Classic Board (Normal)",
        "Normal Difficulty",
        "90 Tiles, Balanced traps & ladders",
        """
        • Roll 2 dice to move.

        • Land on a Ladder to climb up!

        • Land on a Snake to slide down.

        • Some tiles have special effects!

        • First to reach tile 90 wins!
        """);
  }

  /**
   * Sets the panel to display information for the "Hard Mode" of the classic game.
   */
  public void setHardModeInfo() {
    updateGameInfo(
        "Classic Board (Hard)",
        "Hard Difficulty",
        "120 Tiles, Many traps, few ladders",
        """
        • Roll 2 dice to move.

        • Land on a Ladder to climb up!

        • Land on a Snake to slide down.

        • Beware of tricky special tiles!

        • First to reach tile 120 wins!
        """);
  }

  /**
   * Sets the panel to display information for a custom game loaded from a JSON file.
   */
  public void setUploadModeInfo() {
    updateGameInfo(
        "Custom Board (JSON)",
        "User Defined",
        "Varies by file (Snakes & Ladders)",
        """
        • Upload your own board in JSON format.

        • The board file defines tile count, snakes, ladders, and special actions.

        • Play by the rules of your custom creation!
        """);
  }

  /**
   * Sets the panel to display information for the "Astro Rally" game mode.
   */
  public void setAstroRallyInfo() {
    updateGameInfo(
        "Astro Rally Circuit",
        "Race Mode",
        "30 Tiles (example), Ship Abilities, 3 Laps",
        """
        • Choose your Rally Ship, each with unique abilities!

        • Roll 2 dice to navigate the cosmic circuit.

        • Watch out for Asteroid Fields and Nebula Clouds.

        • Utilize Boost Gates and strategic path choices.

        • First to complete 3 laps wins the Astro Rally!
        """);
  }
}
