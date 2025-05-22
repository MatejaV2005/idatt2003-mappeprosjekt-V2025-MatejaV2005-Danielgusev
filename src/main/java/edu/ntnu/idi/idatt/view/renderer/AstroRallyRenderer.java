package edu.ntnu.idi.idatt.view.renderer;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.AsteroidFieldAction;
import edu.ntnu.idi.idatt.model.core.actions.BoostPadAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.view.utils.PlayerTokenData;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Implements the {@link BoardRenderer} interface to visually render an Astro Rally game board.
 * The board is a 40-tile square loop, similar to a Monopoly board.
 * Player movement animation is designed for clarity and correctness, including delayed secondary moves.
 */
public class AstroRallyRenderer implements BoardRenderer {

  private static final Logger LOGGER = Logger.getLogger(AstroRallyRenderer.class.getName());

  private final Map<Integer, Point2D> tileCenterPositions = new HashMap<>();
  private double calculatedTileSize = 50.0;
  private int tilesPerSideExcludingCorners = 9;
  private double offsetX = 0.0;
  private double offsetY = 0.0;
  private int totalTilesOnBoard = 40;

  private static final Duration MOVE_ANIMATION_DURATION = Duration.millis(250);
  private static final Duration JUMP_ANIMATION_DURATION = Duration.millis(400);
  private static final Duration DELAY_BEFORE_SECONDARY_MOVE = Duration.seconds(1);

  private static final Color START_FINISH_TILE_COLOR = Color.web("#4CAF50");
  private static final Color DEFAULT_TILE_COLOR_1 = Color.web("#424242");
  private static final Color DEFAULT_TILE_COLOR_2 = Color.web("#616161");
  private static final Color BOOST_PAD_COLOR = Color.web("#2196F3");
  private static final Color ASTEROID_FIELD_COLOR = Color.web("#795548");
  private static final Color TILE_STROKE_COLOR = Color.web("#BDBDBD", 0.5);
  private static final double TILE_STROKE_WIDTH = 1.0;
  private static final double TILE_NUMBER_FONT_FACTOR = 0.25;
  private static final Color TILE_NUMBER_COLOR = Color.web("#FFFFFF");
  private static final double TOKEN_RADIUS_FACTOR = 0.35;
  private static final double TOKEN_STROKE_WIDTH = 2.0;
  private static final Color TOKEN_STROKE_COLOR = Color.BLACK;

  public AstroRallyRenderer() {
    LOGGER.fine("AstroRallyRenderer instance created.");
  }

  @Override
  public void renderInitialBoard(Pane boardPane, Board board) {
    Objects.requireNonNull(boardPane, "boardPane cannot be null for rendering");
    Objects.requireNonNull(board, "board data model cannot be null for rendering");
    LOGGER.info("Rendering initial Astro Rally board visuals.");

    boardPane.getChildren().clear();
    tileCenterPositions.clear();

    this.totalTilesOnBoard = board.getBoardSize();
    int totalTiles = this.totalTilesOnBoard;

    this.tilesPerSideExcludingCorners = 9;
    if (totalTiles != 40 && totalTiles >= 4) {
      if (totalTiles % 4 == 0) {
        this.tilesPerSideExcludingCorners = (totalTiles / 4) - 1;
      } else {
        LOGGER.warning(
            "Cannot form a perfect square loop with "
                + totalTiles
                + " tiles for Monopoly-style rendering. Using default of 9 for calculations, layout may be imperfect.");
      }
    } else if (totalTiles < 4 && totalTiles != 0) {
      LOGGER.warning("Board has less than 4 tiles, Monopoly-style rendering might look odd.");
      this.tilesPerSideExcludingCorners = 0;
    }


    double paneWidth = boardPane.getWidth() > 0 ? boardPane.getWidth() : boardPane.getPrefWidth();
    double paneHeight = boardPane.getHeight() > 0 ? boardPane.getHeight() : boardPane.getPrefHeight();

    int validTilesPerSide = Math.max(0, this.tilesPerSideExcludingCorners);

    if (paneWidth <= 0) paneWidth = (validTilesPerSide + 2) * 50.0;
    if (paneHeight <= 0) paneHeight = (validTilesPerSide + 2) * 50.0;


    int visualGridSize = validTilesPerSide + 2;
    if (visualGridSize == 0 && totalTiles > 0) visualGridSize = 1;
    else if (visualGridSize == 0 && totalTiles == 0) {
      LOGGER.warning("No tiles and zero visual grid size, cannot render board.");
      return;
    }


    this.calculatedTileSize = Math.min(paneWidth / visualGridSize, paneHeight / visualGridSize);

    if (this.calculatedTileSize <= 1.0) {
      this.calculatedTileSize = 20.0;
      LOGGER.warning( "Calculated tile size very small or zero, defaulted to " + this.calculatedTileSize);
    }

    double actualBoardWidth = visualGridSize * this.calculatedTileSize;
    double actualBoardHeight = visualGridSize * this.calculatedTileSize;
    this.offsetX = Math.max(0, (paneWidth - actualBoardWidth) / 2.0);
    this.offsetY = Math.max(0, (paneHeight - actualBoardHeight) / 2.0);

    Map<Integer, Tile> tilesMap = board.getTiles();
    for (int i = 1; i <= totalTiles; i++) {
      Tile tile = tilesMap.get(i);
      if (tile != null) {
        createTileVisual(boardPane, tile, totalTiles);
      } else {
        LOGGER.warning("Tile with ID " + i + " not found in board model during render.");
      }
    }
    LOGGER.info("Astro Rally board rendering complete. Calculated tile size: " + calculatedTileSize + " for " + totalTiles + " tiles.");
  }

  private void createTileVisual(Pane boardPane, Tile tile, int totalBoardTiles) {
    int tileId = tile.getTileId();
    int tilesPerFullEdge = Math.max(1, this.tilesPerSideExcludingCorners + 1);
    Point2D topLeft = calculateMonopolyStyleTilePosition(tileId, tilesPerFullEdge, totalBoardTiles);

    if (topLeft == null) {
      LOGGER.severe("Failed to calculate position for tile ID: " + tileId);
      return;
    }

    Rectangle tileBg = new Rectangle(calculatedTileSize, calculatedTileSize);
    tileBg.setStroke(TILE_STROKE_COLOR);
    tileBg.setStrokeWidth(TILE_STROKE_WIDTH);

    TileAction action = tile.getLandAction();
    if (tileId == 1) {
      tileBg.setFill(START_FINISH_TILE_COLOR);
    } else if (action != null && action.getActionType() == ActionType.BOOST_PAD) {
      tileBg.setFill(BOOST_PAD_COLOR);
    } else if (action != null && action.getActionType() == ActionType.ASTEROID_FIELD) {
      tileBg.setFill(ASTEROID_FIELD_COLOR);
    } else {
      tileBg.setFill((tileId % 2 == 0) ? DEFAULT_TILE_COLOR_1 : DEFAULT_TILE_COLOR_2);
    }

    Label tileLabel = new Label(String.valueOf(tileId));
    double fontSize = Math.max(8.0, calculatedTileSize * TILE_NUMBER_FONT_FACTOR);
    tileLabel.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
    tileLabel.setTextFill(TILE_NUMBER_COLOR);

    StackPane tileNode = new StackPane(tileBg, tileLabel);
    tileNode.setLayoutX(topLeft.getX());
    tileNode.setLayoutY(topLeft.getY());
    tileNode.setUserData(tileId);

    boardPane.getChildren().add(tileNode);

    Point2D center = new Point2D(
        topLeft.getX() + calculatedTileSize / 2.0,
        topLeft.getY() + calculatedTileSize / 2.0);
    tileCenterPositions.put(tileId, center);
  }

  private Point2D calculateMonopolyStyleTilePosition(int tileId, int tilesPerFullEdge, int totalBoardTiles) {
    if (totalBoardTiles == 0) return new Point2D(offsetX,offsetY);
    if (tilesPerFullEdge == 0 && totalBoardTiles > 0) tilesPerFullEdge = 1;

    if (tileId < 1 || tileId > totalBoardTiles) {
      LOGGER.warning("Invalid tileId " + tileId + " for Monopoly style positioning. Total tiles: " + totalBoardTiles);
      return new Point2D(offsetX, offsetY);
    }
    if (tilesPerFullEdge == 0) {
      LOGGER.warning("tilesPerFullEdge is zero with totalBoardTiles=" + totalBoardTiles + ". Cannot position tile " + tileId);
      return new Point2D(offsetX, offsetY);
    }


    double xGrid = 0, yGrid = 0;

    if (totalBoardTiles == 1) {
      xGrid = 0;
      yGrid = 0;
    } else if (tilesPerSideExcludingCorners == 0 && totalBoardTiles > 0) {
      if (tileId <= tilesPerFullEdge) {
        xGrid = tileId - 1;
        yGrid = 0;
      } else {
        xGrid = 0; yGrid = 0;
      }
    }

    else if (tileId >= 1 && tileId <= tilesPerFullEdge) {
      xGrid = tileId - 1;
      yGrid = tilesPerFullEdge - 1;
    } else if (tileId > tilesPerFullEdge && tileId <= 2 * tilesPerFullEdge) {
      xGrid = tilesPerFullEdge - 1;
      yGrid = (tilesPerFullEdge - 1) - (tileId - (tilesPerFullEdge + 1));
    } else if (tileId > 2 * tilesPerFullEdge && tileId <= 3 * tilesPerFullEdge) {
      xGrid = (tilesPerFullEdge - 1) - (tileId - (2 * tilesPerFullEdge + 1));
      yGrid = 0;
    } else if (tileId > 3 * tilesPerFullEdge && tileId <= 4 * tilesPerFullEdge) {
      xGrid = 0;
      yGrid = tileId - (3 * tilesPerFullEdge + 1);
    } else {
      LOGGER.warning("TileId " + tileId + " out of calculable range for Monopoly style positioning. Defaulting. tilesPerFullEdge=" + tilesPerFullEdge);
      if (tilesPerFullEdge > 0) {
        return new Point2D(offsetX, offsetY + (tilesPerFullEdge - 1) * calculatedTileSize);
      }
      return new Point2D(offsetX, offsetY);
    }
    return new Point2D(offsetX + xGrid * calculatedTileSize, offsetY + yGrid * calculatedTileSize);
  }

  @Override
  public Node createPlayerTokenNode(Player player) {
    Objects.requireNonNull(player, "Player cannot be null for token creation");
    LOGGER.finer(() -> "Creating token for player: " + player.getName());

    Circle token = new Circle(calculatedTileSize * TOKEN_RADIUS_FACTOR);
    token.setStroke(TOKEN_STROKE_COLOR);
    token.setStrokeWidth(TOKEN_STROKE_WIDTH);
    token.setFill(getPlayerColor(player));
    token.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.5)));
    token.setUserData(player);
    return token;
  }

  private Color getPlayerColor(Player player) {
    if (player.getPieceType() != null) {
      switch (player.getPieceType().toLowerCase()) {
        case "interceptor": return Color.CRIMSON;
        case "novajumper": return Color.DEEPSKYBLUE;
        case "warpcruiser": return Color.MEDIUMPURPLE;
        default: break;
      }
    }
    int hash = player.getName().hashCode();
    java.util.Random random = new java.util.Random(hash);
    return Color.rgb(random.nextInt(206) + 50, random.nextInt(206) + 50, random.nextInt(206) + 50, 0.95);
  }

  @Override
  public void placePlayerTokenAtTile(Node playerTokenNode, Tile tile, Pane boardPane) {
    Objects.requireNonNull(playerTokenNode, "Player token node cannot be null.");
    Objects.requireNonNull(tile, "Tile cannot be null for placement.");

    int tileId = tile.getTileId();
    Point2D targetCenter = tileCenterPositions.get(tileId);

    if (targetCenter == null) {
      LOGGER.log(Level.SEVERE, "Cannot place player token: No center position found for tile ID {0}.", tileId);
      return;
    }

    commitTokenPosition(playerTokenNode, targetCenter);

    Object currentUserData = playerTokenNode.getUserData();
    if (currentUserData instanceof Player) {
      playerTokenNode.setUserData(new PlayerTokenData((Player) currentUserData, tileId));
    } else if (currentUserData instanceof PlayerTokenData) {
      ((PlayerTokenData) currentUserData).setCurrentTileId(tileId);
    } else {
      LOGGER.log(Level.WARNING, "PlayerTokenNode's UserData was not Player or PlayerTokenData. UserData: {0}", currentUserData);
    }
    LOGGER.log(Level.FINE, "Player token placed and committed at tile {0}.", tileId);
  }

  private void commitTokenPosition(Node token, Point2D targetCenter) {
    Objects.requireNonNull(token, "Token cannot be null for commitPosition.");
    Objects.requireNonNull(targetCenter, "TargetCenter cannot be null for commitPosition.");

    double tokenWidth = token.getBoundsInLocal().getWidth();
    double tokenHeight = token.getBoundsInLocal().getHeight();

    token.setLayoutX(targetCenter.getX() - tokenWidth / 2.0);
    token.setLayoutY(targetCenter.getY() - tokenHeight / 2.0);
    token.setTranslateX(0);
    token.setTranslateY(0);
    LOGGER.log(Level.FINEST, "Token position committed to layout center: {0}", targetCenter);
  }

  private void updateTokenData(Node tokenNode, int newTileId) {
    Objects.requireNonNull(tokenNode, "TokenNode cannot be null for updateTokenData.");
    if (tokenNode.getUserData() instanceof PlayerTokenData) {
      PlayerTokenData data = (PlayerTokenData) tokenNode.getUserData();
      data.setCurrentTileId(newTileId);
      LOGGER.log(Level.FINER, "PlayerTokenData updated: Player {0} is now on tile {1}.",
          new Object[]{data.getPlayer().getName(), newTileId});
    } else {
      LOGGER.log(Level.WARNING, "Failed to update token data: UserData is not PlayerTokenData. UserData: {0}",
          tokenNode.getUserData() != null ? tokenNode.getUserData().getClass().getName() : "null");
    }
  }

  @Override
  public void updatePlayerTokenPosition(Node playerTokenNode, Tile targetTileAfterDiceRoll, Pane boardPane, Runnable onOverallAnimationComplete) {
    Objects.requireNonNull(playerTokenNode, "PlayerTokenNode cannot be null.");
    Objects.requireNonNull(targetTileAfterDiceRoll, "TargetTileAfterDiceRoll cannot be null.");

    if (!(playerTokenNode.getUserData() instanceof PlayerTokenData)) {
      LOGGER.log(Level.SEVERE, "Cannot animate token: UserData is not PlayerTokenData. UserData: {0}",
          playerTokenNode.getUserData() != null ? playerTokenNode.getUserData().getClass().getName() : "null");
      runFinalCallback(onOverallAnimationComplete);
      return;
    }
    PlayerTokenData tokenData = (PlayerTokenData) playerTokenNode.getUserData();
    Player player = tokenData.getPlayer();

    int currentTileId = tokenData.getCurrentTileId();
    int diceRollDestinationTileId = targetTileAfterDiceRoll.getTileId();

    LOGGER.log(Level.INFO, "Starting animation for {0}: From tile {1} to dice roll destination {2}.",
        new Object[]{player.getName(), currentTileId, diceRollDestinationTileId});

    Point2D startPositionCenter = tileCenterPositions.get(currentTileId);
    if (startPositionCenter == null) {
      LOGGER.log(Level.SEVERE, "Animation failed: No center position for current start tile ID {0}. Attempting to place at final destination.", currentTileId);
      Point2D emergencyEndCenter = tileCenterPositions.get(diceRollDestinationTileId);
      if (emergencyEndCenter != null) {
        commitTokenPosition(playerTokenNode, emergencyEndCenter);
        updateTokenData(playerTokenNode, diceRollDestinationTileId);
      }
      runFinalCallback(onOverallAnimationComplete);
      return;
    }

    commitTokenPosition(playerTokenNode, startPositionCenter);

    if (currentTileId == diceRollDestinationTileId) {
      LOGGER.log(Level.FINE, "Player {0} rolled to the same tile ({1}). Checking for action jump.", new Object[]{player.getName(), currentTileId});
      handleActionVisuals(playerTokenNode, targetTileAfterDiceRoll, onOverallAnimationComplete);
      return;
    }

    List<Integer> path = calculatePath(currentTileId, diceRollDestinationTileId);
    if (path.isEmpty()) {
      LOGGER.log(Level.WARNING, "Animation path from {0} to {1} is empty. Processing as if on target.",
          new Object[]{currentTileId, diceRollDestinationTileId});
      Point2D targetCenter = tileCenterPositions.get(diceRollDestinationTileId);
      if (targetCenter != null) {
        commitTokenPosition(playerTokenNode, targetCenter);
        updateTokenData(playerTokenNode, diceRollDestinationTileId);
      } else {
        LOGGER.log(Level.SEVERE, "Cannot commit to empty path target: Missing center for tile ID {0}.", diceRollDestinationTileId);
      }
      handleActionVisuals(playerTokenNode, targetTileAfterDiceRoll, onOverallAnimationComplete);
      return;
    }

    SequentialTransition walkAnimation = new SequentialTransition(playerTokenNode);
    List<TranslateTransition> walkTransitions = buildWalkTransitions(path, startPositionCenter);
    walkAnimation.getChildren().addAll(walkTransitions);

    walkAnimation.setOnFinished(walkEvent -> {
      LOGGER.log(Level.FINE, "Walk animation for {0} to tile {1} completed.", new Object[]{player.getName(), diceRollDestinationTileId});
      Point2D diceLandCenter = tileCenterPositions.get(diceRollDestinationTileId);
      if (diceLandCenter != null) {
        commitTokenPosition(playerTokenNode, diceLandCenter);
        updateTokenData(playerTokenNode, diceRollDestinationTileId);
      } else {
        LOGGER.log(Level.SEVERE, "Failed to commit after walk: No center for dice roll destination tile ID {0}.", diceRollDestinationTileId);
      }
      handleActionVisuals(playerTokenNode, targetTileAfterDiceRoll, onOverallAnimationComplete);
    });
    walkAnimation.play();
  }

  private void handleActionVisuals(Node playerTokenNode, Tile landTile, Runnable onOverallAnimationComplete) {
    PlayerTokenData tokenData = (PlayerTokenData) playerTokenNode.getUserData();
    Player player = tokenData.getPlayer();
    int tileLandedOnId = landTile.getTileId();

    TileAction action = landTile.getLandAction();
    int effectSteps = 0;

    if (action instanceof BoostPadAction) {
      effectSteps = ((BoostPadAction) action).consumeLastDeterminedMovementEffectSteps();
    } else if (action instanceof AsteroidFieldAction) {
      effectSteps = ((AsteroidFieldAction) action).consumeLastDeterminedMovementEffectSteps();
    }

    if (effectSteps != 0) {
      Point2D actionTileCenter = tileCenterPositions.get(tileLandedOnId);
      if (actionTileCenter == null) {
        LOGGER.log(Level.SEVERE, "Cannot perform secondary move for {0}: action tile {1} has no center.", new Object[]{player.getName(), tileLandedOnId});
        runFinalCallback(onOverallAnimationComplete);
        return;
      }

      List<Integer> secondaryPathTileIds = calculateRelativePath(tileLandedOnId, effectSteps);
      if (secondaryPathTileIds.isEmpty()) {
        LOGGER.log(Level.WARNING, "Secondary move path for {0} from {1} by {2} steps is empty or invalid. No secondary animation.", new Object[]{player.getName(), tileLandedOnId, effectSteps});
        runFinalCallback(onOverallAnimationComplete);
        return;
      }

      final int finalDestinationAfterSecondaryMoveId = secondaryPathTileIds.get(secondaryPathTileIds.size() - 1);

      PauseTransition pause = new PauseTransition(DELAY_BEFORE_SECONDARY_MOVE);
      List<TranslateTransition> secondaryWalkTransitions = buildWalkTransitions(secondaryPathTileIds, actionTileCenter);
      SequentialTransition secondaryWalkAnimation = new SequentialTransition(playerTokenNode);
      secondaryWalkAnimation.getChildren().addAll(secondaryWalkTransitions);
      SequentialTransition delayedEffectAnimation = new SequentialTransition(pause, secondaryWalkAnimation);

      delayedEffectAnimation.setOnFinished(event -> {
        Point2D finalCommitCenter = tileCenterPositions.get(finalDestinationAfterSecondaryMoveId);
        if (finalCommitCenter != null) {
          commitTokenPosition(playerTokenNode, finalCommitCenter);
          updateTokenData(playerTokenNode, finalDestinationAfterSecondaryMoveId);
        } else {
          LOGGER.log(Level.SEVERE, "Cannot commit after secondary move for {0}: final tile {1} has no center.", new Object[]{player.getName(), finalDestinationAfterSecondaryMoveId});
        }
        LOGGER.log(Level.FINE, "Delayed effect animation for {0} to tile {1} completed.", new Object[]{player.getName(), finalDestinationAfterSecondaryMoveId});
        runFinalCallback(onOverallAnimationComplete);
      });

      LOGGER.log(Level.INFO, "Player {0} on action tile {1} (effect: {2} steps). Starting delay then secondary move to {3}.", new Object[]{player.getName(), tileLandedOnId, effectSteps, finalDestinationAfterSecondaryMoveId});
      delayedEffectAnimation.play();

    } else if (action != null && action.getDestinationTileId() > 0 && action.getDestinationTileId() != tileLandedOnId) {
      int jumpDestinationTileId = action.getDestinationTileId();
      LOGGER.log(Level.INFO, "Player {0} on tile {1} triggered predefined jump to tile {2}.",
          new Object[]{player.getName(), tileLandedOnId, jumpDestinationTileId});

      Point2D jumpFromCenter = tileCenterPositions.get(tileLandedOnId);
      Point2D jumpToCenter = tileCenterPositions.get(jumpDestinationTileId);

      if (jumpFromCenter != null && jumpToCenter != null) {
        TranslateTransition jumpAnimation = buildJumpTransition(playerTokenNode, jumpFromCenter, jumpToCenter);
        jumpAnimation.setOnFinished(jumpEvent -> {
          updateTokenData(playerTokenNode, jumpDestinationTileId);
          LOGGER.log(Level.FINE, "Predefined action jump for {0} to tile {1} completed.", new Object[]{player.getName(), jumpDestinationTileId});
          runFinalCallback(onOverallAnimationComplete);
        });
        jumpAnimation.play();
      } else {
        LOGGER.log(Level.WARNING, "Cannot execute predefined action jump for {0}: Missing tile center. From: {1} (center: {2}), To: {3} (center: {4}). Finalizing animation.",
            new Object[]{player.getName(), tileLandedOnId, jumpFromCenter, jumpDestinationTileId, jumpToCenter});
        runFinalCallback(onOverallAnimationComplete);
      }
    } else {
      LOGGER.log(Level.FINE, "No further jump or delayed effect action for {0} from tile {1}. Animation sequence ends here.", new Object[]{player.getName(), tileLandedOnId});
      runFinalCallback(onOverallAnimationComplete);
    }
  }

  @Override
  public void animateTokenDirectly(Node playerTokenNode, Tile targetTile, Pane boardPane, Runnable onAnimationComplete) {
    Objects.requireNonNull(playerTokenNode, "PlayerTokenNode cannot be null.");
    Objects.requireNonNull(targetTile, "TargetTile cannot be null.");

    if (!(playerTokenNode.getUserData() instanceof PlayerTokenData)) {
      LOGGER.log(Level.SEVERE, "Cannot animate token directly: UserData not PlayerTokenData. UserData: {0}",
          playerTokenNode.getUserData() != null ? playerTokenNode.getUserData().getClass().getName() : "null");
      runFinalCallback(onAnimationComplete);
      return;
    }
    PlayerTokenData tokenData = (PlayerTokenData) playerTokenNode.getUserData();
    Player player = tokenData.getPlayer();

    int currentTileId = tokenData.getCurrentTileId();
    int destinationTileId = targetTile.getTileId();

    LOGGER.log(Level.INFO, "Animating {0} directly from tile {1} to {2}.",
        new Object[]{player.getName(), currentTileId, destinationTileId});

    Point2D currentPositionCenter = tileCenterPositions.get(currentTileId);
    Point2D destinationPositionCenter = tileCenterPositions.get(destinationTileId);

    if (destinationPositionCenter == null) {
      LOGGER.log(Level.SEVERE, "Direct animation failed: No center for destination tile ID {0}. Token remains at {1}.",
          new Object[]{destinationTileId, currentTileId});
      runFinalCallback(onAnimationComplete);
      return;
    }
    if (currentPositionCenter == null) {
      LOGGER.log(Level.WARNING, "Direct animation: No center for current tile ID {0}. Committing to destination {1}.",
          new Object[]{currentTileId, destinationTileId});
      commitTokenPosition(playerTokenNode, destinationPositionCenter);
      updateTokenData(playerTokenNode, destinationTileId);
      runFinalCallback(onAnimationComplete);
      return;
    }

    commitTokenPosition(playerTokenNode, currentPositionCenter);

    if (currentTileId == destinationTileId) {
      LOGGER.log(Level.FINE, "{0} is already at direct target tile {1}. No animation needed.", new Object[]{player.getName(), destinationTileId});
      runFinalCallback(onAnimationComplete);
      return;
    }

    TranslateTransition jumpAnimation = buildJumpTransition(playerTokenNode, currentPositionCenter, destinationPositionCenter);
    jumpAnimation.setOnFinished(event -> {
      updateTokenData(playerTokenNode, destinationTileId);
      LOGGER.log(Level.FINE, "Direct animation for {0} to tile {1} completed.", new Object[]{player.getName(), destinationTileId});
      runFinalCallback(onAnimationComplete);
    });
    jumpAnimation.play();
  }

  private List<TranslateTransition> buildWalkTransitions(List<Integer> pathTileIds, Point2D initialLayoutCenter) {
    List<TranslateTransition> transitions = new ArrayList<>();
    for (int tileIdInPath : pathTileIds) {
      Point2D targetCenter = tileCenterPositions.get(tileIdInPath);
      if (targetCenter == null) {
        LOGGER.log(Level.WARNING, "BuildWalk: Skipping step to missing tile ID {0}.", tileIdInPath);
        continue;
      }
      TranslateTransition step = new TranslateTransition(MOVE_ANIMATION_DURATION);
      step.setToX(targetCenter.getX() - initialLayoutCenter.getX());
      step.setToY(targetCenter.getY() - initialLayoutCenter.getY());
      step.setInterpolator(Interpolator.LINEAR);
      transitions.add(step);
    }
    return transitions;
  }

  private TranslateTransition buildJumpTransition(Node tokenNode, Point2D jumpFromCenter, Point2D jumpToCenter) {
    commitTokenPosition(tokenNode, jumpFromCenter);

    TranslateTransition jump = new TranslateTransition(JUMP_ANIMATION_DURATION, tokenNode);
    jump.setFromX(0);
    jump.setFromY(0);
    jump.setToX(jumpToCenter.getX() - jumpFromCenter.getX());
    jump.setToY(jumpToCenter.getY() - jumpFromCenter.getY());
    jump.setInterpolator(Interpolator.EASE_BOTH);

    jump.setOnFinished(event -> commitTokenPosition(tokenNode, jumpToCenter));
    return jump;
  }

  private void runFinalCallback(Runnable callback) {
    if (callback != null) {
      Platform.runLater(callback);
    }
  }

  private List<Integer> calculatePath(int startId, int endId) {
    List<Integer> path = new ArrayList<>();
    if (startId == endId) {
      return path;
    }

    int currentId = startId;
    int safetyBreak = this.totalTilesOnBoard * 2;
    int count = 0;

    if (this.totalTilesOnBoard <= 0) {
      LOGGER.log(Level.SEVERE, "Cannot calculate path: totalTilesOnBoard is not positive ({0})", this.totalTilesOnBoard);
      return path;
    }

    while (currentId != endId && count < safetyBreak) {
      currentId++;
      if (currentId > this.totalTilesOnBoard) {
        currentId = 1;
      }
      path.add(currentId);
      count++;
    }
    if (count >= safetyBreak) {
      LOGGER.log(Level.SEVERE, "Path calculation exceeded safety break. Start: {0}, End: {1}, Path: {2}",
          new Object[]{startId, endId, path});
      return new ArrayList<>();
    }
    LOGGER.log(Level.FINEST, "Calculated path from {0} to {1}: {2}", new Object[]{startId, endId, path});
    return path;
  }

  private List<Integer> calculateRelativePath(int startId, int steps) {
    List<Integer> path = new ArrayList<>();
    if (steps == 0) {
      return path;
    }

    int currentId = startId;
    int numStepsToTake = Math.abs(steps);
    boolean forward = steps > 0;
    int boardSize = this.totalTilesOnBoard;

    if (boardSize <= 0) {
      LOGGER.log(Level.SEVERE, "Cannot calculate relative path: boardSize is not positive ({0})", boardSize);
      return path;
    }

    for (int i = 0; i < numStepsToTake; i++) {
      if (forward) {
        currentId++;
        if (currentId > boardSize) {
          currentId = 1;
        }
      } else {
        currentId--;
        if (currentId < 1) {
          currentId = boardSize;
        }
      }
      path.add(currentId);
    }
    LOGGER.log(Level.FINEST, "Calculated relative path from {0} by {1} steps: {2}", new Object[]{startId, steps, path});
    return path;
  }
}