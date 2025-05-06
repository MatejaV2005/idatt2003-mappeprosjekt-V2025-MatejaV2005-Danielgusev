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
 * The filenames are expected to be in the format 'dice-one.png', 'dice-two.png', etc.
 */
public class DicePanel extends VBox {

  private static final Logger LOGGER = Logger.getLogger(DicePanel.class.getName());
  private static final double DICE_IMAGE_SIZE = 60.0;
  private static final String DICE_IMAGE_FOLDER_PATH = "/edu/ntnu/idi/idatt/view/resources/Dice/";

  private final Label titleLabel;
  private final ImageView die1ImageView;
  private final ImageView die2ImageView;
  private final Label sumLabel;
  private final ResourceLoader resourceLoader;
  private final Map<Integer, Image> diceImageCache;

  /**
   * Constructs a new DicePanel.
   * Initializes the UI elements for displaying dice rolls and preloads images.
   */
  public DicePanel() {
    super(5); // Spacing between elements in the VBox
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

    resetDiceDisplay();

    HBox diceImagesBox = new HBox(10);
    diceImagesBox.setAlignment(Pos.CENTER);
    diceImagesBox.getChildren().addAll(die1ImageView, die2ImageView);

    this.sumLabel = new Label("");
    this.sumLabel.getStyleClass().add("dice-sum-label");

    this.getChildren().addAll(titleLabel, diceImagesBox, sumLabel);
  }

  /**
   * Configures common properties for the dice ImageViews.
   * @param imageView The ImageView to configure.
   */
  private void configureImageView(ImageView imageView) {
    imageView.setFitHeight(DICE_IMAGE_SIZE);
    imageView.setFitWidth(DICE_IMAGE_SIZE);
    imageView.setPreserveRatio(true);
    imageView.getStyleClass().add("dice-image");
  }

  /**
   * Preloads dice images (1-6) into the cache.
   */
  private void preloadDiceImages() {
    for (int i = 1; i <= 6; i++) {
      try {
        Image img = loadDiceImageInternal(i);
        if (img != null) {
          diceImageCache.put(i, img);
        } else {
          LOGGER.log(Level.WARNING, "Failed to preload dice image for value: " + i);
        }
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error preloading dice image " + i, e);
      }
    }
    LOGGER.fine("Dice images preloaded.");
  }

  /**
   * Constructs the filename string based on the dice value (e.g., 1 -> "one").
   * @param value The dice value (1-6).
   * @return The string representation (e.g., "one", "two") or null if invalid.
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
   * Loads a single dice image resource based on its face value using the specific filename format.
   * Handles potential loading errors.
   *
   * @param value The face value of the die (1-6).
   * @return The loaded Image, or null if loading fails or value is invalid.
   */
  private Image loadDiceImageInternal(int value) {
    String valueName = getDiceValueName(value);
    if (valueName == null) {
      LOGGER.log(Level.WARNING, "Invalid dice value requested for image loading: " + value);
      return null;
    }

    String filename = "dice-" + valueName + ".png";
    String imagePath = DICE_IMAGE_FOLDER_PATH + filename;

    try {
      Image img = resourceLoader.loadImage(imagePath);
      if (img.isError()) {
        throw new BoardGameResourceException("Image has error flag set: " + imagePath);
      }
      return img;
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load dice image resource: " + imagePath, e);
      return null;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error loading dice image: " + imagePath, e);
      return null;
    }
  }

  /**
   * Retrieves a dice image from the cache using its integer value.
   *
   * @param value The face value (1-6).
   * @return The cached Image, or null if not found or value is invalid.
   */
  private Image getCachedDiceImage(int value) {
    if (value < 1 || value > 6) return null;
    return diceImageCache.get(value);
  }

  /**
   * Updates the panel to display the results of a specific dice roll.
   * Sets the appropriate images for each die and updates the total sum text.
   *
   * @param die1Value The value shown on the first die (1-6).
   * @param die2Value The value shown on the second die (1-6).
   * @param totalSum  The sum of the two dice values.
   */
  public void updateDiceDisplay(int die1Value, int die2Value, int totalSum) {
    Image die1Image = getCachedDiceImage(die1Value);
    Image die2Image = getCachedDiceImage(die2Value);

    if (die1Image == null) {
      LOGGER.log(Level.WARNING, "Missing cached image for die value: " + die1Value + ". Attempting load.");
      die1Image = loadDiceImageInternal(die1Value);
    }
    if (die2Image == null) {
      LOGGER.log(Level.WARNING, "Missing cached image for die value: " + die2Value + ". Attempting load.");
      die2Image = loadDiceImageInternal(die2Value);
    }

    die1ImageView.setImage(die1Image);
    die2ImageView.setImage(die2Image);

    titleLabel.setText("You rolled: " + totalSum);
    sumLabel.setText(""); // Sum included in title, clear separate label

    LOGGER.fine("Dice display updated: " + die1Value + " + " + die2Value + " = " + totalSum);
  }

  /**
   * Updates the panel based on the state of a {@link edu.ntnu.idi.idatt.model.core.Dice} object.
   * Assumes the Dice object uses exactly two dice (indices 0 and 1).
   *
   * @param dice The Dice object after it has been rolled.
   */
  public void updateDiceDisplay(edu.ntnu.idi.idatt.model.core.Dice dice) {
    if (dice == null) {
      LOGGER.warning("Attempted to update DicePanel with null Dice object.");
      resetDiceDisplay();
      return;
    }
    try {
      // Explicitly get values for die 0 and die 1
      int die1 = dice.getDieValue(0);
      int die2 = dice.getDieValue(1);
      int total = dice.getTotalDiceValue();
      updateDiceDisplay(die1, die2, total);
    } catch (IndexOutOfBoundsException e) {
      LOGGER.log(Level.SEVERE, "Dice object does not have values for index 0 and 1 as expected.", e);
      resetDiceDisplay();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error getting values from Dice object.", e);
      resetDiceDisplay();
    }
  }

  /**
   * Resets the dice display to a default or blank state, for example,
   * showing the '1' face for both dice.
   */
  public void resetDiceDisplay() {
    titleLabel.setText("Roll the dice!");
    die1ImageView.setImage(getCachedDiceImage(1));
    die2ImageView.setImage(getCachedDiceImage(1));
    sumLabel.setText("");
    LOGGER.fine("Dice display reset.");
  }

  /**
   * Disables or enables the visual appearance of the panel, typically making it look faded.
   * Does not affect underlying logic unless interactive elements are added.
   *
   * @param disable True to disable (fade), false to enable (normal opacity).
   */
  public void setDisabledVisual(boolean disable) {
    this.setOpacity(disable ? 0.5 : 1.0); // Example: Fade when disabled
    LOGGER.fine("DicePanel visual disabled state set to: " + disable);
  }
}