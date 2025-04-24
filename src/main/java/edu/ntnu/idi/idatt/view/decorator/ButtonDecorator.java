package edu.ntnu.idi.idatt.view.decorator;

import javafx.scene.control.Button;

// --- Decorator Pattern Implementation ---
// The decorator interface for buttons
public interface ButtonDecorator {

  Button decorate(Button button);
}
