package edu.ntnu.idi.idatt.view.components.gameSelection;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
 */
public class PlayerManagementPanel extends BorderPane {
  private static final Logger LOGGER = Logger.getLogger(PlayerManagementPanel.class.getName());

  private final TabPane playerTabs;
  private final ListView<Player> playerListView; // TODO: Replace String with actual Player later
  private final ListView<Player> savedPlayerListView;
  private final Button addPlayerButton;
  private final Button savePlayerButton;

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

    addPlayerButton.getStyleClass().add(".button");
    savePlayerButton.getStyleClass().add(".button");


    HBox controlsBox = new HBox(10, savePlayerButton, addPlayerButton);
    controlsBox.setAlignment(Pos.CENTER);

    setTop(playerTabs);
    setCenter(playerListView);
    setBottom(controlsBox);

    this.getStyleClass().add("player-management-panel");
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

  // Methods to manipulate the player list
  public void addPlayer(Player player) {
    playerListView.getItems().add(player);
  }

  public void removeSelectedPlayer() {
    int selectedIndex = playerListView.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {
      playerListView.getItems().remove(selectedIndex);
    }
  }
}
