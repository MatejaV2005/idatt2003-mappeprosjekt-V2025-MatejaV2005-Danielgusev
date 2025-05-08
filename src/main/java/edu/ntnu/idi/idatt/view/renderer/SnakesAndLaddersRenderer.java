package edu.ntnu.idi.idatt.view.renderer;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.actions.TileAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
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

  private static final Color LIGHT_TILE_COLOR = Color.web("#BFA6B9");
  private static final Color DARK_TILE_COLOR  = Color.web("#5D3B5C");


  // Define action-specific colors
  private static final Color LADDER_COLOR = Color.web("#7CB342");
  private static final Color SNAKE_COLOR = Color.web("#D32F2F");
  private static final Color SPECIAL_COLOR = Color.web("#42A5F5");

  private static final double ACTION_LINE_WIDTH_FACTOR = 0.12;
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
   * {@inheritDoc}
   * Moves the player token node to the center of the target tile using a smooth
   * {@link TranslateTransition}. The target position is calculated based on the
   * tile's center and the token's dimensions, ensuring it's visually centered on the tile.
   * This method uses the original, smoother animation logic.
   *
   * @param playerTokenNode The {@link Node} representing the player's token. Cannot be null.
   * @param targetTile The {@link Tile} to move the token to. Cannot be null.
   * @param boardPane The parent {@link Pane} containing the board. Cannot be null.
   * @throws NullPointerException if any argument is null.
   */
  @Override
  public void updatePlayerTokenPosition(Node playerTokenNode, Tile targetTile, Pane boardPane) {
    Objects.requireNonNull(playerTokenNode, "playerTokenNode cannot be null for position update");
    Objects.requireNonNull(targetTile, "targetTile cannot be null for position update");
    Objects.requireNonNull(boardPane, "boardPane context cannot be null for position update");

    Point2D targetCenter = tileCenterPositions.get(targetTile.getTileId());

    if (targetCenter == null) {
      LOGGER.log(Level.WARNING, "Renderer Warning: Could not find pre-calculated center for Tile ID: " + targetTile.getTileId() + ". Attempting fallback.");
      targetCenter = calculateCenterFallback(targetTile);
      if (targetCenter == null) {
        LOGGER.severe("Fallback position calculation failed. Cannot position token for tile " + targetTile.getTileId() +
            ". Placing at default offset.");
        playerTokenNode.setLayoutX(offsetX + calculatedTileSize / 4.0);
        playerTokenNode.setLayoutY(offsetY + calculatedTileSize / 4.0);
        return;
      }
    }

    double tokenWidth = playerTokenNode.getBoundsInLocal().getWidth();
    double tokenHeight = playerTokenNode.getBoundsInLocal().getHeight();
    double targetLayoutX = targetCenter.getX() - tokenWidth / 2.0;
    double targetLayoutY = targetCenter.getY() - tokenHeight / 2.0;

    TranslateTransition tt = new TranslateTransition(MOVE_ANIMATION_DURATION, playerTokenNode);
    tt.setInterpolator(Interpolator.EASE_BOTH);

    // calculate delta for translation (animation logci)
    double currentLayoutX = playerTokenNode.getLayoutX();
    double currentLayoutY = playerTokenNode.getLayoutY();
    double currentTranslateX = playerTokenNode.getTranslateX();
    double currentTranslateY = playerTokenNode.getTranslateY();

    tt.setToX(targetLayoutX - currentLayoutX + currentTranslateX);
    tt.setToY(targetLayoutY - currentLayoutY + currentTranslateY);

    Player p = (Player) playerTokenNode.getUserData();
    String playerName = (p != null) ? p.getName() : "Unknown Player";
    LOGGER.finer("Animating token for " + playerName + " to target layout ("+ String.format("%.2f", targetLayoutX) + ", " + String.format("%.2f", targetLayoutY) + ")");

    tt.setOnFinished(e -> {
      playerTokenNode.setLayoutX(targetLayoutX);
      playerTokenNode.setLayoutY(targetLayoutY);
      playerTokenNode.setTranslateX(0);
      playerTokenNode.setTranslateY(0);
      LOGGER.finer("Animation finished for " + playerName + ", snapped to final position.");
    });
    tt.play();
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
    Point2D position = calculateTopLeftWithOffset(tile.getRow(), tile.getColumn());

    TileAction action = tile.getLandAction();
    Color tileColor = ((tile.getRow() + tile.getColumn()) % 2 == 0) ? LIGHT_TILE_COLOR : DARK_TILE_COLOR; // Default

    if (action != null) {
      ActionType actionType = action.getActionType();
      switch (actionType) {
        case LADDER -> tileColor = LADDER_COLOR;
        case SNAKE -> tileColor = SNAKE_COLOR;
        case SPECIAL, RETURN_TO_START, SKIP_TURN -> tileColor = SPECIAL_COLOR;
        default -> { /* Keep default color */ }
      }
    }

    // Create visual components
    Rectangle tileBg = new Rectangle(calculatedTileSize, calculatedTileSize);
    tileBg.setFill(tileColor);
    tileBg.setStroke(TILE_STROKE_COLOR);
    tileBg.setStrokeWidth(TILE_STROKE_WIDTH);

    Label tileLabel = new Label(String.valueOf(tileId));
    tileLabel.setFont(Font.font("Arial", FontWeight.BOLD, calculatedTileSize * TILE_NUMBER_FONT_FACTOR));
    tileLabel.setTextFill(TILE_NUMBER_COLOR);

    // Assemble and position the tile
    StackPane tileNode = new StackPane(tileBg, tileLabel);
    tileNode.setLayoutX(position.getX());
    tileNode.setLayoutY(position.getY());
    tileNode.setUserData(tileId);
    boardPane.getChildren().add(tileNode);

    // Store center position for player tokens and connecting lines
    tileCenterPositions.put(tileId, new Point2D(
        position.getX() + calculatedTileSize / 2.0,
        position.getY() + calculatedTileSize / 2.0
    ));
  }

  /**
   * Calculates the top-left (x, y) coordinate for a tile's visual representation,
   * taking into account the global {@code offsetX} and {@code offsetY} for centering the grid.
   * The calculation also inverts the logical row to match JavaFX's top-left origin.
   *
   * @param logicalRow The 0-indexed row of the tile from the board model (bottom-up).
   * @param logicalCol The 0-indexed column of the tile from the board model (left-to-right).
   * @return A {@link Point2D} representing the top-left (x,y) for drawing the tile.
   */
  private Point2D calculateTopLeftWithOffset(int logicalRow, int logicalCol) {
    int visualRow = (numRows - 1) - logicalRow;
    double x = offsetX + (logicalCol * calculatedTileSize);
    double y = offsetY + (visualRow * calculatedTileSize);
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
          line.setStrokeWidth(Math.max(1.0, calculatedTileSize * ACTION_LINE_WIDTH_FACTOR)); // Ensure minimum width
          line.setOpacity(0.7);
          line.setMouseTransparent(true);

          if (action.getActionType() == ActionType.LADDER) {
            line.setStroke(LADDER_COLOR);
            line.getStrokeDashArray().addAll(Math.max(1.0, calculatedTileSize * 0.15), Math.max(1.0, calculatedTileSize * 0.1));
          } else { // SNAKE
            line.setStroke(SNAKE_COLOR);
          }
          boardPane.getChildren().add(line);
          line.toBack(); // Ensure lines are drawn behind player tokens
        } else {
          LOGGER.warning("Could not draw snake/ladder from tile " + startTileId + " to " + endTileId +
              ": Missing center position(s). Start: " + startCenter + ", End: " + endCenter);
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
