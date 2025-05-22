package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.BoardGameController;
import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.exceptions.CssLoadException;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.view.components.boardgame.BoardComponent;
import edu.ntnu.idi.idatt.view.components.boardgame.CurrentPlayerPanel;
import edu.ntnu.idi.idatt.view.components.boardgame.DicePanel;
import edu.ntnu.idi.idatt.view.components.boardgame.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.boardgame.WinDialog;
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

/**
 * Generic JavaFX view for any {@link BoardGame}.
 *
 * <p>Displays the game board, controls, and informational panels.
 * Implements {@link BoardGameObserver} to receive model updates.
 * </p>
 */
public class GenericBoardGameView implements BoardGameView, BoardGameObserver {

  private static final Logger LOGGER =
      Logger.getLogger(GenericBoardGameView.class.getName());
  private static final String CSS_PATH =
      "/edu/ntnu/idi/idatt/view/resources/GameScreen/GameScreen_styles.css";
  private static final double LAYOUT_PADDING = 10d;
  private static final double LEFT_RIGHT_SPACING = 20d;
  private static final double INNER_PADDING = 10d;
  private static final double SCENE_WIDTH = 1280d;
  private static final double SCENE_HEIGHT = 720d;

  private final Scene scene;
  private final BorderPane root;
  private final BoardComponent boardComponent;
  private final GameInfoPanel gameInfoPanel;
  private final CurrentPlayerPanel currentPlayerPanel;
  private final DicePanel dicePanel;
  private final Button playTurnButton;

  private BoardGameController controller;
  private volatile boolean animationRunning;

  /**
   * Creates a new view using the given {@link BoardRenderer} to draw the board.
   *
   * @param renderer the board renderer; must not be {@code null}
   * @throws NullPointerException if {@code renderer} is {@code null}
   */
  public GenericBoardGameView(BoardRenderer renderer) {
    Objects.requireNonNull(renderer, "Renderer cannot be null");

    root = new BorderPane();
    root.setPadding(new Insets(LAYOUT_PADDING));

    boardComponent       = new BoardComponent(renderer);
    gameInfoPanel        = new GameInfoPanel();
    currentPlayerPanel   = new CurrentPlayerPanel();
    dicePanel            = new DicePanel();
    playTurnButton       = new Button("Play Turn");

    playTurnButton.getStyleClass().add("play-turn-button");
    playTurnButton.setPrefSize(150d, 50d);
    playTurnButton.setDisable(true);

    configureLayout();
    scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    loadStyles();

    LOGGER.info("GenericBoardGameView constructed successfully");
  }

  /**
   * Returns the JavaFX {@link Scene} containing this view.
   *
   * @return the scene
   */
  @Override
  public Scene getScene() {
    return scene;
  }

  /**
   * Binds the given controller to this view and attaches UI event handlers.
   *
   * @param controller the game controller; must not be {@code null}
   * @throws NullPointerException if {@code controller} is {@code null}
   */
  @Override
  public void setController(BoardGameController controller) {
    this.controller = Objects.requireNonNull(controller, "Controller cannot be null");
    bindEventHandlers();
    LOGGER.info("Controller successfully bound to view");
  }

  private void configureLayout() {
    root.setCenter(boardComponent);
    BorderPane.setAlignment(boardComponent, Pos.CENTER);

    VBox leftControls = new VBox(LEFT_RIGHT_SPACING, gameInfoPanel, playTurnButton);
    VBox.setVgrow(gameInfoPanel, Priority.ALWAYS);
    leftControls.setPadding(new Insets(0, INNER_PADDING, 0, 0));
    root.setLeft(leftControls);

    VBox rightControls = new VBox(LEFT_RIGHT_SPACING, currentPlayerPanel, dicePanel);
    VBox.setVgrow(currentPlayerPanel, Priority.SOMETIMES);
    rightControls.setPadding(new Insets(0, 0, 0, INNER_PADDING));
    root.setRight(rightControls);
  }

  private void loadStyles() {
    try {
      String css = ResourceLoader.loadCssResource(CSS_PATH);
      scene.getStylesheets().add(css);
      LOGGER.info(() -> "Successfully loaded CSS stylesheet: " + CSS_PATH);
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING,
          String.format("Could not load CSS: %s", e.getMessage()), e);
      AlertHelper.showWarningAlert(
          "Style Warning",
          "Game styles could not be loaded. Using default JavaFX styles."
      );
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Unexpected error loading CSS resources", e);
      throw new CssLoadException(
          "Failed to load critical UI resources: " + CSS_PATH, e
      );
    }
  }

  private void bindEventHandlers() {
    playTurnButton.setOnAction(e -> {
      try {
        LOGGER.fine("Play turn button clicked.");
        if (controller != null && !animationRunning) {
          playTurnButton.setDisable(true);
          dicePanel.resetDiceDisplay();
          controller.onPlayTurn();
        } else {
          LOGGER.fine("Play turn click ignored; controller null or animation running.");
        }
      } catch (Exception ex) {
        LOGGER.log(Level.SEVERE,
            "Error processing turn from button click", ex);
        AlertHelper.showErrorAlert(
            "Turn Error",
            "An error occurred while processing the turn: " + ex.getMessage()
        );
        Platform.runLater(this::updateUiForCurrentTurn);
      }
    });
  }

  /**
   * Initializes the view with the given game model.
   *
   * <p>Registers this view as an observer, resets the dice panel,
   * refreshes all UI elements, and enables the turn button if appropriate.
   * </p>
   *
   * @param game the game model; must not be {@code null}
   * @throws IllegalStateException if initialization fails
   */
  public void initializeView(BoardGame game) {
    Objects.requireNonNull(game, "Game model cannot be null for initialization");
    try {
      game.addObserver(this);
      dicePanel.resetDiceDisplay();
      refreshAll(game);
      animationRunning = false;
      Platform.runLater(this::updateUiForCurrentTurn);
      LOGGER.info("View successfully initialized with game model.");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Failed to initialize view with game model", e);
      AlertHelper.showErrorAlert(
          "Initialization Error",
          "Failed to initialize game view: " + e.getMessage()
      );
      throw new IllegalStateException("View initialization failed", e);
    }
  }

  @Override
  public void onPlayerMoved(Player player, Tile from, Tile to) {
    if (player == null || to == null) {
      LOGGER.warning("Received invalid player move notification.");
      Platform.runLater(this::updateUiForCurrentTurn);
      return;
    }
    Platform.runLater(() -> handleMoveAnimation(player, from, to));
  }

  @Override
  public void onActionTileEffect(Player player,
      Tile fromTriggerTile,
      Tile toDestinationTile) {
    Platform.runLater(() -> {
      if (player == null
          || fromTriggerTile == null
          || fromTriggerTile.getLandAction() == null) {
        LOGGER.warning(
            "onActionTileEffect called with invalid parameters."
        );
        updateUiForCurrentTurn();
        return;
      }
      String desc = fromTriggerTile.getLandAction().getDescription();
      Tile infoTile = (toDestinationTile != null
          && fromTriggerTile.getTileId() != toDestinationTile.getTileId())
          ? toDestinationTile
          : fromTriggerTile;
      gameInfoPanel.updateActionInfo(player, desc, infoTile);

      boolean noMove = (toDestinationTile == null)
          || toDestinationTile.getTileId() == fromTriggerTile.getTileId();
      if (noMove) {
        LOGGER.fine("Action effect resulted in no new tile; updating UI.");
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
        boardComponent.addPlayerVisual(player);
        currentPlayerPanel.addPlayerEntry(player);
        gameInfoPanel.logEvent(
            String.format("%s joined the game.", player.getName())
        );
        LOGGER.fine(() -> "Added player to view: " + player.getName());
      } catch (Exception e) {
        LOGGER.log(Level.WARNING,
            String.format("Error adding player %s to view.", player.getName()),
            e);
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
    LOGGER.info(() -> "onGameStateUpdated; animationRunning=" + animationRunning);
    Platform.runLater(this::updateUiForCurrentTurn);
  }

  private Runnable createVisualCompletionHandler(
      Player playerWhoMoved,
      Tile tileVisuallyLandedOn
  ) {
    return () -> {
      LOGGER.fine(() ->
          String.format(
              "Visual complete for %s on tile %s",
              playerWhoMoved.getName(),
              tileVisuallyLandedOn != null
                  ? String.valueOf(tileVisuallyLandedOn.getTileId())
                  : "unknown"
          )
      );
      animationRunning = false;
      updateUiForCurrentTurn();
    };
  }

  private void handleMoveAnimation(
      Player player,
      Tile from,
      Tile to
  ) {
    try {
      Dice lastDice = controller.getBoardGame().getLastDiceRoll();
      if (lastDice != null) {
        dicePanel.updateDiceDisplay(lastDice);
        gameInfoPanel.updateDiceInfo(player, lastDice);
      }
      if (boardComponent != null) {
        animationRunning = true;
        playTurnButton.setDisable(true);
        boardComponent.updatePlayerVisual(
            player,
            from,
            to,
            createVisualCompletionHandler(player, to)
        );
      } else {
        LOGGER.warning(
            "BoardComponent is null; skipping animation."
        );
        animationRunning = false;
        updateUiForCurrentTurn();
      }
      gameInfoPanel.updateMoveInfo(player, from, to);
      LOGGER.fine(() ->
          String.format(
              "Initiated move animation for %s from %s to %s",
              player.getName(),
              from != null ? from.getTileId() : "start",
              to   != null ? to.getTileId()   : "unknown"
          )
      );
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Error handling player move animation", e);
      animationRunning = false;
      Platform.runLater(this::updateUiForCurrentTurn);
    }
  }

  private void updateUiForCurrentTurn() {
    LOGGER.fine(() ->
        "updateUiForCurrentTurn called; animationRunning=" + animationRunning
    );
    Player current = null;
    boolean gameOver = true;
    if (controller != null && controller.getBoardGame() != null) {
      current  = controller.getBoardGame().getCurrentPlayer();
      gameOver = controller.getBoardGame().isGameOver();
    } else {
      LOGGER.warning(
          "Controller or BoardGame is null; cannot update UI state."
      );
    }
    boolean canAct = current != null && !gameOver;
    if (playTurnButton != null) {
      playTurnButton.setDisable(!canAct || animationRunning);
      LOGGER.fine(() ->
          String.format(
              "Play button disabled=%s (canAct=%s, animationRunning=%s)",
              playTurnButton.isDisabled(), canAct, animationRunning
          )
      );
    }
    gameInfoPanel.updateTurnInfo(current);
    if (!animationRunning) {
      currentPlayerPanel.updateCurrentPlayerHighlight(current);
    }
  }

  private void handleWin(Player winner) {
    playTurnButton.setDisable(true);
    dicePanel.resetDiceDisplay();
    dicePanel.setDisabledVisual(true);
    gameInfoPanel.showWinner(winner);
    LOGGER.info(() ->
        "Game won by: " + winner.getName() + "; showing WinDialog."
    );

    Optional<ButtonType> result = getButtonType(winner);
    if (result.isEmpty() && controller != null) {
      LOGGER.info("WinDialog closed with no selection; returning to main menu.");
      controller.requestGoToMainMenu();
    }
  }

  private Optional<ButtonType> getButtonType(Player winner) {
    WinDialog winDialog = new WinDialog(winner.getName());
    Optional<ButtonType> result = winDialog.showAndWait();
    result.ifPresent(buttonType -> {
      LOGGER.info(() ->
          "WinDialog selection: " + buttonType.getText()
              + " (ButtonData: " + buttonType.getButtonData() + ")"
      );
      if (controller != null) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
          controller.requestGoToGameSelection();
        } else {
          controller.requestGoToMainMenu();
        }
      } else {
        LOGGER.warning("Controller is null; cannot process dialog choice.");
      }
    });
    return result;
  }

  private void refreshAll(BoardGame game) {
    Objects.requireNonNull(game, "Game model cannot be null for refreshAll");
    try {
      Board board = game.getBoard();
      List<Player> players = game.getPlayers();
      if (board != null && players != null) {
        boardComponent.initializeBoard(board, players);
      } else {
        LOGGER.warning(
            "Board or player list is null; skipping board init."
        );
      }
      currentPlayerPanel.initialize(game.getPlayers());
      dicePanel.resetDiceDisplay();
      LOGGER.fine("Full view refresh completed.");
    } catch (Exception e) {
      LOGGER.log(Level.WARNING,
          "Error during full view refresh", e);
    }
  }
}
