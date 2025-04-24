package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.view.screens.GameSelectionView;

/**
 * Controller for the game mode selection screen.
 * Handles user interactions and navigation related to game selection.
 * Follows MVC pattern by separating business logic from view.
 */
public class GameSelectionController {
  private final GameSelectionView view;
  private final NavigationController navigationController;
  //private final BoardGameFactory gameFactory;

  /**
   * Constructs a new GameModeSelectionController.
   *
   * @param view The game selection view this controller manages
   * @param navigationController The navigation controller for switching screens
   */
  public GameSelectionController(GameSelectionView view, NavigationController navigationController) {
    this.view = view;
    this.navigationController = navigationController;

    // Set this controller to the view
    view.setController(this);
  }

  /**
   * Handles the selection of a game mode.
   * Prepares to navigate to the difficulty selection or game setup screen.
   *
   * @param gameMode The selected game mode
   */
  public void onGameModeSelected(String gameMode) {
    System.out.println("Selected game mode: " + gameMode);
  }

  /**
   * Handles the back button click.
   * Navigates back to the title screen.
   */
  public void onBackButtonClicked() {
    navigationController.navigateToTitleScreen();
  }
}