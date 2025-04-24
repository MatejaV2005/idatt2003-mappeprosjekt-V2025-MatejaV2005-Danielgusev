package edu.ntnu.idi.idatt.controller;


import edu.ntnu.idi.idatt.view.screens.TitleScreenView;

public class TitleScreenController {
  private final TitleScreenView view;
  private final NavigationController navigationController;

  public TitleScreenController(TitleScreenView view, NavigationController navigationController) {
    this.view = view;
    this.navigationController = navigationController;

    view.setController(this);
  }

  /**
   * Handles the "Choose Gamemode" button click.
   * Navigates to the game selection screen.
   */
  public void onChooseGameMode() {
    navigationController.navigateToGameSelection();
  }

  /**
   * Handles the "Settings" button click.
   * Navigates to the settings screen.
   */
  public void onSettings() {
    navigationController.navigateToSettings();
  }

  /**
   * Handles the "Quit Game" button click.
   * Exits the application.
   */
  public void onQuitGame() {
    navigationController.exitApp();
  }
}

