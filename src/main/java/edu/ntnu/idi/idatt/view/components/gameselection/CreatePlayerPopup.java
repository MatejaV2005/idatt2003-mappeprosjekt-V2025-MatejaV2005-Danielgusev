package edu.ntnu.idi.idatt.view.components.gameselection;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ResourceLoader;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A popup window for creating a new player.
 *
 * <p>This dialog allows users to enter a player name and select an icon from a predefined list.
 * It includes input validation for the player's name and provides a visual preview
 * of the selected game piece icon.
 * </p>
 */
public class CreatePlayerPopup {

  private static final String DEFAULT_ICON_PATH_PREFIX =
      "/edu/ntnu/idi/idatt/view/resources/icons/";
  private static final String DEFAULT_ICON_EXTENSION = ".png";

  private static final int PREF_FIELD_WIDTH = 200;
  private static final int ICON_PREVIEW_SIZE = 64;
  private static final int MIN_NAME_LENGTH = 2;
  private static final int MAX_NAME_LENGTH = 15;
  private static final double POPUP_WIDTH = 300;
  private static final double POPUP_HEIGHT = 420;
  private static final double PADDING_INSETS = 20;
  private static final double SPACING_TEN = 10;
  private static final double SPACING_FIFTEEN = 15;


  private final Stage popupStage;
  private final TextField nameField;
  private final ComboBox<String> iconComboBox;
  private final ImageView iconPreview;
  private final Label errorLabel;
  private Player createdPlayer;

  /**
   * Constructs a new {@code CreatePlayerPopup} dialog.
   *
   * <p>Initializes all UI components, sets up the layout, and configures event handlers
   * for player creation and icon preview updates.
   * </p>
   */
  public CreatePlayerPopup() {
    popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Create New Player");
    popupStage.setResizable(false);

    nameField = createNameField();
    iconComboBox = createIconComboBox();
    iconPreview = createIconPreview();
    errorLabel = createErrorLabel();

    Button createButton = createStyledButton("Create", true);
    createButton.setOnAction(e -> handleCreatePlayer());

    Button cancelButton = createStyledButton("Cancel", false);
    cancelButton.setCancelButton(true);
    cancelButton.setOnAction(e -> popupStage.close());

    HBox buttonBox = new HBox(SPACING_FIFTEEN, createButton, cancelButton);
    buttonBox.setAlignment(Pos.CENTER);

    HBox previewBox = new HBox(SPACING_TEN, iconPreview);
    previewBox.setAlignment(Pos.CENTER);
    previewBox.setPadding(new Insets(SPACING_TEN));

    VBox layout = new VBox(SPACING_TEN);
    layout.setPadding(new Insets(PADDING_INSETS));
    layout.setAlignment(Pos.CENTER_LEFT);
    layout.getChildren().addAll(
        new Label("Player Name:"), nameField,
        new Label("Select Icon:"), iconComboBox,
        previewBox,
        errorLabel,
        buttonBox
    );

    iconComboBox.setOnAction(e -> updateIconPreview());

    Scene scene = new Scene(layout, POPUP_WIDTH, POPUP_HEIGHT);
    popupStage.setScene(scene);
  }


  private TextField createNameField() {
    TextField field = new TextField();
    field.setPromptText("Enter player name");
    field.setPrefWidth(PREF_FIELD_WIDTH);
    return field;
  }


  private ComboBox<String> createIconComboBox() {
    ComboBox<String> comboBox = new ComboBox<>();
    comboBox.getItems().addAll("Hat", "Car", "Dog", "Dragon", "Battleship", "Default");
    comboBox.setPromptText("Choose an icon");
    comboBox.setPrefWidth(PREF_FIELD_WIDTH);
    return comboBox;
  }


  private ImageView createIconPreview() {
    ImageView preview = new ImageView();
    preview.setFitHeight(ICON_PREVIEW_SIZE);
    preview.setFitWidth(ICON_PREVIEW_SIZE);
    preview.setPreserveRatio(true);
    return preview;
  }


  private Label createErrorLabel() {
    Label label = new Label();
    label.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");
    label.setWrapText(true);
    label.setMaxWidth(PREF_FIELD_WIDTH + SPACING_TEN * 2);
    label.setMinHeight(30);
    return label;
  }


  private Button createStyledButton(String text, boolean isDefaultButton) {
    Button button = new Button(text);
    if (isDefaultButton) {
      button.setDefaultButton(true);
    }
    return button;
  }


  private void updateIconPreview() {
    String selectedIconName = iconComboBox.getValue();
    if (selectedIconName != null && !selectedIconName.isEmpty()) {
      String iconPath = DEFAULT_ICON_PATH_PREFIX
          + selectedIconName.toLowerCase()
          + DEFAULT_ICON_EXTENSION;
      try {
        Image image = ResourceLoader.loadImage(iconPath);
        iconPreview.setImage(image);
      } catch (IllegalArgumentException | BoardGameResourceException e) {
        iconPreview.setImage(null);
      }
    } else {
      iconPreview.setImage(null);
    }
  }

  private void handleCreatePlayer() {
    String name = nameField.getText().trim();
    String iconName = iconComboBox.getValue();

    if (!isNameValid(name)) {
      return;
    }
    if (iconName == null || iconName.isEmpty()) {
      errorLabel.setText("Please select an icon.");
      return;
    }

    createdPlayer = new Player(name, iconName);
    popupStage.close();
  }


  private boolean isNameValid(String name) {
    if (name.isEmpty()) {
      errorLabel.setText("Player name cannot be empty.");
      return false;
    }
    if (name.length() < MIN_NAME_LENGTH) {
      errorLabel.setText("Name must be at least " + MIN_NAME_LENGTH + " characters long.");
      return false;
    }
    if (name.length() > MAX_NAME_LENGTH) {
      errorLabel.setText("Name must be at most " + MAX_NAME_LENGTH + " characters long.");
      return false;
    }
    errorLabel.setText("");
    return true;
  }

  /**
   * Shows the player creation popup dialog and waits for user input.
   *
   * <p>The dialog is modal, meaning it blocks interaction with other parts of the application
   * until it is closed. Input fields are cleared before showing.
   * </p>
   *
   * @return An {@link Optional} containing the created {@link Player} if the user
   *         successfully created a player; otherwise, an empty {@link Optional}
   *         if the dialog was cancelled or closed without creating a player.
   */
  public Optional<Player> show() {
    nameField.clear();
    iconComboBox.setValue(null);
    iconPreview.setImage(null);
    errorLabel.setText("");
    createdPlayer = null;

    popupStage.showAndWait();
    return Optional.ofNullable(createdPlayer);
  }
}
