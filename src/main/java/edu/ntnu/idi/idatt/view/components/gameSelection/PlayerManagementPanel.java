package edu.ntnu.idi.idatt.view.components.gameSelection;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import edu.ntnu.idi.idatt.view.utils.AlertHelper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Panel for managing players: adding, saving, and switching between saved/current players.
 * Includes validation for maximum player limit and visual feedback.
 */
public class PlayerManagementPanel extends BorderPane {
  private static final Logger LOGGER = Logger.getLogger(PlayerManagementPanel.class.getName());
  private static final int MAX_PLAYERS = 4; // Maximum number of players allowed

  private final TabPane playerTabs;
  private final ListView<Player> playerListView;
  private final ListView<Player> savedPlayerListView;
  private final Button addPlayerButton;
  private final Button savePlayerButton;
  private final Button removePlayerButton;
  private final Button addSavedPlayerButton;
  private final Label statusLabel;

  // Property to track if a player is selected for addition
  private final BooleanProperty playerSelectedForAddition = new SimpleBooleanProperty(false);

  /**
   * Constructs the PlayerManagementPanel using an injected ButtonFactory.
   *
   * @param buttonFactory Factory for creating styled buttons
   */
  public PlayerManagementPanel(ButtonFactory buttonFactory) {
    this.getStyleClass().add("player-panel");

    // Initialize tab pane and set up tabs
    this.playerTabs = new TabPane();
    initializeTabs();

    // Initialize list views
    this.playerListView = new ListView<>();
    this.savedPlayerListView = new ListView<>();

    setupCurrentPlayerListView(playerListView);
    setupSavedPlayerListView(savedPlayerListView);

    // Create buttons with consistent styling
    this.addPlayerButton = buttonFactory.createSmallButton("+ Create New Player");
    this.savePlayerButton = buttonFactory.createSmallButton("Save Player");
    this.removePlayerButton = buttonFactory.createSmallButton("Remove Player");
    this.addSavedPlayerButton = buttonFactory.createSmallButton("Add Selected");

    // Set up tooltips for buttons
    addPlayerButton.setTooltip(new Tooltip("Create a new player to add to the game"));
    savePlayerButton.setTooltip(new Tooltip("Save the selected player for future games"));
    removePlayerButton.setTooltip(new Tooltip("Remove the selected player from the current game"));
    addSavedPlayerButton.setTooltip(new Tooltip("Add selected saved player to the current game"));

    // Status label for feedback
    this.statusLabel = new Label("");
    statusLabel.getStyleClass().add("status-label");

    // Add styling classes
    addPlayerButton.getStyleClass().add("button");
    savePlayerButton.getStyleClass().add("button");
    removePlayerButton.getStyleClass().add("button");
    addSavedPlayerButton.getStyleClass().add("button");

    // Create separate control boxes for each tab
    HBox currentPlayersControls = new HBox(10, savePlayerButton, addPlayerButton, removePlayerButton);
    currentPlayersControls.setAlignment(Pos.CENTER);

    HBox savedPlayersControls = new HBox(10, addSavedPlayerButton);
    savedPlayersControls.setAlignment(Pos.CENTER);

    // Bottom container that will swap controls based on tab selection
    BorderPane controlsContainer = new BorderPane();
    controlsContainer.setCenter(currentPlayersControls); // Initial controls

    // Status box with improved styling
    HBox statusBox = new HBox(statusLabel);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(5, 0, 0, 0));
    statusBox.getStyleClass().add("status-box");

    BorderPane bottomContainer = new BorderPane();
    bottomContainer.setTop(controlsContainer);
    bottomContainer.setBottom(statusBox);
    bottomContainer.setPadding(new Insets(10, 0, 0, 0));

    setTop(playerTabs);
    setCenter(playerListView); // Initial view
    setBottom(bottomContainer);

    this.getStyleClass().add("player-management-panel");

    addSavedPlayerButton.disableProperty().bind(
        Bindings.or(
            Bindings.not(playerSelectedForAddition),
            Bindings.createBooleanBinding(() -> isPlayerLimitReached(), playerListView.getItems())
        )
    );

    // Set up tab change listener to update bottom controls
    playerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
      if (newTab == getCurrentPlayersTab()) {
        controlsContainer.setCenter(currentPlayersControls);
        setCenter(playerListView);
      } else if (newTab == getSavedPlayersTab()) {
        controlsContainer.setCenter(savedPlayersControls);
        setCenter(savedPlayerListView);
      }
      updateButtonStates();
    });

    updateButtonStates();
  }

  /**
   * Sets up the ListView cell factory for current players.
   *
   * @param listView The ListView to set up
   */
  private void setupCurrentPlayerListView(ListView<Player> listView) {
    // Make the rows taller for better visibility
    listView.setFixedCellSize(50);

    listView.setCellFactory(param -> new ListCell<>() {
      private final HBox container = new HBox(15);
      private final ImageView iconView = new ImageView();
      private final Label nameLabel = new Label();

      {
        // Setup cell components once
        iconView.setFitHeight(32);
        iconView.setFitWidth(32);
        iconView.setPreserveRatio(true);
        nameLabel.getStyleClass().add("player-name-label");

        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));
        container.getChildren().addAll(iconView, nameLabel);

        // Add hover effect styling
        container.getStyleClass().add("player-list-cell");
      }

      @Override
      protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);

        if (empty || player == null) {
          setText(null);
          setGraphic(null);
        } else {
          nameLabel.setText(player.getName());
          iconView.setImage(loadPlayerIcon(player.getPieceType()));
          setGraphic(container);
        }
      }
    });

    // Add selection change listener to update button states
    listView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> updateButtonStates());
  }

  /**
   * Sets up the ListView cell factory for saved players with checkboxes.
   *
   * @param listView The ListView to set up
   */
  private void setupSavedPlayerListView(ListView<Player> listView) {
    // Make the rows taller for better visibility
    listView.setFixedCellSize(50);

    listView.setCellFactory(param -> new ListCell<>() {
      private final HBox container = new HBox(15);
      private final ImageView iconView = new ImageView();
      private final Label nameLabel = new Label();
      private final Region spacer = new Region();
      private final CheckBox selectCheckBox = new CheckBox();

      {
        // Setup cell components once
        iconView.setFitHeight(32);
        iconView.setFitWidth(32);
        iconView.setPreserveRatio(true);
        nameLabel.getStyleClass().add("player-name-label");

        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Style the checkbox
        selectCheckBox.getStyleClass().add("player-select-checkbox");

        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));
        container.getChildren().addAll(iconView, nameLabel, spacer, selectCheckBox);
        container.getStyleClass().add("player-list-cell");

        // Add change listener to checkbox
        selectCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
          if (getItem() != null) {
            if (newVal) {
              getListView().getSelectionModel().select(getIndex());
              playerSelectedForAddition.set(true);
            } else if (getListView().getSelectionModel().getSelectedItem() == getItem()) {
              playerSelectedForAddition.set(false);
            }
          }
        });
      }

      @Override
      protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);

        if (empty || player == null) {
          setText(null);
          setGraphic(null);
        } else {
          nameLabel.setText(player.getName());
          iconView.setImage(loadPlayerIcon(player.getPieceType()));

          // Reset checkbox state when item is updated
          selectCheckBox.setSelected(false);

          setGraphic(container);
        }
      }
    });

    // Add selection change listener to update button states
    listView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          playerSelectedForAddition.set(newValue != null);
          updateButtonStates();
        });
  }

  /**
   * Updates the state of buttons based on current selection and player count.
   */
  private void updateButtonStates() {
    Tab selectedTab = playerTabs.getSelectionModel().getSelectedItem();
    boolean isCurrentPlayersTab = selectedTab == getCurrentPlayersTab();

    // Enable/disable add player button based on player count and current tab
    boolean canAddPlayer = playerListView.getItems().size() < MAX_PLAYERS;
    addPlayerButton.setDisable(!canAddPlayer || !isCurrentPlayersTab);

    // Enable/disable remove and save buttons based on selection in current players tab
    boolean hasCurrentSelection = isCurrentPlayersTab &&
        playerListView.getSelectionModel().getSelectedItem() != null;
    removePlayerButton.setDisable(!hasCurrentSelection);
    savePlayerButton.setDisable(!hasCurrentSelection);

    // Update status message based on context
    updateStatusMessage(isCurrentPlayersTab, canAddPlayer);
  }

  /**
   * Updates the status message based on the current context.
   *
   * @param isCurrentPlayersTab Whether the current players tab is selected
   * @param canAddPlayer Whether more players can be added
   */
  private void updateStatusMessage(boolean isCurrentPlayersTab, boolean canAddPlayer) {
    if (isCurrentPlayersTab) {
      if (!canAddPlayer) {
        statusLabel.setText("Maximum " + MAX_PLAYERS + " players reached");
        statusLabel.getStyleClass().removeAll("status-info", "status-error");
        statusLabel.getStyleClass().add("status-warning");
      } else if (playerListView.getItems().isEmpty()) {
        statusLabel.setText("Add players to begin");
        statusLabel.getStyleClass().removeAll("status-warning", "status-error");
        statusLabel.getStyleClass().add("status-info");
      } else {
        // Show current player count
        int count = playerListView.getItems().size();
        statusLabel.setText(count + " player" + (count != 1 ? "s" : "") + " added");
        statusLabel.getStyleClass().removeAll("status-warning", "status-error");
        statusLabel.getStyleClass().add("status-info");
      }
    } else {
      // In saved players tab
      if (savedPlayerListView.getItems().isEmpty()) {
        statusLabel.setText("No saved players found");
        statusLabel.getStyleClass().removeAll("status-warning", "status-error");
        statusLabel.getStyleClass().add("status-info");
      } else if (!canAddPlayer) {
        statusLabel.setText("Cannot add more players (maximum reached)");
        statusLabel.getStyleClass().removeAll("status-info", "status-error");
        statusLabel.getStyleClass().add("status-warning");
      } else {
        statusLabel.setText("Select players to add to game");
        statusLabel.getStyleClass().removeAll("status-warning", "status-error");
        statusLabel.getStyleClass().add("status-info");
      }
    }
  }

  /**
   * Loads a player icon based on piece type with fallback to default.
   *
   * @param pieceType The type of piece to load an icon for
   * @return The loaded image or null if no image could be loaded
   */
  private Image loadPlayerIcon(String pieceType) {
    // Try to load the specific icon
    try {
      return ResourceLoader.loadImage("/edu/ntnu/idi/idatt/view/resources/icons/" +
          pieceType.toLowerCase() + ".png");
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.INFO, "Could not load icon for piece type: {0}", pieceType);

      // Try to load the default icon
      try {
        return ResourceLoader.loadImage("/edu/ntnu/idi/idatt/view/resources/icons/default.png");
      } catch (BoardGameResourceException ex) {
        LOGGER.log(Level.WARNING, "Could not load default player icon", ex);
        return null;
      }
    }
  }

  /**
   * Initializes the tabs for the player management panel.
   */
  private void initializeTabs() {
    Tab currentPlayers = new Tab("Current Players");
    currentPlayers.setClosable(false);

    Tab savedPlayers = new Tab("Saved Players");
    savedPlayers.setClosable(false);

    playerTabs.getTabs().addAll(currentPlayers, savedPlayers);
  }

  /**
   * Shows an alert to confirm removal of a player.
   *
   * @param player The player to remove
   * @return true if the user confirmed, false otherwise
   */
  public boolean confirmPlayerRemoval(Player player) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Remove Player");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to remove player: " + player.getName() + "?");

    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.OK;
  }

  /**
   * Adds a player to the current player list.
   * Validates against the maximum player limit.
   *
   * @param player The player to add
   * @return true if the player was added successfully, false otherwise
   */
  public boolean addPlayer(Player player) {
    if (playerListView.getItems().size() >= MAX_PLAYERS) {
      AlertHelper.showErrorAlert("Player Limit Reached",
          "Maximum of " + MAX_PLAYERS + " players allowed.");
      return false;
    }

    playerListView.getItems().add(player);
    updateButtonStates();
    return true;
  }

  /**
   * Removes the currently selected player from the list.
   *
   * @return The removed player, or null if no player was selected
   */
  public Player removeSelectedPlayer() {
    Player selectedPlayer = playerListView.getSelectionModel().getSelectedItem();
    int selectedIndex = playerListView.getSelectionModel().getSelectedIndex();

    if (selectedIndex >= 0) {
      playerListView.getItems().remove(selectedIndex);
      updateButtonStates();
      return selectedPlayer;
    }

    return null;
  }

  /**
   * Selects the "Current Players" tab and shows its content.
   */
  public void showCurrentPlayers() {
    playerTabs.getSelectionModel().select(getCurrentPlayersTab());
    setCenter(playerListView);
    updateButtonStates();
  }

  /**
   * Selects the "Saved Players" tab and shows its content.
   */
  public void showSavedPlayers() {
    playerTabs.getSelectionModel().select(getSavedPlayersTab());
    setCenter(savedPlayerListView);
    updateButtonStates();
  }

  /**
   * Updates the saved players list with the provided players.
   *
   * @param savedPlayers The list of saved players to display
   */
  public void updateSavedPlayersList(List<Player> savedPlayers) {
    savedPlayerListView.getItems().clear();
    savedPlayerListView.getItems().addAll(savedPlayers);

    // Reset the selection state when updating the list
    playerSelectedForAddition.set(false);
    updateButtonStates();
  }

  /**
   * Updates the current players list with the provided players.
   *
   * @param currentPlayers The list of current players to display
   */
  public void updateCurrentPlayersList(List<Player> currentPlayers) {
    playerListView.getItems().clear();
    playerListView.getItems().addAll(currentPlayers);
    updateButtonStates();
  }

  /**
   * Sets the status message with optional error styling.
   *
   * @param message The message to display
   * @param isError Whether to style the message as an error
   */
  public void setStatusMessage(String message, boolean isError) {
    statusLabel.setText(message);
    statusLabel.getStyleClass().removeAll("status-info", "status-warning", "status-error");
    statusLabel.getStyleClass().add(isError ? "status-error" : "status-info");
  }

  /**
   * Gets the number of players currently in the game.
   *
   * @return The number of players
   */
  public int getPlayerCount() {
    return playerListView.getItems().size();
  }

  /**
   * Checks if the maximum player limit has been reached.
   *
   * @return true if the maximum limit has been reached, false otherwise
   */
  public boolean isPlayerLimitReached() {
    return playerListView.getItems().size() >= MAX_PLAYERS;
  }

  /**
   * Gets the selected player from the saved players list.
   *
   * @return The selected player or null if none is selected
   */
  public Player getSelectedSavedPlayer() {
    return savedPlayerListView.getSelectionModel().getSelectedItem();
  }

  /**
   * Gets the selected player from the current players list.
   *
   * @return The selected player or null if none is selected
   */
  public Player getSelectedCurrentPlayer() {
    return playerListView.getSelectionModel().getSelectedItem();
  }

  // Getter methods for UI components
  public Button getAddPlayerButton() {
    return addPlayerButton;
  }

  public Button getSavePlayerButton() {
    return savePlayerButton;
  }

  public Button getRemovePlayerButton() {
    return removePlayerButton;
  }

  public Button getAddSavedPlayerButton() {
    return addSavedPlayerButton;
  }

  public ListView<Player> getPlayerListView() {
    return playerListView;
  }

  public ListView<Player> getSavedPlayerListView() {
    return savedPlayerListView;
  }

  public TabPane getPlayerTabs() {
    return playerTabs;
  }

  public Tab getCurrentPlayersTab() {
    return playerTabs.getTabs().get(0);
  }

  public Tab getSavedPlayersTab() {
    return playerTabs.getTabs().get(1);
  }
}