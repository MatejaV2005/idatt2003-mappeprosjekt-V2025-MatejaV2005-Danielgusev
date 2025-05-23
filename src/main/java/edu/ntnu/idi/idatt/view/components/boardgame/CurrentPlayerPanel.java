package edu.ntnu.idi.idatt.view.components.boardgame;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ResourceLoader;
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
 * A vertical panel that lists all {@link Player}s and highlights the one
 * whose turn it currently is.
 *
 * <p>Icons are loaded via {@link ResourceLoader}.  If loading fails, a colored circle
 * is shown instead.
 * </p>
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
   * Constructs an empty {@code CurrentPlayerPanel}, ready to have
   * players added via {@link #initialize(List)} or {@link #addPlayerEntry(Player)}.
   *
   * <p>Sets up padding, spacing, and a title label.
   * </p>
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
   * Clears any existing entries and populates this panel with the given list of players.
   *
   * <p>If {@code players} is {@code null} or empty, this will simply clear the display.
   * </p>
   *
   * @param players the list of players to display
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

  /**
   * Highlights {@code player}'s row to indicate the active turn.
   *
   * <p>Any previously highlighted row is cleared first.  If {@code player} is
   * {@code null} or not in the panel, the highlight is simply removed.
   * </p>
   *
   * @param player the player to highlight, or {@code null} to clear
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
   * Adds a new player entry at the bottom if not already present.
   *
   * <p>Duplicate or {@code null} players are ignored.
   * </p>
   *
   * @param player the player to add
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

  // --- Private helpers below ---

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
}
