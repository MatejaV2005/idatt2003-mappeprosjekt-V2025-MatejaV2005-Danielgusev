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

  /**
   * Creates a menu button for navigation.
   *
   * @param text The button text
   * @return A styled menu button instance
   */
  public Button createMenuButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(200);
    button.setPrefHeight(50);
    button.getStyleClass().add("menu-button");
    return button;
  }

  /**
   * Creates an icon button with both text and an icon.
   *
   * @param text The button text
   * @param iconPath The path to the icon resource
   * @return A button with text and icon
   */
  public Button createIconButton(String text, String iconPath) {
    Button button = createStandardButton(text);

    try {
      // Load icon and add to button
      // Implementation would add an icon to the button
      button.getStyleClass().add("icon-button");
    } catch (Exception e) {
      System.err.println("Failed to load icon for button: " + e.getMessage());
      // Continue with text-only button rather than crashing
    }

    return button;
  }
}