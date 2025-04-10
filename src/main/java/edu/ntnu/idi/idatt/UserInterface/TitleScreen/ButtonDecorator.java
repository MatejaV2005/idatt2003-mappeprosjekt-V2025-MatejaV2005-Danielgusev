package edu.ntnu.idi.idatt.UserInterface.TitleScreen;

import javafx.scene.control.Button;

// --- Decorator Pattern Implementation ---
// The decorator interface for buttons
interface ButtonDecorator {

  Button decorate(Button button);
}
