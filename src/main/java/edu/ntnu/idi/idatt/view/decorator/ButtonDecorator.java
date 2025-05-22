package edu.ntnu.idi.idatt.view.decorator;

import javafx.scene.control.Button;

/**
 * Defines an interface for decorating {@link Button} objects.
 *
 * <p>This interface is part of the Decorator design pattern, allowing for dynamic
 * extension of button functionalities or appearances.
 * </p>
 *
 * @see Button
 * @see HoverEffectDecorator
 */
public interface ButtonDecorator {

  /**
   * Applies a decoration to the given {@link Button}.
   *
   * <p>Implementations will modify the button (e.g., add event handlers or change styles)
   * and return the decorated button.
   *
   * @param button The {@link Button} to be decorated. Must not be {@code null}.
   * @return The decorated {@link Button}.
   *
   * @throws NullPointerException if the {@code button} argument is {@code null}.
   */
  Button decorate(Button button);
}
