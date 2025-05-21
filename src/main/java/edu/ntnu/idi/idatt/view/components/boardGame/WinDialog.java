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

    ButtonType backToGameSetup = new ButtonType("Back to Game Selection", ButtonBar.ButtonData.OK_DONE);
    ButtonType mainMenu  = new ButtonType("Main Menu", ButtonBar.ButtonData.CANCEL_CLOSE);
    getDialogPane().getButtonTypes().setAll(backToGameSetup, mainMenu);

    VBox content = new VBox(10,
        new Label("Well played, " + winnerName + "!")
    );
    content.setAlignment(Pos.CENTER);
    getDialogPane().setContent(content);
  }

}
