package edu.ntnu.idi.idatt.UserInterface.TitleScreen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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