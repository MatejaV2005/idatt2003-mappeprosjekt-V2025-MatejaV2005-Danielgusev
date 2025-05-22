package edu.ntnu.idi.idatt.view.components.boardgame;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A JavaFX component that displays two dice faces and their total sum.
 *
 * <p>Face images are expected at
 * {@code /edu/ntnu/idi/idatt/view/resources/Dice/dice-{one|two|…|six}.png}.
 * Images 1–6 are preloaded into a cache for performance.
 * </p>
 */
public class DicePanel extends VBox {

  private static final Logger LOGGER = Logger.getLogger(DicePanel.class.getName());
  private static final double SPACING = 5.0;
  private static final Insets PADDING = new Insets(10);
  private static final String STYLE_CLASS = "dice-panel";
  private static final String TITLE_STYLE = "dice-panel-title";
  private static final String IMAGE_STYLE = "dice-image";
  private static final double IMAGE_SIZE = 60.0;
  private static final String IMAGE_FOLDER = "/edu/ntnu/idi/idatt/view/resources/Dice/";
  private static final String IMAGE_SUFFIX = ".png";
  private static final int MIN_FACE = 1;
  private static final int MAX_FACE = 6;

  private final Label    titleLabel;
  private final ImageView die1ImageView;
  private final ImageView die2ImageView;
  private final Label    sumLabel;
  private final Map<Integer, Image> diceImageCache = new HashMap<>();

  /**
   * Constructs a new {@code DicePanel}, preloads all dice faces,
   * and resets to its default state.
   */
  public DicePanel() {
    super(SPACING);
    setPadding(PADDING);
    setAlignment(Pos.CENTER);
    getStyleClass().add(STYLE_CLASS);

    preloadDiceImages();

    titleLabel = new Label();
    titleLabel.getStyleClass().add(TITLE_STYLE);

    die1ImageView = createImageView();
    die2ImageView = createImageView();

    sumLabel = new Label();
    sumLabel.getStyleClass().add("dice-sum-label");

    resetDiceDisplay();

    HBox diceBox = new HBox(SPACING, die1ImageView, die2ImageView);
    diceBox.setAlignment(Pos.CENTER);
    getChildren().setAll(titleLabel, diceBox, sumLabel);
  }

  /**
   * Preloads all dice face images (1–6) into {@link #diceImageCache}.
   * Logs at WARN level if any single face fails to load.
   */
  private void preloadDiceImages() {
    for (int i = MIN_FACE; i <= MAX_FACE; i++) {
      try {
        Image img = loadDiceImageInternal(i);
        if (img != null) {
          diceImageCache.put(i, img);
        } else {
          LOGGER.log(Level.WARNING,
              "Failed to preload dice image for face {0}", i);
        }
      } catch (Exception e) {
        LOGGER.log(
            Level.SEVERE,
            String.format("Error preloading dice image for face %d", i),
            e
        );
      }
    }
    LOGGER.fine("Preload of dice images complete.");
  }

  /**
   * Updates the panel to show two specific dice values and their sum.
   *
   * <p>Attempts to retrieve each face from cache; if missing, falls back
   * to loading via {@link #loadDiceImageInternal(int)}.
   * </p>
   *
   * @param die1Value face value for the first die (1–6)
   * @param die2Value face value for the second die (1–6)
   * @param totalSum  computed sum of both dice (e.g. 7)
   */
  public void updateDiceDisplay(int die1Value, int die2Value, int totalSum) {
    Image img1 = diceImageCache.getOrDefault(die1Value, loadDiceImageInternal(die1Value));
    Image img2 = diceImageCache.getOrDefault(die2Value, loadDiceImageInternal(die2Value));

    die1ImageView.setImage(img1);
    die2ImageView.setImage(img2);

    titleLabel.setText(String.format("You rolled: %d", totalSum));
    sumLabel.setText("");

    LOGGER.fine(() ->
        String.format("Dice updated: %d + %d = %d", die1Value, die2Value, totalSum));
  }

  /**
   * Updates the panel based on a {@link Dice} model.
   *
   * <p>If {@code dice} is {@code null} or indexing fails, the panel is reset.
   * </p>
   *
   * @param dice the dice object containing two face values, or {@code null}
   */
  public void updateDiceDisplay(Dice dice) {
    if (dice == null) {
      LOGGER.warning("Null Dice provided; resetting display.");
      resetDiceDisplay();
      return;
    }
    try {
      updateDiceDisplay(
          dice.getDieValue(0),
          dice.getDieValue(1),
          dice.getTotalDiceValue());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Error extracting values from Dice; resetting display.", e);
      resetDiceDisplay();
    }
  }

  /**
   * Resets this panel to its initial, default state:
   * showing two '1' faces and a default title.
   */
  public void resetDiceDisplay() {
    titleLabel.setText("Roll the dice!");
    sumLabel.setText("");
    die1ImageView.setImage(diceImageCache.get(MIN_FACE));
    die2ImageView.setImage(diceImageCache.get(MIN_FACE));
    LOGGER.fine("DicePanel reset to default state.");
  }

  /**
   * Toggles a 'disabled' visual state by changing opacity.
   *
   * @param disable {@code true} to fade out, {@code false} for full opacity
   */
  public void setDisabledVisual(boolean disable) {
    setOpacity(disable ? 0.5 : 1.0);
    LOGGER.fine(() -> "Disabled visual set to " + disable);
  }

  // --- Private helpers unchanged from core logic ---

  private ImageView createImageView() {
    ImageView iv = new ImageView();
    iv.setFitWidth(IMAGE_SIZE);
    iv.setFitHeight(IMAGE_SIZE);
    iv.setPreserveRatio(true);
    iv.getStyleClass().add(IMAGE_STYLE);
    return iv;
  }

  private Image loadDiceImageInternal(int value) {
    String name = switch (value) {
      case 1 -> "one";
      case 2 -> "two";
      case 3 -> "three";
      case 4 -> "four";
      case 5 -> "five";
      case 6 -> "six";
      default -> null;
    };
    if (name == null) {
      LOGGER.log(Level.WARNING,
          "Invalid dice face requested: {0}", value);
      return null;
    }
    String path = IMAGE_FOLDER + "dice-" + name + IMAGE_SUFFIX;
    try {
      Image img = ResourceLoader.loadImage(path);
      if (img.isError()) {
        throw new BoardGameResourceException("Error state in image: " + path);
      }
      return img;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          String.format("Failed to load dice image '%s'", path), e);
      return null;
    }
  }
}
