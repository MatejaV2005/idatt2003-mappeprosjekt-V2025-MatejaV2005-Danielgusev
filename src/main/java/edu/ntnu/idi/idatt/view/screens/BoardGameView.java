package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.BoardGameController;
import javafx.scene.Scene;

/**
 * Defines a board game UI in JavaFX.
 *
 * <p>Provides the primary {@link Scene} and allows
 * binding a {@link BoardGameController} to handle user actions
 * and orchestrate updates.</p>
 *
 * @see edu.ntnu.idi.idatt.controller.BoardGameController
 */
public interface BoardGameView {

  /**
   * Retrieves this view’s JavaFX scene.
   *
   * @return the configured Scene; never null
   */
  Scene getScene();

  /**
   * Sets the controller responsible for handling user input
   * and driving game logic updates.
   *
   * @param controller the BoardGameController; must not be null
   */
  void setController(BoardGameController controller);

}

