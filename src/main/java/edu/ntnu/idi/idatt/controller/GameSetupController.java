package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.exceptions.BoardManagementException;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.exceptions.PlayerManagementException;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.management.BoardManager;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.components.gameSelection.CreatePlayerPopup;
import edu.ntnu.idi.idatt.view.components.gameSelection.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import edu.ntnu.idi.idatt.view.utils.AlertHelper;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  private GameType selectedGame = GameType.SNAKES_AND_LADDERS; // Default difficulty
  private final Stage stage;
  private Board customBoard = null;
  private String selectedDifficulty = "normal";

  /**
   * Constructs a GameSetupController.
   *
   * @param view The game setup view
   * @param navigationController The navigation controller for screen transitions
   * @param playerManager The player manager for player data handling
   * @param stage The primary stage for displaying dialogs
   */
  public GameSetupController(GameSetupView view, NavigationController navigationController,
      PlayerManager playerManager, Stage stage) {
    this.view = view;
    this.navigationController = navigationController;
    this.playerManager = playerManager;
    this.boardManager = BoardManager.getInstance(); // Get singleton instance
    this.stage = stage;

    view.setController(this);

    onDifficultySelected("Normal");

    createBoardsDirectory();

    initializeView();
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
   * Initializes the view with player data and sets up event handlers.
   */
  private void initializeView() {
    refreshCurrentPlayersList();

    setupPlayerManagementHandlers();
  }

  /**
   * Sets up handlers for player management operations.
   */
  private void setupPlayerManagementHandlers() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();

    panel.getAddPlayerButton().setOnAction(e -> onAddPlayer());

    panel.getRemovePlayerButton().setOnAction(e -> onRemovePlayer());

    panel.getSavePlayerButton().setOnAction(e -> onSavePlayer());

    panel.getAddSavedPlayerButton().setOnAction(e -> onAddSavedPlayer());

    panel.getPlayerTabs().getSelectionModel().selectedItemProperty().addListener(
        (obs, oldTab, newTab) -> {
          if (newTab == panel.getCurrentPlayersTab()) {
            onCurrentPlayersTabSelected();
          } else if (newTab == panel.getSavedPlayersTab()) {
            onSavedPlayersTabSelected();
          }
        });
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
      AlertHelper.showErrorAlert("Not Enough Players",
          "Please add at least " + MIN_PLAYERS + " players to start the game.");
      view.updateStatusMessage("Need at least " + MIN_PLAYERS + " players to start", true);
      return;
    }

    view.clearStatusMessage();

    try {
      if ("custom".equalsIgnoreCase(this.selectedDifficulty) && this.customBoard != null) {
        LOGGER.log(Level.INFO, "Starting game with custom uploaded board.");
        navigationController.startCustomGame(this.customBoard);
      } else {
        LOGGER.log(Level.INFO, "Starting new game with type: {0} and standard difficulty: {1}",
            new Object[]{selectedGame, this.selectedDifficulty});
        navigationController.startNewGame(selectedGame, this.selectedDifficulty);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error starting game", e);
      AlertHelper.showErrorAlert("Game Start Error", "Could not start game: " + e.getMessage());
    }
  }

  /**
   * Handles back button click.
   */
  public void onBack() {
    navigationController.navigateToGameSelection();
  }

  public void refreshPlayerListViews() {
    LOGGER.info("GameSetupController: Forcing refresh of player list views.");
    onCurrentPlayersTabSelected();

    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    if (panel.getPlayerTabs().getSelectionModel().getSelectedItem() == panel.getSavedPlayersTab()) {
      onSavedPlayersTabSelected();
    }
    view.clearStatusMessage();
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
    PlayerManagementPanel panel = view.getPlayerManagementPanel();

    if (panel.isPlayerLimitReached()) {
      AlertHelper.showErrorAlert("Player Limit Reached",
          "Maximum of " + MAX_PLAYERS + " players allowed.");
      view.updateStatusMessage("Maximum " + MAX_PLAYERS + " players reached", true);
      return;
    }

    CreatePlayerPopup popup = new CreatePlayerPopup();
    Optional<Player> result = popup.show();

    result.ifPresent(player -> {
      try {
        boolean playerExists = playerManager.getPlayers().stream()
            .anyMatch(p -> p.getName().equals(player.getName()));

        if (playerExists) {
          view.updateStatusMessage("Player " + player.getName() + " already exists", true);
          return;
        }

        playerManager.addPlayer(player);
        view.updateStatusMessage("Player " + player.getName() + " added", false);
        refreshCurrentPlayersList();
      } catch (PlayerManagementException e) {
        LOGGER.log(Level.WARNING, "Failed to add player: {0}", e.getMessage());
        AlertHelper.showErrorAlert("Add Player Failed", e.getMessage());
      }
    });
  }

  /**
   * Handles removing the selected player.
   */
  public void onRemovePlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    Player selectedPlayer = panel.getSelectedCurrentPlayer();

    if (selectedPlayer != null && panel.confirmPlayerRemoval(selectedPlayer)) {
      playerManager.removePlayer(selectedPlayer);
      refreshCurrentPlayersList();
      view.updateStatusMessage("Player " + selectedPlayer.getName() + " removed", false);
    }
  }

  /**
   * Saves a player to the permanent storage.
   */
  public void onSavePlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    Player selectedPlayer = panel.getSelectedCurrentPlayer();

    if (selectedPlayer == null) {
      AlertHelper.showErrorAlert("No Player Selected", "Please select a player to save.");
      return;
    }

    try {
      boolean saved = playerManager.savePlayer(selectedPlayer);

      if (saved) {
        view.updateStatusMessage("Player " + selectedPlayer.getName() + " saved", false);
        if (panel.getPlayerTabs().getSelectionModel().getSelectedItem() == panel.getSavedPlayersTab()) {
          loadSavedPlayers();
        }
      } else {
        view.updateStatusMessage("Player " + selectedPlayer.getName() + " already saved", true);
      }
    } catch (PlayerManagementException e) {
      LOGGER.log(Level.SEVERE, "Failed to save player", e);
      AlertHelper.showErrorAlert("Save Failed", "Could not save player: " + e.getMessage());
    }
  }

  /**
   * Handles adding a selected saved player to current players.
   */
  public void onAddSavedPlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    Player selectedPlayer = panel.getSelectedSavedPlayer();

    if (selectedPlayer == null) {
      AlertHelper.showErrorAlert("No Player Selected", "Please select a saved player to add.");
      return;
    }

    try {
      boolean playerAlreadyAdded = playerManager.getPlayers().stream()
          .anyMatch(p -> p.getName().equals(selectedPlayer.getName()));

      if (playerAlreadyAdded) {
        view.updateStatusMessage("Player " + selectedPlayer.getName() + " is already in the game", true);
        return;
      }

      if (panel.isPlayerLimitReached()) {
        AlertHelper.showErrorAlert("Player Limit Reached",
            "Maximum of " + MAX_PLAYERS + " players allowed.");
        view.updateStatusMessage("Maximum " + MAX_PLAYERS + " players reached", true);
        return;
      }

      playerManager.addPlayer(selectedPlayer);
      view.updateStatusMessage("Added " + selectedPlayer.getName() + " to the game", false);

      // Switch to current players tab and refresh
      panel.showCurrentPlayers();
      refreshCurrentPlayersList();

    } catch (PlayerManagementException e) {
      LOGGER.log(Level.SEVERE, "Failed to add saved player", e);
      AlertHelper.showErrorAlert("Add Player Failed", "Could not add player: " + e.getMessage());
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
        this.customBoard = boardManager.loadBoardFromFile(selectedFile.getAbsolutePath());
        this.selectedDifficulty = "custom"; // Indikerer at custom board er valgt
        view.getGameInfoPanel().setUploadModeInfo();
        view.updateStatusMessage("Custom board loaded: " + selectedFile.getName(), false);
        LOGGER.log(Level.INFO, "Loaded custom board from: {0}", selectedFile.getAbsolutePath());

      } catch (InvalidBoardFormatException e) { // Fang det spesifikke unntaket
        LOGGER.log(Level.WARNING, "Invalid board format: " + e.getMessage(), e);
        AlertHelper.showErrorAlert("Invalid Board File", e.getMessage()); // Vis den detaljerte feilmeldingen
        view.updateStatusMessage("Invalid board file: " + e.getMessage(), true);
        this.customBoard = null;
        this.selectedDifficulty = "normal"; // Tilbakestill
        view.getGameInfoPanel().setNormalModeInfo();

      } catch (BoardManagementException e) { // For andre lastingsfeil (f.eks. fil ikke funnet via BoardManager)
        LOGGER.log(Level.SEVERE, "Failed to load board file: " + e.getMessage(), e);
        AlertHelper.showErrorAlert("Board Load Error", "Failed to load board: " + e.getMessage());
        view.updateStatusMessage("Failed to load board: " + e.getMessage(), true);
        this.customBoard = null;
        this.selectedDifficulty = "normal";
        view.getGameInfoPanel().setNormalModeInfo();
      }
    }
  }

  /**
   * Refreshes the current players list with updated data from the player manager.
   */
  public void refreshCurrentPlayersList() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();

    List<Player> currentPlayers = playerManager.getPlayers();
    panel.updateCurrentPlayersList(currentPlayers);

    if (currentPlayers.isEmpty()) {
      LOGGER.info("Current players list is empty");
    }
  }

  /**
   * Loads saved players from file and updates the UI.
   */
  private void loadSavedPlayers() {
    try {
      List<Player> savedPlayers = playerManager.loadPlayersFromFile();
      PlayerManagementPanel panel = view.getPlayerManagementPanel();
      panel.updateSavedPlayersList(savedPlayers);

      // Update status based on loaded players
      if (savedPlayers.isEmpty()) {
        view.updateStatusMessage("No saved players found", false);
      } else {
        view.updateStatusMessage(savedPlayers.size() + " players available", false);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading saved players", e);
      AlertHelper.showErrorAlert("Load Error", "Could not load saved players: " + e.getMessage());
      view.updateStatusMessage("Failed to load saved players", true);
    }
  }

  /**
   * Handles when the Current Players tab is selected.
   */
  public void onCurrentPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    refreshCurrentPlayersList();
    panel.showCurrentPlayers();
  }

  /**
   * Handles when the Saved Players tab is selected.
   */
  public void onSavedPlayersTabSelected() {
    loadSavedPlayers();
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    panel.showSavedPlayers();
  }
}