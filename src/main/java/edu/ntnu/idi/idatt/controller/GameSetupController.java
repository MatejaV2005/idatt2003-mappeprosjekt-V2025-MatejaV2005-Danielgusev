package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.components.gameSelection.CreatePlayerPopup;
import edu.ntnu.idi.idatt.view.components.gameSelection.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.scene.control.ListView;

public class GameSetupController {
  private static final Logger LOGGER = Logger.getLogger(GameSetupController.class.getName());


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
    GameInfoPanel infoPanel = view.getGameInfoPanel();

    switch (difficulty.toLowerCase()) {
      case "easy" -> infoPanel.setEasyModeInfo();
      case "normal" -> infoPanel.setNormalModeInfo();
      case "hard" -> infoPanel.setHardModeInfo();
      case "upload" -> infoPanel.setUploadModeInfo();
    }
  }

  public void onAddPlayer() {
    CreatePlayerPopup popup = new CreatePlayerPopup();
    Optional<Player> result = popup.show();

    result.ifPresent(player -> {
        playerManager.addPlayer(player);
        onCurrentPlayersTabSelected();
    });
  }

  public void onSavePlayer() {

  }


  public void onCurrentPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    panel.getPlayerListView().getItems().clear();


    List<Player> currentPlayers = playerManager.getPlayers();
    for (Player player : currentPlayers) {
      panel.addPlayer(player);
    }

    if (currentPlayers.isEmpty()) {
      LOGGER.info("Current players list is empty");
    }

    panel.showCurrentPlayers();

  }

  public void onSavedPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();

    List<Player> savedPlayers = playerManager.loadPlayersFromFile();

    // Update the saved players list view
    ListView<Player> savedPlayersListView = panel.getSavedPlayerListView();
    savedPlayersListView.getItems().clear();

    for (Player player : savedPlayers) {
      savedPlayersListView.getItems().add(player);
    }

    panel.showSavedPlayers();

  }
}
