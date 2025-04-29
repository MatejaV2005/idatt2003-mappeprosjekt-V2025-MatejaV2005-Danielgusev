package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.management.BoardManager;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.components.gameSelection.CreatePlayerPopup;
import edu.ntnu.idi.idatt.view.components.gameSelection.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for the game setup screen. Manages player selection,
 * difficulty settings, and game initialization.
 */
public class GameSetupController {
  private static final Logger LOGGER = Logger.getLogger(GameSetupController.class.getName());
  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 4;
  private static final String BOARDS_DIRECTORY = "Files/Boards";

  private final GameSetupView view;
  private final NavigationController navigationController;
  private final PlayerManager playerManager;
  private final BoardManager boardManager;
  private String selectedDifficulty = "Normal"; // Default difficulty
  private final Stage stage;
  private Board customBoard = null;

  /**
   * Constructs a GameSetupController.
   *
   * @param view The game setup view
   * @param navigationController The navigation controller for screen transitions
   * @param playerManager The player manager for player data handling
   * @param stage The primary stage for displaying dialogs
   */
  public GameSetupController(GameSetupView view, NavigationController navigationController, PlayerManager playerManager, Stage stage) {
    this.view = view;
    this.navigationController = navigationController;
    this.playerManager = playerManager;
    this.boardManager = BoardManager.getInstance(); // Get singleton instance
    this.stage = stage;

    view.setController(this);

    onDifficultySelected("Normal");

    refreshCurrentPlayersList();

    createBoardsDirectory();
  }

  /**
   * Alternative constructor without stage parameter.
   *
   * @param view The game setup view
   * @param navigationController The navigation controller for screen transitions
   * @param playerManager The player manager for player data handling
   */
  public GameSetupController(GameSetupView view, NavigationController navigationController,
      PlayerManager playerManager) {
    this(view, navigationController, playerManager, null);
  }

  /**
   * Creates the boards directory if it doesn't exist.
   */
  private void createBoardsDirectory() {
    File directory = new File(BOARDS_DIRECTORY);
    if (!directory.exists()) {
      boolean created = directory.mkdirs();
      if (created) {
        LOGGER.info("Created boards directory: " + BOARDS_DIRECTORY);
      } else {
        LOGGER.warning("Failed to create boards directory: " + BOARDS_DIRECTORY);
      }
    }
  }

  /**
   * Handles game start button click. Validates player count before starting.
   */
  public void onGameStart() {
    List<Player> players = playerManager.getPlayers();

    if (players.size() < MIN_PLAYERS) {
      showErrorAlert("Not Enough Players",
          "Please add at least " + MIN_PLAYERS + " players to start the game.");
      view.updateStatusMessage("Need at least " + MIN_PLAYERS + " players to start", true);
      return;
    }

    view.clearStatusMessage();

    try {
      navigationController.startNewGame(selectedDifficulty.toLowerCase());

      LOGGER.log(Level.INFO, "Starting new game with difficulty: {0}", selectedDifficulty);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error starting game", e);
      showErrorAlert("Game Start Error", "Could not start game: " + e.getMessage());
    }
  }

  /**
   * Handles back button click.
   */
  public void onBack() {
    navigationController.navigateToGameSelection();
  }

  /**
   * Updates game info based on selected difficulty.
   *
   * @param difficulty The selected difficulty
   */
  public void onDifficultySelected(String difficulty) {
    this.selectedDifficulty = difficulty;
    GameInfoPanel infoPanel = view.getGameInfoPanel();

    switch (difficulty.toLowerCase()) {
      case "easy" -> infoPanel.setEasyModeInfo();
      case "normal" -> infoPanel.setNormalModeInfo();
      case "hard" -> infoPanel.setHardModeInfo();
      case "upload" -> infoPanel.setUploadModeInfo();
      default -> infoPanel.setNormalModeInfo();
    }

    if (!"custom".equalsIgnoreCase(difficulty)) {
      customBoard = null;
    }

    LOGGER.log(Level.INFO, "Difficulty selected: {0}", difficulty);
  }

  /**
   * Opens dialog to add a new player.
   */
  public void onAddPlayer() {
    if (playerManager.getPlayers().size() >= MAX_PLAYERS) {
      showErrorAlert("Player Limit Reached",
          "Maximum of " + MAX_PLAYERS + " players allowed.");
      view.updateStatusMessage("Maximum " + MAX_PLAYERS + " players reached", true);
      return;
    }

    CreatePlayerPopup popup = new CreatePlayerPopup();
    Optional<Player> result = popup.show();

    result.ifPresent(player -> {
      try {
        playerManager.addPlayer(player);
        view.updateStatusMessage("Player " + player.getName() + " added", false);
        refreshCurrentPlayersList();

        // Update button states based on player count
        updateButtonStates();
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Failed to add player: {0}", e.getMessage());
        showErrorAlert("Add Player Failed", e.getMessage());
      }
    });
  }

  /**
   * Handles removing the selected player.
   */
  public void onRemovePlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    Player selectedPlayer = panel.getPlayerListView().getSelectionModel().getSelectedItem();

    if (selectedPlayer != null) {
      playerManager.removePlayer(selectedPlayer);
      refreshCurrentPlayersList();
      view.updateStatusMessage("Player " + selectedPlayer.getName() + " removed", false);

      updateButtonStates();
    }
  }

  /**
   * Saves a player to the permanent storage.
   */
  public void onSavePlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    Player selectedPlayer = panel.getPlayerListView().getSelectionModel().getSelectedItem();

    if (selectedPlayer == null) {
      showErrorAlert("No Player Selected", "Please select a player to save.");
      return;
    }

    try {
      // Check if player is already saved
      List<Player> savedPlayers = playerManager.loadPlayersFromFile();
      boolean alreadySaved = savedPlayers.stream()
          .anyMatch(p -> p.getName().equals(selectedPlayer.getName()));

      if (alreadySaved) {
        view.updateStatusMessage("Player " + selectedPlayer.getName() + " already saved", true);
        return;
      }

      savedPlayers.add(selectedPlayer);
      playerManager.savePlayers(savedPlayers);

      view.updateStatusMessage("Player " + selectedPlayer.getName() + " saved", false);
      onSavedPlayersTabSelected();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to save player", e);
      showErrorAlert("Save Failed", "Could not save player: " + e.getMessage());
    }
  }

  /**
   * Handles upload board button click to load a custom board file.
   */
  public void onUploadBoard() {
    if (stage == null) {
      LOGGER.warning("Stage is null, cannot show file chooser");
      return;
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Board File");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("JSON Files", "*.json"));

    File boardsDir = new File(BOARDS_DIRECTORY);
    if (boardsDir.exists() && boardsDir.isDirectory()) {
      fileChooser.setInitialDirectory(boardsDir);
    }

    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      try {
        customBoard = boardManager.loadBoardFromFile(selectedFile.getAbsolutePath());

        this.selectedDifficulty = "Custom";
        view.getGameInfoPanel().setUploadModeInfo();
        view.updateStatusMessage("Custom board loaded: " + selectedFile.getName(), false);

        LOGGER.log(Level.INFO, "Loaded custom board from: {0}", selectedFile.getAbsolutePath());
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to load board file", e);
        showErrorAlert("Board Load Error", "Failed to load board file: " + e.getMessage());
        view.updateStatusMessage("Failed to load board", true);
      }
    }
  }

  /**
   * Refreshes the current players tab with updated data from the player manager.
   */
  private void refreshCurrentPlayersList() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    panel.getPlayerListView().getItems().clear();

    List<Player> currentPlayers = playerManager.getPlayers();
    for (Player player : currentPlayers) {
      panel.getPlayerListView().getItems().add(player);
    }

    if (currentPlayers.isEmpty()) {
      LOGGER.info("Current players list is empty");
    }

    updateButtonStates();
  }

  /**
   * Handles when the Current Players tab is selected.
   */
  public void onCurrentPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    refreshCurrentPlayersList();
    panel.showCurrentPlayers();

    updateButtonStates();
  }

  /**
   * Handles when the Saved Players tab is selected.
   */
  public void onSavedPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();

    try {
      List<Player> savedPlayers = playerManager.loadPlayersFromFile();

      // Update the saved players list view
      ListView<Player> savedPlayersListView = panel.getSavedPlayerListView();
      savedPlayersListView.getItems().clear();

      for (Player player : savedPlayers) {
        savedPlayersListView.getItems().add(player);
      }

      panel.showSavedPlayers();

      if (savedPlayers.isEmpty()) {
        view.updateStatusMessage("No saved players found", false);
      } else {
        view.updateStatusMessage(savedPlayers.size() + " players loaded", false);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading saved players", e);
      showErrorAlert("Load Error", "Could not load saved players: " + e.getMessage());
      view.updateStatusMessage("Failed to load saved players", true);
    }

    updateButtonStates();
  }

  /**
   * Updates button states based on player count and selection.
   */
  private void updateButtonStates() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();

    boolean canAddPlayer = playerManager.getPlayers().size() < MAX_PLAYERS;
    panel.getAddPlayerButton().setDisable(!canAddPlayer);

    boolean isCurrentPlayersTab = panel.getPlayerTabs().getSelectionModel().getSelectedItem() == panel.getCurrentPlayersTab();
    boolean hasCurrentSelection = isCurrentPlayersTab &&
        panel.getPlayerListView().getSelectionModel().getSelectedItem() != null;

    panel.getRemovePlayerButton().setDisable(!hasCurrentSelection);
    panel.getSavePlayerButton().setDisable(!hasCurrentSelection);

    if (!canAddPlayer) {
      view.updateStatusMessage("Maximum " + MAX_PLAYERS + " players reached", true);
    } else if (playerManager.getPlayers().isEmpty() && isCurrentPlayersTab) {
      view.updateStatusMessage("Add players to begin", false);
    } else if (isCurrentPlayersTab) {
      view.updateStatusMessage(playerManager.getPlayers().size() + " player(s) added", false);
    }
  }

  /**
   * Shows an error alert dialog.
   *
   * @param title The dialog title
   * @param message The error message
   */
  private void showErrorAlert(String title, String message) {
    if (stage == null) {
      LOGGER.warning("Stage is null, cannot show alert: " + title + " - " + message);
      return;
    }

    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}