package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.exceptions.BoardManagementException;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.exceptions.PlayerManagementException;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.management.BoardManager;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.view.components.gameSelection.CreatePlayerPopup;
import edu.ntnu.idi.idatt.view.components.gameSelection.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.screens.GameSetupView;
import edu.ntnu.idi.idatt.view.utils.AlertHelper;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for the game setup screen. Manages player selection, difficulty/game configuration,
 * and game initialization based on the selected GameType.
 */
public class GameSetupController {
  private static final Logger LOGGER = Logger.getLogger(GameSetupController.class.getName());
  private static final int MIN_PLAYERS_SNAKES_LADDERS = 2;
  private static final int MIN_PLAYERS_ASTRO_RALLY = 2;
  private static final int MAX_PLAYERS = 4;
  private static final String BOARDS_DIRECTORY = "Files/Boards";

  private final GameSetupView view;
  private final NavigationController navigationController;
  private final PlayerManager playerManager;
  private final BoardManager boardManager;
  private final Stage stage;

  private GameType currentGameType;
  private String selectedDifficultyOrConfig;
  private Board customBoard = null;

  /**
   * Constructs a GameSetupController. This constructor is called once when NavigationController
   * initializes scenes. The specific game type configuration happens in prepareGuiForGameType.
   *
   * @param view The game setup view.
   * @param navigationController The navigation controller for screen transitions.
   * @param playerManager The player manager for player data handling.
   * @param stage The primary stage for displaying dialogs.
   */
  public GameSetupController(
      GameSetupView view,
      NavigationController navigationController,
      PlayerManager playerManager,
      Stage stage) {
    this.view = Objects.requireNonNull(view, "GameSetupView cannot be null.");
    this.navigationController =
        Objects.requireNonNull(navigationController, "NavigationController cannot be null.");
    this.playerManager = Objects.requireNonNull(playerManager, "PlayerManager cannot be null.");
    this.boardManager = BoardManager.getInstance();
    this.stage = stage;

    this.view.setController(this);
    setupPlayerManagementHandlers();

    LOGGER.info("GameSetupController initialized.");
  }

  /**
   * Prepares the GUI elements of the GameSetupView based on the selected GameType. This method is
   * called by the NavigationController when navigating to the setup screen.
   *
   * @param gameType The type of game to set up the GUI for.
   */
  public void prepareGuiForGameType(GameType gameType) {
    this.currentGameType = Objects.requireNonNull(gameType, "GameType cannot be null for GUI prep.");
    LOGGER.info("Preparing GameSetup GUI for game type: " + this.currentGameType);

    view.setScreenTitle(getScreenTitleForGameType(this.currentGameType) + " - Setup");
    GameInfoPanel gameInfoPanelFromView = view.getGameInfoPanel();

    switch (this.currentGameType) {
      case SNAKES_AND_LADDERS:
        view.setDifficultyPanelVisible(true);
        onDifficultySelected("Normal");
        break;
      case ASTRO_RALLY:
        view.setDifficultyPanelVisible(false);
        this.selectedDifficultyOrConfig = "default";
        if (gameInfoPanelFromView != null) {
          gameInfoPanelFromView.setAstroRallyInfo();
        } else {
          LOGGER.warning("GameInfoPanel is null in GameSetupView. Cannot set Astro Rally info.");
        }
        break;
      default:
        LOGGER.log(Level.WARNING, "Unsupported game type for setup GUI: {0}", this.currentGameType);
        view.setDifficultyPanelVisible(true);
        onDifficultySelected("Normal");
        break;
    }
    view.clearStatusMessage();
    customBoard = null;
  }

  private String getScreenTitleForGameType(GameType gameType) {
    return switch (gameType) {
      case SNAKES_AND_LADDERS -> "Snakes & Ladders";
      case ASTRO_RALLY -> "Astro Rally";
    };
  }

  private void setupPlayerManagementHandlers() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    panel.getAddPlayerButton().setOnAction(e -> onAddPlayer());
    panel.getRemovePlayerButton().setOnAction(e -> onRemovePlayer());
    panel.getSavePlayerButton().setOnAction(e -> onSavePlayer());
    panel.getAddSavedPlayerButton().setOnAction(e -> onAddSavedPlayer());

    panel
        .getPlayerTabs()
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldTab, newTab) -> {
              if (newTab == panel.getCurrentPlayersTab()) {
                onCurrentPlayersTabSelected();
              } else if (newTab == panel.getSavedPlayersTab()) {
                onSavedPlayersTabSelected();
              }
            });
  }

  /**
   * Handles game start button click. Validates player count before starting. The type of game to
   * start is determined by {@code currentGameType}.
   */
  public void onGameStart() {
    List<Player> players = playerManager.getPlayers();
    int minPlayersForCurrentGame = getMinPlayersForGame(currentGameType);

    if (players.size() < minPlayersForCurrentGame) {
      AlertHelper.showErrorAlert(
          "Not Enough Players",
          "Please add at least " + minPlayersForCurrentGame + " players for " + getScreenTitleForGameType(currentGameType) + ".");
      view.updateStatusMessage(
          "Need at least " + minPlayersForCurrentGame + " players", true);
      return;
    }
    view.clearStatusMessage();

    try {
      if (GameType.SNAKES_AND_LADDERS.equals(currentGameType)
          && "custom".equalsIgnoreCase(this.selectedDifficultyOrConfig)
          && this.customBoard != null) {
        LOGGER.log(Level.INFO, "Starting Snakes & Ladders with custom uploaded board.");
        navigationController.startCustomGame(this.customBoard);
      } else {
        LOGGER.log(
            Level.INFO,
            "Starting new game of type: {0} with configuration: {1}",
            new Object[] {currentGameType, this.selectedDifficultyOrConfig});
        navigationController.startNewGame(currentGameType, this.selectedDifficultyOrConfig);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error starting game", e);
      AlertHelper.showErrorAlert("Game Start Error", "Could not start game: " + e.getMessage());
    }
  }

  private int getMinPlayersForGame(GameType gameType) {
    return switch (gameType) {
      case SNAKES_AND_LADDERS -> MIN_PLAYERS_SNAKES_LADDERS;
      case ASTRO_RALLY -> MIN_PLAYERS_ASTRO_RALLY;
    };
  }


  /** Handles back button click. Navigates to game selection screen. */
  public void onBack() {
    navigationController.navigateToGameSelection();
  }

  /**
   * Updates game info based on selected difficulty. This is primarily for Snakes & Ladders.
   *
   * @param difficulty The selected difficulty (e.g., "Easy", "Normal", "Hard", "Upload").
   */
  public void onDifficultySelected(String difficulty) {
    if (currentGameType == GameType.SNAKES_AND_LADDERS) {
      this.selectedDifficultyOrConfig = difficulty.toLowerCase();
      GameInfoPanel infoPanelFromView = view.getGameInfoPanel();

      if (infoPanelFromView != null) {
        switch (this.selectedDifficultyOrConfig) {
          case "easy" -> infoPanelFromView.setEasyModeInfo();
          case "normal" -> infoPanelFromView.setNormalModeInfo();
          case "hard" -> infoPanelFromView.setHardModeInfo();
          case "upload" -> {
            infoPanelFromView.setUploadModeInfo();
          }
          default -> infoPanelFromView.setNormalModeInfo();
        }
      }
      if (!"upload".equals(this.selectedDifficultyOrConfig)) {
        customBoard = null;
      }
      LOGGER.log(Level.INFO, "S&L Difficulty selected: {0}", this.selectedDifficultyOrConfig);
    } else {
      LOGGER.finer("Difficulty selection ignored for game type: " + currentGameType);
    }
  }

  /**
   * Handles upload board button click. This is specific to games that support custom boards (like
   * Snakes & Ladders).
   */
  public void onUploadBoard() {
    if (currentGameType != GameType.SNAKES_AND_LADDERS) {
      AlertHelper.showInfoAlert(
          "Upload Not Applicable",
          "Board upload is not applicable for " + getScreenTitleForGameType(currentGameType) + ".");
      return;
    }

    if (stage == null) {
      LOGGER.warning("Stage is null, cannot show file chooser for board upload.");
      AlertHelper.showErrorAlert(
          "Error", "Cannot open file dialog: main window context is missing.");
      return;
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Board File for Snakes & Ladders");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
    File boardsDir = new File(BOARDS_DIRECTORY);
    if (boardsDir.exists() && boardsDir.isDirectory()) {
      fileChooser.setInitialDirectory(boardsDir);
    } else {
      LOGGER.info("Boards directory not found at: " + BOARDS_DIRECTORY + ". Using default initial directory.");
    }

    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      try {
        this.customBoard = boardManager.loadBoardFromFile(selectedFile.getAbsolutePath());
        this.selectedDifficultyOrConfig = "custom";
        GameInfoPanel infoPanelFromView = view.getGameInfoPanel();
        if (infoPanelFromView != null) {
          infoPanelFromView.setUploadModeInfo();
        }
        view.updateStatusMessage("Custom board loaded: " + selectedFile.getName(), false);
        LOGGER.log(Level.INFO, "Loaded custom board from: {0}", selectedFile.getAbsolutePath());
      } catch (InvalidBoardFormatException e) {
        LOGGER.log(Level.WARNING, "Invalid board format: " + e.getMessage(), e);
        AlertHelper.showErrorAlert("Invalid Board File", e.getMessage());
        view.updateStatusMessage("Invalid board file: " + e.getMessage(), true);
        resetToDefaultBoardConfigForCurrentGame();
      } catch (BoardManagementException e) {
        LOGGER.log(Level.SEVERE, "Failed to load board file: " + e.getMessage(), e);
        AlertHelper.showErrorAlert("Board Load Error", "Failed to load board: " + e.getMessage());
        view.updateStatusMessage("Failed to load board: " + e.getMessage(), true);
        resetToDefaultBoardConfigForCurrentGame();
      }
    }
  }

  private void resetToDefaultBoardConfigForCurrentGame() {
    this.customBoard = null;
    GameInfoPanel infoPanelFromView = view.getGameInfoPanel();
    if (infoPanelFromView == null) {
      return;
    }

    if (currentGameType == GameType.SNAKES_AND_LADDERS) {
      this.selectedDifficultyOrConfig = "normal";
      infoPanelFromView.setNormalModeInfo();
    } else if (currentGameType == GameType.ASTRO_RALLY) {
      this.selectedDifficultyOrConfig = "default";
      infoPanelFromView.setAstroRallyInfo();
    }
  }

  // --- Player Management Methods ---
  public void refreshPlayerListViews() {
    LOGGER.fine("GameSetupController: Refreshing player list views.");
    onCurrentPlayersTabSelected();

    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    if (panel.getPlayerTabs().getSelectionModel().getSelectedItem()
        == panel.getSavedPlayersTab()) {
      onSavedPlayersTabSelected();
    }
  }

  public void onAddPlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    if (panel.isPlayerLimitReached()) {
      AlertHelper.showErrorAlert(
          "Player Limit Reached", "Maximum of " + MAX_PLAYERS + " players allowed.");
      view.updateStatusMessage("Maximum " + MAX_PLAYERS + " players reached", true);
      return;
    }

    CreatePlayerPopup popup = new CreatePlayerPopup();
    Optional<Player> result = popup.show();

    result.ifPresent(
        player -> {
          try {
            boolean playerExists =
                playerManager.getPlayers().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(player.getName()));

            if (playerExists) {
              view.updateStatusMessage(
                  "Player " + player.getName() + " is already in the game.", true);
              return;
            }
            playerManager.addPlayer(player);
            view.updateStatusMessage("Player " + player.getName() + " added.", false);
            refreshCurrentPlayersList();
          } catch (PlayerManagementException e) {
            LOGGER.log(Level.WARNING, "Failed to add player: {0}", e.getMessage());
            AlertHelper.showErrorAlert("Add Player Failed", e.getMessage());
          }
        });
  }

  public void onRemovePlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    Player selectedPlayer = panel.getSelectedCurrentPlayer();

    if (selectedPlayer != null) {
      if (panel.confirmPlayerRemoval(selectedPlayer)) {
        playerManager.removePlayer(selectedPlayer);
        refreshCurrentPlayersList();
        view.updateStatusMessage("Player " + selectedPlayer.getName() + " removed.", false);
      }
    } else {
      view.updateStatusMessage("No player selected to remove.", true);
    }
  }

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
        view.updateStatusMessage("Player " + selectedPlayer.getName() + " saved.", false);
        if (panel.getPlayerTabs().getSelectionModel().getSelectedItem()
            == panel.getSavedPlayersTab()) {
          loadSavedPlayersAndUpdateView();
        }
      } else {
        view.updateStatusMessage(
            "Player " + selectedPlayer.getName() + " is already saved.", true);
      }
    } catch (PlayerManagementException e) {
      LOGGER.log(Level.SEVERE, "Failed to save player", e);
      AlertHelper.showErrorAlert("Save Failed", "Could not save player: " + e.getMessage());
    }
  }

  public void onAddSavedPlayer() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    Player selectedSavedPlayer = panel.getSelectedSavedPlayer();

    if (selectedSavedPlayer == null) {
      AlertHelper.showErrorAlert("No Player Selected", "Please select a saved player to add.");
      return;
    }
    if (panel.isPlayerLimitReached()) {
      AlertHelper.showErrorAlert(
          "Player Limit Reached", "Maximum of " + MAX_PLAYERS + " players allowed.");
      view.updateStatusMessage("Maximum " + MAX_PLAYERS + " players reached", true);
      return;
    }
    try {
      boolean playerAlreadyAdded =
          playerManager.getPlayers().stream()
              .anyMatch(p -> p.getName().equalsIgnoreCase(selectedSavedPlayer.getName()));

      if (playerAlreadyAdded) {
        view.updateStatusMessage(
            "Player " + selectedSavedPlayer.getName() + " is already in the game.", true);
        return;
      }
      playerManager.addPlayer(selectedSavedPlayer);
      view.updateStatusMessage(
          "Added " + selectedSavedPlayer.getName() + " to the game.", false);
      panel.showCurrentPlayers();
      refreshCurrentPlayersList();
    } catch (PlayerManagementException e) {
      LOGGER.log(Level.SEVERE, "Failed to add saved player", e);
      AlertHelper.showErrorAlert("Add Player Failed", "Could not add player: " + e.getMessage());
    }
  }

  public void refreshCurrentPlayersList() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    List<Player> currentPlayers = playerManager.getPlayers();
    panel.updateCurrentPlayersList(currentPlayers);
  }

  public void loadSavedPlayersAndUpdateView() {
    try {
      List<Player> savedPlayers = playerManager.loadPlayersFromFile();
      PlayerManagementPanel panel = view.getPlayerManagementPanel();
      panel.updateSavedPlayersList(savedPlayers);
      view.updateStatusMessage(
          savedPlayers.isEmpty()
              ? "No saved players found."
              : savedPlayers.size() + " saved players available.",
          false);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading saved players for view", e);
      AlertHelper.showErrorAlert(
          "Load Error", "Could not load saved players: " + e.getMessage());
      view.updateStatusMessage("Failed to load saved players.", true);
    }
  }

  public void onCurrentPlayersTabSelected() {
    PlayerManagementPanel panel = view.getPlayerManagementPanel();
    refreshCurrentPlayersList();
    // panel.showCurrentPlayers(); // This logic is usually handled by PlayerManagementPanel itself on tab switch
  }

  public void onSavedPlayersTabSelected() {
    loadSavedPlayersAndUpdateView();
    // PlayerManagementPanel panel = view.getPlayerManagementPanel();
    // panel.showSavedPlayers(); // This logic is usually handled by PlayerManagementPanel itself
  }

  /**
   * Gets the current game type being configured.
   * @return The current GameType.
   */
  public GameType getCurrentGameType() {
    return currentGameType;
  }
}
