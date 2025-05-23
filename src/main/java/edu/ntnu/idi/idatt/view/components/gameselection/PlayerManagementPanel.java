package edu.ntnu.idi.idatt.view.components.gameselection;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.AlertHelper;
import edu.ntnu.idi.idatt.utils.ResourceLoader;
import java.util.Arrays;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * A panel for managing players within a game setup screen.
 *
 * <p>This panel utilizes a {@link TabPane} to switch between views for "Current Players"
 * and "Saved Players". Each view is represented by a {@link TableView}.
 * It provides functionality to add new players, save current players,
 * remove players from the current game, and add saved players to the current game.
 * The panel enforces a maximum player limit and provides visual feedback on actions
 * through timed status messages. Button states are dynamically updated based on
 * the current selection and player counts.
 * </p>
 * It uses a {@link ButtonFactory} for creating styled buttons.
 *
 * @see Player
 * @see TableView
 * @see TabPane
 * @see ButtonFactory
 */
public class PlayerManagementPanel extends BorderPane {

  private static final Logger LOGGER = Logger.getLogger(PlayerManagementPanel.class.getName());
  private static final int MAX_PLAYERS = 4;
  private static final Duration STATUS_MESSAGE_DURATION = Duration.seconds(2);
  private static final double DEFAULT_SPACING = 10.0;
  private static final double BOTTOM_CONTAINER_PADDING_TOP = 10.0;
  private static final double STATUS_BOX_PADDING_TOP = 5.0;
  private static final double ICON_COLUMN_WIDTH = 60.0;
  private static final double NAME_COLUMN_PREF_WIDTH = 150.0;
  private static final double PIECE_COLUMN_PREF_WIDTH = 100.0;
  private static final double ICON_VIEW_SIZE = 32.0;

  private static final String STYLE_CLASS_PLAYER_PANEL = "player-panel";
  private static final String STYLE_CLASS_PLAYER_MANAGEMENT_PANEL = "player-management-panel";
  private static final String STYLE_CLASS_STATUS_LABEL = "status-label";
  private static final String STYLE_CLASS_STATUS_BOX = "status-box";
  private static final String STYLE_CLASS_BUTTON = "button";
  private static final String STYLE_CLASS_CENTERED_CELL = "centered-cell";
  private static final String STYLE_CLASS_FIRST_COLUMN = "first-column";
  private static final String STYLE_CLASS_STATUS_INFO = "status-info";
  private static final String STYLE_CLASS_STATUS_WARNING = "status-warning";

  private static final String TAB_TITLE_CURRENT_PLAYERS = "Current Players";
  private static final String TAB_TITLE_SAVED_PLAYERS = "Saved Players";
  private static final String PLACEHOLDER_NO_PLAYERS_ADDED = "No players added yet.";
  private static final String PLACEHOLDER_NO_SAVED_PLAYERS = "No saved players found.";
  private static final String TOOLTIP_CREATE_NEW_PLAYER =
      "Create a new player to add to the game";
  private static final String TOOLTIP_SAVE_PLAYER = "Save the selected player for future games";
  private static final String TOOLTIP_REMOVE_PLAYER =
      "Remove the selected player from the current game";
  private static final String TOOLTIP_ADD_SAVED_PLAYER =
      "Add selected saved player to the current game";
  private static final String STATUS_MAX_PLAYERS_REACHED = "Maximum " + MAX_PLAYERS
      + " players reached";
  private static final String STATUS_ADD_PLAYERS_TO_BEGIN = "Add players to begin";
  private static final String STATUS_PLAYERS_ADDED_FORMAT = "%d player%s added";
  private static final String STATUS_NO_SAVED_PLAYERS = "No saved players found";
  private static final String STATUS_CANNOT_ADD_MAX_REACHED =
      "Cannot add more players (maximum reached)";
  private static final String STATUS_SELECT_SAVED_TO_ADD = "Select a player to add to the game";
  private static final String ICON_COLUMN_TITLE = "Icon";
  private static final String NAME_COLUMN_TITLE = "Name";
  private static final String PIECE_COLUMN_TITLE = "Piece";
  private static final String DEFAULT_PIECE_TYPE = "default";
  private static final String ICON_RESOURCE_PATH_FORMAT =
      "/edu/ntnu/idi/idatt/view/resources/icons/%s.png";
  private static final String CONFIRM_REMOVAL_TITLE = "Remove Player";
  private static final String CONFIRM_REMOVAL_MESSAGE_FORMAT =
      "Are you sure you want to remove player: %s?";


  private final TabPane playerTabs;
  private final TableView<Player> currentPlayerTableView;
  private final TableView<Player> savedPlayerTableView;
  private final ObservableList<Player> currentPlayersObservableList =
      FXCollections.observableArrayList();
  private final ObservableList<Player> savedPlayersObservableList =
      FXCollections.observableArrayList();

  private final Button addPlayerButton;
  private final Button savePlayerButton;
  private final Button removePlayerButton;
  private final Button addSavedPlayerButton;
  private final Label statusLabel;
  private final PauseTransition statusClearTimer;

  private final HBox currentPlayersControls;
  private final HBox savedPlayersControls;
  private final BorderPane controlsContainer;


  /**
   * Constructs the PlayerManagementPanel.
   *
   * <p>Initializes the UI components, including tabs for current and saved players,
   * table views to display player information, and control buttons.
   * It sets up event listeners for tab changes and button actions, and binds
   * button disable properties based on the application state.
   * </p>
   *
   * @param buttonFactory A factory for creating styled buttons used within this panel.
   *                      Must not be {@code null}.
   * @throws NullPointerException if {@code buttonFactory} is {@code null}.
   */
  public PlayerManagementPanel(final ButtonFactory buttonFactory) {
    if (buttonFactory == null) {
      throw new NullPointerException("ButtonFactory cannot be null.");
    }
    this.getStyleClass().add(STYLE_CLASS_PLAYER_PANEL);

    this.playerTabs = createPlayerTabs();
    this.currentPlayerTableView = createPlayerTableView(
        currentPlayersObservableList, PLACEHOLDER_NO_PLAYERS_ADDED
    );
    this.savedPlayerTableView = createPlayerTableView(
        savedPlayersObservableList, PLACEHOLDER_NO_SAVED_PLAYERS
    );

    this.addPlayerButton = buttonFactory.createSmallButton("+ Create New Player");
    this.savePlayerButton = buttonFactory.createSmallButton("Save Player");
    this.removePlayerButton = buttonFactory.createSmallButton("Remove Player");
    this.addSavedPlayerButton = buttonFactory.createSmallButton("Add Selected");
    setupButtonTooltips();
    styleButtons();

    this.statusLabel = createStatusLabel();
    this.statusClearTimer = createStatusClearTimer();

    this.currentPlayersControls = createControlsHbox(
        savePlayerButton, addPlayerButton, removePlayerButton
    );
    this.savedPlayersControls = createControlsHbox(addSavedPlayerButton);

    this.controlsContainer = new BorderPane();
    this.controlsContainer.setCenter(currentPlayersControls);

    final BorderPane bottomContainer = layoutBottomControlsAndStatus();

    setTop(playerTabs);
    setCenter(currentPlayerTableView);
    setBottom(bottomContainer);

    this.getStyleClass().add(STYLE_CLASS_PLAYER_MANAGEMENT_PANEL);

    setupTabChangeListener();
    bindButtonProperties();
    updateButtonStatesAndStatus();
  }

  private HBox createControlsHbox(final Node... children) {
    HBox hbox = new HBox(DEFAULT_SPACING, children);
    hbox.setAlignment(Pos.CENTER);
    return hbox;
  }

  private TabPane createPlayerTabs() {
    final TabPane tabs = new TabPane();
    final Tab currentPlayersTab = new Tab(TAB_TITLE_CURRENT_PLAYERS);
    currentPlayersTab.setClosable(false);
    final Tab savedPlayersTab = new Tab(TAB_TITLE_SAVED_PLAYERS);
    savedPlayersTab.setClosable(false);
    tabs.getTabs().addAll(currentPlayersTab, savedPlayersTab);
    return tabs;
  }

  private TableView<Player> createPlayerTableView(
      final ObservableList<Player> playersObservableList,
      final String placeholderText) {
    final TableView<Player> tableView = new TableView<>(playersObservableList);
    tableView.setPlaceholder(new Label(placeholderText));
    tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    final TableColumn<Player, Player> iconColumn = createIconColumn();
    final TableColumn<Player, String> nameColumn = createTextColumn(
        NAME_COLUMN_TITLE, "name"
    );
    final TableColumn<Player, String> pieceTypeColumn = createTextColumn(
        PIECE_COLUMN_TITLE, "pieceType"
    );

    nameColumn.getStyleClass().add(STYLE_CLASS_CENTERED_CELL);
    pieceTypeColumn.getStyleClass().add(STYLE_CLASS_CENTERED_CELL);

    iconColumn.setMaxWidth(ICON_COLUMN_WIDTH);
    iconColumn.setMinWidth(ICON_COLUMN_WIDTH);
    nameColumn.setPrefWidth(NAME_COLUMN_PREF_WIDTH);
    pieceTypeColumn.setPrefWidth(PIECE_COLUMN_PREF_WIDTH);


    tableView.getColumns().setAll(Arrays.asList(iconColumn, nameColumn, pieceTypeColumn));

    tableView.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> updateButtonStatesAndStatus()
    );
    return tableView;
  }

  private void setupButtonTooltips() {
    addPlayerButton.setTooltip(new Tooltip(TOOLTIP_CREATE_NEW_PLAYER));
    savePlayerButton.setTooltip(new Tooltip(TOOLTIP_SAVE_PLAYER));
    removePlayerButton.setTooltip(new Tooltip(TOOLTIP_REMOVE_PLAYER));
    addSavedPlayerButton.setTooltip(new Tooltip(TOOLTIP_ADD_SAVED_PLAYER));
  }

  private void styleButtons() {
    addPlayerButton.getStyleClass().add(STYLE_CLASS_BUTTON);
    savePlayerButton.getStyleClass().add(STYLE_CLASS_BUTTON);
    removePlayerButton.getStyleClass().add(STYLE_CLASS_BUTTON);
    addSavedPlayerButton.getStyleClass().add(STYLE_CLASS_BUTTON);
  }

  private Label createStatusLabel() {
    final Label label = new Label(""); // Initial text is empty
    label.getStyleClass().add(STYLE_CLASS_STATUS_LABEL);
    return label;
  }

  private PauseTransition createStatusClearTimer() {
    final PauseTransition timer = new PauseTransition(STATUS_MESSAGE_DURATION);
    timer.setOnFinished(e -> {
      statusLabel.setText("");
      updateButtonStatesAndStatus(); // Re-evaluate default status after clearing
    });
    return timer;
  }

  private BorderPane layoutBottomControlsAndStatus() {
    final HBox statusBox = new HBox(statusLabel);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.setPadding(new Insets(STATUS_BOX_PADDING_TOP, 0, 0, 0));
    statusBox.getStyleClass().add(STYLE_CLASS_STATUS_BOX);

    final BorderPane bottomPane = new BorderPane();
    bottomPane.setTop(controlsContainer);
    bottomPane.setBottom(statusBox);
    bottomPane.setPadding(new Insets(BOTTOM_CONTAINER_PADDING_TOP, 0, 0, 0));
    return bottomPane;
  }

  private void setupTabChangeListener() {
    playerTabs.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldTab, newTab) -> {
          if (newTab == getCurrentPlayersTab()) {
            controlsContainer.setCenter(currentPlayersControls);
            setCenter(currentPlayerTableView);
          } else if (newTab == getSavedPlayersTab()) {
            controlsContainer.setCenter(savedPlayersControls);
            setCenter(savedPlayerTableView);
          }
          statusClearTimer.stop();
          statusLabel.setText("");
          updateButtonStatesAndStatus();
        });
  }

  private void bindButtonProperties() {
    addSavedPlayerButton.disableProperty().bind(
        Bindings.or(
            savedPlayerTableView.getSelectionModel().selectedItemProperty().isNull(),
            Bindings.createBooleanBinding(this::isPlayerLimitReached,
                currentPlayersObservableList)
        )
    );
    removePlayerButton.disableProperty().bind(
        currentPlayerTableView.getSelectionModel().selectedItemProperty().isNull()
    );
    savePlayerButton.disableProperty().bind(
        currentPlayerTableView.getSelectionModel().selectedItemProperty().isNull()
    );
    addPlayerButton.disableProperty().bind(
        Bindings.createBooleanBinding(this::isPlayerLimitReached, currentPlayersObservableList)
    );
  }

  private TableColumn<Player, String> createTextColumn(final String title,
      final String propertyName) {
    final TableColumn<Player, String> column = new TableColumn<>(title);
    column.setCellValueFactory(new PropertyValueFactory<>(propertyName));

    column.setCellFactory(tc -> {
      // Standard way to create a cell that just displays text and centers it
      TableCell<Player, String> cell = new TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty ? null : item);
        }
      };
      cell.setAlignment(Pos.CENTER);
      return cell;
    });
    return column;
  }

  private static class PlayerIconCell extends TableCell<Player, Player> {
    private final ImageView iconView = new ImageView();
    private final HBox graphicContainer = new HBox(iconView);

    PlayerIconCell() {
      iconView.setFitHeight(ICON_VIEW_SIZE);
      iconView.setFitWidth(ICON_VIEW_SIZE);
      iconView.setPreserveRatio(true);
      graphicContainer.setAlignment(Pos.CENTER);
      setAlignment(Pos.CENTER);
    }

    @Override
    protected void updateItem(final Player player, final boolean empty) {
      super.updateItem(player, empty);
      if (empty || player == null) {
        setGraphic(null);
      } else {
        iconView.setImage(loadPlayerIconStatic(player.getPieceType()));
        setGraphic(graphicContainer);
      }
    }
  }

  private TableColumn<Player, Player> createIconColumn() {
    final TableColumn<Player, Player> iconColumn = new TableColumn<>(ICON_COLUMN_TITLE);
    iconColumn.getStyleClass().add(STYLE_CLASS_FIRST_COLUMN);
    iconColumn.setCellValueFactory(cellDataFeatures ->
        new SimpleObjectProperty<>(cellDataFeatures.getValue()));
    iconColumn.setCellFactory(param -> new PlayerIconCell());
    return iconColumn;
  }

  private void updateButtonStatesAndStatus() {
    statusClearTimer.stop();

    final Tab selectedTab = playerTabs.getSelectionModel().getSelectedItem();
    final boolean isCurrentPlayersTabActive = (selectedTab == getCurrentPlayersTab());
    final boolean canAddMorePlayers = !isPlayerLimitReached();

    if (statusLabel.getText().isEmpty()
        || statusClearTimer.getStatus() != javafx.animation.Animation.Status.RUNNING) {
      updateDefaultStatusMessage(isCurrentPlayersTabActive, canAddMorePlayers);
    }
  }

  private void updateDefaultStatusMessage(final boolean isCurrentPlayersTabActive,
      final boolean canAddMorePlayers) {
    if (isCurrentPlayersTabActive) {
      if (!canAddMorePlayers) {
        statusLabel.setText(STATUS_MAX_PLAYERS_REACHED);
        statusLabel.getStyleClass().setAll(STYLE_CLASS_STATUS_LABEL, STYLE_CLASS_STATUS_WARNING);
      } else if (currentPlayersObservableList.isEmpty()) {
        statusLabel.setText(STATUS_ADD_PLAYERS_TO_BEGIN);
        statusLabel.getStyleClass().setAll(STYLE_CLASS_STATUS_LABEL, STYLE_CLASS_STATUS_INFO);
      } else {
        final int count = currentPlayersObservableList.size();
        final String pluralS = (count != 1 ? "s" : "");
        statusLabel.setText(String.format(STATUS_PLAYERS_ADDED_FORMAT, count, pluralS));
        statusLabel.getStyleClass().setAll(STYLE_CLASS_STATUS_LABEL, STYLE_CLASS_STATUS_INFO);
      }
    } else { // Saved Players Tab is active
      if (savedPlayersObservableList.isEmpty()) {
        statusLabel.setText(STATUS_NO_SAVED_PLAYERS);
        statusLabel.getStyleClass().setAll(STYLE_CLASS_STATUS_LABEL, STYLE_CLASS_STATUS_INFO);
      } else if (!canAddMorePlayers) {
        statusLabel.setText(STATUS_CANNOT_ADD_MAX_REACHED);
        statusLabel.getStyleClass().setAll(STYLE_CLASS_STATUS_LABEL, STYLE_CLASS_STATUS_WARNING);
      } else {
        statusLabel.setText(STATUS_SELECT_SAVED_TO_ADD);
        statusLabel.getStyleClass().setAll(STYLE_CLASS_STATUS_LABEL, STYLE_CLASS_STATUS_INFO);
      }
    }
  }

  private static Image loadPlayerIconStatic(final String pieceType) {
    final String effectivePieceType = (pieceType == null || pieceType.isBlank())
        ? DEFAULT_PIECE_TYPE : pieceType.toLowerCase();
    try {
      final String iconPath = String.format(ICON_RESOURCE_PATH_FORMAT, effectivePieceType);
      return ResourceLoader.loadImage(iconPath);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.INFO, "Could not load icon for piece type: {0}. Attempting default.",
          effectivePieceType);
      try {
        final String defaultIconPath = String.format(ICON_RESOURCE_PATH_FORMAT,
            DEFAULT_PIECE_TYPE);
        return ResourceLoader.loadImage(defaultIconPath);
      } catch (BoardGameResourceException ex) {
        LOGGER.log(Level.WARNING, "Could not load default player icon.", ex);
        return null;
      }
    }
  }

  /**
   * Displays a confirmation dialog to the user before removing a player.
   *
   * @param player The {@link Player} to be potentially removed. Must not be {@code null}.
   * @return {@code true} if the user confirms the removal, {@code false} otherwise.
   * @throws NullPointerException if {@code player} is {@code null}.
   */
  public boolean confirmPlayerRemoval(final Player player) {
    if (player == null) {
      throw new NullPointerException("Player to confirm removal for cannot be null.");
    }
    return AlertHelper.showConfirmationAlert(CONFIRM_REMOVAL_TITLE,
        String.format(CONFIRM_REMOVAL_MESSAGE_FORMAT, player.getName()));
  }

  /**
   * Switches the UI to display the "Current Players" tab.
   *
   * <p>After calling this method, the controls and table view associated with
   * current players become visible.
   * </p>
   */
  public void showCurrentPlayers() {
    playerTabs.getSelectionModel().select(getCurrentPlayersTab());
  }

  private void updatePlayerListInternal(
      final ObservableList<Player> internalList,
      final List<Player> newList
  ) {
    internalList.setAll(
        newList == null ? FXCollections.emptyObservableList() : newList
    );
    updateButtonStatesAndStatus();
  }

  /**
   * Updates the "Saved Players" table with a new list of players.
   *
   * <p>Delegates to {@link #updatePlayerListInternal(ObservableList, List)}.
   * If {@code newSavedPlayers} is {@code null}, the saved players list is cleared.
   * </p>
   *
   * @param newSavedPlayers the new list of saved {@link Player}s, or {@code null}
   */
  public void updateSavedPlayersList(final List<Player> newSavedPlayers) {
    updatePlayerListInternal(savedPlayersObservableList, newSavedPlayers);
  }

  /**
   * Updates the "Current Players" table with a new list of players.
   *
   * <p>Delegates to {@link #updatePlayerListInternal(ObservableList, List)}.
   * If {@code newCurrentPlayers} is {@code null}, the current players list is cleared.
   * </p>
   *
   * @param newCurrentPlayers the new list of current {@link Player}s, or {@code null}
   */
  public void updateCurrentPlayersList(final List<Player> newCurrentPlayers) {
    updatePlayerListInternal(currentPlayersObservableList, newCurrentPlayers);
  }


  public boolean isPlayerLimitReached() {
    return currentPlayersObservableList.size() >= MAX_PLAYERS;
  }

  public Player getSelectedSavedPlayer() {
    return savedPlayerTableView.getSelectionModel().getSelectedItem();
  }

  public Player getSelectedCurrentPlayer() {
    return currentPlayerTableView.getSelectionModel().getSelectedItem();
  }



  /**
   * Gets the "Add New Player" button.
   *
   * @return The "Add New Player" button.
   */
  public Button getAddPlayerButton() {
    return addPlayerButton;
  }

  /**
   * Gets the "Save Player" button.
   *
   * @return The "Save Player" button.
   */
  public Button getSavePlayerButton() {
    return savePlayerButton;
  }

  /**
   * Gets the "Remove Player" button.
   *
   * @return The "Remove Player" button.
   */
  public Button getRemovePlayerButton() {
    return removePlayerButton;
  }

  /**
   * Gets the "Add Selected Saved Player" button.
   *
   * @return The "Add Selected Saved Player" button.
   */
  public Button getAddSavedPlayerButton() {
    return addSavedPlayerButton;
  }

  /**
   * Gets the {@link TabPane} used for switching between player views.
   *
   * @return The player {@link TabPane}.
   */
  public TabPane getPlayerTabs() {
    return playerTabs;
  }

  /**
   * Gets the {@link Tab} for "Current Players".
   * Assumes current players tab is always the first.
   *
   * @return The "Current Players" tab.
   */
  public Tab getCurrentPlayersTab() {
    if (!playerTabs.getTabs().isEmpty()) {
      return playerTabs.getTabs().getFirst();
    }
    LOGGER.log(Level.SEVERE, "Current Players tab not found or tabs not initialized.");
    return null;
  }

  /**
   * Gets the {@link Tab} for "Saved Players".
   * Assumes saved players tab is always the second.
   *
   * @return The "Saved Players" tab.
   */
  public Tab getSavedPlayersTab() {
    if (playerTabs.getTabs().size() > 1) {
      return playerTabs.getTabs().get(1);
    }
    LOGGER.log(Level.SEVERE, "Saved Players tab not found or tabs not initialized sufficiently.");
    return null;
  }
}