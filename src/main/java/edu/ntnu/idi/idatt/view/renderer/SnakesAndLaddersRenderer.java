package edu.ntnu.idi.idatt.view.renderer;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class SnakesAndLaddersRenderer implements BoardRenderer {

  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersRenderer.class.getName());

  private final Map<Integer, Point2D> tileCenterPositions;
  private final Map<Integer, Rectangle> tileRectangles;
  private double calculatedTileSize = 50.0;
  private int numCols = 10;
  private int numRows = 10;

  // Visual settings
  private static final Duration MOVE_ANIMATION_DURATION = Duration.millis(400);
  private static final Color LIGHT_TILE_COLOR = Color.rgb(240, 240, 250);
  private static final Color DARK_TILE_COLOR = Color.rgb(200, 200, 220);
  private static final Color LADDER_COLOR = Color.GREEN;
  private static final Color SNAKE_COLOR = Color.RED;
  private static final Color PLAYER_TOKEN_STROKE = Color.BLACK;
  private static final double PLAYER_TOKEN_STROKE_WIDTH = 1.5;

  public SnakesAndLaddersRenderer() {
    this.tileCenterPositions = new HashMap<>();
    this.tileRectangles =  new HashMap<>();
  }

  @Override
  public void renderInitialBoard(Pane boardPane, Board board) {
    this.numCols = board.getColumns();
    this.numRows = board.getRows();

    boardPane.getChildren().clear();
    tileCenterPositions.clear();
    tileRectangles.clear();


  }

  @Override
  public Node createPlayerTokenNode(Player player) {
    return null; // Placeholder return
  }

  @Override
  public void updatePlayerTokenPosition(Node playerTokenNode, Tile targetTile, Pane boardPane) {
  }

  @Override
  public void updateBoardState(Board board) {

  }

  private Point2D calculateTopLeft(int logicalRow, int logicalCol, double paneHeight) {
    return null; // Placeholder return
  }

  private void drawSnakesAndLadders(Pane boardPane, Board board) {
  }

}
