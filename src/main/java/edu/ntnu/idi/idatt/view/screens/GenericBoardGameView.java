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
import edu.ntnu.idi.idatt.view.components.boardGame.WinDialog;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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

    this.root = new BorderPane();
    this.root.setPadding(new Insets(10));

    this.boardComponent = new BoardComponent(renderer);
    this.gameInfoPanel = new GameInfoPanel();
    this.currentPlayerPanel = new CurrentPlayerPanel();
    this.dicePanel = new DicePanel();
    this.playTurnButton = new Button("Play Turn");

    playTurnButton.getStyleClass().add("play-turn-button");
    playTurnButton.setPrefSize(150, 50);
    playTurnButton.setDisable(true);

    configureLayout();
    this.scene = new Scene(root, 1280, 720);
    loadStyles();
    LOGGER.info("GenericBoardGameView constructed successfully");
  }

  private void configureLayout() {
    root.setCenter(boardComponent);
    BorderPane.setAlignment(boardComponent, Pos.CENTER);

    VBox leftControls = new VBox(20, gameInfoPanel, playTurnButton);
    VBox.setVgrow(gameInfoPanel, Priority.ALWAYS);
    leftControls.setPadding(new Insets(0, 10, 0, 0));
    root.setLeft(leftControls);

    VBox rightControls = new VBox(20, currentPlayerPanel, dicePanel);
    VBox.setVgrow(currentPlayerPanel, Priority.SOMETIMES);
    rightControls.setPadding(new Insets(0, 0, 0, 10));
    root.setRight(rightControls);
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
      throw new CssLoadException("Failed to load critical UI resources: " + CSS_PATH, e);
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
    playTurnButton.setOnAction(e -> {
      try {
        LOGGER.fine("Play turn button clicked.");
        if (controller != null && !animationRunning) {
          playTurnButton.setDisable(true);
          if (dicePanel != null) {
            dicePanel.resetDiceDisplay();
          }
          controller.onPlayTurn();
        } else {
          LOGGER.fine("Play turn button click ignored: controller null or animation running.");
        }
      } catch (Exception ex) {
        LOGGER.log(Level.SEVERE, "Error processing turn from button click", ex);
        AlertHelper.showErrorAlert("Turn Error", "An error occurred while processing the turn: " + ex.getMessage());
        Platform.runLater(this::updateUiForCurrentTurn);
      }
    });
  }

  public void initializeView(BoardGame game) {
    Objects.requireNonNull(game, "Game model cannot be null for initialization");
    try {
      game.addObserver(this);
      if (dicePanel != null) {
        dicePanel.resetDiceDisplay();
      }
      refreshAll(game);
      animationRunning = false;
      Platform.runLater(this::updateUiForCurrentTurn);
      LOGGER.info("View successfully initialized with game model.");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize view with game model", e);
      AlertHelper.showErrorAlert("Initialization Error", "Failed to initialize game view: " + e.getMessage());
      throw new IllegalStateException("View initialization failed", e);
    }
  }

  @Override
  public void onPlayerMoved(Player player, Tile from, Tile to) {
    if (player == null || to == null) {
      LOGGER.warning("Received invalid player move notification (player or 'to' tile is null). Attempting to update UI.");
      Platform.runLater(this::updateUiForCurrentTurn);
      return;
    }
    Platform.runLater(() -> handleMoveAnimation(player, from, to));
  }

  @Override
  public void onActionTileEffect(Player player, Tile fromTriggerTile, Tile toDestinationTile) {
    Platform.runLater(() -> {
      if (player == null || fromTriggerTile == null || fromTriggerTile.getLandAction() == null) {
        LOGGER.warning("onActionTileEffect called with invalid parameters. Attempting to update UI.");
        updateUiForCurrentTurn();
        return;
      }

      if (gameInfoPanel != null) {
        String actionDescription = fromTriggerTile.getLandAction().getDescription();
        Tile infoTile = (toDestinationTile != null && fromTriggerTile.getTileId() != toDestinationTile.getTileId()) ? toDestinationTile : fromTriggerTile;
        gameInfoPanel.updateActionInfo(player, actionDescription, infoTile);
      }

      boolean actionResultedInNoNewTilePosition = (toDestinationTile == null) || (toDestinationTile.getTileId() == fromTriggerTile.getTileId());

      if (actionResultedInNoNewTilePosition) {
        LOGGER.fine("Action tile effect resulted in no new tile position. Updating UI for current turn.");
        updateUiForCurrentTurn();
      }
    });
  }

  @Override
  public void onPlayerAdded(Player player) {
    if (player == null) {
      LOGGER.warning("Received notification of null player being added.");
      return;
    }
    Platform.runLater(() -> {
      try {
        if (boardComponent != null) boardComponent.addPlayerVisual(player);
        if (currentPlayerPanel != null) currentPlayerPanel.addPlayerEntry(player);
        if (gameInfoPanel != null) gameInfoPanel.logEvent(player.getName() + " joined the game.");
        LOGGER.fine("Added player to view: " + player.getName());
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Error adding player " + player.getName() + " to view.", e);
      }
    });
  }

  @Override
  public void onGameWon(Player winner) {
    if (winner == null) {
      LOGGER.warning("Received game won notification with null winner.");
      return;
    }

    Platform.runLater(() -> {
      animationRunning = false;
      handleWin(winner);
    });
  }

  @Override
  public void onGameStateUpdated(BoardGame game) {
    if (game == null) {
      LOGGER.warning("onGameStateUpdated: game model is null.");
      return;
    }
    LOGGER.info("onGameStateUpdated received. Current animationRunning state: " + animationRunning);
    Platform.runLater(this::updateUiForCurrentTurn);
  }

  private Runnable createVisualCompletionHandler(Player playerWhoMoved, Tile tileVisuallyLandedOn) {
    return () -> {
      LOGGER.fine("Visual sequence for " + playerWhoMoved.getName() +
          " visually landing on " + (tileVisuallyLandedOn != null ? tileVisuallyLandedOn.getTileId() : "unknown") +
          " has completed.");
      animationRunning = false;

      updateUiForCurrentTurn();
    };
  }


  private void handleMoveAnimation(Player player, Tile from, Tile to) {
    try {
      if (controller != null && controller.getBoardGame() != null) {
        Dice dice = controller.getBoardGame().getLastDiceRoll();
        if (dice != null && dicePanel != null) {
          dicePanel.updateDiceDisplay(dice);
        }
        if (gameInfoPanel != null && dice != null) {
          gameInfoPanel.updateDiceInfo(player, dice);
        }
      }

      if (boardComponent != null) {
        animationRunning = true;
        playTurnButton.setDisable(true);

        boardComponent.updatePlayerVisual(player, from, to, createVisualCompletionHandler(player, to));

      } else {
        LOGGER.warning("BoardComponent is null. Cannot animate move. Updating UI directly.");
        animationRunning = false;
        updateUiForCurrentTurn();
      }

      if (gameInfoPanel != null) {
        gameInfoPanel.updateMoveInfo(player, from, to);
      }
      LOGGER.fine("Initiating move animation for " + player.getName() + " from " +
          (from != null ? from.getTileId() : "start") + " to " + (to != null ? to.getTileId() : "unknown"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error handling player move animation", e);
      animationRunning = false;
      Platform.runLater(this::updateUiForCurrentTurn);
    }
  }

  private void updateUiForCurrentTurn() {
    LOGGER.fine("updateUiForCurrentTurn called. animationRunning: " + animationRunning);
    Player currentPlayerFromModel = null;
    boolean gameIsEffectivelyOver = true;

    if (controller != null && controller.getBoardGame() != null) {
      currentPlayerFromModel = controller.getBoardGame().getCurrentPlayer();
      gameIsEffectivelyOver = controller.getBoardGame().isGameOver();
    } else {
      LOGGER.warning("Controller or BoardGame is null in updateUiForCurrentTurn. Assuming game cannot proceed.");
    }

    boolean canPlayerAct = currentPlayerFromModel != null && !gameIsEffectivelyOver;

    if (playTurnButton != null) {
      playTurnButton.setDisable(!canPlayerAct || animationRunning);
      LOGGER.fine("Play button disabled state: " + playTurnButton.isDisabled() +
          " (canPlayerAct: " + canPlayerAct + ", animationRunning: " + animationRunning + ")");
    }

    if (gameInfoPanel != null) {
      gameInfoPanel.updateTurnInfo(currentPlayerFromModel);
    }

    if (!animationRunning && currentPlayerPanel != null) {
      currentPlayerPanel.updateCurrentPlayerHighlight(currentPlayerFromModel);
    }
  }


  private void handleWin(Player winner) {
    if (playTurnButton != null) playTurnButton.setDisable(true);

    if (dicePanel != null) {
      dicePanel.resetDiceDisplay();
      try {
        dicePanel.setDisabledVisual(true);
      } catch (Exception e) {
        LOGGER.log(Level.FINE, "Could not set disabled visual on dice panel during win.", e);
      }
    }
    if (gameInfoPanel != null) gameInfoPanel.showWinner(winner);
    LOGGER.info("Game won by: " + winner.getName() + ". Displaying WinDialog.");

    Optional<ButtonType> result = getButtonType(winner);

    if (!result.isPresent() && controller != null) {
      LOGGER.info("WinDialog closed without button action. Defaulting to main menu.");
      controller.requestGoToMainMenu();
    }
  }

  private Optional<ButtonType> getButtonType(Player winner) {
    WinDialog winDialog = new WinDialog(winner.getName());
    Optional<ButtonType> result = winDialog.showAndWait();

    result.ifPresent(buttonType -> {
      LOGGER.info("WinDialog selection: " + buttonType.getText() + " (ButtonData: " + buttonType.getButtonData() + ")");
      if (controller != null) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
          controller.requestGoToGameSelection();
        } else if (buttonType.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
          controller.requestGoToMainMenu();
        } else {
          controller.requestGoToMainMenu();
        }
      } else {
        LOGGER.warning("Controller is null after WinDialog. Cannot process dialog choice.");
      }
    });
    return result;
  }

  private void refreshAll(BoardGame game) {
    Objects.requireNonNull(game, "Game model cannot be null for refreshAll");
    try {
      if (boardComponent != null) {
        Board board = game.getBoard();
        List<Player> players = game.getPlayers();
        if (board != null && players != null) {
          boardComponent.initializeBoard(board, players);
        } else {
          LOGGER.warning("Board or player list is null during refreshAll. BoardComponent might not initialize correctly.");
        }
      }
      if (currentPlayerPanel != null) {
        currentPlayerPanel.initialize(game.getPlayers());
      }
      if (dicePanel != null) {
        dicePanel.resetDiceDisplay();
      }
      LOGGER.fine("Full view refresh completed.");
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error during full view refresh", e);
    }
  }
}