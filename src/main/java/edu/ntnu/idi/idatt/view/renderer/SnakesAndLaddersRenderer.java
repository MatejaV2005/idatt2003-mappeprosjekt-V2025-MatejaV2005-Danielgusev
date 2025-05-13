package edu.ntnu.idi.idatt.view.renderer;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;

import edu.ntnu.idi.idatt.view.utils.PlayerTokenData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Implements the {@link BoardRenderer} interface to visually render
 * a Snakes and Ladders game board onto a {@link Pane}.
 * This class handles the specific layout, visual elements (tiles, snakes, ladders, player tokens),
 * and smooth animations for player movement. It adapts dynamically to the provided
 * {@link Board} model's dimensions and calculates tile positions to center the
 * drawn board content within the provided {@code boardPane}. The rendering logic
 * is designed to be responsive to changes in the size of the {@code boardPane}.
 */
public class SnakesAndLaddersRenderer implements BoardRenderer {

  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersRenderer.class.getName());

  private final Map<Integer, Point2D> tileCenterPositions = new HashMap<>();
  private double calculatedTileSize = 50.0; // Default, will be recalculated
  private int numCols = 10; // Default, updated from Board model
  private int numRows = 10; // Default, updated from Board model

  private double offsetX = 0.0; // For centering the grid
  private double offsetY = 0.0; // For centering the grid

  private static final Duration MOVE_ANIMATION_DURATION = Duration.millis(400);


  private static final Color LIGHT_TILE_COLOR = Color.web("#5D3B5C");
  private static final Color DARK_TILE_COLOR  = Color.web("#BFA6B9");


  // Define action-specific colors
  private static final Color LADDER_COLOR = Color.web("#7CB342");
  private static final Color SNAKE_COLOR = Color.web("#D32F2F");
  private static final Color SPECIAL_COLOR = Color.web("#42A5F5");

  private static final double TILE_STROKE_WIDTH = 0.5;

  private static final Color TILE_STROKE_COLOR = Color.web("#556B2F", 0.4);
  private static final double TILE_NUMBER_FONT_FACTOR = 0.3;
  private static final Color TILE_NUMBER_COLOR = Color.web("#333");
  private static final double TOKEN_RADIUS_FACTOR = 0.3;
  private static final double TOKEN_STROKE_WIDTH = 1.5;
  private static final Color TOKEN_DEFAULT_COLOR = Color.DARKORANGE;
  private static final Color TOKEN_STROKE_COLOR = Color.BLACK;

  /**
   * Default constructor for SnakesAndLaddersRenderer.
   * Initializes the renderer with default values.
   */
  public SnakesAndLaddersRenderer() {
  }

  /**
   * {@inheritDoc}
   * Renders the initial static board elements including tiles, snakes, and ladders,
   * ensuring the entire drawn grid is centered within the {@code boardPane}.
   * This method is designed to be called when the board is first displayed or
   * when the size of the {@code boardPane} changes, requiring a re-render.
   *
   * @param boardPane The {@link Pane} on which to render the board. Cannot be null.
   * @param board The {@link Board} data model. Cannot be null.
   * @throws NullPointerException if boardPane or board is null.
   * @throws IllegalStateException if board dimensions are invalid or tiles are missing.
   */
  @Override
  public void renderInitialBoard(Pane boardPane, Board board) {
    Objects.requireNonNull(boardPane, "boardPane cannot be null for rendering");
    Objects.requireNonNull(board, "board data model cannot be null for rendering");
    LOGGER.info("Rendering initial board visuals or re-rendering due to size change.");

    boardPane.getChildren().clear(); // Clear previous visuals for a clean re-render
    tileCenterPositions.clear(); // Clear cached positions

    this.numRows = board.getRows();
    this.numCols = board.getColumns();
    if (numRows <= 0 || numCols <= 0) {
      throw new IllegalStateException("Invalid board dimensions from Board model: Rows=" + numRows + ", Cols=" + numCols);
    }
    Map<Integer, Tile> tiles = board.getTiles();
    if (tiles == null || tiles.isEmpty()) {
      throw new IllegalStateException("Board model contains no tiles to render.");
    }

    // Use current actual width/height of the pane if available, otherwise fall back to preferred size
    double paneWidth = boardPane.getWidth() > 0 ? boardPane.getWidth() : boardPane.getPrefWidth();
    double paneHeight = boardPane.getHeight() > 0 ? boardPane.getHeight() : boardPane.getPrefHeight();

    if (paneWidth <= 0) {
      LOGGER.warning("Board pane width is zero or not set. Using default estimate for width.");
      paneWidth = numCols * 50.0;
    }
    if (paneHeight <= 0) {
      LOGGER.warning("Board pane height is zero or not set. Using default estimate for height.");
      paneHeight = numRows * 50.0;
    }

    this.calculatedTileSize = Math.min(paneWidth / numCols, paneHeight / numRows);
    if (this.calculatedTileSize <= 0.1) {
      LOGGER.warning("Calculated tile size is too small or zero (" + this.calculatedTileSize + "). Defaulting to 10.0.");
      this.calculatedTileSize = 10.0; // Minimum sensible tile size
    }

    double actualGridWidth = numCols * this.calculatedTileSize;
    double actualGridHeight = numRows * this.calculatedTileSize;

    this.offsetX = (paneWidth - actualGridWidth) / 2.0;
    this.offsetY = (paneHeight - actualGridHeight) / 2.0;
    if (this.offsetX < 0) {
      this.offsetX = 0;
    }

    if (this.offsetY < 0) {
      this.offsetY = 0;
    }


    for (Tile tile : tiles.values()) {
      createTileVisual(boardPane, tile);
    }

    drawSnakesAndLadders(boardPane, board);
    LOGGER.info("Board rendering complete.");
  }

  /**
   * {@inheritDoc}
   * Creates a {@link Circle} node representing a player's token. The token's size is
   * relative to the {@code calculatedTileSize}. Styling (color, stroke, shadow)
   * is applied based on predefined constants and the player's piece type.
   *
   * @param player The {@link Player} for whom to create the token. Cannot be null.
   * @return A {@link Node} (specifically, a Circle) representing the player's token.
   * @throws NullPointerException if player is null.
   */
  @Override
  public Node createPlayerTokenNode(Player player) {
    Objects.requireNonNull(player, "Player cannot be null for token creation");
    LOGGER.finer("Creating token for player: " + player.getName());

    Circle token = new Circle(calculatedTileSize * TOKEN_RADIUS_FACTOR);
    token.setStroke(TOKEN_STROKE_COLOR);
    token.setStrokeWidth(TOKEN_STROKE_WIDTH);
    token.setFill(getPlayerColor(player));
    token.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.4)));
    token.setUserData(player);
    return token;
  }

  /**
   * Sets the initial position of a player token at the specified tile without animation.
   * This should be used when first placing tokens on the board.
   *
   * @param playerTokenNode The player token node to position
   * @param tile The tile where the player should be placed
   */
  @Override
  public void placePlayerTokenAtTile(Node playerTokenNode, Tile tile, Pane boardPane) {
    Objects.requireNonNull(playerTokenNode, "playerTokenNode cannot be null for initial placement");
    Objects.requireNonNull(tile, "tile cannot be null for initial placement");

    int tileId = tile.getTileId();
    Point2D tileCenter = tileCenterPositions.get(tileId);

    if (tileCenter == null) {
      LOGGER.warning("Missing position for tile " + tileId + " during initial placement. Calculating fallback position.");
      tileCenter = calculateCenterFallback(tile);
      if (tileCenter == null) {
        LOGGER.severe("Unable to place player token at tile " + tileId + " due to missing position data.");
        return;
      }
    }

    double tokenWidth = playerTokenNode.getBoundsInLocal().getWidth();
    double tokenHeight = playerTokenNode.getBoundsInLocal().getHeight();

    playerTokenNode.setLayoutX(tileCenter.getX() - tokenWidth / 2.0);
    playerTokenNode.setLayoutY(tileCenter.getY() - tokenHeight / 2.0);
    playerTokenNode.setTranslateX(0);
    playerTokenNode.setTranslateY(0);

    Player player = (Player)playerTokenNode.getUserData();
    playerTokenNode.setUserData(new PlayerTokenData(player, tileId));
    LOGGER.fine("Player token directly placed at tile " + tileId);
  }

  /**
   * {@inheritDoc}
   * Moves the player token node through each intermediate tile to reach the target tile,
   * creating a sequential animation that shows the token "walking" the path.
   *
   * @param playerTokenNode The {@link Node} representing the player's token.
   * @param targetTile The final {@link Tile} to move the token to.
   * @param boardPane The parent {@link Pane} containing the board.
   * @throws NullPointerException if any argument is null.
   */
  @Override
  public void updatePlayerTokenPosition(Node playerTokenNode, Tile targetTile, Pane boardPane) {
    Objects.requireNonNull(playerTokenNode, "playerTokenNode cannot be null for position update");
    Objects.requireNonNull(targetTile, "targetTile cannot be null for position update");
    Objects.requireNonNull(boardPane, "boardPane context cannot be null for position update");

    Player player;
    if (playerTokenNode.getUserData() instanceof PlayerTokenData) {
      player = ((PlayerTokenData)playerTokenNode.getUserData()).getPlayer();
    } else {
      player = (Player)playerTokenNode.getUserData();
    }
    String playerName = (player != null) ? player.getName() : "Unknown Player";

    int currentTileId = getCurrentTileId(playerTokenNode);
    int targetTileId = targetTile.getTileId();

    playerTokenNode.setUserData(new PlayerTokenData(player, targetTileId));

    LOGGER.info("Moving " + playerName + " from tile " + currentTileId + " to tile " + targetTileId);

    List<Integer> path = calculatePath(currentTileId, targetTileId);

    SequentialTransition sequentialTransition = new javafx.animation.SequentialTransition();

    double tokenWidth = playerTokenNode.getBoundsInLocal().getWidth();
    double tokenHeight = playerTokenNode.getBoundsInLocal().getHeight();

    double currentX = playerTokenNode.getLayoutX();
    double currentY = playerTokenNode.getLayoutY();

    for (int i = 0; i < path.size(); i++) {
      int tileId = path.get(i);
      Point2D targetCenter = tileCenterPositions.get(tileId);
      if (targetCenter == null) {
        LOGGER.warning("Missing position for tile " + tileId + ". Skipping in animation.");
        continue;
      }

      double targetX = targetCenter.getX() - tokenWidth / 2.0;
      double targetY = targetCenter.getY() - tokenHeight / 2.0;

      TranslateTransition tt = new TranslateTransition(MOVE_ANIMATION_DURATION, playerTokenNode);
      tt.setInterpolator(Interpolator.EASE_BOTH);

      // Calculate translation amounts
      double translateX = targetX - currentX;
      double translateY = targetY - currentY;

      tt.setFromX(0);
      tt.setFromY(0);
      tt.setToX(translateX);
      tt.setToY(translateY);

      final int thisStepTileId = tileId;
      final double finalTargetX = targetX;
      final double finalTargetY = targetY;

      tt.setOnFinished(e -> {
        // Reset the translate properties and update layout for next animation
        playerTokenNode.setLayoutX(finalTargetX);
        playerTokenNode.setLayoutY(finalTargetY);
        playerTokenNode.setTranslateX(0);
        playerTokenNode.setTranslateY(0);
        LOGGER.finer("Reached intermediate tile " + thisStepTileId);
      });

      sequentialTransition.getChildren().add(tt);

      currentX = targetX;
      currentY = targetY;
    }

    // Start the animation sequence
    sequentialTransition.play();
  }

  /**
   * Updated version of getCurrentTileId that uses the stored PlayerTokenData
   */
  private int getCurrentTileId(Node playerTokenNode) {
    if (playerTokenNode.getUserData() instanceof PlayerTokenData) {
      return ((PlayerTokenData)playerTokenNode.getUserData()).getCurrentTileId();
    }

    double tokenX = playerTokenNode.getLayoutX() + playerTokenNode.getTranslateX() +
        playerTokenNode.getBoundsInLocal().getWidth() / 2.0;
    double tokenY = playerTokenNode.getLayoutY() + playerTokenNode.getTranslateY() +
        playerTokenNode.getBoundsInLocal().getHeight() / 2.0;

    int closestTile = 0;
    double minDistance = Double.MAX_VALUE;

    for (Map.Entry<Integer, Point2D> entry : tileCenterPositions.entrySet()) {
      Point2D tileCenter = entry.getValue();
      double distance = Math.sqrt(
          Math.pow(tokenX - tileCenter.getX(), 2) + Math.pow(tokenY - tileCenter.getY(), 2)
      );

      if (distance < minDistance) {
        minDistance = distance;
        closestTile = entry.getKey();
      }
    }

    return closestTile;
  }

  /**
   * Calculates the path of tiles to visit when moving from start to end tile.
   *
   * @param startTileId The starting tile ID
   * @param endTileId The ending tile ID
   * @return A list of tile IDs representing the path
   */
  private List<Integer> calculatePath(int startTileId, int endTileId) {
    List<Integer> path = new ArrayList<>();

    if (startTileId == endTileId) {
      path.add(endTileId);
      return path;
    }

    if (endTileId > startTileId) {
      // Moving forward
      for (int i = startTileId + 1; i <= endTileId; i++) {
        path.add(i);
      }
    } else {
      // Moving backward (for special actions like snakes)
      for (int i = startTileId - 1; i >= endTileId; i--) {
        path.add(i);
      }
    }

    return path;
  }

  /**
   * Creates the visual representation (a {@link StackPane} containing a {@link Rectangle}
   * and a {@link Label}) for a single tile on the board.
   * The tile's position is calculated using its logical row and column, adjusted by
   * the global {@code offsetX} and {@code offsetY} to ensure the entire grid is centered.
   *
   * @param boardPane The parent {@link Pane} to which the tile visual will be added.
   * @param tile The {@link Tile} data model object.
   */
  /**
   * Creates the visual representation for a single tile on the board.
   *
   * @param boardPane The parent {@link Pane} to which the tile visual will be added.
   * @param tile The {@link Tile} data model object.
   */
  private void createTileVisual(Pane boardPane, Tile tile) {
    int tileId = tile.getTileId();
    int logicalRow = tile.getRow();
    int logicalCol = tile.getColumn();

    Point2D topLeftWithOffset = calculateTopLeftWithOffset(logicalRow, logicalCol);

    Rectangle tileBg = new Rectangle(calculatedTileSize, calculatedTileSize);
    TileAction action = tile.getLandAction();
    Color tileFillColor;

    // Prioritize special action colors
    boolean hasSpecialColor = false;
    if (action != null) {
      switch (action.getActionType()) {
        case LADDER:
          tileFillColor = LADDER_COLOR;
          hasSpecialColor = true;
          break;
        case SNAKE:
          tileFillColor = SNAKE_COLOR;
          hasSpecialColor = true;
          break;
        case SPECIAL:
        case RETURN_TO_START:
        case SKIP_TURN:
          tileFillColor = SPECIAL_COLOR;
          hasSpecialColor = true;
          break;
        case NO_OP:
        default:
          // No special color, will use alternating logic below
          tileFillColor = null; // Explicitly null to trigger default logic
          break;
      }
    } else {
      // Action is null, use alternating logic below
      tileFillColor = null;
      LOGGER.warning("Tile " + tileId + " has a null TileAction. Using default background color logic.");
    }

    // If no special color was assigned, use the standard alternating pattern based on tileId
    if (!hasSpecialColor) {
      // Standard pattern: Tile 1 light, Tile 2 dark, Tile 3 light...
      // Corresponds to: odd tileId -> LIGHT_TILE_COLOR, even tileId -> DARK_TILE_COLOR
      tileFillColor = (tileId % 2 != 0) ? LIGHT_TILE_COLOR : DARK_TILE_COLOR;
    }

    // Apply the determined color
    tileBg.setFill(tileFillColor);
    tileBg.setStroke(TILE_STROKE_COLOR);
    tileBg.setStrokeWidth(TILE_STROKE_WIDTH);

    Label tileLabel = new Label(String.valueOf(tileId));
    double fontSize = Math.max(8.0, calculatedTileSize * TILE_NUMBER_FONT_FACTOR);
    tileLabel.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
    tileLabel.setTextFill(TILE_NUMBER_COLOR); // White text

    StackPane tileNode = new StackPane(tileBg, tileLabel);
    tileNode.setLayoutX(topLeftWithOffset.getX());
    tileNode.setLayoutY(topLeftWithOffset.getY());
    tileNode.setUserData(tileId);

    boardPane.getChildren().add(tileNode);

    Point2D center = new Point2D(
        topLeftWithOffset.getX() + calculatedTileSize / 2.0,
        topLeftWithOffset.getY() + calculatedTileSize / 2.0
    );
    tileCenterPositions.put(tileId, center);
  }


  private Point2D calculateTopLeftWithOffset(int logicalRow, int logicalCol) {
    int visualRow = (numRows - 1) - logicalRow;
    double y = offsetY + (visualRow * calculatedTileSize);
    double x;
    if (logicalRow % 2 == 0) {
      x = offsetX + (logicalCol * calculatedTileSize);
    } else {
      int visualCol = (numCols - 1) - logicalCol;
      x = offsetX + (visualCol * calculatedTileSize);
    }

    return new Point2D(x, y);
  }

  /**
   * Calculates the center point of a tile as a fallback if its position was not
   * pre-calculated or found in {@code tileCenterPositions}. This method is used
   * primarily for robustness if {@code renderInitialBoard} did not fully complete
   * or if a tile ID is somehow missing.
   *
   * @param tile The {@link Tile} for which to calculate the center.
   * @return A {@link Point2D} representing the calculated center of the tile, or null if calculation fails.
   */
  private Point2D calculateCenterFallback(Tile tile) {
    if (tile == null || numRows <= 0 || numCols <= 0) {
      LOGGER.warning("Cannot calculate fallback center: invalid tile or board dimensions.");
      return null;
    }
    double currentTileSize = (this.calculatedTileSize > 0) ? this.calculatedTileSize : 50.0;

    Point2D topLeftWithOffset = calculateTopLeftWithOffset(tile.getRow(), tile.getColumn());
    return new Point2D(
        topLeftWithOffset.getX() + currentTileSize / 2.0,
        topLeftWithOffset.getY() + currentTileSize / 2.0
    );
  }

  /**
   * Draws lines on the {@code boardPane} to represent snakes and ladders.
   * Lines are drawn between the center points of the start and end tiles of each action,
   * using the pre-calculated and offset-adjusted center positions.
   *
   * @param boardPane The {@link Pane} on which to draw the lines.
   * @param board The {@link Board} data model containing tile and action information.
   */
  private void drawSnakesAndLadders(Pane boardPane, Board board) {
    LOGGER.finer("Drawing snakes and ladders visuals on the board.");
    if (board == null || board.getTiles() == null) {
      LOGGER.warning("Cannot draw snakes/ladders: board or its tiles map is null.");
      return;
    }

    for (Tile tile : board.getTiles().values()) {
      TileAction action = tile.getLandAction();

      if (action != null && (action.getActionType() == ActionType.SNAKE || action.getActionType() == ActionType.LADDER)) {
        int startTileId = tile.getTileId();
        int endTileId = action.getDestinationTileId();

        if (endTileId <= 0 || endTileId == startTileId) {
          LOGGER.warning("Skipping snake/ladder from tile " + startTileId + ": Invalid or non-moving destination ID " + endTileId);
          continue;
        }

        Point2D startCenter = tileCenterPositions.get(startTileId);
        Point2D endCenter = tileCenterPositions.get(endTileId);

        if (startCenter != null && endCenter != null) {
          Line line = new Line(startCenter.getX(), startCenter.getY(), endCenter.getX(), endCenter.getY());
          line.setStrokeWidth(10.0);
          line.setOpacity(.5);
          line.setMouseTransparent(true);

          if (action.getActionType() == ActionType.LADDER) {
            line.setStroke(LADDER_COLOR);
            line.getStrokeDashArray().addAll(Math.max(1.0, calculatedTileSize * 0.15), Math.max(1.0, calculatedTileSize * 0.1));
          } else {
            line.setStroke(SNAKE_COLOR);
          }
          boardPane.getChildren().add(line);
        } else {
          LOGGER.warning("Could not draw snake/ladder from tile " + startTileId + " to " + endTileId + ": Missing center position(s). Start: " + startCenter + ", End: " + endCenter);
        }
      }
    }
  }

  /**
   * Determines the fill {@link Color} for a player's token based on their piece type.
   * Uses a predefined mapping of piece types to colors, with a default color if
   * the piece type is unknown, null, or blank.
   *
   * @param player The {@link Player} whose token color is being determined. Cannot be null.
   * @return The {@link Color} for the player's token.
   */
  private Color getPlayerColor(Player player) {
    Objects.requireNonNull(player, "Player cannot be null for color determination");
    String pieceType = player.getPieceType();

    if (pieceType != null && !pieceType.trim().isEmpty()) {
      switch (pieceType.toLowerCase().trim()) {
        case "car": return Color.INDIANRED;
        case "hat": return Color.ROYALBLUE;
        case "dragon": return Color.SEAGREEN;
        case "dog": return Color.GOLD;
        case "ship": return Color.SLATEBLUE;
        default:
          LOGGER.finer("Player " + player.getName() + " has unmapped pieceType '" + pieceType + "'. Using default color.");
          return TOKEN_DEFAULT_COLOR;
      }
    }
    LOGGER.warning("Player " + player.getName() + " has null or blank pieceType. Using default color.");
    return TOKEN_DEFAULT_COLOR;
  }
}
