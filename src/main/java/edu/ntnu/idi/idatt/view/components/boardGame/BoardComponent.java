package edu.ntnu.idi.idatt.view.components.boardGame;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import edu.ntnu.idi.idatt.view.renderer.BoardRenderer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * JavaFX component for displaying and managing a board game visual representation.
 * This component uses a provided BoardRenderer to handle the specific rendering
 * strategy while managing the player tokens and their positions. It acts as a container
 * for the board visuals and player tokens within the GUI.
 */
public class BoardComponent extends Pane {
  private static final Logger LOGGER = Logger.getLogger(BoardComponent.class.getName());

  private final BoardRenderer renderer;
  private final Map<Player, Node> playerTokens;
  private Board currentBoard;

  /**
   * Creates a new BoardComponent with the specified renderer.
   *
   * @param renderer The board renderer implementation to use for drawing.
   * @throws NullPointerException if renderer is null.
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
   * Initializes the board visualization with the specified model and players.
   * This method clears any existing visuals, renders the static board elements
   * using the BoardRenderer, and then adds visual tokens for each player,
   * positioning them on their starting tiles. This should typically be called
   * once when the game view is first displayed. Ensures UI updates occur
   * on the JavaFX Application Thread.
   *
   * @param board The board data model to visualize.
   * @param players The list of players participating in the game.
   * @throws NullPointerException if board or players is null.
   */
  public void initializeBoard(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "Board cannot be null");
    ExceptionHandling.requireNonNull(players, "Players list cannot be null");

    Platform.runLater(() -> {
      if (players.isEmpty()) {
        LOGGER.warning("Initializing board component with an empty player list.");
      } else {
        LOGGER.info("Initializing board component with " + players.size() + " players");
      }

      this.currentBoard = board;
      this.getChildren().clear();
      playerTokens.clear();

      if (this.getWidth() > 0 && this.getHeight() > 0) {
        renderer.renderInitialBoard(this, currentBoard);
      } else {
        LOGGER.warning("BoardComponent size not determined during initializeBoard. Rendering might be delayed or use estimated sizes.");
        this.widthProperty().addListener((obs, oldVal, newVal) -> {
          if (newVal.doubleValue() > 0 && this.getHeight() > 0 && this.getChildren().isEmpty()) {
            LOGGER.info("Rendering board after size determined.");
            renderer.renderInitialBoard(this, currentBoard);
            for (Player p : players) {
              addPlayerVisual(p);
            }
          }
        });
      }

      for (Player player : players) {
        addPlayerVisual(player);
      }
      LOGGER.info("Board initialization complete (or deferred pending layout).");
    });
  }

  /**
   * Adds a visual token representation for a single player to the board.
   * If the player already has a token, its position is updated instead.
   * The token is created using the BoardRenderer, added to this Pane,
   * stored internally, and positioned on the player's current tile.
   * Ensures UI updates occur on the JavaFX Application Thread.
   *
   * @param player The player for whom to add a visual token.
   * @throws NullPointerException if player is null.
   */
  public void addPlayerVisual(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    Tile currentTile = player.getCurrentTile();
    if (currentTile == null) {
      LOGGER.warning("Cannot add visual for player " + player.getName() + ": Player has no current tile set.");
      if (currentBoard != null) {
        currentTile = currentBoard.getTileById(1);
        if(currentTile != null) {
          LOGGER.info("Placing player " + player.getName() + " on starting tile (ID 1).");
        } else {
          LOGGER.severe("Cannot place player " + player.getName() + " on starting tile: Tile 1 not found.");
          return;
        }
      } else {
        LOGGER.severe("Cannot add visual for player " + player.getName() + ": No current tile and board not initialized.");
        return;
      }
    }

    final Tile placementTile = currentTile;

    Platform.runLater(() -> {
      if (playerTokens.containsKey(player)) {
        LOGGER.warning("Player " + player.getName() + " already has a visual token. Updating position instead.");
        updatePlayerVisual(player, placementTile);
        return;
      }

      Node tokenNode = renderer.createPlayerTokenNode(player);
      playerTokens.put(player, tokenNode);
      this.getChildren().add(tokenNode);
      LOGGER.fine("Added visual token for player: " + player.getName());
      renderer.updatePlayerTokenPosition(tokenNode, placementTile, this);
    });
  }

  /**
   * Updates the visual position of a player's token, animating the movement.
   * This method primarily delegates the task to the simpler update method,
   * as the current renderer handles animation based on the target tile.
   * Ensures UI updates occur on the JavaFX Application Thread.
   *
   * @param player The player whose token is moving.
   * @param fromTile The tile the player is moving from (for potential future use).
   * @param toTile The destination tile for the player's token.
   * @throws NullPointerException if player or toTile is null.
   */
  public void updatePlayerVisual(Player player, Tile fromTile, Tile toTile) {
    ExceptionHandling.requireNonNull(toTile, "toTile cannot be null"); // fromTile can be null if starting
    LOGGER.fine("Updating visual for " + player.getName() + " from " + (fromTile != null ? fromTile.getTileId() : "start") + " to " + toTile.getTileId());
    updatePlayerVisual(player, toTile);
  }

  /**
   * Directly updates the visual position of a player's token to a specific target tile.
   * Retrieves the player's token node and instructs the BoardRenderer to move it.
   * Ensures UI updates occur on the JavaFX Application Thread.
   *
   * @param player The player whose token position needs updating.
   * @param newTile The target tile to move the token to.
   * @throws NullPointerException if player or newTile is null.
   */
  public void updatePlayerVisual(Player player, Tile newTile) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    ExceptionHandling.requireNonNull(newTile, "Tile cannot be null");

    Platform.runLater(() -> {
      Node tokenNode = playerTokens.get(player);
      if (tokenNode != null) {
        LOGGER.fine("Moving player " + player.getName() + " visual to tile " + newTile.getTileId());
        if (this.getWidth() <= 0 || this.getHeight() <= 0) {
          LOGGER.warning("BoardComponent size not determined during updatePlayerVisual. Animation might be incorrect.");
        }
        renderer.updatePlayerTokenPosition(tokenNode, newTile, this);
      } else {
        LOGGER.warning("Cannot update visual for player " + player.getName() + ": token not found in map.");
      }
    });
  }

  /**
   * Removes a player's visual token from the board display and internal tracking.
   * Ensures UI updates occur on the JavaFX Application Thread.
   *
   * @param player The player whose token should be removed.
   * @throws NullPointerException if player is null.
   */
  public void removePlayerVisual(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    Platform.runLater(() -> {
      Node tokenNode = playerTokens.remove(player);
      if (tokenNode != null) {
        this.getChildren().remove(tokenNode);
        LOGGER.fine("Removed visual token for player " + player.getName());
      } else {
        LOGGER.warning("Could not remove visual token for player " + player.getName() + ": token not found.");
      }
    });
  }

  /**
   * Refreshes the visual positions of all currently displayed player tokens
   * based on their state in the game model. Useful for ensuring visual consistency
   * after potentially complex state changes or loading a game.
   * Ensures UI updates occur on the JavaFX Application Thread.
   */
  public void updateBoardVisuals() {
    LOGGER.info("Updating all board visuals (refreshing player positions).");
    Map<Player, Node> currentTokens = new HashMap<>(playerTokens);
    for (Player player : currentTokens.keySet()) {
      Tile currentTile = player.getCurrentTile();
      if (currentTile != null) {
        updatePlayerVisual(player, currentTile);
      } else {
        LOGGER.warning("Cannot update visual for player " + player.getName() + " during full update: Player has no current tile.");
      }
    }
  }

  /**
   * Retrieves the JavaFX Node representing a specific player's token.
   *
   * @param player The player whose token Node is requested.
   * @return The visual Node for the player, or null if the player is not found or has no token.
   * @throws NullPointerException if player is null.
   */
  public Node getPlayerToken(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    return playerTokens.get(player);
  }

  /**
   * Checks if a visual token currently exists for the specified player on the board.
   *
   * @param player The player to check for.
   * @return true if a visual token exists for the player, false otherwise.
   * @throws NullPointerException if player is null.
   */
  public boolean hasPlayerToken(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    return playerTokens.containsKey(player);
  }

  /**
   * Gets the underlying Board data model currently associated with this visual component.
   *
   * @return The Board object being visualized, or null if the board has not been initialized.
   */
  public Board getCurrentBoard() {
    return currentBoard;
  }
}