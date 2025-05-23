package edu.ntnu.idi.idatt.view.renderer;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;
import static javafx.application.Platform.runLater;
import static javafx.scene.text.Font.font;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.AsteroidFieldAction;
import edu.ntnu.idi.idatt.model.core.actions.BoostPadAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.utils.PlayerTokenData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;


/**
 * Renders a board and player token movements for the Astro Rally game variant
 * in a JavaFX application. Implements a "Monopoly"-style loop layout where tiles
 * wrap around the edges. Handles initial board drawing, token creation, placement,
 * and animated movements including walk sequences, jumps, and special-tile actions
 * (boost pads and asteroid fields). All animations are queued on the JavaFX
 * Application Thread, and detailed logging is provided for debugging.
 *
 *
 * <p>KI-assistanse (Gemini 2.5 Pro) ble brukt for å:
 * - Utvikle og forfine algoritmen i `calculateTilePosition` for å
 * plassere fliser korrekt rundt kanten av brettet i en "Monopol"-stil.
 * - Håndtere dynamisk beregning av `calculatedTileSize`
 * og `tilesPerSideExcludingCorners` basert på totalt antall fliser og panelets dimensjoner.
 * - Logikk for sentrering av brettet (offsetX, offsetY).
 * Dato: 18-05-25
 *
 * @see BoardRenderer
 * @see edu.ntnu.idi.idatt.model.core.actions.TileAction
 * @see javafx.animation.TranslateTransition
 */
public class AstroRallyRenderer implements BoardRenderer {

  private static final java.util.logging.Logger LOGGER =
      getLogger(AstroRallyRenderer.class.getName());

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

  /**
   * Default constructor.
   *
   * <p>Intentionally left empty as no initialization beyond field defaults is required.
   * </p>
   */
  public AstroRallyRenderer() {
    // No-op constructor: all setup is handled in renderInitialBoard when called
  }


  /**
   * Renders the board’s tiles into the given pane according to current layout settings.
   * This includes calculating tile positions for a "Monopoly-style" loop layout,
   * determining tile size dynamically, and centering the board within the pane.
   *
   * <p>KI-assistanse (Gemini 2.5 Pro) ble brukt for å:
   * - Utvikle og forfine algoritmen i `calculateTilePosition` for å plassere fliser
   * korrekt rundt kanten av brettet i en "Monopol"-stil.
   * - Håndtere dynamisk beregning av `calculatedTileSize` og `tilesPerSideExcludingCorners`
   * basert på totalt antall fliser og panelets dimensjoner.
   * - Logikk for sentrering av brettet (offsetX, offsetY).
   * Dato: 18-05-25
   *
   * @param boardPane the JavaFX Pane into which tile nodes will be added; must not be null
   * @param board the board model containing tile definitions and count; must not be null
   */
  @Override
  public void renderInitialBoard(final Pane boardPane, final Board board) {
    requireNonNull(boardPane, "boardPane cannot be null for rendering");
    requireNonNull(board, "board data model cannot be null for rendering");
    LOGGER.info(() -> "Rendering initial Astro Rally board visuals.");

    boardPane.getChildren().clear();
    this.tileCenterPositions.clear();

    this.totalTilesOnBoard = board.getBoardSize();
    final int totalTiles = this.totalTilesOnBoard;

    this.tilesPerSideExcludingCorners = 9;
    if (totalTiles != 40 && totalTiles >= 4) {
      if (totalTiles % 4 == 0) {
        this.tilesPerSideExcludingCorners = (totalTiles / 4) - 1;
      } else {
        LOGGER.log(
            WARNING,
            "Cannot form a perfect square loop with {0} tiles for Monopoly-style rendering. "
                + "Using default of {1} for calculations, layout may be imperfect.",
            new Object[]{totalTiles, this.tilesPerSideExcludingCorners}
        );
      }
    } else if (totalTiles < 4 && totalTiles != 0) {
      LOGGER.warning(() -> "Board has less than 4 tiles, Monopoly-style rendering might look odd.");
      this.tilesPerSideExcludingCorners = 0;
    }

    double paneWidth = boardPane.getWidth() > 0
        ? boardPane.getWidth()
        : boardPane.getPrefWidth();
    double paneHeight = boardPane.getHeight() > 0
        ? boardPane.getHeight()
        : boardPane.getPrefHeight();

    final int validTilesPerSide = max(0, this.tilesPerSideExcludingCorners);

    if (paneWidth <= 0) {
      paneWidth = (validTilesPerSide + 2) * 50.0;
    }
    if (paneHeight <= 0) {
      paneHeight = (validTilesPerSide + 2) * 50.0;
    }

    int visualGridSize = validTilesPerSide + 2;
    this.calculatedTileSize = Math.min(
        paneWidth / visualGridSize,
        paneHeight / visualGridSize
    );

    if (this.calculatedTileSize <= 1.0) {
      this.calculatedTileSize = 20.0;
      LOGGER.log(
          WARNING,
          "Calculated tile size very small or zero, defaulted to {0}",
          this.calculatedTileSize
      );
    }

    final double actualBoardWidth = visualGridSize * this.calculatedTileSize;
    final double actualBoardHeight = visualGridSize * this.calculatedTileSize;
    this.offsetX = max(0, (paneWidth - actualBoardWidth) / 2.0);
    this.offsetY = max(0, (paneHeight - actualBoardHeight) / 2.0);

    final Map<Integer, Tile> tilesMap = board.getTiles();
    for (int i = 1; i <= totalTiles; i++) {
      final Tile tile = tilesMap.get(i);
      if (tile != null) {
        createTileVisual(boardPane, tile, totalTiles);
      } else {
        LOGGER.log(
            WARNING,
            "Tile with ID {0} not found in board model during render.",
            i
        );
      }
    }
    LOGGER.log(
        INFO,
        "Astro Rally board rendering complete. Calculated tile size: {0} for {1} tiles.",
        new Object[]{this.calculatedTileSize, totalTiles}
    );
  }

  /**
   * Creates and adds a visual representation of a single tile to the board pane.
   * This includes calculating its position, styling its background based on type
   * (start, action, or default alternating), and adding a numbered label.
   * The tile's center position is then cached for player token management.
   *
   * <p>Key styling based on {@link ActionType}:
   * <ul>
   * <li>Start Tile (ID 1): {@code START_FINISH_TILE_COLOR}</li>
   * <li>Boost Pad: {@code BOOST_PAD_COLOR}</li>
   * <li>Asteroid Field: {@code ASTEROID_FIELD_COLOR}</li>
   * <li>Others: Alternating {@code DEFAULT_TILE_COLOR_1} / {@code DEFAULT_TILE_COLOR_2}</li>
   * </ul>
   * </p>
   *
   * @param boardPane the pane to which the tile node will be added; must not be null
   * @param tile the Tile model to render; must not be null
   * @param totalBoardTiles the total number of tiles on the board (for layout math)
   */
  private void createTileVisual(final Pane boardPane, final Tile tile,
      final int totalBoardTiles) {
    final int tileId = tile.getTileId();
    final int tilesPerFullEdge = max(1, this.tilesPerSideExcludingCorners + 1);
    final Point2D topLeft =
        calculateTilePosition(tileId, tilesPerFullEdge, totalBoardTiles);

    final Rectangle tileBg = new Rectangle(this.calculatedTileSize, this.calculatedTileSize);
    tileBg.setStroke(TILE_STROKE_COLOR);
    tileBg.setStrokeWidth(TILE_STROKE_WIDTH);

    final TileAction action = tile.getLandAction();
    if (tileId == 1) {
      tileBg.setFill(START_FINISH_TILE_COLOR);
    } else if (action != null && action.getActionType() == ActionType.BOOST_PAD) {
      tileBg.setFill(BOOST_PAD_COLOR);
    } else if (action != null && action.getActionType() == ActionType.ASTEROID_FIELD) {
      tileBg.setFill(ASTEROID_FIELD_COLOR);
    } else {
      tileBg.setFill((tileId % 2 == 0) ? DEFAULT_TILE_COLOR_1 : DEFAULT_TILE_COLOR_2);
    }

    final Label tileLabel = new Label(String.valueOf(tileId));
    tileLabel.setFont(font("Arial", FontWeight.BOLD,
        max(8.0, this.calculatedTileSize * TILE_NUMBER_FONT_FACTOR)));
    tileLabel.setTextFill(TILE_NUMBER_COLOR);

    final StackPane tileNode = new StackPane(tileBg, tileLabel);
    tileNode.setLayoutX(topLeft.getX());
    tileNode.setLayoutY(topLeft.getY());
    tileNode.setUserData(tileId);

    boardPane.getChildren().add(tileNode);

    final Point2D center = new Point2D(
        topLeft.getX() + this.calculatedTileSize / 2.0,
        topLeft.getY() + this.calculatedTileSize / 2.0);
    this.tileCenterPositions.put(tileId, center);
  }

  /**
   * Computes the top-left position for a tile in the Monopoly-style loop layout.
   * This method determines which side of the board the tile belongs to
   * (bottom, left, top, right) and calculates its (x,y) coordinates accordingly,
   * applying offsets for centering.
   *
   * <p>KI-assistanse (Gemini 2.5 Pro) var sentral for å:
   * - Utvikle og feilsøke algoritmen for å korrekt plassere fliser langs de fire
   * sidene av et "Monopol"-brett, inkludert hjørnehåndtering.
   * - Oversette flisens ID til en posisjon på en av de fire kantene.
   * - Håndtere spesialtilfeller som brett med få fliser.
   * Dato: YYYY-MM-DD
   *
   * @param tileId the 1-based index of the tile to position
   * @param tilesPerFullEdge number of tiles per side (including corners)
   * @param totalBoardTiles total number of tiles on the board
   * @return the Point2D (x,y) where the tile’s top-left corner should be placed
   */
  private Point2D calculateTilePosition(
      final int tileId,
      int tilesPerFullEdge,
      final int totalBoardTiles
  ) {
    if (totalBoardTiles == 0) {
      return new Point2D(offsetX, offsetY);
    }
    if (tilesPerFullEdge == 0 && totalBoardTiles > 0) {
      tilesPerFullEdge = 1;
    }

    if (tileId < 1 || tileId > totalBoardTiles) {
      LOGGER.log(
          WARNING,
          "Invalid tileId {0} for Monopoly style positioning. Total tiles: {1}",
          new Object[]{tileId, totalBoardTiles}
      );
      return new Point2D(offsetX, offsetY);
    }

    double Xgrid;
    double Ygrid;

    if (totalBoardTiles == 1) {
      Xgrid = 0;
      Ygrid = 0;
    } else if (tilesPerSideExcludingCorners == 0) {
      if (tileId <= tilesPerFullEdge) {
        Xgrid = tileId - 1;
      } else {
        Xgrid = 0;
      }
      Ygrid = 0;
    } else if (tileId <= tilesPerFullEdge) {
      Xgrid = tileId - 1;
      Ygrid = tilesPerFullEdge - 1;
    } else if (tileId <= 2 * tilesPerFullEdge) {
      Xgrid = tilesPerFullEdge - 1;
      Ygrid = (tilesPerFullEdge - 1) - (tileId - (tilesPerFullEdge + 1));
    } else if (tileId <= 3 * tilesPerFullEdge) {
      Xgrid = (tilesPerFullEdge - 1) - (tileId - (2 * tilesPerFullEdge + 1));
      Ygrid = 0;
    } else if (tileId <= 4 * tilesPerFullEdge) {
      Xgrid = 0;
      Ygrid = tileId - (3 * tilesPerFullEdge + 1);
    } else {
      LOGGER.log(
          WARNING,
          "TileId {0} out of calculable range for Monopoly style positioning. "
              + "Defaulting. tilesPerFullEdge={1}",
          new Object[]{tileId, tilesPerFullEdge}
      );
      if (tilesPerFullEdge > 0) {
        return new Point2D(
            offsetX,
            offsetY + (tilesPerFullEdge - 1) * calculatedTileSize
        );
      }
      return new Point2D(offsetX, offsetY);
    }

    return new Point2D(
        offsetX + Xgrid * calculatedTileSize,
        offsetY + Ygrid * calculatedTileSize
    );
  }

  /**
   * Creates a circular token node for the given player.
   *
   * @param player the Player for whom to create the token; must not be null
   * @return a JavaFX Node (Circle) styled and sized for the player
   */
  @Override
  public Node createPlayerTokenNode(final Player player) {
    requireNonNull(player, "Player cannot be null for token creation");

    final Circle token = new Circle(this.calculatedTileSize * TOKEN_RADIUS_FACTOR);
    token.setStroke(TOKEN_STROKE_COLOR);
    token.setStrokeWidth(TOKEN_STROKE_WIDTH);
    token.setFill(getPlayerColor(player));
    token.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.5)));
    token.setUserData(player);
    return token;
  }

  private Color getPlayerColor(final Player player) {
    if (player.getPieceType() != null) {
      final String pieceTypeLower = player.getPieceType().toLowerCase();
      switch (pieceTypeLower) {
        case "dragon" -> {
          return Color.CRIMSON;
        }
        case "battleship" -> {
          return Color.DEEPSKYBLUE;
        }
        case "dog" -> {
          return Color.MEDIUMPURPLE;
        }
        case "car" -> {
          return Color.VIOLET;
        }
        case "hat" -> {
          return Color.TOMATO;
        }
        default -> {
          return Color.TURQUOISE;
        }
      }
    }
    final int hash = player.getName().hashCode();
    final Random random = new Random(hash);
    return Color.rgb(random.nextInt(206) + 50,
        random.nextInt(206) + 50,
        random.nextInt(206) + 50, 0.95);
  }

  /**
   * Immediately positions a player token node at the
   * calculated center of the specified {@code tile}
   * without animation.
   *
   * <p>This method updates the token's visual layout coordinates using pre-calculated
   * center positions from {@code tileCenterPositions}. It also robustly updates the
   * token's {@code userData} to reflect the new {@code tileId}, handling cases
   * where {@code userData} is either a {@link Player} instance (wrapping it in
   * a new {@link PlayerTokenData}) or an existing {@link PlayerTokenData} instance
   * (updating its {@code currentTileId}). Logs a warning for unexpected
   * {@code userData} types or missing tile center data.
   * </p>
   *
   * @param playerTokenNode the token Node to place; must not be null.
   * @param tile the Tile on which to place the token; must not be null
   * @param boardPane the Pane containing the board (unused here but part of interface);
   *                  must not be null
   */
  @Override
  public void placePlayerTokenAtTile(
      final Node playerTokenNode,
      final Tile tile,
      final Pane boardPane
  ) {
    requireNonNull(playerTokenNode, "Player token node cannot be null.");
    requireNonNull(tile, "Tile cannot be null for placement.");

    final int tileId = tile.getTileId();
    final Point2D targetCenter = tileCenterPositions.get(tileId);

    if (targetCenter == null) {
      LOGGER.log(
          SEVERE,
          "Cannot place player token: No center position found for tile ID {0}.",
          new Object[]{tileId}
      );
      return;
    }

    commitTokenPosition(playerTokenNode, targetCenter);

    final Object currentUserData = playerTokenNode.getUserData();
    switch (currentUserData) {
      case Player player -> playerTokenNode.setUserData(new PlayerTokenData(player, tileId));

      case PlayerTokenData ptd -> ptd.setCurrentTileId(tileId);

      default -> LOGGER.log(
          WARNING,
          "PlayerTokenNode's UserData was not Player or PlayerTokenData. UserData: {0}",
          currentUserData
      );
    }
  }


  private void commitTokenPosition(final Node token, final Point2D targetCenter) {
    requireNonNull(token, "Token cannot be null for commitPosition.");
    requireNonNull(targetCenter, "TargetCenter cannot be null for commitPosition.");

    token.setLayoutX(targetCenter.getX() - token.getBoundsInLocal().getWidth() / 2.0);
    token.setLayoutY(targetCenter.getY() - token.getBoundsInLocal().getHeight() / 2.0);
    token.setTranslateX(0);
    token.setTranslateY(0);
  }

  private void updateTokenData(final Node tokenNode, final int newTileId) {
    requireNonNull(tokenNode, "TokenNode cannot be null for updateTokenData.");
    if (tokenNode.getUserData() instanceof PlayerTokenData data) {
      data.setCurrentTileId(newTileId);
    } else {
      LOGGER.log(WARNING,
          "Failed to update token data: UserData is not PlayerTokenData. UserData: {0}",
          tokenNode.getUserData() != null ? tokenNode.getUserData().getClass().getName() : "null");
    }
  }

  /**
   * Animates a token moving along the board according to dice outcome,
   * then performs any landing-tile action animation (like Boost Pad or Asteroid Field effects),
   * and finally invokes the onOverallAnimationComplete callback.
   * The animation sequence involves an initial "walk" based on the dice roll,
   * followed by a potential secondary move if an action tile is landed upon.
   *
   * <p>KI-assistanse (Gemini 2.5 Pro) ble brukt for å:
   * - Strukturere den overordnede logikken for flerstegsanimasjoner:
   * 1. Første bevegelse basert på terningkast.
   * 2. Håndtering av spesialeffekter (Boost Pad/Asteroid Field) via `handleActionVisuals`,
   * som kan initiere en sekundær bevegelse.
   * - Utvikle `buildWalkTransitions` for å lage jevne "gå"-animasjoner langs brettets løkke.
   * - Sikre korrekt timing og sekvensiering av animasjoner (f.eks. bruk av PauseTransition).
   * - Håndtere `onFinished` callbacks for å kjede animasjoner og oppdatere token-data.
   * - Feilsøke logikk relatert til `PlayerTokenData` og posisjonsoppdateringer.
   * Dato: 18-05-25
   *
   * @param playerTokenNode the token Node to animate; must have PlayerTokenData userData
   * @param targetTileAfterDiceRoll the destination Tile after rolling dice; must not be null
   * @param boardPane the Pane containing the board and tokens; must not be null
   * @param onOverallAnimationComplete a callback Runnable to invoke when all animations finish
   */
  @Override
  public void updatePlayerTokenPosition(final Node playerTokenNode,
      final Tile targetTileAfterDiceRoll, final Pane boardPane,
      final Runnable onOverallAnimationComplete) {
    requireNonNull(playerTokenNode, "PlayerTokenNode cannot be null.");
    requireNonNull(targetTileAfterDiceRoll, "TargetTileAfterDiceRoll cannot be null.");

    if (!(playerTokenNode.getUserData() instanceof PlayerTokenData tokenData)) {
      LOGGER.log(SEVERE,
          "Cannot animate token: UserData is not PlayerTokenData. UserData: {0}",
          playerTokenNode.getUserData() != null
              ? playerTokenNode.getUserData().getClass().getName() : "null");
      runFinalCallback(onOverallAnimationComplete);
      return;
    }
    final Player player = tokenData.getPlayer();
    final int currentTileId = tokenData.getCurrentTileId();
    final int diceRollDestinationTileId = targetTileAfterDiceRoll.getTileId();

    LOGGER.log(INFO,
        "Starting animation for {0}: From tile {1} to dice roll destination {2}.",
        new Object[]{player.getName(), currentTileId, diceRollDestinationTileId});

    final Point2D startPositionCenter = this.tileCenterPositions.get(currentTileId);
    if (startPositionCenter == null) {
      LOGGER.log(SEVERE,
          "Animation failed: No center position for current start tile ID {0}."
              + " Attempting to place at final destination.", currentTileId);
      final Point2D emergencyEndCenter =
          this.tileCenterPositions.get(diceRollDestinationTileId);
      if (emergencyEndCenter != null) {
        commitTokenPosition(playerTokenNode, emergencyEndCenter);
        updateTokenData(playerTokenNode, diceRollDestinationTileId);
      }
      runFinalCallback(onOverallAnimationComplete);
      return;
    }

    commitTokenPosition(playerTokenNode, startPositionCenter);

    if (currentTileId == diceRollDestinationTileId) {
      handleActionVisuals(playerTokenNode, targetTileAfterDiceRoll, onOverallAnimationComplete);
      return;
    }

    final List<Integer> path = calculatePath(currentTileId, diceRollDestinationTileId);
    if (path.isEmpty()) {
      LOGGER.log(WARNING, "Animation path from {0} to {1} is empty."
          + " Processing as if on target.", new Object[]{currentTileId, diceRollDestinationTileId});
      final Point2D targetCenter = this.tileCenterPositions.get(diceRollDestinationTileId);
      if (targetCenter != null) {
        commitTokenPosition(playerTokenNode, targetCenter);
        updateTokenData(playerTokenNode, diceRollDestinationTileId);
      } else {
        LOGGER.log(SEVERE,
            "Cannot commit to empty path target: Missing center for tile ID {0}.",
            diceRollDestinationTileId);
      }
      handleActionVisuals(playerTokenNode, targetTileAfterDiceRoll, onOverallAnimationComplete);
      return;
    }

    final SequentialTransition walkAnimation = new SequentialTransition(playerTokenNode);
    final List<TranslateTransition> walkTransitions =
        buildWalkTransitions(path, startPositionCenter);
    walkAnimation.getChildren().addAll(walkTransitions);

    walkAnimation.setOnFinished(walkEvent -> {
      final Point2D diceLandCenter = this.tileCenterPositions.get(diceRollDestinationTileId);
      if (diceLandCenter != null) {
        commitTokenPosition(playerTokenNode, diceLandCenter);
        updateTokenData(playerTokenNode, diceRollDestinationTileId);
      } else {
        LOGGER.log(SEVERE,
            "Failed to commit after walk: No center for dice roll destination tile ID {0}.",
            diceRollDestinationTileId);
      }
      handleActionVisuals(playerTokenNode, targetTileAfterDiceRoll, onOverallAnimationComplete);
    });
    walkAnimation.play();
  }


  /**
   * KI-assistanse (Gemini 2.5 Pro) bidro til:
   * - Logikken for å hente effektstyrken (antall steg) fra BoostPadAction/AsteroidFieldAction.
   * - Å sette opp en `SequentialTransition` med en `PauseTransition` før den
   * sekundære bevegelsesanimasjonen.
   * - Kalkulering av den sekundære bevegelsesstien (`calculateRelativePath`).
   * - Korrekt kjedning av `onFinished` hendelser for å oppdatere spillerens
   * posisjon etter den sekundære bevegelsen.
   * Dato: 18-05-25
   *
   */
  private void handleActionVisuals(final Node playerTokenNode, final Tile landTile,
      final Runnable onOverallAnimationComplete) {
    if (!(playerTokenNode.getUserData() instanceof PlayerTokenData tokenData)) {
      LOGGER.log(SEVERE, "Action visuals: UserData is not PlayerTokenData. UserData: {0}",
          playerTokenNode.getUserData() != null
              ? playerTokenNode.getUserData().getClass().getName() : "null");
      runFinalCallback(onOverallAnimationComplete);
      return;
    }

    final Player player = tokenData.getPlayer();
    final int tileLandedOnId = landTile.getTileId();
    final TileAction action = landTile.getLandAction();
    int effectSteps = 0;

    if (action instanceof BoostPadAction boostAction) {
      effectSteps = boostAction.consumeLastDeterminedMovementEffectSteps();
    } else if (action instanceof AsteroidFieldAction asteroidAction) {
      effectSteps = asteroidAction.consumeLastDeterminedMovementEffectSteps();
    }

    if (effectSteps != 0) {
      final Point2D actionTileCenter = this.tileCenterPositions.get(tileLandedOnId);
      if (actionTileCenter == null) {
        LOGGER.log(SEVERE,
            "Cannot perform secondary move for {0}: action tile {1} has no center.",
            new Object[]{player.getName(), tileLandedOnId});
        runFinalCallback(onOverallAnimationComplete);
        return;
      }

      final List<Integer> secondaryPathTileIds =
          calculateRelativePath(tileLandedOnId, effectSteps);
      if (secondaryPathTileIds.isEmpty()) {
        LOGGER.log(WARNING,
            "Secondary move path for {0} from {1} by {2} steps is empty or invalid."
                + " No secondary animation.",
            new Object[]{player.getName(), tileLandedOnId, effectSteps});
        runFinalCallback(onOverallAnimationComplete);
        return;
      }

      final int finalDestId = secondaryPathTileIds.getLast();
      final PauseTransition pause = new PauseTransition(DELAY_BEFORE_SECONDARY_MOVE);
      final List<TranslateTransition> secondaryTransitions =
          buildWalkTransitions(secondaryPathTileIds, actionTileCenter);
      final SequentialTransition secondaryWalk = new SequentialTransition(playerTokenNode);
      secondaryWalk.getChildren().addAll(secondaryTransitions);
      final SequentialTransition delayedEffectAnim = new SequentialTransition(pause, secondaryWalk);

      delayedEffectAnim.setOnFinished(event -> {
        final Point2D finalCommitCenter = this.tileCenterPositions.get(finalDestId);
        if (finalCommitCenter != null) {
          commitTokenPosition(playerTokenNode, finalCommitCenter);
          updateTokenData(playerTokenNode, finalDestId);
        } else {
          LOGGER.log(SEVERE,
              "Cannot commit after secondary move for {0}: final tile {1} has no center.",
              new Object[]{player.getName(), finalDestId});
        }
        runFinalCallback(onOverallAnimationComplete);
      });

      LOGGER.log(INFO,
          "Player {0} on action tile {1} (effect: {2} steps)."
              + " Starting delay then secondary move to {3}.",
          new Object[]{player.getName(), tileLandedOnId, effectSteps, finalDestId});
      delayedEffectAnim.play();

    } else if (action != null && action.getDestinationTileId() > 0
        && action.getDestinationTileId() != tileLandedOnId) {
      final int jumpDestId = action.getDestinationTileId();
      LOGGER.log(INFO, "Player {0} on tile {1} triggered predefined jump to tile {2}.",
          new Object[]{player.getName(), tileLandedOnId, jumpDestId});

      final Point2D jumpFromCenter = this.tileCenterPositions.get(tileLandedOnId);
      final Point2D jumpToCenter = this.tileCenterPositions.get(jumpDestId);

      if (jumpFromCenter != null && jumpToCenter != null) {
        final TranslateTransition jumpAnim =
            buildJumpTransition(playerTokenNode, jumpFromCenter, jumpToCenter);
        jumpAnim.setOnFinished(jumpEvent -> {
          updateTokenData(playerTokenNode, jumpDestId);
          runFinalCallback(onOverallAnimationComplete);
        });
        jumpAnim.play();
      } else {
        LOGGER.log(WARNING,
            "Cannot execute predefined action jump for {0}: Missing tile center."
                + " From: {1} (center: {2}), To: {3} (center: {4}). Finalizing animation.",
            new Object[]{player.getName(), tileLandedOnId, jumpFromCenter,
                jumpDestId, jumpToCenter});
        runFinalCallback(onOverallAnimationComplete);
      }
    } else {
      runFinalCallback(onOverallAnimationComplete);
    }
  }


  /**
   * KI-assistanse (Gemini 2.5 Pro) ble brukt for å:
   * - Strukturere løkken for å iterere gjennom {@code pathTileIds}.
   * - Korrekt hente målsenterposisjonen ({@code targetCenter}) for hver flis i stien.
   * - Sette opp {@link javafx.animation.TranslateTransition} for hvert steg, spesielt
   * {@code setToX()} og {@code setToY()} for å beregne den relative bevegelsen
   * fra {@code initialLayoutCenter} til hver {@code targetCenter}.
   * - Velge en passende {@link javafx.animation.Interpolator} (Interpolator.LINEAR) for
   * en jevn "gå"-bevegelse.
   * Dato: 18-05-25
   */
  private List<TranslateTransition> buildWalkTransitions(final List<Integer> pathTileIds,
      final Point2D initialLayoutCenter) {
    final List<TranslateTransition> transitions = new ArrayList<>();
    for (final int tileIdInPath : pathTileIds) {
      final Point2D targetCenter = this.tileCenterPositions.get(tileIdInPath);
      if (targetCenter == null) {
        LOGGER.log(WARNING, "BuildWalk: Skipping step to missing tile ID {0}.",
            tileIdInPath);
        continue;
      }
      final TranslateTransition step = new TranslateTransition(MOVE_ANIMATION_DURATION);
      step.setToX(targetCenter.getX() - initialLayoutCenter.getX());
      step.setToY(targetCenter.getY() - initialLayoutCenter.getY());
      step.setInterpolator(Interpolator.LINEAR);
      transitions.add(step);
    }
    return transitions;
  }

  /**
   * KI-assistanse (Gemini 2.5 Pro) bidro til:
   * - Oppsettet av {@link javafx.animation.TranslateTransition} for et direkte "hopp".
   * - Korrekt bruk av {@code setFromX(0)} og {@code setFromY(0)} da translasjonen er relativ
   * til tokenets nåværende posisjon etter {@code commitTokenPosition(tokenNode, jumpFromCenter)}.
   * - Beregning av {@code setToX()} and {@code setToY()} basert på differansen mellom
   * {@code jumpToCenter} og {@code jumpFromCenter}.
   * - Valg av {@link javafx.animation.Interpolator} (Interpolator.EASE_BOTH) for en
   * mykere start/stopp-effekt for hoppet.
   * - Håndtering av {@code setOnFinished} for å endelig posisjonere tokenet på
   * {@code jumpToCenter} ved hjelp av {@code commitTokenPosition}.
   * Dato: 18-05-25
   */
  private TranslateTransition buildJumpTransition(final Node tokenNode,
      final Point2D jumpFromCenter, final Point2D jumpToCenter) {
    commitTokenPosition(tokenNode, jumpFromCenter);

    final TranslateTransition jump = new TranslateTransition(JUMP_ANIMATION_DURATION, tokenNode);
    jump.setFromX(0);
    jump.setFromY(0);
    jump.setToX(jumpToCenter.getX() - jumpFromCenter.getX());
    jump.setToY(jumpToCenter.getY() - jumpFromCenter.getY());
    jump.setInterpolator(Interpolator.EASE_BOTH);

    jump.setOnFinished(event -> commitTokenPosition(tokenNode, jumpToCenter));
    return jump;
  }

  private void runFinalCallback(final Runnable callback) {
    if (callback != null) {
      runLater(callback);
    }
  }

  private List<Integer> calculatePath(final int startId, final int endId) {
    final List<Integer> path = new ArrayList<>();
    if (startId == endId) {
      return path;
    }

    int currentId = startId;
    final int safetyBreak = this.totalTilesOnBoard * 2;
    int count = 0;

    if (this.totalTilesOnBoard <= 0) {
      LOGGER.log(SEVERE,
          "Cannot calculate path: totalTilesOnBoard is not positive ({0})",
          this.totalTilesOnBoard);
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
      LOGGER.log(SEVERE,
          "Path calculation exceeded safety break. Start: {0}, End: {1}, Path: {2}",
          new Object[]{startId, endId, path});
      return new ArrayList<>();
    }
    return path;
  }

  private List<Integer> calculateRelativePath(final int startId, final int steps) {
    final List<Integer> path = new ArrayList<>();
    if (steps == 0) {
      return path;
    }

    int currentId = startId;
    final int numStepsToTake = Math.abs(steps);
    final boolean forward = steps > 0;
    final int boardSize = this.totalTilesOnBoard;

    if (boardSize <= 0) {
      LOGGER.log(SEVERE,
          "Cannot calculate relative path: boardSize is not positive ({0})", boardSize);
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
    return path;
  }
}