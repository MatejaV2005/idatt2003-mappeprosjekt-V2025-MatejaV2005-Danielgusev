package edu.ntnu.idi.idatt.view.components.boardgame;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import edu.ntnu.idi.idatt.view.renderer.BoardRenderer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * A JavaFX {@link Pane} that renders a {@link Board}
 * and manages the visual tokens for each {@link Player}.
 *
 * <p>All rendering is delegated to the injected {@link BoardRenderer}. Player-token updates are
 * scheduled on the JavaFX Application Thread.
 * </p>
 *
 * <p>KI-assistanse (Gemini 2.5 Pro) ble benyttet som sparringspartner for å utvikle og
 * forbedre logikken knyttet til håndtering av spiller-tokens, inkludert
 * tillegging, umiddelbar oppdatering og animert oppdatering av deres
 * visuelle representasjon på brettet. Spesifikke metoder hvor
 * KI-assistanse var sentral er kommentert deretter.
 * Dato for assistanse: 29-04-25
 */
public class BoardComponent extends Pane {

  private static final Logger LOGGER = Logger.getLogger(BoardComponent.class.getName());

  private final BoardRenderer renderer;
  private final Map<Player, Node> playerTokens;
  private Board currentBoard;

  /**
   * Constructs a new {@code BoardComponent} using the given {@link BoardRenderer}.
   *
   * <p>Applies the CSS style class {@code "board-component"} and sets minimum
   * and preferred sizes of 400×400 and 600×600 pixels, respectively.
   * </p>
   *
   * @param renderer the {@link BoardRenderer} used to draw the board; must not be {@code null}
   * @throws NullPointerException if {@code renderer} is {@code null}
   */
  public BoardComponent(BoardRenderer renderer) {
    ExceptionHandling.requireNonNull(renderer, "Renderer cannot be null");
    this.renderer = renderer;
    this.playerTokens = new HashMap<>();
    this.getStyleClass().add("board-component");
    this.setMinSize(400, 400);
    this.setPrefSize(600, 600);
  }

  /**
   * Initializes this component with the specified board model and players.
   *
   * <p>Clears any existing children, renders the static board, and places each
   * player's token at its current {@link Player#getCurrentTile()}.  All UI work
   * is deferred to the JavaFX Application Thread.
   * </p>
   *
   * @param board the non‐{@code null} {@link Board} model to display
   * @param players the non‐{@code null} list of {@link Player}s to render (may be empty)
   * @throws NullPointerException if {@code board} or {@code players} is {@code null}
   * @see #runInitialize(Board, List)
   */
  public void initializeBoard(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "Board cannot be null");
    ExceptionHandling.requireNonNull(players, "Players list cannot be null");
    Platform.runLater(() -> runInitialize(board, players));
  }

  /**
   * Schedules rendering of the board and tokens when this component has a valid size.
   *
   * <p>If size is already known, {@link #renderBoardAndTokens(List)} is invoked immediately;
   * otherwise a listener is registered to fire once this pane is laid out.
   * </p>
   *
   * @param board   the {@link Board} to render
   * @param players the list of {@link Player}s whose tokens will be added
   */
  private void runInitialize(Board board, List<Player> players) {
    logPlayerCount(players);
    this.currentBoard = board;
    getChildren().clear();
    playerTokens.clear();

    if (isSizeKnown()) {
      renderBoardAndTokens(players);
    } else {
      deferRenderingUntilSized(players);
    }

    LOGGER.info("Board initialization complete (or deferred pending layout).");
  }

  /**
   * Logs a message indicating how many players are about to be initialized.
   *
   * @param players the list of players (never {@code null})
   */
  private void logPlayerCount(List<Player> players) {
    if (players.isEmpty()) {
      LOGGER.warning("Initializing board component with an empty player list.");
    } else {
      LOGGER.log(Level.INFO, "Initializing board component with {0} players", players.size());
    }
  }

  /**
   * Returns {@code true} if this pane has a non‐zero width and height.
   *
   * @return whether this component has been laid out
   */
  private boolean isSizeKnown() {
    return getWidth() > 0 && getHeight() > 0;
  }

  /**
   * Renders the static board and then places each player's token.
   *
   * @param players the list of players whose tokens will be shown
   */
  private void renderBoardAndTokens(List<Player> players) {
    renderer.renderInitialBoard(this, currentBoard);
    for (Player p : players) {
      addPlayerVisual(p);
    }
  }

  /**
   * Defers {@link #renderBoardAndTokens(List)} until this pane has a known size.
   *
   * @param players the list of players to render after layout
   */
  private void deferRenderingUntilSized(List<Player> players) {
    LOGGER.warning(
        "BoardComponent size not determined during initializeBoard. "
            + "Rendering will be deferred until layout pass.");

    this.widthProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.doubleValue() > 0
          && getHeight() > 0
          && getChildren().isEmpty()) {
        LOGGER.info("Rendering board after size determined.");
        renderBoardAndTokens(players);
      }
    });
  }

  /**
   * Adds a visual token for the given {@link Player} to this board.
   *
   * <p>If the player's {@link Player#getCurrentTile()} is {@code null}, it will
   * attempt to default to tile ID 1. All modifications to the scene graph are
   * performed on the JavaFX Application Thread via {@code Platform.runLater}.
   * </p>
   *
   * <p>KI-assistanse (Gemini 2.5 Pro) ble brukt for å:
   * - Strukturere logikken for å håndtere tilfellet der spilleren ikke har en
   * {@code currentTile} (fallback til startflis).
   * - Sikre at token-opprettelse og plassering skjer på JavaFX Application Thread
   * ved hjelp av {@code Platform.runLater}.
   * - Utvikle logikk for å sjekke om spilleren allerede har et token, og i så fall
   * oppdatere posisjonen i stedet for å legge til et nytt.
   * Dato: 29-04-25
   *
   * @param player the {@link Player} whose token is to be added; must not be {@code null}
   * @throws NullPointerException if {@code player} is {@code null}
   */
  public void addPlayerVisual(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    Tile currentTile = player.getCurrentTile();
    if (currentTile == null) {
      LOGGER.warning("Cannot add visual for player "
          + player.getName() + ": Player has no current tile set.");
      if (currentBoard != null) {
        currentTile = currentBoard.getTileById(1);
        if (currentTile != null) {
          LOGGER.info("Placing player "
              + player.getName() + " on starting tile (ID 1).");
        } else {
          LOGGER.severe("Cannot place player "
              + player.getName() + " on starting tile: Tile 1 not found.");
          return;
        }
      } else {
        LOGGER.severe("Cannot add visual for player "
            + player.getName() + ": No current tile and board not initialized.");
        return;
      }
    }

    final Tile placementTile = currentTile;

    Platform.runLater(() -> {
      if (playerTokens.containsKey(player)) {
        LOGGER.warning("Player " + player.getName()
            + " already has a visual token. Updating position instead.");
        updatePlayerVisual(player, placementTile);
        return;
      }

      Node tokenNode = renderer.createPlayerTokenNode(player);
      playerTokens.put(player, tokenNode);
      this.getChildren().add(tokenNode);
      LOGGER.fine("Added visual token for player: " + player.getName());

      renderer.placePlayerTokenAtTile(tokenNode, placementTile, this);
    });
  }

  /**
   * Immediately moves the existing visual token for {@code player} to {@code newTile}
   * without animation.
   *
   * <p>If this pane has no size yet, positioning may be incorrect. This call is
   * internally wrapped in {@code Platform.runLater} to ensure it executes on the
   * JavaFX Application Thread.
   * </p>
   *
   * <p>KI-assistanse (Gemini 2.5 Pro) bidro til:
   * - Implementeringen av å hente spillerens token fra {@code playerTokens}.
   * - Å sikre at oppdateringen kjøres på JavaFX Application Thread med {@code Platform.runLater}.
   * - Logikken for å kalle rendererens metode for umiddelbar posisjonsoppdatering
   * ({@code renderer.updatePlayerTokenPosition(tokenNode, newTile, this, null)}).
   * - Håndtering av logging for feilsituasjoner (f.eks. token ikke funnet).
   * Dato: 29-04-25
   *
   * @param player  the {@link Player} whose token to move; must not be {@code null}
   * @param newTile the target {@link Tile} for the token; must not be {@code null}
   * @throws NullPointerException if {@code player} or {@code newTile} is {@code null}
   */
  public void updatePlayerVisual(Player player, Tile newTile) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    ExceptionHandling.requireNonNull(newTile, "Tile cannot be null");

    Platform.runLater(() -> {
      Node tokenNode = playerTokens.get(player);
      if (tokenNode != null) {
        LOGGER.fine("Moving player " + player.getName()
            + " visual to tile " + newTile.getTileId());
        if (this.getWidth() <= 0 || this.getHeight() <= 0) {
          LOGGER.warning("BoardComponent size not determined during updatePlayerVisual. "
              + "Animation might be incorrect.");
        }
        renderer.updatePlayerTokenPosition(tokenNode, newTile, this, null);
      } else {
        LOGGER.warning("Cannot update visual for player "
            + player.getName() + ": token not found in map.");
      }
    });
  }

  /**
   /**
   * Animates moving the visual token for {@code player} from {@code fromTile} to {@code toTile}.
   *
   * <p>When the animation completes, the {@code onAnimationComplete} {@link Runnable} (if non-null)
   * will be invoked. All work is scheduled on the JavaFX Application Thread.
   * </p>
   *
   * <p>KI-assistanse (Gemini 2.5 Pro) ble brukt for å:
   * - Strukturere kallet til rendererens animasjonsmetode
   * ({@code renderer.updatePlayerTokenPosition}) for å starte animasjonen.
   * - Sikre at hele operasjonen, inkludert kall til renderer, skjer på
   * JavaFX Application Thread via {@code Platform.runLater}.
   * - Håndtere logging og feilsjekking (f.eks. hvis token ikke finnes i {@code playerTokens}).
   * Dato: 29-04-25
   *
   * @param player the {@link Player} whose token is to be animated; must not be {@code null}
   * @param fromTile the starting {@link Tile}; may be {@code null} to indicate no prior placement
   * @param toTile the destination {@link Tile} for the animation; must not be {@code null}
   * @param onAnimationComplete  an optional {@link Runnable} to run
   *                             after animation finishes; may be {@code null}
   *
   * @throws NullPointerException if {@code toTile} is {@code null}
   */
  public void updatePlayerVisual(
      Player player,
      Tile fromTile,
      Tile toTile,
      Runnable onAnimationComplete
  ) {
    ExceptionHandling.requireNonNull(toTile, "toTile cannot be null");
    LOGGER.log(
        Level.FINE,
        "Updating visual for {0} from {1} to {2}",
        new Object[] {
            player.getName(),
            fromTile != null ? fromTile.getTileId() : "start",
            toTile.getTileId()
        });

    Platform.runLater(() -> {
      Node tokenNode = playerTokens.get(player);
      if (tokenNode != null) {
        renderer.updatePlayerTokenPosition(
            tokenNode,
            toTile,
            this,
            onAnimationComplete
        );
      } else {
        LOGGER.warning("Cannot update visual for player "
            + player.getName() + ": token not found in map.");
      }
    });
  }
}
