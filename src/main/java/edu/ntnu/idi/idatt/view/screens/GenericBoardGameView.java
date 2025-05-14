package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.BoardGameController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.exceptions.CssLoadException;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.view.components.boardGame.BoardComponent;
import edu.ntnu.idi.idatt.view.components.boardGame.CurrentPlayerPanel;
import edu.ntnu.idi.idatt.view.components.boardGame.DicePanel;
import edu.ntnu.idi.idatt.view.components.boardGame.GameInfoPanel;
import edu.ntnu.idi.idatt.view.renderer.BoardRenderer;
import edu.ntnu.idi.idatt.view.utils.AlertHelper;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Represents the main view for playing a generic board game.
 * This class implements the BoardGameView interface for interaction with the controller
 * and BoardGameObserver interface to receive updates from the model.
 * <p>
 * The view is composed of several UI components:
 * <ul>
 *   <li>A central board component showing the game board</li>
 *   <li>A panel showing current player and player list</li>
 *   <li>A panel displaying dice roll results</li>
 *   <li>A game info panel showing game events and messages</li>
 *   <li>A button to trigger the player's turn</li>
 * </ul>
 * <p>
 * The view follows the MVC (Model-View-Controller) pattern and provides
 * methods to update the UI in response to model changes.
 *
 * @author NTNU IDATT2003 Studentnavn (foreløpig)
 * @version 1.0
 */
public class GenericBoardGameView implements BoardGameView, BoardGameObserver {
  private static final Logger LOGGER = Logger.getLogger(GenericBoardGameView.class.getName());
  private static final String CSS_PATH = "/edu/ntnu/idi/idatt/view/resources/GameScreen/GameScreen_styles.css";

  private final Scene scene;
  private final BorderPane root;
  private final BoardComponent boardComponent;
  private final GameInfoPanel gameInfoPanel;
  private final CurrentPlayerPanel currentPlayerPanel;
  private final DicePanel dicePanel;
  private final Button playTurnButton;

  private BoardGameController controller;

  /**
   * Constructs a new GenericBoardGameView with the specified board renderer.
   * Initializes all UI components and arranges them in the scene.
   *
   * @param renderer the board renderer to use for visualizing the game board
   * @throws NullPointerException if renderer is null
   * @throws IllegalStateException if essential UI components could not be initialized
   */
  public GenericBoardGameView(BoardRenderer renderer) {
    Objects.requireNonNull(renderer, "Renderer cannot be null");

    try {
      this.root = new BorderPane();
      this.root.setPadding(new Insets(10));

      // Initialize UI components
      this.boardComponent = Objects.requireNonNull(new BoardComponent(renderer),
          "Failed to create board component");
      this.gameInfoPanel = Objects.requireNonNull(new GameInfoPanel(),
          "Failed to create game info panel");
      this.currentPlayerPanel = Objects.requireNonNull(new CurrentPlayerPanel(),
          "Failed to create current player panel");
      this.dicePanel = Objects.requireNonNull(new DicePanel(),
          "Failed to create dice panel");
      this.playTurnButton = new Button("Play Turn");

      // Configure play turn button
      playTurnButton.getStyleClass().add("play-turn-button");
      playTurnButton.setPrefSize(150, 50);
      playTurnButton.setDisable(true); // Default to disabled until game starts

      // Layout configuration
      configureLayout();

      // Create scene with layout
      this.scene = new Scene(root, 1280, 720);
      loadStyles();
      LOGGER.info("GenericBoardGameView constructed successfully");
    } catch (NullPointerException e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize UI component", e);
      throw new IllegalStateException("Failed to initialize essential UI components", e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error during view construction", e);
      throw new IllegalStateException("Failed to construct game view", e);
    }
  }

  /**
   * Configures the layout of UI components within the root BorderPane.
   * This includes setting up the left, center, and right regions with
   * appropriate spacing and growth constraints.
   */
  private void configureLayout() {
    // Center - Board
    root.setCenter(boardComponent);
    BorderPane.setAlignment(boardComponent, Pos.CENTER);


    // Left side - Game info and play button
    VBox left = new VBox(20, gameInfoPanel, playTurnButton);
    VBox.setVgrow(gameInfoPanel, Priority.ALWAYS); // Allow game info to grow
    left.setPadding(new Insets(0, 10, 0, 0));
    root.setLeft(left);

    // Right side - Player panel and dice display
    VBox right = new VBox(20, currentPlayerPanel, dicePanel);
    VBox.setVgrow(currentPlayerPanel, Priority.SOMETIMES); // Allow player panel to grow if needed
    right.setPadding(new Insets(0, 0, 0, 10));
    root.setRight(right);
  }

  /**
   * Loads the CSS stylesheet for the game view.
   * If the stylesheet cannot be loaded, a warning is logged but the application continues
   * with default styles.
   *
   * @throws CssLoadException if a critical CSS resource cannot be loaded and application
   *                          cannot continue without it
   */
  private void loadStyles() {
    try {
      String css = ResourceLoader.loadCssResource(CSS_PATH);
      scene.getStylesheets().add(css);
      LOGGER.info("Successfully loaded CSS stylesheet: " + CSS_PATH);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING, "Could not load CSS: " + e.getMessage(), e);
      AlertHelper.showWarningAlert("Style Warning",
          "Game styles could not be loaded. Using default JavaFX styles.");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error loading CSS resources", e);
      throw new CssLoadException("Failed to load critical UI resources", e);
    }
  }

  /**
   * Returns the JavaFX Scene for this view.
   *
   * @return the Scene object representing this view
   */
  @Override
  public Scene getScene() {
    return scene;
  }

  /**
   * Sets the controller for this view and binds UI event handlers.
   *
   * @param controller the BoardGameController to handle UI events
   * @throws NullPointerException if controller is null
   */
  @Override
  public void setController(BoardGameController controller) {
    this.controller = Objects.requireNonNull(controller, "Controller cannot be null");
    bindEventHandlers();
    LOGGER.info("Controller successfully bound to view");
  }

  /**
   * Binds UI event handlers to controller methods.
   * This includes setting up the action for the play turn button.
   */
  private void bindEventHandlers() {
    if (playTurnButton != null) {
      playTurnButton.setOnAction(e -> {
        try {
          LOGGER.fine("Play turn button clicked");
          if (controller != null) {
            controller.onPlayTurn();
          }
        } catch (Exception ex) {
          LOGGER.log(Level.SEVERE, "Error processing turn", ex);
          AlertHelper.showErrorAlert("Turn Error",
              "An error occurred while processing the turn: " + ex.getMessage());
          playTurnButton.setDisable(false); // Re-enable button on error
        }
      });
    }
  }

  /**
   * Initializes the view with data from the specified BoardGame model.
   * Registers this view as an observer of the model and performs an initial UI refresh.
   *
   * @param game the BoardGame model to observe and display
   * @throws NullPointerException if game is null
   */
  public void initializeView(BoardGame game) {
    Objects.requireNonNull(game, "Game model cannot be null");
    try {
      game.addObserver(this);
      refreshAll(game);
      LOGGER.info("View successfully initialized with game model");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize view with game model", e);
      AlertHelper.showErrorAlert("Initialization Error",
          "Failed to initialize game view: " + e.getMessage());
      throw new IllegalStateException("View initialization failed", e);
    }
  }

  /**
   * Handles notification that a player has moved.
   * Updates the board visualization, dice display, and game info panel.
   *
   * @param player the player who moved
   * @param from the tile from which the player moved (may be null for initial placement)
   * @param to the tile to which the player moved
   */
  @Override
  public void onPlayerMoved(Player player, Tile from, Tile to) {
    if (player == null || to == null) {
      LOGGER.warning("Received invalid player move notification: player=" +
          (player == null ? "null" : player.getName()) + ", to=" + (to == null ? "null" : to.getTileId()));
      return;
    }

    Platform.runLater(() -> handleMove(player, from, to));
  }

  @Override
  public void onActionTileEffect(Player player, Tile fromActionTile, Tile toDestinationTile) {
    Platform.runLater(() -> {
      if (boardComponent != null) {
        boardComponent.animateActionTileEffect(player, fromActionTile, toDestinationTile);
      }

      if (gameInfoPanel != null) {
        if (fromActionTile.getActionType() != null) {
          if (fromActionTile.getTileId() < toDestinationTile.getTileId()) {
            gameInfoPanel.logEvent(player.getName() + " climbed a ladder from " + fromActionTile.getTileId() + " to " + toDestinationTile.getTileId() + "!");
          } else {
            gameInfoPanel.logEvent(player.getName() + " slid down a snake from " + fromActionTile.getTileId() + " to " + toDestinationTile.getTileId() + "!");
          }
        }
      }
    });
  }

  /**
   * Handles notification that a player has been added to the game.
   * Updates the board visualization and player list.
   *
   * @param player the player who was added
   */
  @Override
  public void onPlayerAdded(Player player) {
    if (player == null) {
      LOGGER.warning("Received notification of null player being added");
      return;
    }

    Platform.runLater(() -> {
      try {
        if (boardComponent != null) {
          boardComponent.addPlayerVisual(player);
        }
        if (currentPlayerPanel != null) {
          currentPlayerPanel.addPlayerEntry(player);
        }
        if (gameInfoPanel != null) {
          gameInfoPanel.logEvent(player.getName() + " joined the game.");
        }
        LOGGER.fine("Added player to view: " + player.getName());
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Error adding player to view: " + player.getName(), e);
      }
    });
  }



  /**
   * Handles notification that the game has been won.
   * Updates the UI to show the winner and disables controls.
   *
   * @param winner the player who won the game
   */
  @Override
  public void onGameWon(Player winner) {
    if (winner == null) {
      LOGGER.warning("Received game won notification with null winner");
      return;
    }

    Platform.runLater(() -> handleWin(winner));
  }

  /**
   * Handles a general update notification from the observed BoardGame.
   * Refreshes all UI components based on the current game state.
   *
   * @param game the BoardGame containing the updated state
   */
  @Override
  public void update(BoardGame game) {
    if (game == null) {
      LOGGER.warning("Received update notification with null game");
      return;
    }

    Platform.runLater(() -> {
      try {
        // For targeted updates, use refreshPartial instead of refreshAll for better performance
        refreshPartial(game);
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Error updating view from game model", e);
        // If partial refresh fails, try full refresh as fallback
        try {
          refreshAll(game);
        } catch (Exception ex) {
          LOGGER.log(Level.SEVERE, "Critical error during view refresh", ex);
          AlertHelper.showErrorAlert("View Error",
              "Failed to update game view. Please restart the game.");
        }
      }
    });
  }

  /**
   * Helper method to handle UI updates when a player moves.
   * Updates the board visualization, dice display, and game info.
   *
   * @param player the player who moved
   * @param from the tile from which the player moved
   * @param to the tile to which the player moved
   */
  private void handleMove(Player player, Tile from, Tile to) {
    try {
      if (boardComponent != null) {
        boardComponent.updatePlayerVisual(player, from, to);
      }

      if (controller != null && controller.getBoardGame() != null) {
        Dice dice = controller.getBoardGame().getLastDiceRoll();

        if (dice != null && dicePanel != null) {
          dicePanel.updateDiceDisplay(dice);

          if (gameInfoPanel != null) {
            gameInfoPanel.updateDiceInfo(player, dice);
          }
        }
      }

      if (gameInfoPanel != null) {
        gameInfoPanel.updateMoveInfo(player, to);
      }

      LOGGER.fine(player.getName() + " moved from " +
          (from != null ? from.getTileId() : "start") + " to " + to.getTileId());
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error handling player move in view", e);
    }
  }

  /**
   * Helper method to handle UI updates when the turn changes.
   * Updates player highlighting, game info, dice display, and button state.
   *
   * @param current the player whose turn it is now
   */
  private void handleTurnChange(Player current) {
    try {
      // Determine if the game is in a playable state
      boolean canPlay = controller != null
          && controller.getBoardGame() != null
          && !controller.getBoardGame().isGameOver()
          && current != null;

      if (playTurnButton != null) {
        playTurnButton.setDisable(!canPlay);
      }

      if (currentPlayerPanel != null) {
        currentPlayerPanel.updateCurrentPlayerHighlight(current);
      }

      if (gameInfoPanel != null) {
        gameInfoPanel.updateTurnInfo(current);
      }

      if (dicePanel != null) {
        dicePanel.resetDiceDisplay();
      }

      LOGGER.fine("Turn changed to: " + (current != null ? current.getName() : "none") +
          ", can play: " + canPlay);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error handling turn change in view", e);
    }
  }

  /**
   * Helper method to handle UI updates when a player wins the game.
   * Disables controls, displays winner information, and shows a game over alert.
   *
   * @param winner the player who won the game
   */
  private void handleWin(Player winner) {
    try {
      if (playTurnButton != null) {
        playTurnButton.setDisable(true);
      }

      if (dicePanel != null) {
        dicePanel.resetDiceDisplay();
        try {
          dicePanel.setDisabledVisual(true);
        } catch (Exception e) {
          // If this method doesn't exist or fails, just log and continue
          LOGGER.log(Level.FINE, "Could not set disabled visual on dice panel", e);
        }
      }

      if (gameInfoPanel != null) {
        gameInfoPanel.showWinner(winner);
      }

      AlertHelper.showInfoAlert("Game Over!", winner.getName() + " is the winner!");
      LOGGER.info("Game won by: " + winner.getName());
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error handling game win in view", e);
      // Ensure user is notified of winner even if UI update fails
      AlertHelper.showInfoAlert("Game Over!",
          "The game has ended. " + winner.getName() + " is the winner!");
    }
  }

  /**
   * Performs a complete refresh of all UI components based on the game state.
   * This is more resource-intensive than partial updates and should be used
   * for initialization or when significant state changes occur.
   *
   * @param game the BoardGame containing the current state
   * @throws NullPointerException if game is null
   */
  private void refreshAll(BoardGame game) {
    Objects.requireNonNull(game, "Cannot refresh view with null game model");

    try {
      if (boardComponent != null) {
        Board board = game.getBoard();
        List<Player> players = game.getPlayers();

        if (board != null && players != null) {
          boardComponent.initializeBoard(board, players);
        } else {
          LOGGER.warning("Cannot initialize board with null board or players list");
        }
      }

      if (currentPlayerPanel != null) {
        currentPlayerPanel.initialize(game.getPlayers());
      }

      handleTurnChange(game.getCurrentPlayer());

      LOGGER.fine("Full view refresh completed successfully");
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error during full view refresh", e);
      throw new IllegalStateException("Failed to refresh game view", e);
    }
  }

  /**
   * Performs a partial refresh of UI components based on the game state.
   * This is more efficient than a full refresh for routine updates.
   *
   * @param game the BoardGame containing the current state
   */
  private void refreshPartial(BoardGame game) {
    if (game == null) {
      LOGGER.warning("Cannot perform partial refresh with null game model");
      return;
    }

    try {
      if (boardComponent != null) {
        boardComponent.updateBoardVisuals();
      }

      if (currentPlayerPanel != null) {
        currentPlayerPanel.updateCurrentPlayerHighlight(game.getCurrentPlayer());
      }

      // Update dice display if needed
      if (dicePanel != null) {
        Dice lastDice = game.getLastDiceRoll();
        if (lastDice != null) {
          dicePanel.updateDiceDisplay(lastDice);
        } else {
          dicePanel.resetDiceDisplay();
        }
      }

      // Update turn state and controls
      handleTurnChange(game.getCurrentPlayer());

      LOGGER.fine("Partial view refresh completed successfully");
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error during partial view refresh", e);
      // Let caller handle the exception if needed
      throw e;
    }
  }
}