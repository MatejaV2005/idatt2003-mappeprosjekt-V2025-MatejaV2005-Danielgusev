package edu.ntnu.idi.idatt.view.components.boardGame;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color; // For fallback icon color
import javafx.scene.shape.Circle; // For fallback icon shape

/**
 * A JavaFX component that displays a list of players participating in the game.
 * It highlights the player whose turn it currently is.
 * This panel is typically placed on the side of the main game board view.
 */
public class CurrentPlayerPanel extends VBox {
  private static final Logger LOGGER = Logger.getLogger(CurrentPlayerPanel.class.getName());
  private static final double ICON_SIZE = 32.0; // Size for the player icons

  private final Map<Player, HBox> playerRowMap = new HashMap<>();
  private final ResourceLoader resourceLoader = new ResourceLoader();
  private HBox currentlyHighlightedRow = null;
  private final VBox playerListContainer; // VBox to hold the player rows

  /**
   * Constructs a new CurrentPlayerPanel.
   */
  public CurrentPlayerPanel() {
    super(10); // Spacing between title and player list
    this.setPadding(new Insets(15));
    this.getStyleClass().add("current-player-panel");
    // Optional: Set a preferred width if needed, or let it grow
    // this.setPrefWidth(200);

    Label titleLabel = new Label("Players");
    titleLabel.getStyleClass().add("panel-title"); // Add a CSS class for styling
    titleLabel.setMaxWidth(Double.MAX_VALUE);
    titleLabel.setAlignment(Pos.CENTER);

    this.playerListContainer = new VBox(5); // Spacing between player rows
    this.playerListContainer.getStyleClass().add("player-list-container");

    this.getChildren().addAll(titleLabel, playerListContainer);
  }

  /**
   * Initializes the panel with the list of players.
   * Clears any previous player entries and creates new rows for each player.
   *
   * @param players The list of players in the game.
   */
  public void initialize(List<Player> players) {
    playerListContainer.getChildren().clear();
    playerRowMap.clear();
    currentlyHighlightedRow = null;

    if (players == null || players.isEmpty()) {
      LOGGER.warning("Initializing CurrentPlayerPanel with no players.");
      // Optionally display a message like "No players added yet."
      return;
    }

    int playerNumber = 1;
    for (Player player : players) {
      HBox playerRow = createPlayerRow(player, playerNumber++);
      playerRowMap.put(player, playerRow);
      playerListContainer.getChildren().add(playerRow);
    }
    LOGGER.info("CurrentPlayerPanel initialized with " + players.size() + " players.");
  }

  /**
   * Creates a visual row (HBox) for a single player.
   *
   * @param player The player to create the row for.
   * @param number The player's number (e.g., 1, 2, 3, 4).
   * @return The HBox representing the player row.
   */
  private HBox createPlayerRow(Player player, int number) {
    HBox row = new HBox(10); // Spacing between elements in the row
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("player-row");

    // 1. Player Icon
    ImageView iconView = createPlayerIcon(player.getPieceType());

    // 2. Player Number/Ordinal (like in the screenshot)
    Label numberLabel = new Label(number + ".");
    numberLabel.getStyleClass().add("player-number"); // Add CSS class

    // 3. Player Name
    Label nameLabel = new Label(player.getName());
    nameLabel.getStyleClass().add("player-name-display"); // Add CSS class
    HBox.setHgrow(nameLabel, Priority.ALWAYS); // Allow name to take available space

    // Add elements to the row
    row.getChildren().addAll(iconView, numberLabel, nameLabel);

    return row;
  }

  /**
   * Creates an ImageView for the player's icon based on their piece type.
   * Provides a fallback visual if the image cannot be loaded.
   *
   * @param pieceType The piece type string (e.g., "Hat", "Car").
   * @return An ImageView for the icon.
   */
  private ImageView createPlayerIcon(String pieceType) {
    ImageView iconView = new ImageView();
    iconView.setFitHeight(ICON_SIZE);
    iconView.setFitWidth(ICON_SIZE);
    iconView.setPreserveRatio(true);
    iconView.getStyleClass().add("player-icon"); // Add CSS class

    Image iconImage = null;
    if (pieceType != null && !pieceType.isBlank()) {
      try {
        String iconPath = "/edu/ntnu/idi/idatt/view/resources/icons/" + pieceType.toLowerCase() + ".png";
        iconImage = resourceLoader.loadImage(iconPath);
      } catch (BoardGameResourceException e) {
        LOGGER.log(Level.WARNING, "Could not load icon for piece type: " + pieceType, e);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Unexpected error loading icon for piece type: " + pieceType, e);
      }
    } else {
      LOGGER.warning("Player piece type is null or blank.");
    }


    if (iconImage != null && !iconImage.isError()) {
      iconView.setImage(iconImage);
    } else {
      LOGGER.warning("Using fallback icon for piece type: " + pieceType);
      try {
        Image defaultIcon = resourceLoader.loadImage("/edu/ntnu/idi/idatt/view/resources/icons/default.png");
        if (defaultIcon != null && !defaultIcon.isError()) {
          iconView.setImage(defaultIcon);
        } else {
          throw new BoardGameResourceException("Default icon failed to load.");
        }
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to load default icon, using colored circle.", e);
        Circle fallbackCircle = new Circle(ICON_SIZE / 2, Color.DARKGRAY); // Example color
        fallbackCircle.setStroke(Color.BLACK);
        iconView.setImage(null);
      }
    }
    return iconView;
  }


  /**
   * Updates the visual highlight to indicate which player's turn it is.
   * Removes highlight from the previous player and applies it to the current one.
   *
   * @param currentPlayer The player whose turn it is now.
   */
  public void updateCurrentPlayerHighlight(Player currentPlayer) {
    if (currentPlayer == null) {
      LOGGER.warning("Cannot highlight null player. Clearing any existing highlight.");
      if (currentlyHighlightedRow != null) {
        currentlyHighlightedRow.getStyleClass().remove("current-player-row");
        LOGGER.fine("Removed highlight from previously highlighted row (player was null).");
        currentlyHighlightedRow = null;
      }
      return;
    }

    LOGGER.info("CurrentPlayerPanel.updateCurrentPlayerHighlight: Attempting to highlight player - Name: " + currentPlayer.getName() + " (Object: " + currentPlayer.toString() + ", HashCode: " + currentPlayer.hashCode() + ")");

    HBox rowToDeselect = currentlyHighlightedRow;

    HBox newRowToHighlight = playerRowMap.get(currentPlayer);

    if (newRowToHighlight == null) {
      LOGGER.warning("Could not find row for player: " + currentPlayer.getName() + ". Player object used for lookup: " + currentPlayer.toString());
      if (rowToDeselect != null) {
        rowToDeselect.getStyleClass().remove("current-player-row");
        LOGGER.fine("Removed highlight from previously highlighted row (new row not found).");
      }
      currentlyHighlightedRow = null; // No new row to be the current one
    } else {
      if (rowToDeselect != null && rowToDeselect != newRowToHighlight) {
        rowToDeselect.getStyleClass().remove("current-player-row");
        LOGGER.fine("Removed highlight from previously highlighted row: " + (rowToDeselect.getChildren().get(2) instanceof Label ? ((Label)rowToDeselect.getChildren().get(2)).getText() : "Unknown"));
      }

      if (!newRowToHighlight.getStyleClass().contains("current-player-row")) {
        newRowToHighlight.getStyleClass().add("current-player-row");
      }
      currentlyHighlightedRow = newRowToHighlight;
      LOGGER.info("Successfully highlighted current player: " + currentPlayer.getName());
    }
  }

  /**
   * Adds a single player entry to the panel.
   * Useful if players can be added after initial setup.
   *
   * @param player The player to add.
   */
  public void addPlayerEntry(Player player) {
    if (player == null || playerRowMap.containsKey(player)) {
      LOGGER.warning("Attempted to add null player or duplicate player entry: " + (player != null ? player.getName() : "null"));
      return;
    }
    int playerNumber = playerListContainer.getChildren().size() + 1;
    HBox playerRow = createPlayerRow(player, playerNumber);
    playerRowMap.put(player, playerRow);
    playerListContainer.getChildren().add(playerRow);
    LOGGER.info("Added player entry: " + player.getName());
  }

  /**
   * Removes a player entry from the panel.
   *
   * @param player The player to remove.
   */
  public void removePlayerEntry(Player player) {
    if (player == null) {
      LOGGER.warning("Attempted to remove null player entry.");
      return;
    }
    HBox rowToRemove = playerRowMap.remove(player);
    if (rowToRemove != null) {
      playerListContainer.getChildren().remove(rowToRemove);
      if (rowToRemove == currentlyHighlightedRow) {
        currentlyHighlightedRow = null;
      }
      LOGGER.info("Removed player entry: " + player.getName());
    } else {
      LOGGER.warning("Could not find player entry to remove for player: " + player.getName());
    }
  }

  /**
   * Updates the displayed information for a specific player,
   * for example, if their name or tile position changes (though position isn't shown here).
   * Currently only updates name if needed, can be expanded.
   *
   * @param player The player whose display needs updating.
   */
  public void updatePlayerInfo(Player player) {
    if (player == null) return;
    HBox row = playerRowMap.get(player);
    if (row != null && row.getChildren().size() >= 3) {

      Label nameLabel = (Label) row.getChildren().get(2);
      if (!nameLabel.getText().equals(player.getName())) {
        nameLabel.setText(player.getName());
        LOGGER.fine("Updated display name for player: " + player.getName());
      }
    }
  }
}