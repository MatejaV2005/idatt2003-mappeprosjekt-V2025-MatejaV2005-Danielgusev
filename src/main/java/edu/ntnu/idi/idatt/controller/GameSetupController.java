package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.model.playertype.Player;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import java.util.List;
import javafx.scene.control.ListView;

public class GameSetupController {
  private final GameSetupView view;
  private final NavigationController navigationController;
  private final PlayerManager playerManager;

  public GameSetupController(GameSetupView view, NavigationController navigationController, PlayerManager playerManager) {
    this.view = view;
    this.navigationController = navigationController;
    this.playerManager = playerManager;

    view.setController(this);
  }

  public void onGameStart() {

  }

  public void onBack() {
    navigationController.navigateToGameSelection();
  }

  public void onDifficultySelected(String difficulty) {

  }

  public void onAddPlayer() {

  }

  public void onSavePlayer() {

  }

  public void onCurrentPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    panel.getPlayerListView().getItems().clear();

    List<Player> currentPlayers = playerManager.getPlayers();
    for (Player player : currentPlayers) {
      panel.addPlayer(player.getName());
    }

    panel.getSavePlayerButton().setDisable(currentPlayers.isEmpty());

  }

  public void onSavedPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();

    List<Player> savedPlayers = playerManager.loadPlayersFromFile();

    // Update the saved players list view
    ListView<String> savedPlayersListView = panel.getSavedPlayerListView();
    savedPlayersListView.getItems().clear();

    for (Player player : savedPlayers) {
      savedPlayersListView.getItems().add(player.getName());
    }
  }
}
