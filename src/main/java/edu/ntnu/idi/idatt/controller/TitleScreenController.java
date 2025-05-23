package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.view.screens.TitleScreenView;

/**
 * Controls title‐screen interactions.
 *
 * <p>Routes the user to the game selection screen or exits the application based on button clicks.
 */
public class TitleScreenController {

  private final NavigationController navigationController;

  /**
   * Creates a TitleScreenController.
   *
   * @param view the title‐screen view; must not be null
   * @param navigationController the navigation controller; must not be null
   */
  public TitleScreenController(
      TitleScreenView view,
      NavigationController navigationController) {
    this.navigationController = navigationController;
    view.setController(this);
  }

  /**
   * Invoked when the user clicks "Choose Gamemode".
   *
   * <p>Transitions to the game selection screen.
   */
  public void onChooseGameMode() {
    navigationController.navigateToGameSelection();
  }

  /**
   * Invoked when the user clicks "Quit Game".
   *
   * <p>Exits the application.
   */
  public void onQuitGame() {
    navigationController.exitApp();
  }
}
