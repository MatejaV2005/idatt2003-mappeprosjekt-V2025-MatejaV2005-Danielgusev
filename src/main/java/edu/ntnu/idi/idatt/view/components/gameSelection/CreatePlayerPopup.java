package edu.ntnu.idi.idatt.view.components.gameSelection;

import edu.ntnu.idi.idatt.model.core.playertype.HumanPlayer;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

import java.util.Optional;

/**
 * A popup window for creating a new player by entering a name and selecting an icon.
 * Includes validation and visual preview of the selected piece.
 */
public class CreatePlayerPopup {

  private final Stage popupStage;
  private final TextField nameField;
  private final ComboBox<String> iconComboBox;
  private final ImageView iconPreview;
  private Player createdPlayer;
  private final Label errorLabel;

  /**
   * Constructs a new player creation popup dialog.
   */
  public CreatePlayerPopup() {
    popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Create New Player");
    popupStage.setResizable(false);

    // Create UI components
    Label nameLabel = new Label("Player Name:");
    nameField = new TextField();
    nameField.setPromptText("Enter player name");
    nameField.setPrefWidth(200);

    Label iconLabel = new Label("Select Icon:");
    iconComboBox = new ComboBox<>();
    iconComboBox.getItems().addAll("Hat", "Car", "Dog", "Dragon", "Ship");
    iconComboBox.setPromptText("Choose an icon");
    iconComboBox.setPrefWidth(200);

    // Icon preview
    iconPreview = new ImageView();
    iconPreview.setFitHeight(64);
    iconPreview.setFitWidth(64);
    iconPreview.setPreserveRatio(true);

    // Error label for validation messages
    errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");
    errorLabel.setWrapText(true);
    errorLabel.setMaxWidth(250);

    // Preview section with the currently selected icon
    HBox previewBox = new HBox(10, iconPreview);
    previewBox.setAlignment(Pos.CENTER);
    previewBox.setPadding(new Insets(10));

    // Button controls
    Button createButton = new Button("Create");
    createButton.setDefaultButton(true);
    createButton.setOnAction(e -> onCreate());

    Button cancelButton = new Button("Cancel");
    cancelButton.setCancelButton(true);
    cancelButton.setOnAction(e -> popupStage.close());

    HBox buttonBox = new HBox(15, createButton, cancelButton);
    buttonBox.setAlignment(Pos.CENTER);

    // Layout for the entire popup
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.getChildren().addAll(
        nameLabel, nameField,
        iconLabel, iconComboBox,
        previewBox,
        errorLabel,
        buttonBox
    );

    // Set up icon preview update when selection changes
    iconComboBox.setOnAction(e -> updateIconPreview());

    Scene scene = new Scene(layout, 300, 400);
    popupStage.setScene(scene);
  }

  /**
   * Updates the icon preview when a selection is made.
   */
  private void updateIconPreview() {
    String selectedIcon = iconComboBox.getValue();
    if (selectedIcon != null) {
      String iconPath = "/edu/ntnu/idi/idatt/view/resources/icons/" + selectedIcon.toLowerCase() + ".png";
      try {
        Image image = new Image(getClass().getResourceAsStream(iconPath));
        iconPreview.setImage(image);
      } catch (Exception e) {
        // If icon loading fails, clear the preview
        iconPreview.setImage(null);
      }
    } else {
      iconPreview.setImage(null);
    }
  }

  /**
   * Validates inputs and creates a player if validation passes.
   */
  private void onCreate() {
    String name = nameField.getText().trim();
    String icon = iconComboBox.getValue();

    // Validation
    if (name.isEmpty() || icon == null) {
      errorLabel.setText("Please enter a name and select an icon.");
      return;
    }

    if (name.length() < 2) {
      errorLabel.setText("Name must be at least 2 characters long.");
      return;
    }

    if (name.length() > 15) {
      errorLabel.setText("Name must be at most 15 characters long.");
      return;
    }

    // Create player and close dialog
    createdPlayer = new HumanPlayer(name, icon);
    popupStage.close();
  }

  /**
   * Shows the popup and waits for the user to create or cancel.
   *
   * @return Optional containing the created Player if creation was successful, otherwise empty.
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

  /**
   * Shows an error alert with the specified title and message.
   *
   * @param title The alert title
   * @param message The alert message
   */
  private void showErrorAlert(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}