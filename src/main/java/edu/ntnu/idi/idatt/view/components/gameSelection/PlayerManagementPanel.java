package edu.ntnu.idi.idatt.view.components.gameSelection;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import edu.ntnu.idi.idatt.view.utils.AlertHelper;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * Panel for managing players using TableView: adding, saving, and switching between saved/current players.
 * Includes validation for maximum player limit and visual feedback with timed status messages.
 */
public class PlayerManagementPanel extends BorderPane {
  private static final Logger LOGGER = Logger.getLogger(PlayerManagementPanel.class.getName());
  private static final int MAX_PLAYERS = 4;
  private static final Duration STATUS_MESSAGE_DURATION = Duration.seconds(2);

  private final TabPane playerTabs;
  private final TableView<Player> currentPlayerTableView;
  private final TableView<Player> savedPlayerTableView;
  private final ObservableList<Player> currentPlayersObservableList = FXCollections.observableArrayList();
  private final ObservableList<Player> savedPlayersObservableList = FXCollections.observableArrayList();

  private final Button addPlayerButton;
  private final Button savePlayerButton;
  private final Button removePlayerButton;
  private final Button addSavedPlayerButton;
  private final Label statusLabel;
  private final PauseTransition statusClearTimer;

  /**
   * Constructs the PlayerManagementPanel using TableViews and an injected ButtonFactory.
   *
   * @param buttonFactory Factory for creating styled buttons
   */
  public PlayerManagementPanel(ButtonFactory buttonFactory) {
    this.getStyleClass().add("player-panel");

    this.playerTabs = new TabPane();
    initializeTabs();

    this.currentPlayerTableView = new TableView<>(currentPlayersObservableList);
    this.savedPlayerTableView = new TableView<>(savedPlayersObservableList);

    setupCurrentPlayerTableView(currentPlayerTableView);
    setupSavedPlayerTableView(savedPlayerTableView);

    this.addPlayerButton = buttonFactory.createSmallButton("+ Create New Player");
    this.savePlayerButton = buttonFactory.createSmallButton("Save Player");
    this.removePlayerButton = buttonFactory.createSmallButton("Remove Player");
    this.addSavedPlayerButton = buttonFactory.createSmallButton("Add Selected");

    addPlayerButton.setTooltip(new Tooltip("Create a new player to add to the game"));
    savePlayerButton.setTooltip(new Tooltip("Save the selected player for future games"));
    removePlayerButton.setTooltip(new Tooltip("Remove the selected player from the current game"));
    addSavedPlayerButton.setTooltip(new Tooltip("Add selected saved player to the current game"));

    this.statusLabel = new Label("");
    statusLabel.getStyleClass().add("status-label");

    statusClearTimer = new PauseTransition(STATUS_MESSAGE_DURATION);
    statusClearTimer.setOnFinished(e -> {
      statusLabel.setText("");
      updateButtonStates();
    });

    addPlayerButton.getStyleClass().add("button");
    savePlayerButton.getStyleClass().add("button");
    removePlayerButton.getStyleClass().add("button");
    addSavedPlayerButton.getStyleClass().add("button");

    HBox currentPlayersControls = new HBox(10, savePlayerButton, addPlayerButton, removePlayerButton);
    currentPlayersControls.setAlignment(Pos.CENTER);

    HBox savedPlayersControls = new HBox(10, addSavedPlayerButton);
    savedPlayersControls.setAlignment(Pos.CENTER);

    BorderPane controlsContainer = new BorderPane();
    controlsContainer.setCenter(currentPlayersControls);

    HBox statusBox = new HBox(statusLabel);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(5, 0, 0, 0));
    statusBox.getStyleClass().add("status-box");

    BorderPane bottomContainer = new BorderPane();
    bottomContainer.setTop(controlsContainer);
    bottomContainer.setBottom(statusBox);
    bottomContainer.setPadding(new Insets(10, 0, 0, 0));

    setTop(playerTabs);
    setCenter(currentPlayerTableView);
    setBottom(bottomContainer);

    this.getStyleClass().add("player-management-panel");

    addSavedPlayerButton.disableProperty().bind(
        Bindings.or(
            savedPlayerTableView.getSelectionModel().selectedItemProperty().isNull(),
            Bindings.createBooleanBinding(() -> isPlayerLimitReached(), currentPlayersObservableList)
        )
    );
    removePlayerButton.disableProperty().bind(
        currentPlayerTableView.getSelectionModel().selectedItemProperty().isNull()
    );
    savePlayerButton.disableProperty().bind(
        currentPlayerTableView.getSelectionModel().selectedItemProperty().isNull()
    );
    addPlayerButton.disableProperty().bind(
        Bindings.createBooleanBinding(() -> isPlayerLimitReached(), currentPlayersObservableList)
    );

    playerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
      if (newTab == getCurrentPlayersTab()) {
        controlsContainer.setCenter(currentPlayersControls);
        setCenter(currentPlayerTableView);
      } else if (newTab == getSavedPlayersTab()) {
        controlsContainer.setCenter(savedPlayersControls);
        setCenter(savedPlayerTableView);
      }
      statusClearTimer.stop();
      statusLabel.setText("");
      updateButtonStates();
    });

    updateButtonStates();
  }

  private void setupCurrentPlayerTableView(TableView<Player> tableView) {
    tableView.setPlaceholder(new Label("No players added yet."));
    tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    TableColumn<Player, Player> iconColumn = createIconColumn("Icon");
    TableColumn<Player, String> nameColumn = createTextColumn("Name", "name", true);
    TableColumn<Player, String> pieceTypeColumn = createTextColumn("Piece", "pieceType", true);

    nameColumn.getStyleClass().add("centered-cell");
    pieceTypeColumn.getStyleClass().add("centered-cell");

    iconColumn.setMaxWidth(60);
    iconColumn.setMinWidth(60);
    nameColumn.setPrefWidth(150);
    pieceTypeColumn.setPrefWidth(100);

    tableView.getColumns().addAll(iconColumn, nameColumn, pieceTypeColumn);

    tableView.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> updateButtonStates());
  }

  private void setupSavedPlayerTableView(TableView<Player> tableView) {
    tableView.setPlaceholder(new Label("No saved players found."));
    tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    TableColumn<Player, Player> iconColumn = createIconColumn("Icon");
    TableColumn<Player, String> nameColumn = createTextColumn("Name", "name", true);
    TableColumn<Player, String> pieceTypeColumn = createTextColumn("Piece", "pieceType", true);

    nameColumn.getStyleClass().add("centered-cell");
    pieceTypeColumn.getStyleClass().add("centered-cell");

    iconColumn.setMaxWidth(60);
    iconColumn.setMinWidth(60);
    nameColumn.setPrefWidth(150);
    pieceTypeColumn.setPrefWidth(100);

    tableView.getColumns().addAll(iconColumn, nameColumn, pieceTypeColumn);

    tableView.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> updateButtonStates());
  }

  private TableColumn<Player, String> createTextColumn(String title, String propertyName, boolean centerText) {
    TableColumn<Player, String> column = new TableColumn<>(title);
    column.setCellValueFactory(new PropertyValueFactory<>(propertyName));

    if (centerText) {
      column.setCellFactory(tc -> {
        TableCell<Player, String> cell = new TableCell<>();
        cell.textProperty().bind(cell.itemProperty());
        cell.setAlignment(Pos.CENTER);
        return cell;
      });
    }

    return column;
  }

  private TableColumn<Player, Player> createIconColumn(String title) {
    TableColumn<Player, Player> iconColumn = new TableColumn<>(title);
    iconColumn.getStyleClass().add("first-column");

    iconColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
    iconColumn.setCellFactory(param -> new TableCell<>() {
      private final ImageView iconView = new ImageView();
      private final HBox container = new HBox(iconView);

      {
        iconView.setFitHeight(32);
        iconView.setFitWidth(32);
        iconView.setPreserveRatio(true);
        container.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER);
      }

      @Override
      protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);
        if (empty || player == null) {
          setGraphic(null);
        } else {
          iconView.setImage(loadPlayerIcon(player.getPieceType()));
          setGraphic(container);
        }
      }
    });
    return iconColumn;
  }

  private void updateButtonStates() {
    statusClearTimer.stop();

    Tab selectedTab = playerTabs.getSelectionModel().getSelectedItem();
    boolean isCurrentPlayersTab = selectedTab == getCurrentPlayersTab();
    boolean canAddPlayer = !isPlayerLimitReached();

    if (statusLabel.getText().isEmpty() || statusClearTimer.getStatus() != javafx.animation.Animation.Status.RUNNING) {
      updateDefaultStatusMessage(isCurrentPlayersTab, canAddPlayer);
    }
  }

  private void updateDefaultStatusMessage(boolean isCurrentPlayersTab, boolean canAddPlayer) {
    if (isCurrentPlayersTab) {
      if (!canAddPlayer) {
        statusLabel.setText("Maximum " + MAX_PLAYERS + " players reached");
        statusLabel.getStyleClass().setAll("status-label", "status-warning");
      } else if (currentPlayersObservableList.isEmpty()) {
        statusLabel.setText("Add players to begin");
        statusLabel.getStyleClass().setAll("status-label", "status-info");
      } else {
        int count = currentPlayersObservableList.size();
        statusLabel.setText(count + " player" + (count != 1 ? "s" : "") + " added");
        statusLabel.getStyleClass().setAll("status-label", "status-info");
      }
    } else {
      if (savedPlayersObservableList.isEmpty()) {
        statusLabel.setText("No saved players found");
        statusLabel.getStyleClass().setAll("status-label", "status-info");
      } else if (!canAddPlayer) {
        statusLabel.setText("Cannot add more players (maximum reached)");
        statusLabel.getStyleClass().setAll("status-label", "status-warning");
      } else {
        statusLabel.setText("Select a player to add to the game");
        statusLabel.getStyleClass().setAll("status-label", "status-info");
      }
    }
  }

  private Image loadPlayerIcon(String pieceType) {
    if (pieceType == null || pieceType.isBlank()) {
      pieceType = "default";
    }
    try {
      return ResourceLoader.loadImage("/edu/ntnu/idi/idatt/view/resources/icons/" +
          pieceType.toLowerCase() + ".png");
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.INFO, "Could not load icon for piece type: {0}, trying default.", pieceType);
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
    return AlertHelper.showConfirmationAlert("Remove Player",
        "Are you sure you want to remove player: " + player.getName() + "?");
  }

  /**
   * Selects the "Current Players" tab and shows its content.
   */
  public void showCurrentPlayers() {
    playerTabs.getSelectionModel().select(getCurrentPlayersTab());
    setCenter(currentPlayerTableView);
    updateButtonStates();
  }

  /**
   * Selects the "Saved Players" tab and shows its content.
   */
  public void showSavedPlayers() {
    playerTabs.getSelectionModel().select(getSavedPlayersTab());
    setCenter(savedPlayerTableView);
    updateButtonStates();
  }

  /**
   * Updates the saved players table with the provided players.
   *
   * @param savedPlayers The list of saved players to display
   */
  public void updateSavedPlayersList(List<Player> savedPlayers) {
    savedPlayersObservableList.setAll(savedPlayers);
    updateButtonStates();
  }

  /**
   * Updates the current players table with the provided players.
   *
   * @param currentPlayers The list of current players to display
   */
  public void updateCurrentPlayersList(List<Player> currentPlayers) {
    currentPlayersObservableList.setAll(currentPlayers);
    updateButtonStates();
  }

  /**
   * Sets the status message with optional styling and starts the timer to clear it.
   *
   * @param message The message to display
   * @param isError If true, use error style; if false, use info style.
   */
  public void setStatusMessage(String message, boolean isError) {
    statusLabel.setText(message);
    if (isError) {
      statusLabel.getStyleClass().setAll("status-label", "status-error");
    } else {
      statusLabel.getStyleClass().setAll("status-label", "status-info");
    }
    statusClearTimer.playFromStart();
  }

  /**
   * Checks if the maximum player limit has been reached.
   *
   * @return true if the maximum limit has been reached, false otherwise
   */
  public boolean isPlayerLimitReached() {
    return currentPlayersObservableList.size() >= MAX_PLAYERS;
  }

  /**
   * Gets the selected player from the saved players table.
   *
   * @return The selected player or null if none is selected
   */
  public Player getSelectedSavedPlayer() {
    return savedPlayerTableView.getSelectionModel().getSelectedItem();
  }

  /**
   * Gets the selected player from the current players table.
   *
   * @return The selected player or null if none is selected
   */
  public Player getSelectedCurrentPlayer() {
    return currentPlayerTableView.getSelectionModel().getSelectedItem();
  }

  public Button getAddPlayerButton() { return addPlayerButton; }
  public Button getSavePlayerButton() { return savePlayerButton; }
  public Button getRemovePlayerButton() { return removePlayerButton; }
  public Button getAddSavedPlayerButton() { return addSavedPlayerButton; }
  public TabPane getPlayerTabs() { return playerTabs; }
  public Tab getCurrentPlayersTab() { return playerTabs.getTabs().get(0); }
  public Tab getSavedPlayersTab() { return playerTabs.getTabs().get(1); }
  public TableView<Player> getCurrentPlayerTableView() { return currentPlayerTableView; }
  public TableView<Player> getSavedPlayerTableView() { return savedPlayerTableView; }
}