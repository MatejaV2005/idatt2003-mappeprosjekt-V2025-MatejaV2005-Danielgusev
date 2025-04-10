package edu.ntnu.idi.idatt.UserInterface.TitleScreen;

import javafx.scene.control.Button;

public class ButtonBuilder {
  private String text = "";
  private double prefWidth = 300;
  private double prefHeight = 60;

  public ButtonBuilder withText(String text) {
    this.text = text;
    return this;
  }

  public ButtonBuilder withPrefWidth(double width) {
    this.prefWidth = width;
    return this;
  }

  public ButtonBuilder withPrefHeight(double height) {
    this.prefHeight = height;
    return this;
  }

  public Button build() {
    Button button = new Button(text);
    button.setPrefWidth(prefWidth);
    button.setPrefHeight(prefHeight);
    // Instead of inline style, add our CSS style class:
    button.getStyleClass().add("styled-button");
    return button;
  }
}
