package edu.ntnu.idi.idatt.view.components.boardgame;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * A modal dialog displayed when a player wins the game.
 *
 * <p>Shows a congratulatory header with the winner's name and provides two
 * actions:
 * <ul>
 *   <li><b>Back to Game Selection:</b> returns the user to the game setup screen</li>
 *   <li><b>Main Menu:</b> returns the user to the application's main menu</li>
 * </ul>
 * </p>
 */
public class WinDialog extends Dialog<ButtonType> {

  /**
   * Constructs a new {@code WinDialog} for the specified winner.
   *
   * <p>The dialog title is set to "Congratulations!" and the header text
   * includes celebratory emojis around the winner's name. The content area
   * contains a brief message and two buttons: one to go back to game
   * selection and one to return to the main menu.
   * </p>
   *
   * @param winnerName the display name of the winning player; must not be {@code null}
   */
  public WinDialog(String winnerName) {
    setTitle("Congratulations!");
    setHeaderText("🎉 " + winnerName + " is the winner! 🎉");

    ButtonType backToGameSetup = new ButtonType(
        "Back to Game Selection",
        ButtonBar.ButtonData.OK_DONE
    );
    ButtonType mainMenu = new ButtonType(
        "Main Menu",
        ButtonBar.ButtonData.CANCEL_CLOSE
    );
    getDialogPane().getButtonTypes().setAll(backToGameSetup, mainMenu);

    VBox content = new VBox(
        10,
        new Label("Well played, " + winnerName + "!")
    );
    content.setAlignment(Pos.CENTER);

    getDialogPane().setContent(content);
  }
}
