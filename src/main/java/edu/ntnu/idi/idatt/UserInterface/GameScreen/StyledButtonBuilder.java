/*package edu.ntnu.idi.idatt.UserInterface;

import javafx.scene.control.Button;

// --- Builder Pattern Implementation ---
// A builder that creates styled buttons with predefined settings
class StyledButtonBuilder {
  private String text = "";
  private double prefWidth = 300;
  private double prefHeight = 60;
  private String style =
      "-fx-background-radius: 15px;" +
          "-fx-font-size: 20px;" +
          "-fx-font-weight: bold;" +
          "-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #C834FF, #7501B3);" +
          "-fx-text-fill: white;" +
          "-fx-effect: dropshadow(one-pass-box, black, 6, 0, 0, 4);";

  public StyledButtonBuilder withText(String text) {
    this.text = text;
    return this;
  }

  public StyledButtonBuilder withPrefWidth(double width) {
    this.prefWidth = width;
    return this;
  }

  public StyledButtonBuilder withPrefHeight(double height) {
    this.prefHeight = height;
    return this;
  }

  public StyledButtonBuilder withStyle(String style) {
    this.style = style;
    return this;
  }

  public Button build() {
    Button button = new Button(text);
    button.setPrefWidth(prefWidth);
    button.setPrefHeight(prefHeight);
    button.setStyle(style);
    return button;
  }
}*/

