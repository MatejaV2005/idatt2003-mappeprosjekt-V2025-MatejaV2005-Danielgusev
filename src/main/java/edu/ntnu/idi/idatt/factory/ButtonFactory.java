package edu.ntnu.idi.idatt.factory;

import javafx.scene.control.Button;

/**
 * Factory class for creating different types of buttons with consistent styling.
 * Implements the Factory design pattern as required in section 4.5.1.
 */
public class ButtonFactory {

  /**
   * Creates a standard button with default styling.
   *
   * @param text The button text
   * @return A styled button instance
   */
  public Button createStandardButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(300);
    button.setPrefHeight(60);
    button.getStyleClass().add("styled-button");
    return button;
  }

  /**
   * Creates a small button for secondary actions.
   *
   * @param text The button text
   * @return A styled small button instance
   */
  public Button createSmallButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(150);
    button.setPrefHeight(40);
    button.getStyleClass().add("small-button");
    return button;
  }
}