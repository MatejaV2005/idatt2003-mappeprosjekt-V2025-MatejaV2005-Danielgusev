package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.view.screens.GameSelectionView;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the game mode selection screen. Handles user interactions and navigation related
 * to game selection.
 */
public class GameSelectionController {

  private static final Logger LOG = Logger.getLogger(GameSelectionController.class.getName());
  private final NavigationController navigationController;

  /**
   * Constructs a new GameModeSelectionController.
   *
   * @param view The game selection view this controller manages. Must not be null.
   * @param navigationController The navigation controller for switching screens. Must not be null.
   */
  public GameSelectionController(
      GameSelectionView view, NavigationController navigationController) {
    GameSelectionView view1 = Objects.requireNonNull(view, "GameSelectionView cannot be null.");
    this.navigationController =
        Objects.requireNonNull(navigationController, "NavigationController cannot be null.");
    view1.setController(this);
  }

  /**
   * Handles the selection of a game mode. Determines the {@link GameType} and instructs the {@link
   * NavigationController} to navigate to the game setup screen, configured for the selected game
   * type.
   *
   * @param gameModeName The string name of the selected game mode
   *                     (e.g., "Snakes & Ladders", "AstroRally").
   *                     This should match constants defined in GameSelectionView.
   */
  public void onGameModeSelected(String gameModeName) {
    GameType selectedType;

    if (GameSelectionView.SNAKES_AND_LADDERS.equals(gameModeName)) {
      selectedType = GameType.SNAKES_AND_LADDERS;
    } else if (GameSelectionView.ASTRO_RALLY.equals(gameModeName)) {
      selectedType = GameType.ASTRO_RALLY;
    } else {
      LOG.log(
          Level.WARNING,
          "Unknown game mode name received from view: {0}. Defaulting to SNAKES_AND_LADDERS.",
          gameModeName);
      selectedType = GameType.SNAKES_AND_LADDERS;
    }

    navigationController.navigateToGameSetup(selectedType);
  }

  /**
   * Handles the action triggered when the user clicks the 'back' button on the game selection
   * screen. This method instructs the {@link NavigationController} to navigate the user away from
   * the current screen, typically returning to the main title screen or a previous menu.
   */
  public void onBackButtonClicked() {
    LOG.info("Back button clicked on game selection screen. Navigating to title screen.");
    navigationController.navigateToTitleScreen();
  }
}
