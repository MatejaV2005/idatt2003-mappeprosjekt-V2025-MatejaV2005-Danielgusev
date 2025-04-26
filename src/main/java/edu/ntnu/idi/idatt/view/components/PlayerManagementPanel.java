package edu.ntnu.idi.idatt.view.components;

import edu.ntnu.idi.idatt.model.player_type.Player;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class PlayerManagementPanel extends BorderPane {
    private final TabPane playerTabs;
    private final ListView<String> playerListView; // CURRENTLY ONLY STRING, FIX TO ACTUAL PLAYER LATER;
    private final Button addPlayerButton;
    private final Button savePlayerButton;

    public PlayerManagementPanel() {
      this.playerTabs = new TabPane();
      initializeTabs();

      this.playerListView = new ListView<>();
      playerListView.getItems().addAll("Player 1", "Player 2", "Player 3");

      this.addPlayerButton = new Button("+ Create New Player");
      this.savePlayerButton = new Button("Save Player");



      setTop(playerTabs);
      setCenter(playerListView);
      setBottom();
    }


    private void initializeTabs() {
      Tab savedPlayers = new Tab("Saved players");
      savedPlayers.setClosable(false);

      Tab currentPlayers = new Tab("Current players");
      currentPlayers.setClosable(false);

      playerTabs.getTabs().addAll(currentPlayers, savedPlayers);
    }
}
