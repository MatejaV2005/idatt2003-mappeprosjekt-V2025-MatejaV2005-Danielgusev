package edu.ntnu.idi.idatt.view.components.boardGame;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WinDialog extends Dialog<ButtonType> {

  public WinDialog(String winnerName) {
    setTitle("Congratulations!");
    setHeaderText("🎉 " + winnerName + " is the winner! 🎉");
    // add your own button types
    ButtonType playAgain = new ButtonType("Play Again", ButtonBar.ButtonData.OK_DONE);
    ButtonType mainMenu  = new ButtonType("Main Menu", ButtonBar.ButtonData.CANCEL_CLOSE);
    getDialogPane().getButtonTypes().setAll(playAgain, mainMenu);
    // custom content—image, text, whatever
    VBox content = new VBox(10,
        new Label("Well played, " + winnerName + "!")
    );
    content.setAlignment(Pos.CENTER);
    getDialogPane().setContent(content);
    // Optional: style via CSS, set width/height, icons, etc.
  }

}
