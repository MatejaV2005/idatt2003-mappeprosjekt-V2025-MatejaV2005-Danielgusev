package edu.ntnu.idi.idatt.view.components.boardGame;

import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * A JavaFX component designed to display information about the most recent
 * event or current state in the game, such as dice rolls, player movement,
 * triggered actions, or whose turn it is. Information typically overwrites
 * previous messages rather than accumulating as a log.
 */
public class GameInfoPanel extends VBox {

  private static final Logger LOGGER = Logger.getLogger(GameInfoPanel.class.getName());

  private final Label titleLabel;
  private final Label turnInfoLabel;
  private final Label diceInfoLabel;
  private final Label moveActionInfoLabel;

  /**
   * Constructs a new GameInfoPanel.
   * Initializes the labels used to display game state information.
   */
  public GameInfoPanel() {
    super(12); // Spacing between labels
    this.setPadding(new Insets(20));
    this.getStyleClass().add("game-info-panel");

    // Fixed size for the panel to prevent layout changes
    this.setMinWidth(300);
    this.setMaxWidth(300);
    this.setPrefWidth(300);

    // Fixed height to maintain layout stability
    this.setMinHeight(400);
    this.setPrefHeight(400);

    this.titleLabel = createStyledLabel("Game Information", "panel-title");
    this.turnInfoLabel = createStyledLabel("", "info-label-turn");
    this.diceInfoLabel = createStyledLabel("", "info-label-dice");
    this.moveActionInfoLabel = createStyledLabel("", "info-label-move");

    this.getChildren().addAll(titleLabel, turnInfoLabel, diceInfoLabel, moveActionInfoLabel);
    applyBolderTextStyle();
  }

  /**
   * Helper method to create and configure labels consistently.
   * @param initialText The initial text for the label.
   * @param styleClass The CSS style class to apply.
   * @return A configured Label.
   */
  private Label createStyledLabel(String initialText, String styleClass) {
    Label label = new Label(initialText);
    label.getStyleClass().add(styleClass);
    label.setWrapText(true);
    label.setMaxWidth(260); // Fixed width for label content
    label.setTextAlignment(TextAlignment.LEFT);
    return label;
  }

  /**
   * Applies bolder and larger text style to all labels.
   */
  private void applyBolderTextStyle() {
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 800;");
    turnInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    diceInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    moveActionInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
  }

  /**
   * Updates the dice information label with the result of the latest roll.
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
        diceInfoLabel.setText("🎲 " + player.getName() + " rolled " + d1 + " + " + d2 + " = " + total);
        LOGGER.fine("Dice info updated for " + player.getName());
      } catch (Exception e) {
        LOGGER.warning("Could not update dice info: " + e.getMessage());
        diceInfoLabel.setText("🎲 " + player.getName() + " rolled.");
      }
    });
  }

  /**
   * Updates the move/action label to show the result of a player's movement.
   * Now correctly displays source and destination tile IDs.
   */
  public void updateMoveInfo(Player player, Tile toTile) {
    Platform.runLater(() -> {
      if (player == null || toTile == null) {
        moveActionInfoLabel.setText("");
        return;
      }

      // Get the previous tile ID directly from the Player object
      int fromTileId = 0;  // Default start position
      Tile fromTile = player.getCurrentTile();
      if (fromTile != null) {
        fromTileId = fromTile.getTileId();
      }

      int destinationTileId = toTile.getTileId();

      // Only show movement info if the positions are different
      if (fromTileId != destinationTileId) {
        moveActionInfoLabel.setText("⏩ " + player.getName() + " moved from tile " +
            fromTileId + " to tile " + destinationTileId);
      } else {
        moveActionInfoLabel.setText("🔄 " + player.getName() + " remains at tile " + destinationTileId);
      }

      LOGGER.fine("Move info updated for " + player.getName());
    });
  }

  /**
   * Updates the move/action label to show the result of a special tile action.
   * Now includes more detailed information about the action type and effect.
   */
  public void updateActionInfo(Player player, String actionDescription, Tile destinationTile) {
    Platform.runLater(() -> {
      if (player == null || actionDescription == null) {
        moveActionInfoLabel.setText("");
        return;
      }

      String icon = actionDescription.toLowerCase().contains("ladder") ? "🪜" :
          actionDescription.toLowerCase().contains("snake") ? "🐍" : "⚡";

      Tile currentTile = player.getCurrentTile();
      int currentTileId = (currentTile != null) ? currentTile.getTileId() : 0;
      int destinationTileId = (destinationTile != null) ? destinationTile.getTileId() : currentTileId;

      StringBuilder message = new StringBuilder();
      message.append(icon).append(" ").append(player.getName());

      if (currentTileId != destinationTileId) {
        message.append(" on tile ").append(currentTileId)
            .append(": ").append(actionDescription)
            .append(" to tile ").append(destinationTileId).append("!");
      } else {
        message.append(": ").append(actionDescription);
      }

      moveActionInfoLabel.setText(message.toString());
      LOGGER.fine("Action info updated for " + player.getName());
    });
  }

  /**
   * Updates the move/action label when a player skips a turn.
   */
  public void updateSkippedTurnInfo(Player player) {
    Platform.runLater(() -> {
      if (player == null) {
        moveActionInfoLabel.setText("");
        return;
      }
      moveActionInfoLabel.setText("⏸️ " + player.getName() + " skips their turn.");
      LOGGER.fine("Skipped turn info updated for " + player.getName());
    });
  }

  /**
   * Updates the main status label to indicate whose turn it is.
   */
  public void updateTurnInfo(Player currentPlayer) {
    Platform.runLater(() -> {
      if (currentPlayer == null) {
        turnInfoLabel.setText("Game Ready");
        return;
      }
      turnInfoLabel.setText("👉 It's " + currentPlayer.getName() + "'s turn");
      diceInfoLabel.setText("");
      moveActionInfoLabel.setText("");
      LOGGER.fine("Turn info updated for " + currentPlayer.getName());
    });
  }

  /**
   * Updates the main status label to show the game winner.
   */
  public void showWinner(Player winner) {
    Platform.runLater(() -> {
      if (winner == null) {
        turnInfoLabel.setText("Game Over - No Winner?");
        return;
      }
      turnInfoLabel.setText("🏆 " + winner.getName() + " Wins!");
      diceInfoLabel.setText("");
      moveActionInfoLabel.setText("");
      LOGGER.info("Winner displayed: " + winner.getName());
    });
  }

  /**
   * Clears all dynamic information labels in the panel.
   */
  public void clearInfo() {
    Platform.runLater(() -> {
      turnInfoLabel.setText("");
      diceInfoLabel.setText("");
      moveActionInfoLabel.setText("");
      LOGGER.info("Game info panel cleared.");
    });
  }

  public void logEvent(String s) {
    // Implementation for logging events
  }
}
