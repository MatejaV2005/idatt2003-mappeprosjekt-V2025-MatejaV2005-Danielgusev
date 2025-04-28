package edu.ntnu.idi.idatt.view.components.gameSelection;

import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.model.playertype.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Panel for managing players: adding, saving, and switching between saved/current players.
 */
public class PlayerManagementPanel extends BorderPane {

  private final TabPane playerTabs;
  private final ListView<String> playerListView; // TODO: Replace String with actual Player later
  private final ListView<String> savedPlayerListView;
  private final Button addPlayerButton;
  private final Button savePlayerButton;

  /**
   * Constructs the PlayerManagementPanel using an injected ButtonFactory.
   *
   * @param buttonFactory Factory for creating styled buttons
   */
  public PlayerManagementPanel(ButtonFactory buttonFactory) {
    this.getStyleClass().add("player-panel");

    this.playerTabs = new TabPane();
    initializeTabs();

    this.playerListView = new ListView<>();
    playerListView.getItems().addAll("Player 1", "Player 2", "Player 3"); // Dummy data for now

    this.savedPlayerListView = new ListView<>();
    savedPlayerListView.getItems().addAll("Player 4", "Player 5", "Player 6"); // Dummy data for now

    this.addPlayerButton = buttonFactory.createSmallButton("+ Create New Player");
    this.savePlayerButton = buttonFactory.createSmallButton("Save Player");

    HBox controlsBox = new HBox(10, savePlayerButton, addPlayerButton);
    controlsBox.setAlignment(Pos.CENTER);

    setTop(playerTabs);
    setCenter(playerListView);
    setBottom(controlsBox);

    this.getStyleClass().add("player-management-panel");
  }

  private void initializeTabs() {
    Tab currentPlayers = new Tab("Current Players");
    currentPlayers.setClosable(false);

    Tab savedPlayers = new Tab("Saved Players");
    savedPlayers.setClosable(false);

    playerTabs.getTabs().addAll(currentPlayers, savedPlayers);
  }

  // Getter methods for buttons and list view
  public Button getAddPlayerButton() {
    return addPlayerButton;
  }

  public Button getSavePlayerButton() {
    return savePlayerButton;
  }

  public ListView<String> getPlayerListView() {
    return playerListView;
  }

  // Methods to manipulate the player list
  public void addPlayer(String playerName) {
    playerListView.getItems().add(playerName);
  }

  public void removeSelectedPlayer() {
    int selectedIndex = playerListView.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {
      playerListView.getItems().remove(selectedIndex);
    }
  }
}
