package edu.ntnu.idi.idatt.view.titleScreen;

import javafx.scene.control.Button;

// A concrete decorator that adds a hover effect to the button
class HoverEffectDecorator implements ButtonDecorator {

  @Override
  public Button decorate(Button button) {
    String originalStyle = button.getStyle();
    button.setOnMouseEntered(e -> button.setStyle(originalStyle + "; -fx-opacity: 0.8;"));
    button.setOnMouseExited(e -> button.setStyle(originalStyle));
    return button;
  }
}