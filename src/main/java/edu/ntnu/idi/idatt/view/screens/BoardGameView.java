package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.BoardGameController;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import javafx.scene.Scene;

public interface BoardGameView {
  Scene getScene();

  void setController(BoardGameController controller);

}
