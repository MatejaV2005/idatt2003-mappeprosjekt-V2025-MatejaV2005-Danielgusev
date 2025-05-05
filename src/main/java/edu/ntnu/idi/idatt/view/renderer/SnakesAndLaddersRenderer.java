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
 * a Snakes and Ladders game board onto a {@link Pane} using calculated positions.
 * This class handles the specific layout, visual elements, and animations
 * for the Snakes and Ladders game type, adapting dynamically to the provided
 * {@link Board} model's dimensions and tile actions.
 */
public class SnakesAndLaddersRenderer implements BoardRenderer {

  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersRenderer.class.getName());

  private final Map<Integer, Point2D> tileCenterPositions = new HashMap<>();
  private double calculatedTileSize = 50.0;
  private int numCols = 10;
  private int numRows = 10;

  private static final Duration MOVE_ANIMATION_DURATION = Duration.millis(400);

  private static final Color LIGHT_TILE_COLOR = Color.web("#C8E6C9");
  private static final Color DARK_TILE_COLOR = Color.web("#81C784");
  private static final Color LADDER_COLOR = Color.web("#A1887F", 0.8);
  private static final Color SNAKE_COLOR = Color.web("#EF5350", 0.8);

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
   */
  public SnakesAndLaddersRenderer() {
  }

  /**
   * {@inheritDoc}
   * Renders the initial static board elements including tiles, snakes, and ladders.
   * Clears the provided pane first and calculates tile positions based on the board model.
   *
   * @throws NullPointerException if boardPane or board is null.
   * @throws IllegalStateException if board dimensions are invalid or tiles are missing.
   */
  @Override
  public void renderInitialBoard(Pane boardPane, Board board) {
    Objects.requireNonNull(boardPane, "boardPane cannot be null");
    Objects.requireNonNull(board, "board cannot be null");
    LOGGER.info("Rendering initial Snakes and Ladders board.");

    boardPane.getChildren().clear();
    tileCenterPositions.clear();

    this.numRows = board.getRows();
    this.numCols = board.getColumns();
    if (numRows <= 0 || numCols <= 0) {
      throw new IllegalStateException("Invalid board dimensions from Board model: " + numRows + "x" + numCols);
    }
    Map<Integer, Tile> tiles = board.getTiles();
    if (tiles == null || tiles.isEmpty()) {
      throw new IllegalStateException("Board model contains no tiles to render.");
    }

    double paneWidth = boardPane.getWidth() > 0 ? boardPane.getWidth() : boardPane.getPrefWidth();
    double paneHeight = boardPane.getHeight() > 0 ? boardPane.getHeight() : boardPane.getPrefHeight();
    if (paneWidth <= 0 || paneHeight <= 0) {
      LOGGER.warning("Board pane size not determined, using estimated size. Layout might be incorrect initially.");
      paneWidth = numCols * this.calculatedTileSize;
      paneHeight = numRows * this.calculatedTileSize;
    }
    this.calculatedTileSize = Math.min(paneWidth / numCols, paneHeight / numRows);
    LOGGER.finer("Board dimensions: " + numRows + "x" + numCols + ", Tile size: " + calculatedTileSize);

    for (Tile tile : tiles.values()) {
      createTileVisual(boardPane, tile, paneHeight);
    }

    drawSnakesAndLadders(boardPane, board);
  }

  /**
   * {@inheritDoc}
   * Creates a Circle node with appropriate styling based on the player's piece type.
   *
   * @throws NullPointerException if player is null.
   */
  @Override
  public Node createPlayerTokenNode(Player player) {
    Objects.requireNonNull(player, "player cannot be null");
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
   * Moves the player token node to the center of the target tile using a smooth animation.
   *
   * @throws NullPointerException if any argument is null.
   */
  @Override
  public void updatePlayerTokenPosition(Node playerTokenNode, Tile targetTile, Pane boardPane) {
    Objects.requireNonNull(playerTokenNode, "playerTokenNode cannot be null");
    Objects.requireNonNull(targetTile, "targetTile cannot be null");
    Objects.requireNonNull(boardPane, "boardPane cannot be null");

    Point2D targetCenter = tileCenterPositions.get(targetTile.getTileId());

    if (targetCenter == null) {
      LOGGER.log(Level.SEVERE, "Renderer Error: Could not find center position for Tile ID: " + targetTile.getTileId());
      double paneHeight = boardPane.getHeight() > 0 ? boardPane.getHeight() : boardPane.getPrefHeight();

      if (paneHeight <= 0) {
        paneHeight = numRows * calculatedTileSize;
      }

      targetCenter = calculateCenterFallback(targetTile, paneHeight);

      if (targetCenter == null) {
        LOGGER.severe("Fallback position calculation failed. Cannot position token for tile " + targetTile.getTileId());
        return;
      }
    }

    double tokenWidth = playerTokenNode.getBoundsInLocal().getWidth();
    double tokenHeight = playerTokenNode.getBoundsInLocal().getHeight();
    double targetLayoutX = targetCenter.getX() - tokenWidth / 2.0;
    double targetLayoutY = targetCenter.getY() - tokenHeight / 2.0;

    TranslateTransition tt = new TranslateTransition(MOVE_ANIMATION_DURATION, playerTokenNode);
    tt.setInterpolator(Interpolator.EASE_BOTH);

    double currentLayoutX = playerTokenNode.getLayoutX();
    double currentLayoutY = playerTokenNode.getLayoutY();
    double currentTranslateX = playerTokenNode.getTranslateX();
    double currentTranslateY = playerTokenNode.getTranslateY();

    tt.setToX(targetLayoutX - currentLayoutX + currentTranslateX);
    tt.setToY(targetLayoutY - currentLayoutY + currentTranslateY);

    LOGGER.finer("Animating token to target layout ("+ targetLayoutX + ", " + targetLayoutY + ")");

    tt.setOnFinished(e -> {
      playerTokenNode.setLayoutX(targetLayoutX);
      playerTokenNode.setLayoutY(targetLayoutY);
      playerTokenNode.setTranslateX(0);
      playerTokenNode.setTranslateY(0);
      LOGGER.finer("Animation finished, snapped to final position.");
    });
    tt.play();
  }

  private void createTileVisual(Pane boardPane, Tile tile, double paneHeight) {
    int tileId = tile.getTileId();
    int logicalRow = tile.getRow();
    int logicalCol = tile.getColumn();

    Point2D topLeft = calculateTopLeft(logicalRow, logicalCol, paneHeight);

    Rectangle tileBg = new Rectangle(calculatedTileSize, calculatedTileSize);
    tileBg.setFill(((logicalRow + logicalCol) % 2 == 0) ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
    tileBg.setStroke(TILE_STROKE_COLOR);
    tileBg.setStrokeWidth(TILE_STROKE_WIDTH);

    Label tileLabel = new Label(String.valueOf(tileId));
    tileLabel.setFont(Font.font("Arial", FontWeight.BOLD, calculatedTileSize * TILE_NUMBER_FONT_FACTOR));
    tileLabel.setTextFill(TILE_NUMBER_COLOR);

    StackPane tileNode = new StackPane(tileBg, tileLabel);
    tileNode.setLayoutX(topLeft.getX());
    tileNode.setLayoutY(topLeft.getY());
    tileNode.setUserData(tileId);

    boardPane.getChildren().add(tileNode);

    Point2D center = new Point2D(
        topLeft.getX() + calculatedTileSize / 2.0,
        topLeft.getY() + calculatedTileSize / 2.0
    );
    tileCenterPositions.put(tileId, center);
  }

  private Point2D calculateTopLeft(int logicalRow, int logicalCol, double paneHeight) {
    int visualRow = (numRows - 1) - logicalRow;
    double x = logicalCol * calculatedTileSize;
    double y = visualRow * calculatedTileSize;
    return new Point2D(x, y);
  }

  private Point2D calculateCenterFallback(Tile tile, double paneHeight) {
    if (tile == null || numRows <= 0 || numCols <= 0) return null;
    if (calculatedTileSize <= 0) calculatedTileSize = 50.0;
    Point2D topLeft = calculateTopLeft(tile.getRow(), tile.getColumn(), paneHeight);
    return new Point2D(
        topLeft.getX() + calculatedTileSize / 2.0,
        topLeft.getY() + calculatedTileSize / 2.0
    );
  }

  private void drawSnakesAndLadders(Pane boardPane, Board board) {
    LOGGER.finer("Drawing snakes and ladders.");
    if (board == null || board.getTiles() == null) return;

    for (Tile tile : board.getTiles().values()) {
      TileAction action = tile.getLandAction();

      if (action != null && (action.getActionType() == ActionType.SNAKE || action.getActionType() == ActionType.LADDER)) {
        int startTileId = tile.getTileId();
        int endTileId = action.getDestinationTileId();

        if (endTileId <= 0) {
          LOGGER.warning("Skipping snake/ladder from tile " + startTileId + ": Invalid destination ID " + endTileId);
          continue;
        }

        Point2D startCenter = tileCenterPositions.get(startTileId);
        Point2D endCenter = tileCenterPositions.get(endTileId);

        if (startCenter != null && endCenter != null) {
          Line line = new Line(startCenter.getX(), startCenter.getY(), endCenter.getX(), endCenter.getY());
          line.setStrokeWidth(calculatedTileSize * ACTION_LINE_WIDTH_FACTOR);
          line.setOpacity(0.7);
          line.setMouseTransparent(true);

          if (action.getActionType() == ActionType.LADDER) {
            line.setStroke(LADDER_COLOR);
            line.getStrokeDashArray().addAll(calculatedTileSize * 0.1, calculatedTileSize * 0.1);
          } else {
            line.setStroke(SNAKE_COLOR);
          }
          boardPane.getChildren().add(line);
          line.toBack();
        } else {
          LOGGER.warning("Could not draw snake/ladder from " + startTileId + " to " + endTileId + ": Missing center position(s).");
        }
      }
    }
  }

  private Color getPlayerColor(Player player) {
    String pieceType = player.getPieceType();
    if (pieceType != null) {
      switch (pieceType.toLowerCase()) {
        case "car": return Color.INDIANRED;
        case "hat": return Color.ROYALBLUE;
        case "dragon": return Color.SEAGREEN;
        case "dog": return Color.GOLD;
        case "ship": return Color.SLATEBLUE;
        default: return TOKEN_DEFAULT_COLOR;
      }
    }
    LOGGER.warning("Player " + player.getName() + " has null pieceType, using default color.");
    return TOKEN_DEFAULT_COLOR;
  }
}
