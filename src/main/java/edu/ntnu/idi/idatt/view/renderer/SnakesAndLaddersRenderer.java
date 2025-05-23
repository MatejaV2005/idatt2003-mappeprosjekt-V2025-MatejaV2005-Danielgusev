package edu.ntnu.idi.idatt.view.renderer;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.view.utils.PlayerTokenData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
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
  private double calculatedTileSize = 50.0;
  private int numCols = 10;
  private int numRows = 10;

  private double offsetX = 0.0;
  private double offsetY = 0.0;

  private static final Duration MOVE_ANIMATION_DURATION = Duration.millis(400);


  private static final Color LIGHT_TILE_COLOR = Color.web("#5D3B5C");
  private static final Color DARK_TILE_COLOR  = Color.web("#BFA6B9");


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
   *
   * <p>Intentionally left empty; all necessary initialization is done in field declarations.
   * </p>
   */
  public SnakesAndLaddersRenderer() {
    // no-op: initialization is managed via field defaults and renderInitialBoard
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

    boardPane.getChildren().clear();
    tileCenterPositions.clear();

    this.numRows = board.getRows();
    this.numCols = board.getColumns();
    if (numRows <= 0 || numCols <= 0) {
      throw new IllegalStateException("Invalid board dimensions from Board model: Rows="
          + numRows + ", Cols=" + numCols);
    }
    Map<Integer, Tile> tiles = board.getTiles();
    if (tiles == null || tiles.isEmpty()) {
      throw new IllegalStateException("Board model contains no tiles to render.");
    }

    double paneWidth = boardPane.getWidth() > 0 ? boardPane.getWidth() : boardPane.getPrefWidth();
    double paneHeight = boardPane.getHeight() > 0
        ? boardPane.getHeight() : boardPane.getPrefHeight();

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
      LOGGER.log(
          Level.WARNING,
          "Calculated tile size is too small or zero ({0}). Defaulting to 10.0.",
          this.calculatedTileSize
      );
      this.calculatedTileSize = 10.0;
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
      LOGGER.log(
          Level.WARNING,
          "Missing position for tile {0} during initial placement. Calculating fallback position.",
          tileId
      );
      tileCenter = calculateCenterFallback(tile);
      if (tileCenter == null) {
        LOGGER.log(
            Level.SEVERE,
            "Unable to place player token at tile {0} due to missing position data.",
            tileId
        );
        return;
      }
    }

    double tokenWidth = playerTokenNode.getBoundsInLocal().getWidth();
    double tokenHeight = playerTokenNode.getBoundsInLocal().getHeight();

    playerTokenNode.setLayoutX(tileCenter.getX() - tokenWidth / 2.0);
    playerTokenNode.setLayoutY(tileCenter.getY() - tokenHeight / 2.0);
    playerTokenNode.setTranslateX(0);
    playerTokenNode.setTranslateY(0);

    Player player = (Player) playerTokenNode.getUserData();
    playerTokenNode.setUserData(new PlayerTokenData(player, tileId));
    LOGGER.log(
        Level.FINE,
        "Player token directly placed at tile {0}",
        tileId
    );
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

  @SuppressWarnings("checkstyle:NeedBraces")
  @Override
  public void updatePlayerTokenPosition(
      Node playerTokenNode,
      Tile targetTile,
      Pane boardPane,
      Runnable onAnimationComplete
  ) {
    Objects.requireNonNull(playerTokenNode);
    Objects.requireNonNull(targetTile);
    Objects.requireNonNull(boardPane);

    PlayerTokenData data = (PlayerTokenData) playerTokenNode.getUserData();
    int startId = data.getCurrentTileId();
    int endId   = targetTile.getTileId();

    List<Integer> path = calculatePath(startId, endId);
    Point2D startCenter = tileCenterPositions.get(startId);
    Point2D endCenter   = tileCenterPositions.get(endId);
    if (startCenter == null || endCenter == null) {
      return;
    }

    SequentialTransition seq = new SequentialTransition();
    seq.getChildren().addAll(
        buildWalkTransitions(playerTokenNode, path, startCenter)
    );

    TileAction action = targetTile.getLandAction();
    if (action != null && action.getDestinationTileId() > 0
        && action.getDestinationTileId() != endId) {
      Point2D jumpCenter = tileCenterPositions.get(action.getDestinationTileId());
      seq.getChildren().add(
          buildJumpTransition(playerTokenNode, endCenter, jumpCenter)
      );
      seq.setOnFinished(e -> {
        updateTokenData(playerTokenNode, action.getDestinationTileId());
        if (onAnimationComplete != null) onAnimationComplete.run();
      });
    } else {
      seq.setOnFinished(e -> {
        updateTokenData(playerTokenNode, endId);
        if (onAnimationComplete != null) onAnimationComplete.run();
      });
    }

    seq.play();
  }



  private List<TranslateTransition> buildWalkTransitions(
      Node token,
      List<Integer> path,
      Point2D startPos
  ) {
    List<TranslateTransition> steps = new ArrayList<>();
    Point2D currentPos = startPos;

    for (int nextId : path) {
      Point2D nextCenter = tileCenterPositions.get(nextId);
      if (nextCenter == null) {
        break;
      }

      final Point2D fromPos = currentPos;
      final Point2D toPos   = nextCenter;

      double dx = toPos.getX() - fromPos.getX();
      double dy = toPos.getY() - fromPos.getY();

      Duration dur = MOVE_ANIMATION_DURATION;

      if (dx != 0 && dy != 0) {
        TranslateTransition h = new TranslateTransition(dur, token);
        h.setFromX(0);
        h.setFromY(0);
        h.setToX(dx);
        h.setToY(0);
        h.setInterpolator(Interpolator.EASE_BOTH);
        h.setOnFinished(e -> commitTokenPosition(
            token,
            new Point2D(
                fromPos.getX() + dx,
                fromPos.getY()
            )
        ));

        TranslateTransition v = new TranslateTransition(dur, token);
        v.setFromX(0);
        v.setFromY(0);
        v.setToX(0);
        v.setToY(dy);
        v.setInterpolator(Interpolator.EASE_BOTH);
        v.setOnFinished(e -> commitTokenPosition(
            token,
            new Point2D(
                fromPos.getX() + dx,
                fromPos.getY() + dy
            )
        ));

        steps.add(h);
        steps.add(v);
      } else {
        TranslateTransition s = new TranslateTransition(dur, token);
        s.setFromX(0);
        s.setFromY(0);
        s.setToX(dx);
        s.setToY(dy);
        s.setInterpolator(Interpolator.EASE_BOTH);
        s.setOnFinished(e -> commitTokenPosition(token, toPos));

        steps.add(s);
      }

      // Update for next iteration
      currentPos = nextCenter;
    }

    return steps;
  }

  private TranslateTransition buildJumpTransition(
      Node token,
      Point2D fromCenter,
      Point2D toCenter
  ) {
    double dx = toCenter.getX() - fromCenter.getX();
    double dy = toCenter.getY() - fromCenter.getY();

    TranslateTransition jump = new TranslateTransition(Duration.millis(800), token);
    jump.setInterpolator(Interpolator.SPLINE(0.2, 0.8, 0.2, 1.0));
    jump.setFromX(0);
    jump.setFromY(0);
    jump.setToX(dx);
    jump.setToY(dy);
    jump.setOnFinished(e -> commitTokenPosition(
        token,
        toCenter
    ));

    return jump;
  }

  private void commitTokenPosition(Node token, Point2D center) {
    token.setLayoutX(center.getX() - token.getBoundsInLocal().getWidth() / 2);
    token.setLayoutY(center.getY() - token.getBoundsInLocal().getHeight() / 2);
    token.setTranslateX(0);
    token.setTranslateY(0);
  }

  private void updateTokenData(Node token, int finalTileId) {
    PlayerTokenData old = (PlayerTokenData) token.getUserData();
    token.setUserData(new PlayerTokenData(old.getPlayer(), finalTileId));
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
      for (int i = startTileId + 1; i <= endTileId; i++) {
        path.add(i);
      }
    } else {
      for (int i = startTileId - 1; i >= endTileId; i--) {
        path.add(i);
      }
    }

    return path;
  }


  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private void createTileVisual(Pane boardPane, Tile tile) {
    int tileId = tile.getTileId();
    int logicalRow = tile.getRow();
    int logicalCol = tile.getColumn();

    Point2D topLeftWithOffset = calculateTopLeftWithOffset(logicalRow, logicalCol);

    Rectangle tileBg = new Rectangle(calculatedTileSize, calculatedTileSize);
    TileAction action = tile.getLandAction();
    Color tileFillColor;

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
        case SPECIAL, RETURN_TO_START, SKIP_TURN:
          tileFillColor = SPECIAL_COLOR;
          hasSpecialColor = true;
          break;
        case NO_OP:
        default:
          tileFillColor = null;
          break;
      }
    } else {
      tileFillColor = null;
      LOGGER.log(
          Level.WARNING,
          "Tile {0} has a null TileAction. Using default background color logic.",
          tileId
      );
    }

    if (!hasSpecialColor) {
      tileFillColor = (tileId % 2 != 0) ? LIGHT_TILE_COLOR : DARK_TILE_COLOR;
    }

    tileBg.setFill(tileFillColor);
    tileBg.setStroke(TILE_STROKE_COLOR);
    tileBg.setStrokeWidth(TILE_STROKE_WIDTH);

    Label tileLabel = new Label(String.valueOf(tileId));
    double fontSize = Math.max(8.0, calculatedTileSize * TILE_NUMBER_FONT_FACTOR);
    tileLabel.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
    tileLabel.setTextFill(TILE_NUMBER_COLOR);

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
   * @return A {@link Point2D} representing the calculated center of the tile,
   *         or null if calculation fails.
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

      if (action != null && (action.getActionType() == ActionType.SNAKE
          || action.getActionType() == ActionType.LADDER)) {
        int startTileId = tile.getTileId();
        int endTileId = action.getDestinationTileId();

        if (endTileId <= 0 || endTileId == startTileId) {
          LOGGER.log(
              Level.WARNING,
              "Skipping snake/ladder from tile {0}: invalid or non-moving destination ID {1}",
              new Object[]{startTileId, endTileId}
          );
          continue;
        }

        Point2D startCenter = tileCenterPositions.get(startTileId);
        Point2D endCenter = tileCenterPositions.get(endTileId);

        if (startCenter != null && endCenter != null) {
          Line line = new Line(startCenter.getX(), startCenter.getY(),
              endCenter.getX(), endCenter.getY());
          line.setStrokeWidth(10.0);
          line.setOpacity(.5);
          line.setMouseTransparent(true);

          if (action.getActionType() == ActionType.LADDER) {
            line.setStroke(LADDER_COLOR);
            line.getStrokeDashArray()
                .addAll(Math.max(1.0, calculatedTileSize * 0.15),
                    Math.max(1.0, calculatedTileSize * 0.1));
          } else {
            line.setStroke(SNAKE_COLOR);
          }
          boardPane.getChildren().add(line);
        } else {
          LOGGER.log(
              Level.WARNING,
              "Could not draw snake/ladder from tile {0} to {1}: "
                  + "Missing center position(s). Start: {2}, End: {3}",
              new Object[]{startTileId, endTileId, startCenter, endCenter}
          );
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
      return switch (pieceType.toLowerCase().trim()) {
        case "car" -> Color.INDIANRED;
        case "hat" -> Color.ROYALBLUE;
        case "dragon" -> Color.SEAGREEN;
        case "dog" -> Color.GOLD;
        case "ship" -> Color.SLATEBLUE;
        default -> {
          LOGGER.finer("Player " + player.getName() + " has unmapped pieceType '" + pieceType
              + "'. Using default color.");
          yield TOKEN_DEFAULT_COLOR;
        }
      };
    }
    LOGGER.warning("Player " + player.getName()
        + " has null or blank pieceType. Using default color.");
    return TOKEN_DEFAULT_COLOR;
  }
}
