package edu.ntnu.idi.idatt.view.components.boardgame;

import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * A JavaFX {@link VBox} that presents the current game state information.
 *
 * <p>This panel displays four types of messages:
 * <ul>
 *   <li><b>Turn info:</b> whose turn it is, via {@link #updateTurnInfo(Player)}</li>
 *   <li><b>Dice info:</b> result of the latest roll, via {@link #updateDiceInfo(Player, Dice)}</li>
 *   <li><b>Move info:</b> normal movement between tiles,
 *   via {@link #updateMoveInfo(Player, Tile, Tile)}</li>
 *   <li><b>Action info:</b> special tile actions (snakes, ladders, etc.),
 *   via {@link #updateActionInfo(Player, String, Tile)}</li>
 * </ul>
 * Each update call replaces the previous message rather than accumulating.
 * All UI updates are executed on the JavaFX Application Thread.
 * </p>
 */
public class GameInfoPanel extends VBox {

  private static final Logger LOGGER = Logger.getLogger(GameInfoPanel.class.getName());
  private static final double SPACING = 12d;
  private static final Insets PADDING = new Insets(20);
  private static final double FIXED_WIDTH = 300d;
  private static final double FIXED_HEIGHT = 400d;

  private final Label titleLabel;
  private final Label turnInfoLabel;
  private final Label diceInfoLabel;
  private final Label moveActionInfoLabel;

  /**
   * Constructs a new {@code GameInfoPanel}, setting spacing, padding,
   * fixed dimensions, and initializing all four information labels.
   */
  public GameInfoPanel() {
    super(SPACING);
    setPadding(PADDING);
    getStyleClass().add("game-info-panel");

    setMinWidth(FIXED_WIDTH);
    setPrefWidth(FIXED_WIDTH);
    setMaxWidth(FIXED_WIDTH);

    setMinHeight(FIXED_HEIGHT);
    setPrefHeight(FIXED_HEIGHT);

    titleLabel = createStyledLabel("Game Information", "panel-title");
    turnInfoLabel = createStyledLabel("", "info-label-turn");
    diceInfoLabel = createStyledLabel("", "info-label-dice");
    moveActionInfoLabel = createStyledLabel("", "info-label-move");

    getChildren().addAll(
        titleLabel,
        turnInfoLabel,
        diceInfoLabel,
        moveActionInfoLabel
    );
    applyBolderTextStyle();
  }

  /**
   * Updates the panel to display whose turn it currently is.
   *
   * <p>If {@code currentPlayer} is {@code null}, displays "Game Ready".
   * Otherwise shows an arrow and the player’s name.
   * </p>
   *
   * @param currentPlayer the player whose turn it is, or {@code null}
   */
  public void updateTurnInfo(Player currentPlayer) {
    Platform.runLater(() -> {
      if (currentPlayer == null) {
        turnInfoLabel.setText("Game Ready");
      } else {
        turnInfoLabel.setText("👉 It's " + currentPlayer.getName() + "'s turn");
        LOGGER.fine(() -> "Turn info updated for " + currentPlayer.getName());
      }
    });
  }

  /**
   * Updates the panel to show the result of a dice roll.
   *
   * <p>If either {@code player} or {@code dice} is {@code null}, clears the dice info label.
   * Otherwise, reads the two die values and total from {@code dice} and formats them.
   * </p>
   *
   * @param player the player who rolled the dice
   * @param dice   the {@link Dice} object containing the two die values
   */
  public void updateDiceInfo(Player player, Dice dice) {
    Platform.runLater(() -> {
      if (player == null || dice == null) {
        diceInfoLabel.setText("");
        return;
      }
      try {
        int d1 = dice.getDieValue(0);
        int d2 = dice.getDieValue(1);
        int total = dice.getTotalDiceValue();
        diceInfoLabel.setText(
            String.format("🎲 %s rolled %d + %d = %d", player.getName(), d1, d2, total)
        );
        LOGGER.fine(() -> "Dice info updated for " + player.getName());
      } catch (Exception e) {
        LOGGER.log(
            Level.WARNING,
            String.format("Could not update dice info for %s", player.getName()),
            e
        );
        diceInfoLabel.setText("🎲 " + player.getName() + " rolled.");
      }
    });
  }

  /**
   * Updates the panel to show a normal movement between two tiles.
   *
   * <p>If any parameter is {@code null}, the movement label is cleared.
   * Otherwise displays a right-arrow, player name, source tile ID, and destination tile ID.
   * </p>
   *
   * @param player   the player who moved
   * @param fromTile the origin {@link Tile}
   * @param toTile   the destination {@link Tile}
   */
  public void updateMoveInfo(Player player, Tile fromTile, Tile toTile) {
    Platform.runLater(() -> {
      if (player == null || fromTile == null || toTile == null) {
        moveActionInfoLabel.setText("");
        return;
      }
      moveActionInfoLabel.setText(
          String.format(
              "⏩ %s moved from tile %d to tile %d",
              player.getName(),
              fromTile.getTileId(),
              toTile.getTileId()
          )
      );
      LOGGER.fine(() -> "Move info updated for " + player.getName());
    });
  }

  /**
   * Updates the panel for a special tile action (e.g., snake or ladder).
   *
   * <p>If {@code player} or {@code actionDescription} is {@code null}, clears the label.
   * Otherwise chooses an emoji icon based on keywords in {@code actionDescription},
   * then displays either just the action or the full tile movement depending on
   * the player's current tile vs. {@code destinationTile}.
   * </p>
   *
   * @param player            the player on whom the action occurred
   * @param actionDescription text describing the action (e.g., "ladder up")
   * @param destinationTile   the target {@link Tile}, or {@code null} to use current tile
   */
  public void updateActionInfo(Player player,
      String actionDescription,
      Tile destinationTile) {
    Platform.runLater(() -> {
      if (player == null || actionDescription == null) {
        moveActionInfoLabel.setText("");
        return;
      }

      String icon;
      String lower = actionDescription.toLowerCase();
      if (lower.contains("ladder")) {
        icon = "🪜";
      } else if (lower.contains("snake")) {
        icon = "🐍";
      } else {
        icon = "⚡";
      }

      int currentId = player.getCurrentTile() != null
          ? player.getCurrentTile().getTileId() : 0;
      int destId = destinationTile != null
          ? destinationTile.getTileId() : currentId;

      String message;
      if (currentId != destId) {
        message = String.format(
            "%s %s on tile %d: %s to tile %d!",
            icon, player.getName(), currentId,
            actionDescription, destId
        );
      } else {
        message = String.format("%s %s: %s",
            icon, player.getName(), actionDescription);
      }

      moveActionInfoLabel.setText(message);
      LOGGER.fine(() -> "Action info updated for " + player.getName());
    });
  }

  /**
   * Updates the panel to announce the game winner.
   *
   * <p>If {@code winner} is {@code null}, displays a fallback "Game Over" message.
   * Otherwise shows a trophy emoji and the winner’s name, and clears other labels.
   * </p>
   *
   * @param winner the winning {@link Player}, or {@code null}
   */
  public void showWinner(Player winner) {
    Platform.runLater(() -> {
      if (winner == null) {
        titleLabel.setText("Game Over - No Winner?");
      } else {
        titleLabel.setText("🏆 " + winner.getName() + " Wins!");
      }
      diceInfoLabel.setText("");
      moveActionInfoLabel.setText("");
      LOGGER.info(() -> "Winner displayed: "
          + (winner != null ? winner.getName() : "none"));
    });
  }


  /**
   * Logs a custom event message at INFO level.
   *
   * <p>Provided for external callers that need to send arbitrary text to this panel’s logger.
   * </p>
   *
   * @param event the message to log; if {@code null}, logs the literal "null"
   */
  public void logEvent(String event) {
    LOGGER.info(event);
  }


  private Label createStyledLabel(String initialText, String styleClass) {
    Label label = new Label(initialText);
    label.getStyleClass().add(styleClass);
    label.setWrapText(true);
    label.setMaxWidth(FIXED_WIDTH - 40);
    label.setTextAlignment(TextAlignment.LEFT);
    return label;
  }

  private void applyBolderTextStyle() {
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 800;");
    turnInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    diceInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    moveActionInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
  }
}
