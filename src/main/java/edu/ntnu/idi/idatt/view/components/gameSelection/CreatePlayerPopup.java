package edu.ntnu.idi.idatt.view.components.gameSelection;

import edu.ntnu.idi.idatt.model.core.playertype.HumanPlayer;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * A popup window for creating a new player by entering a name and selecting an icon.
 */
public class CreatePlayerPopup {

  private final Stage popupStage;
  private final TextField nameField;
  private final ComboBox<String> iconComboBox;
  private Player createdPlayer;

  public CreatePlayerPopup() {
    popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Create New Player");

    // Create UI components
    Label nameLabel = new Label("Player Name:");
    nameField = new TextField();
    nameField.setPromptText("Enter player name");

    Label iconLabel = new Label("Select Icon:");
    iconComboBox = new ComboBox<>();
    iconComboBox.getItems().addAll("Hat", "Car", "Dog", "dragon", "Battleship");
    iconComboBox.setPromptText("Choose an icon");

    Button createButton = new Button("Create");
    createButton.setOnAction(e -> onCreate());

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> popupStage.close());

    VBox layout = new VBox(10);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.getChildren().addAll(nameLabel, nameField, iconLabel, iconComboBox, createButton, cancelButton);

    Scene scene = new Scene(layout, 300, 300);
    popupStage.setScene(scene);
  }

  private void onCreate() {
    String name = nameField.getText().trim();
    String icon = iconComboBox.getValue();

    if (name.isEmpty() && icon == null) {
      // Simple validation: Require both name and icon
      return;
    }

    createdPlayer = new HumanPlayer(name, icon); // Assuming Player constructor (String name, String pieceType)
    popupStage.close();
  }

  /**
   * Shows the popup and waits for the user to create or cancel.
   *
   * @return Optional containing the created Player if creation was successful, otherwise empty.
   */
  public Optional<Player> show() {
    popupStage.showAndWait();
    return Optional.ofNullable(createdPlayer);
  }

}
