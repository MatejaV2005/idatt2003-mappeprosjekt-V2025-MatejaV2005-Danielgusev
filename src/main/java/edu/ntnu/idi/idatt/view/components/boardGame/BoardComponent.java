package edu.ntnu.idi.idatt.view.components.boardGame;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import edu.ntnu.idi.idatt.view.renderer.BoardRenderer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * JavaFX component for displaying and managing a board game visual representation.
 * This component uses a provided BoardRenderer to handle the specific rendering
 * strategy while managing the player tokens and their positions.
 */
public class BoardComponent extends Pane {
  private static final Logger LOGGER = Logger.getLogger(BoardComponent.class.getName());
  private final PlayerManager playerManager;

  private final BoardRenderer renderer;
  private final Map<Player, Node> playerTokens;
  private Board currentBoard;

  /**
   * Creates a new BoardComponent with the specified renderer.
   *
   * @param renderer The board renderer implementation to use
   * @throws NullPointerException if renderer is null
   */
  public BoardComponent(BoardRenderer renderer) {
    ExceptionHandling.requireNonNull(renderer, "Renderer cannot be null");
    this.playerManager = PlayerManager.getInstance();
    this.renderer = renderer;
    this.playerTokens = new HashMap<>();
    this.getStyleClass().add("board-component");

    this.setMinSize(400, 400);
    this.setPrefSize(600, 600);
  }

  /**
   * Initializes the board with the specified model and players.
   * Creates and positions all visual elements including player tokens.
   *
   * @param board The board model to visualize
   * @param players The list of players to place on the board
   * @throws NullPointerException if board or players is null
   * @throws IllegalArgumentException if players list is empty
   */
  public void initializeBoard(Board board, List<Player> players) {
    ExceptionHandling.requireNonNull(board, "Board cannot be null");
    ExceptionHandling.requireNonNull(players, "Players list cannot be null");

    if (players.isEmpty()) {
      throw new IllegalArgumentException("Players list cannot be empty");
    }

    LOGGER.info("Initializing board component with " + players.size() + " players");
    this.currentBoard = board;

    // Clear existing visuals
    this.getChildren().clear();
    playerTokens.clear();

    // Initialize the board visuals
    renderer.renderInitialBoard(this, currentBoard);

    // Create and place player tokens
    for (Player player : players) {
      Node tokenNode = renderer.createPlayerTokenNode(player);
      playerTokens.put(player, tokenNode);
      this.getChildren().add(tokenNode);

      // Position token on starting tile (usually tile 1)
      Tile startingTile = currentBoard.getTileById(1);
      if (startingTile != null) {
        renderer.updatePlayerTokenPosition(tokenNode, startingTile, this);
      } else {
        LOGGER.warning("Could not find starting tile for player: " + player.getName());
      }
    }

    LOGGER.info("Board initialization complete");
  }


  public void updatePlayerVisual(Player player, Tile fromTile, Tile toTile) {

  }

  /**
   * Updates the visual position of a player's token to a specific tile.
   * This is a simplified version for direct positioning without animation tracking.
   *
   * @param player The player whose token should be moved
   * @param newTile The destination tile
   * @throws NullPointerException if player or newTile is null
   */
  public void updatePlayerVisual(Player player, Tile newTile) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    ExceptionHandling.requireNonNull(newTile, "Tile cannot be null");

    Node tokenNode = playerTokens.get(player);
    if (tokenNode != null) {
      LOGGER.fine("Moving player " + player.getName() + " to tile " + newTile.getTileId());
      renderer.updatePlayerTokenPosition(tokenNode, newTile, this);
    } else {
      LOGGER.warning("Cannot update visual for player " + player.getName() + ": token not found");
    }
  }


  public void removePlayerVisual(Player player) {
  }


  public void updateBoardVisuals() {

  }


  public Node getPlayerToken(Player player) {
    return null;
  }

  /**
   * Checks if a player has a visual token on the board.
   *
   * @param player The player to check
   * @return True if the player has a token, false otherwise
   * @throws NullPointerException if player is null
   */
  public boolean hasPlayerToken(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null");
    return playerTokens.containsKey(player);
  }

  /**
   * Gets the current board model.
   *
   * @return The current board model, or null if not initialized
   */
  public Board getCurrentBoard() {
    return currentBoard;
  }
}