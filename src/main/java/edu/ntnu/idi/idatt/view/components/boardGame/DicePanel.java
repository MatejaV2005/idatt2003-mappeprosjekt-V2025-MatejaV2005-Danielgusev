package edu.ntnu.idi.idatt.view.components.boardGame;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
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
 * A JavaFX component to display the results of a dice roll, showing
 * individual die faces as images based on provided PNG files and the total sum.
 * The filenames are expected to be in the format 'dice-one.png', 'dice-two.png', etc.,
 * located in a 'Dice' subdirectory within the resources.
 */
public class DicePanel extends VBox {

  private static final Logger LOGGER = Logger.getLogger(DicePanel.class.getName());
  private static final double DICE_IMAGE_SIZE = 60.0;
  private static final String DICE_IMAGE_FOLDER_PATH = "/edu/ntnu/idi/idatt/view/resources/Dice/"; // Ensure this path is correct
  private static final String DICE_IMAGE_SUFFIX = ".png";

  private final Label titleLabel;
  private final ImageView die1ImageView;
  private final ImageView die2ImageView;
  private final Label sumLabel; // Label for the sum of dice
  private final ResourceLoader resourceLoader;
  private final Map<Integer, Image> diceImageCache;

  /**
   * Constructs a new DicePanel.
   * Initializes the UI elements for displaying dice rolls, including preloading dice images
   * for efficient display during gameplay.
   */
  public DicePanel() {
    super(5);
    this.resourceLoader = new ResourceLoader();
    this.diceImageCache = new HashMap<>();
    this.setPadding(new Insets(10));
    this.setAlignment(Pos.CENTER);
    this.getStyleClass().add("dice-panel");

    preloadDiceImages();

    this.titleLabel = new Label("You rolled:");
    this.titleLabel.getStyleClass().add("dice-panel-title");

    this.die1ImageView = new ImageView();
    this.die2ImageView = new ImageView();
    configureImageView(die1ImageView);
    configureImageView(die2ImageView);

    // Initialize sumLabel BEFORE calling resetDiceDisplay
    this.sumLabel = new Label("");
    this.sumLabel.getStyleClass().add("dice-sum-label");

    resetDiceDisplay(); // Now safe to call

    HBox diceImagesBox = new HBox(10);
    diceImagesBox.setAlignment(Pos.CENTER);
    diceImagesBox.getChildren().addAll(die1ImageView, die2ImageView);

    this.getChildren().addAll(titleLabel, diceImagesBox, sumLabel);
  }

  /**
   * Configures common visual properties for the dice ImageViews,
   * such as size and style class.
   * @param imageView The {@link ImageView} to configure.
   */
  private void configureImageView(ImageView imageView) {
    imageView.setFitHeight(DICE_IMAGE_SIZE);
    imageView.setFitWidth(DICE_IMAGE_SIZE);
    imageView.setPreserveRatio(true);
    imageView.getStyleClass().add("dice-image");
  }

  /**
   * Preloads dice images (faces 1 through 6) into an internal cache
   * to improve performance by avoiding repeated file loading during gameplay.
   * Logs warnings if any specific image fails to load.
   */
  private void preloadDiceImages() {
    for (int i = 1; i <= 6; i++) {
      try {
        Image img = loadDiceImageInternal(i);
        if (img != null) {
          diceImageCache.put(i, img);
        } else {
          LOGGER.log(Level.WARNING, "Failed to preload dice image for value: " + i + ". Image was null.");
        }
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error preloading dice image " + i, e);
      }
    }
    LOGGER.fine("Dice images preloading attempt complete.");
  }

  /**
   * Converts a numeric dice value (1-6) to its corresponding string representation
   * (e.g., 1 to "one") used for constructing image filenames.
   * @param value The numeric value of the die face (1-6).
   * @return The string name of the dice value, or null if the value is out of range.
   */
  private String getDiceValueName(int value) {
    return switch (value) {
      case 1 -> "one";
      case 2 -> "two";
      case 3 -> "three";
      case 4 -> "four";
      case 5 -> "five";
      case 6 -> "six";
      default -> null;
    };
  }

  /**
   * Loads a single dice image resource from the file system based on its face value.
   * Uses the specific filename format (e.g., "dice-one.png").
   * Logs an error and returns null if the image cannot be loaded or the value is invalid.
   *
   * @param value The face value of the die (1-6).
   * @return The loaded {@link Image}, or null if loading fails or the value is invalid.
   */
  private Image loadDiceImageInternal(int value) {
    String valueName = getDiceValueName(value);
    if (valueName == null) {
      LOGGER.log(Level.WARNING, "Invalid dice value requested for image loading: " + value);
      return null;
    }

    String filename = "dice-" + valueName + DICE_IMAGE_SUFFIX;
    String imagePath = DICE_IMAGE_FOLDER_PATH + filename;

    try {
      Image img = resourceLoader.loadImage(imagePath); // Assuming ResourceLoader handles null path or error
      if (img != null && img.isError()) { // Check if image loaded but has an error state
        throw new BoardGameResourceException("Image loaded with error state: " + imagePath);
      }
      return img; // Can be null if ResourceLoader.loadImage returns null
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load dice image resource: " + imagePath, e);
      return null;
    } catch (Exception e) { // Catch any other unexpected exceptions
      LOGGER.log(Level.SEVERE, "Unexpected error loading dice image: " + imagePath, e);
      return null;
    }
  }

  /**
   * Retrieves a preloaded dice image from the cache using its integer value.
   *
   * @param value The face value of the die (1-6).
   * @return The cached {@link Image}, or null if not found in cache or value is invalid.
   */
  private Image getCachedDiceImage(int value) {
    if (value < 1 || value > 6) {
      LOGGER.warning("Requested cached image for invalid dice value: " + value);
      return null;
    }
    return diceImageCache.get(value);
  }

  /**
   * Updates the panel to display the results of a specific dice roll.
   * Sets the appropriate images for each die based on their values and updates the total sum text.
   * Attempts to use cached images first, with a fallback to direct loading if an image is missing.
   *
   * @param die1Value The value shown on the first die (1-6).
   * @param die2Value The value shown on the second die (1-6).
   * @param totalSum  The sum of the two dice values.
   */
  public void updateDiceDisplay(int die1Value, int die2Value, int totalSum) {
    Image die1Image = getCachedDiceImage(die1Value);
    Image die2Image = getCachedDiceImage(die2Value);

    if (die1Image == null && (die1Value >= 1 && die1Value <=6) ) {
      LOGGER.log(Level.WARNING, "Missing cached image for die value: " + die1Value + ". Attempting direct load.");
      die1Image = loadDiceImageInternal(die1Value);
    }
    if (die2Image == null && (die2Value >= 1 && die2Value <=6) ) {
      LOGGER.log(Level.WARNING, "Missing cached image for die value: " + die2Value + ". Attempting direct load.");
      die2Image = loadDiceImageInternal(die2Value);
    }

    die1ImageView.setImage(die1Image);
    die2ImageView.setImage(die2Image);

    titleLabel.setText("You rolled: " + totalSum);
    sumLabel.setText("");

    LOGGER.fine("Dice display updated: " + die1Value + " + " + die2Value + " = " + totalSum);
  }

  /**
   * Updates the panel based on the state of a {@link edu.ntnu.idi.idatt.model.core.Dice} object.
   * Assumes the Dice object uses exactly two dice (accessed by indices 0 and 1)
   * and that it has been rolled to reflect the latest values.
   *
   * @param dice The {@link edu.ntnu.idi.idatt.model.core.Dice} object after it has been rolled.
   * If null, the display will be reset.
   */
  public void updateDiceDisplay(edu.ntnu.idi.idatt.model.core.Dice dice) {
    if (dice == null) {
      LOGGER.warning("Attempted to update DicePanel with null Dice object. Resetting display.");
      resetDiceDisplay();
      return;
    }
    try {
      int die1 = dice.getDieValue(0);
      int die2 = dice.getDieValue(1);
      int total = dice.getTotalDiceValue();
      updateDiceDisplay(die1, die2, total);
    } catch (IndexOutOfBoundsException e) {
      LOGGER.log(Level.SEVERE, "Dice object does not have values for index 0 and 1 as expected. Resetting display.", e);
      resetDiceDisplay();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error getting values from Dice object. Resetting display.", e);
      resetDiceDisplay();
    }
  }

  /**
   * Resets the dice display to a default or blank state.
   * This is typically called at the start of a new turn or when the game initializes,
   * showing placeholder images for the dice (e.g., face '1').
   */
  public void resetDiceDisplay() {
    titleLabel.setText("Roll the dice!");
    if (sumLabel != null) {
      sumLabel.setText("");
    } else {
      LOGGER.severe("sumLabel is null during resetDiceDisplay. Initialization order might be incorrect.");
    }
    die1ImageView.setImage(getCachedDiceImage(1)); // Default to showing '1'
    die2ImageView.setImage(getCachedDiceImage(1)); // Default to showing '1'
    LOGGER.fine("Dice display reset to default state.");
  }

  /**
   * Visually disables or enables the panel, typically by changing its opacity.
   * This can be used to indicate that dice interaction is not currently allowed.
   *
   * @param disable True to apply a 'disabled' visual style (e.g., faded),
   * false to restore normal appearance.
   */
  public void setDisabledVisual(boolean disable) {
    this.setOpacity(disable ? 0.5 : 1.0);
    LOGGER.fine("DicePanel visual disabled state set to: " + disable);
  }
}
