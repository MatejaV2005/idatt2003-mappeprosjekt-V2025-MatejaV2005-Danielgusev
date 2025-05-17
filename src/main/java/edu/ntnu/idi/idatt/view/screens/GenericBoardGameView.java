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
import java.util.Optional;
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
  private volatile boolean animationRunning = false;

  public GenericBoardGameView(BoardRenderer renderer) {
    Objects.requireNonNull(renderer, "Renderer cannot be null");

    try {
      this.root = new BorderPane();
      this.root.setPadding(new Insets(10));

      this.boardComponent = Objects.requireNonNull(new BoardComponent(renderer), "Failed to create board component");
      this.gameInfoPanel = Objects.requireNonNull(new GameInfoPanel(), "Failed to create game info panel");
      this.currentPlayerPanel = Objects.requireNonNull(new CurrentPlayerPanel(), "Failed to create current player panel");
      this.dicePanel = Objects.requireNonNull(new DicePanel(), "Failed to create dice panel");
      this.playTurnButton = new Button("Play Turn");

      playTurnButton.getStyleClass().add("play-turn-button");
      playTurnButton.setPrefSize(150, 50);
      playTurnButton.setDisable(true);

      configureLayout();
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

  private void configureLayout() {
    root.setCenter(boardComponent);
    BorderPane.setAlignment(boardComponent, Pos.CENTER);

    VBox left = new VBox(20, gameInfoPanel, playTurnButton);
    VBox.setVgrow(gameInfoPanel, Priority.ALWAYS);
    left.setPadding(new Insets(0, 10, 0, 0));
    root.setLeft(left);

    VBox right = new VBox(20, currentPlayerPanel, dicePanel);
    VBox.setVgrow(currentPlayerPanel, Priority.SOMETIMES);
    right.setPadding(new Insets(0, 0, 0, 10));
    root.setRight(right);
  }

  private void loadStyles() {
    try {
      String css = ResourceLoader.loadCssResource(CSS_PATH);
      scene.getStylesheets().add(css);
      LOGGER.info("Successfully loaded CSS stylesheet: " + CSS_PATH);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING, "Could not load CSS: " + e.getMessage(), e);
      AlertHelper.showWarningAlert("Style Warning", "Game styles could not be loaded. Using default JavaFX styles.");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error loading CSS resources", e);
      throw new CssLoadException("Failed to load critical UI resources", e);
    }
  }

  @Override
  public Scene getScene() {
    return scene;
  }

  @Override
  public void setController(BoardGameController controller) {
    this.controller = Objects.requireNonNull(controller, "Controller cannot be null");
    bindEventHandlers();
    LOGGER.info("Controller successfully bound to view");
  }

  private void bindEventHandlers() {
    if (playTurnButton != null) {
      playTurnButton.setOnAction(e -> {
        try {
          LOGGER.fine("Play turn button clicked");
          if (controller != null && !animationRunning) {
            disablePlayButton();
            if (dicePanel != null) {
              dicePanel.resetDiceDisplay();
            }
            controller.onPlayTurn();
          }
        } catch (Exception ex) {
          LOGGER.log(Level.SEVERE, "Error processing turn", ex);
          AlertHelper.showErrorAlert("Turn Error", "An error occurred while processing the turn: " + ex.getMessage());
          if (this.controller != null && this.controller.getBoardGame() != null) {
            handleTurnChange(this.controller.getBoardGame().getCurrentPlayer());
          } else {
            enablePlayButton();
          }
        }
      });
    }
  }

  public void initializeView(BoardGame game) {
    Objects.requireNonNull(game, "Game model cannot be null");
    try {
      game.addObserver(this);
      if (dicePanel != null) {
        dicePanel.resetDiceDisplay();
      }
      refreshAll(game);
      LOGGER.info("View successfully initialized with game model");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize view with game model", e);
      AlertHelper.showErrorAlert("Initialization Error", "Failed to initialize game view: " + e.getMessage());
      throw new IllegalStateException("View initialization failed", e);
    }
  }

  @Override
  public void onPlayerMoved(Player player, Tile from, Tile to) {
    if (player == null || to == null) {
      LOGGER.warning("Received invalid player move notification.");
      return;
    }
    Platform.runLater(() -> handleMove(player, from, to));
  }

  @Override
  public void onActionTileEffect(Player player, Tile fromTriggerTile, Tile toDestinationTile) {
    Platform.runLater(() -> {
      if (gameInfoPanel != null && player != null && fromTriggerTile != null && toDestinationTile != null) {
        String actionDescription;
        if (fromTriggerTile.getTileId() < toDestinationTile.getTileId()) {
          actionDescription = "climbed a ladder";
        } else if (fromTriggerTile.getTileId() > toDestinationTile.getTileId()) {
          actionDescription = "slid down a snake";
        } else {
          actionDescription = "experienced a tile effect";
        }
        gameInfoPanel.updateActionInfo(player, actionDescription, toDestinationTile);
      }
    });
  }

  @Override
  public void onPlayerAdded(Player player) {
    if (player == null) {
      LOGGER.warning("Received notification of null player being added");
      return;
    }
    Platform.runLater(() -> {
      try {
        if (boardComponent != null) boardComponent.addPlayerVisual(player);
        if (currentPlayerPanel != null) currentPlayerPanel.addPlayerEntry(player);
        if (gameInfoPanel != null) gameInfoPanel.logEvent(player.getName() + " joined the game.");
        LOGGER.fine("Added player to view: " + player.getName());
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Error adding player to view: " + player.getName(), e);
      }
    });
  }

  @Override
  public void onGameWon(Player winner) {
    if (winner == null) {
      LOGGER.warning("Received game won notification with null winner");
      return;
    }
    Platform.runLater(() -> handleWin(winner));
  }

  @Override
  public void onGameStateUpdated(BoardGame game) {
    if (game == null) {
      LOGGER.warning("onGameStateUpdated: game is null");
      return;
    }
    LOGGER.info("onGameStateUpdated: current=" +
        Optional.ofNullable(game.getCurrentPlayer())
            .map(Player::getName)
            .orElse("none") +
        ", animationRunning: " + animationRunning);

    Platform.runLater(() -> handleTurnChange(game.getCurrentPlayer()));
  }

  private void handleMove(Player player, Tile from, Tile to) {
    try {
      if (controller != null && controller.getBoardGame() != null) {
        Dice dice = controller.getBoardGame().getLastDiceRoll();
        if (dice != null && dicePanel != null) {
          dicePanel.updateDiceDisplay(dice);
          if (gameInfoPanel != null) {
            gameInfoPanel.updateDiceInfo(player, dice);
          }
        }
      }

      if (boardComponent != null) {
        animationRunning = true;
        if(playTurnButton != null) playTurnButton.setDisable(true);

        boardComponent.updatePlayerVisual(player, from, to, () -> {
          animationRunning = false;
          if (controller != null && controller.getBoardGame() != null) {
            Platform.runLater(() -> handleTurnChange(controller.getBoardGame().getCurrentPlayer()));
          } else {
            LOGGER.warning("Controller or game is null in animation callback, cannot update turn state.");
          }
        });
      } else {
        if (controller != null && controller.getBoardGame() != null) {
          Platform.runLater(() -> handleTurnChange(controller.getBoardGame().getCurrentPlayer()));
        }
      }

      if (gameInfoPanel != null) {
        gameInfoPanel.updateMoveInfo(player, from, to);
      }

      LOGGER.fine(player.getName() + " moved from " +
          (from != null ? from.getTileId() : "start") + " to " + to.getTileId());

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error handling player move in view", e);
      animationRunning = false;
      if (controller != null && controller.getBoardGame() != null) {
        Platform.runLater(() -> handleTurnChange(controller.getBoardGame().getCurrentPlayer()));
      } else {
        if(playTurnButton != null) Platform.runLater(() -> playTurnButton.setDisable(false));
      }
    }
  }

  private void handleTurnChange(Player current) {
    try {
      boolean gameIsOver = (controller == null || controller.getBoardGame() == null || controller.getBoardGame().isGameOver());
      boolean canPlay = current != null && !gameIsOver;

      if (playTurnButton != null) {
        playTurnButton.setDisable(!canPlay || animationRunning);
      }

      if (gameInfoPanel != null) {
        gameInfoPanel.updateTurnInfo(current);
      }

      if (!animationRunning) {
        if (currentPlayerPanel != null) {
          currentPlayerPanel.updateCurrentPlayerHighlight(current);
        }
      }

      LOGGER.fine("Turn changed to: " + (current != null ? current.getName() : "none") +
          ", canPlay: " + canPlay +
          ", animationRunning: " + animationRunning +
          ", gameOver: " + gameIsOver);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error handling turn change in view", e);
    }
  }

  private void handleWin(Player winner) {
    try {
      animationRunning = false;
      if (playTurnButton != null) playTurnButton.setDisable(true);
      if (dicePanel != null) {
        dicePanel.resetDiceDisplay();
        try {
          dicePanel.setDisabledVisual(true);
        } catch (Exception e) {
          LOGGER.log(Level.FINE, "Could not set disabled visual on dice panel", e);
        }
      }
      if (gameInfoPanel != null) gameInfoPanel.showWinner(winner);
      AlertHelper.showInfoAlert("Game Over!", winner.getName() + " is the winner!");
      LOGGER.info("Game won by: " + winner.getName());
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error handling game win in view", e);
      AlertHelper.showInfoAlert("Game Over!", "The game has ended. " + winner.getName() + " is the winner!");
    }
  }

  private void refreshAll(BoardGame game) {
    Objects.requireNonNull(game, "Cannot refresh view with null game model");
    try {
      if (boardComponent != null) {
        Board board = game.getBoard();
        List<Player> players = game.getPlayers();
        if (board != null && players != null) {
          boardComponent.initializeBoard(board, players);
        }
      }
      if (currentPlayerPanel != null) {
        currentPlayerPanel.initialize(game.getPlayers());
      }
      if (dicePanel != null) {
        dicePanel.resetDiceDisplay();
      }
      handleTurnChange(game.getCurrentPlayer());
      LOGGER.fine("Full view refresh completed successfully");
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error during full view refresh", e);
      throw new IllegalStateException("Failed to refresh game view", e);
    }
  }

  public void disablePlayButton() {
    Platform.runLater(() -> {
      if (playTurnButton != null) playTurnButton.setDisable(true);
    });
  }

  public void enablePlayButton() {
    Platform.runLater(() -> {
      if (playTurnButton != null) {
        boolean gameNotOver = controller != null && controller.getBoardGame() != null && !controller.getBoardGame().isGameOver();
        boolean playerExists = controller != null && controller.getBoardGame() != null && controller.getBoardGame().getCurrentPlayer() != null;
        if (gameNotOver && playerExists && !animationRunning) {
          playTurnButton.setDisable(false);
        } else {
          playTurnButton.setDisable(true);
        }
      }
    });
  }
}
