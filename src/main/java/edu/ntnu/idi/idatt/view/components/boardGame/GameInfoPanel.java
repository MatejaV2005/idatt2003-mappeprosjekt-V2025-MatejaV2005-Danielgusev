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
  private final Label turnInfoLabel; // Shows whose turn or game outcome
  private final Label diceInfoLabel; // Shows the last dice roll
  private final Label moveActionInfoLabel; // Shows the last move or action consequence

  /**
   * Constructs a new GameInfoPanel.
   * Initializes the labels used to display game state information.
   */
  public GameInfoPanel() {
    super(8); // Spacing between labels
    this.setPadding(new Insets(15));
    this.getStyleClass().add("game-info-panel"); // Add CSS class for the panel

    this.titleLabel = createStyledLabel("Game Info", "panel-title"); // Title for the panel
    this.turnInfoLabel = createStyledLabel("", "info-label-turn"); // Will show "Player X's turn" or "Player Y Wins!"
    this.diceInfoLabel = createStyledLabel("", "info-label-dice"); // Will show "Rolled A + B = C"
    this.moveActionInfoLabel = createStyledLabel("", "info-label-move"); // Will show "Moved to Z" or "Landed on Ladder..."

    this.getChildren().addAll(titleLabel, turnInfoLabel, diceInfoLabel, moveActionInfoLabel);
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
    label.setMaxWidth(Double.MAX_VALUE); // Allow label to use full width of VBox
    label.setTextAlignment(TextAlignment.LEFT); // Or CENTER if preferred
    return label;
  }

  /**
   * Updates the dice information label with the result of the latest roll.
   * Ensures the update happens on the JavaFX Application Thread.
   *
   * @param player The player who rolled.
   * @param dice The Dice object containing the result.
   */
  public void updateDiceInfo(Player player, Dice dice) {
    Platform.runLater(() -> {
      if (player == null || dice == null) {
        diceInfoLabel.setText(""); // Clear if invalid input
        return;
      }
      try {
        int d1 = dice.getDieValue(0);
        int d2 = dice.getDieValue(1);
        int total = dice.getTotalDiceValue();
        diceInfoLabel.setText(player.getName() + " rolled " + d1 + " + " + d2 + " = " + total);
        LOGGER.fine("Dice info updated for " + player.getName());
      } catch (Exception e) {
        LOGGER.warning("Could not update dice info: " + e.getMessage());
        diceInfoLabel.setText(player.getName() + " rolled."); // Fallback
      }
    });
  }

  /**
   * Updates the move/action label to show the result of a player's movement.
   * Ensures the update happens on the JavaFX Application Thread.
   *
   * @param player The player who moved.
   * @param toTile The tile the player landed on.
   */
  public void updateMoveInfo(Player player, Tile toTile) {
    Platform.runLater(() -> {
      if (player == null || toTile == null) {
        moveActionInfoLabel.setText("");
        return;
      }
      moveActionInfoLabel.setText(player.getName() + " moved to tile " + toTile.getTileId());
      LOGGER.fine("Move info updated for " + player.getName());
    });
  }

  /**
   * Updates the move/action label to show the result of a special tile action.
   * Ensures the update happens on the JavaFX Application Thread.
   *
   * @param player The player who triggered the action.
   * @param actionDescription A description of the action.
   * @param destinationTile The tile the player ended on after the action (can be null).
   */
  public void updateActionInfo(Player player, String actionDescription, Tile destinationTile) {
    Platform.runLater(() -> {
      if (player == null || actionDescription == null) {
        moveActionInfoLabel.setText("");
        return;
      }
      String message = player.getName() + " " + actionDescription;
      if (destinationTile != null) {
        message += " to tile " + destinationTile.getTileId();
      }
      moveActionInfoLabel.setText(message);
      LOGGER.fine("Action info updated for " + player.getName());
    });
  }

  /**
   * Updates the move/action label when a player skips a turn.
   * Ensures the update happens on the JavaFX Application Thread.
   *
   * @param player The player skipping the turn.
   */
  public void updateSkippedTurnInfo(Player player) {
    Platform.runLater(() -> {
      if (player == null) {
        moveActionInfoLabel.setText("");
        return;
      }
      moveActionInfoLabel.setText(player.getName() + " skips their turn.");
      LOGGER.fine("Skipped turn info updated for " + player.getName());
      // Clear dice info from previous turn if needed
      // diceInfoLabel.setText("");
    });
  }


  /**
   * Updates the main status label to indicate whose turn it is.
   * Clears previous roll and move/action info.
   * Ensures the update happens on the JavaFX Application Thread.
   *
   * @param currentPlayer The player whose turn is starting.
   */
  public void updateTurnInfo(Player currentPlayer) {
    Platform.runLater(() -> {
      if (currentPlayer == null) {
        turnInfoLabel.setText("Game Ready"); // Or appropriate initial/error state
        return;
      }
      turnInfoLabel.setText("It's " + currentPlayer.getName() + "'s turn");
      // Clear info from the previous turn
      diceInfoLabel.setText("");
      moveActionInfoLabel.setText("");
      LOGGER.fine("Turn info updated for " + currentPlayer.getName());
    });
  }

  /**
   * Updates the main status label to show the game winner.
   * Clears other potentially irrelevant info.
   * Ensures the update happens on the JavaFX Application Thread.
   *
   * @param winner The player who won.
   */
  public void showWinner(Player winner) {
    Platform.runLater(() -> {
      if (winner == null) {
        turnInfoLabel.setText("Game Over - No Winner?");
        return;
      }
      turnInfoLabel.setText(winner.getName() + " Wins!");
      // Clear other info
      diceInfoLabel.setText("");
      moveActionInfoLabel.setText("");
      LOGGER.info("Winner displayed: " + winner.getName());
    });
  }

  /**
   * Clears all dynamic information labels in the panel.
   * Ensures the operation happens on the JavaFX Application Thread.
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
  }
}