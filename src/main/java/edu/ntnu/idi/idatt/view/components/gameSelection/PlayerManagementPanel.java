package edu.ntnu.idi.idatt.view.components.gameSelection;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
  private final Label statusLabel;

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
    this.savedPlayerListView = new ListView<>();

    setupPlayerListView(playerListView);
    setupPlayerListView(savedPlayerListView);

    this.addPlayerButton = buttonFactory.createSmallButton("+ Create New Player");
    this.savePlayerButton = buttonFactory.createSmallButton("Save Player");
    this.removePlayerButton = buttonFactory.createSmallButton("Remove Player");

    // Status label for feedback
    this.statusLabel = new Label("");
    statusLabel.getStyleClass().add("status-label");

    addPlayerButton.getStyleClass().add("button");
    savePlayerButton.getStyleClass().add("button");
    removePlayerButton.getStyleClass().add("button");

    HBox controlsBox = new HBox(10, savePlayerButton, addPlayerButton, removePlayerButton);
    controlsBox.setAlignment(Pos.CENTER);

    // VBox for bottom controls including status label
    HBox statusBox = new HBox(statusLabel);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(5, 0, 0, 0));

    BorderPane bottomContainer = new BorderPane();
    bottomContainer.setTop(controlsBox);
    bottomContainer.setBottom(statusBox);

    setTop(playerTabs);
    setCenter(playerListView);
    setBottom(bottomContainer);

    this.getStyleClass().add("player-management-panel");

    // Add event handler for the remove button
    removePlayerButton.setOnAction(e -> removeSelectedPlayer());

    // Initial button state
    updateButtonStates();
  }

  private void setupPlayerListView(ListView<Player> listView) {
    // Make the rows taller
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
        nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));
        container.getChildren().addAll(iconView, nameLabel);
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
   * Updates the state of buttons based on current selection and player count.
   */
  private void updateButtonStates() {
    // Enable/disable add player button based on player count
    boolean canAddPlayer = isPlayerLimitReached();
    addPlayerButton.setDisable(!canAddPlayer);

    // Enable/disable remove button based on selection
    boolean hasSelection = playerListView.getSelectionModel().getSelectedItem() != null;
    removePlayerButton.setDisable(!hasSelection);

    // Enable/disable save button based on selection
    savePlayerButton.setDisable(!hasSelection);

    // Update status message
    if (!canAddPlayer) {
      statusLabel.setText("Maximum " + MAX_PLAYERS + " players reached");
      statusLabel.setStyle("-fx-text-fill: #ff9966;");
    } else {
      statusLabel.setText("");
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
      LOGGER.log(Level.INFO, "Could not load icon for piece type: " + pieceType, e);

      // Try to load the default icon
      try {
        return ResourceLoader.loadImage("/edu/ntnu/idi/idatt/view/resources/icons/default.png");
      } catch (BoardGameResourceException ex) {
        LOGGER.log(Level.WARNING, "Could not load default player icon", ex);
        return null;
      }
    }
  }

  private void initializeTabs() {
    Tab currentPlayers = new Tab("Current Players");
    currentPlayers.setClosable(false);
    currentPlayers.setContent(playerListView);

    Tab savedPlayers = new Tab("Saved Players");
    savedPlayers.setClosable(false);
    savedPlayers.setContent(savedPlayerListView);

    playerTabs.getTabs().addAll(currentPlayers, savedPlayers);
  }

  // Getter methods for buttons, list view and tabs
  public Button getAddPlayerButton() {
    return addPlayerButton;
  }

  public Button getSavePlayerButton() {
    return savePlayerButton;
  }

  public Button getRemovePlayerButton() {
    return removePlayerButton;
  }

  // Get-methods for retrieving players
  public ListView<Player> getPlayerListView() {
    return playerListView;
  }

  public ListView<Player> getSavedPlayerListView() {
    return savedPlayerListView;
  }

  // In PlayerManagementPanel
  public void showCurrentPlayers() {
    setCenter(playerListView);
  }

  public void showSavedPlayers() {
    setCenter(savedPlayerListView);
  }

  public TabPane getPlayerTabs() {
    return playerTabs;
  }

  public Tab getCurrentPlayersTab() {
    return playerTabs.getTabs().getFirst();
  }

  public Tab getSavedPlayersTab() {
    return playerTabs.getTabs().get(1);
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
      showErrorAlert("Player Limit Reached",
          "Maximum of " + MAX_PLAYERS + " players allowed.");
      return false;
    }

    playerListView.getItems().add(player);
    updateButtonStates();
    return true;
  }

  /**
   * Removes the currently selected player from the list.
   */
  public void removeSelectedPlayer() {
    int selectedIndex = playerListView.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {
      playerListView.getItems().remove(selectedIndex);
      updateButtonStates();
    }
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
}