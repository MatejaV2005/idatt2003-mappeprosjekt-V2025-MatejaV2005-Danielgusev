package edu.ntnu.idi.idatt.view.components.boardgame;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Panel displaying players and indicating the current turn.
 */
public class CurrentPlayerPanel extends VBox {

  private static final Logger LOGGER =
      Logger.getLogger(CurrentPlayerPanel.class.getName());
  private static final double ICON_SIZE = 32d;
  private static final int PANEL_SPACING = 10;
  private static final int CONTAINER_SPACING = 5;
  private static final Insets PANEL_PADDING = new Insets(15);
  private static final String ICON_DIR =
      "/edu/ntnu/idi/idatt/view/resources/icons/";
  private static final String DEFAULT_ICON = "default.png";

  private final Map<Player, HBox> playerRows = new HashMap<>();
  private HBox highlightedRow;
  private final VBox rowsContainer;

  /**
   * Creates the player panel.
   */
  public CurrentPlayerPanel() {
    super(PANEL_SPACING);
    setPadding(PANEL_PADDING);
    getStyleClass().add("current-player-panel");

    Label title = new Label("Players");
    title.getStyleClass().add("panel-title");
    title.setMaxWidth(Double.MAX_VALUE);
    title.setAlignment(Pos.CENTER);

    rowsContainer = new VBox(CONTAINER_SPACING);
    rowsContainer.getStyleClass().add("player-list-container");

    getChildren().addAll(title, rowsContainer);
  }

  /**
   * Sets up player entries, clearing existing rows.
   *
   * @param players list of players; if null or empty, no rows are shown
   */
  public void initialize(List<Player> players) {
    rowsContainer.getChildren().clear();
    playerRows.clear();
    highlightedRow = null;

    if (players == null || players.isEmpty()) {
      LOGGER.warning("No players to display in CurrentPlayerPanel.");
      return;
    }

    int index = 1;
    for (Player player : players) {
      HBox row = createRow(player, index++);
      playerRows.put(player, row);
      rowsContainer.getChildren().add(row);
    }
    LOGGER.info(() -> String.format(
        "Initialized CurrentPlayerPanel with %d players.", players.size()));
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private HBox createRow(Player player, int number) {
    HBox row = new HBox(PANEL_SPACING);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("player-row");

    Node icon = createIcon(player.getPieceType());
    Label numLabel = new Label(number + ".");
    numLabel.getStyleClass().add("player-number");

    Label nameLabel = new Label(player.getName());
    nameLabel.getStyleClass().add("player-name-display");
    HBox.setHgrow(nameLabel, Priority.ALWAYS);

    row.getChildren().setAll(icon, numLabel, nameLabel);
    return row;
  }

  private Node createIcon(String type) {
    Image image = loadIcon(type);
    if (image != null) {
      ImageView view = new ImageView(image);
      view.setFitWidth(ICON_SIZE);
      view.setFitHeight(ICON_SIZE);
      view.setPreserveRatio(true);
      view.getStyleClass().add("player-icon");
      return view;
    }
    Circle circle = new Circle(ICON_SIZE / 2, Color.DARKGRAY);
    circle.setStroke(Color.BLACK);
    return circle;
  }

  private Image loadIcon(String type) {
    String filename = (type == null || type.isBlank())
        ? DEFAULT_ICON
        : type.toLowerCase() + ".png";
    String path = ICON_DIR + filename;

    try {
      Image img = ResourceLoader.loadImage(path);
      if (img.isError()) {
        throw new BoardGameResourceException("Icon load failed: " + path);
      }
      return img;
    } catch (Exception e) {
      LOGGER.log(Level.WARNING,
          String.format("Failed to load icon '%s'. Using fallback.", path), e);
      return null;
    }
  }

  /**
   * Highlights the row for the active player.
   *
   * @param player current player; null clears highlight
   */
  public void updateCurrentPlayerHighlight(Player player) {
    if (highlightedRow != null) {
      highlightedRow.getStyleClass().remove("current-player-row");
      highlightedRow = null;
    }
    if (player == null) {
      LOGGER.warning("Cleared highlight: no current player.");
      return;
    }
    HBox row = playerRows.get(player);
    if (row == null) {
      LOGGER.warning("No row found for player: " + player.getName());
      return;
    }
    row.getStyleClass().add("current-player-row");
    highlightedRow = row;
    LOGGER.info(() -> "Highlighted current player: " + player.getName());
  }

  /**
   * Adds a player entry if not already present.
   *
   * @param player player to add; ignored if null or duplicate
   */
  public void addPlayerEntry(Player player) {
    if (player == null || playerRows.containsKey(player)) {
      LOGGER.log(Level.WARNING,
          "Invalid or duplicate player entry: {0}",
          player);
      return;
    }
    HBox row = createRow(player, rowsContainer.getChildren().size() + 1);
    playerRows.put(player, row);
    rowsContainer.getChildren().add(row);
    LOGGER.info(() -> "Added player: " + player.getName());
  }
}
