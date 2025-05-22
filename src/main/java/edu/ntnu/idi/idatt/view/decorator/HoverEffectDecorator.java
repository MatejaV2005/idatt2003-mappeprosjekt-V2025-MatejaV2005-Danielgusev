package edu.ntnu.idi.idatt.view.decorator;

import javafx.scene.control.Button;

/**
 * A {@link ButtonDecorator} that applies a hover effect to JavaFX {@link Button}s.
 *
 * <p>On mouse enter, the button's opacity is reduced for a visual hover cue.
 * On mouse exit, the original style is restored.
 * </p>
 */
public class HoverEffectDecorator implements ButtonDecorator {

  /**
   * Decorates the given button by registering mouse event handlers to modify
   * its CSS style for a hover effect.
   *
   * @param button the {@link Button} to decorate; must not be null
   * @return the same button instance with hover behavior applied
   */
  @Override
  public Button decorate(Button button) {
    String originalStyle = button.getStyle();
    button.setOnMouseEntered(e ->
        button.setStyle(originalStyle + "; -fx-opacity: 0.8;"));
    button.setOnMouseExited(e ->
        button.setStyle(originalStyle));
    return button;
  }
}
