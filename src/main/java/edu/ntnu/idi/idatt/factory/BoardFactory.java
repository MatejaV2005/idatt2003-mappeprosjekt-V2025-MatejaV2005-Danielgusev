package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.dto.BoardDto;
import edu.ntnu.idi.idatt.converter.BoardConverter;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;

/**
 * Factory for creating game boards.
 * This class can produce various predefined board configurations for different games
 * and difficulty levels, as well as create boards from DTOs.
 */
public class BoardFactory {

  private final TileFactory tileFactory;

  /**
   * Constructs a BoardFactory.
   */
  public BoardFactory() {
    this.tileFactory = new TileFactory();
  }

  /**
   * Creates an easy board for Snakes & Ladders: a small board (50 tiles) with many ladders.
   *
   * @return a new Board instance configured as an easy board.
   */
  public Board createEasyBoard() {
    Board board = new Board(10, 5);
    assignEasyActions(board);
    return board;
  }

  /**
   * Creates a normal board for Snakes & Ladders: a default board (100 tiles)
   * with an equal number of ladders and snakes, plus some skip turn tiles.
   *
   * @return a new Board instance configured as a normal board.
   */
  public Board createNormalBoard() {
    Board board = new Board(10, 10);
    assignNormalActions(board);
    return board;
  }

  /**
   * Creates a hard board for Snakes & Ladders: a larger board (120 tiles) with significantly more
   * snakes than ladders, and other penalty actions.
   *
   * @return a new Board instance configured as a hard board.
   */
  public Board createHardBoard() {
    Board board = new Board(12, 10);
    assignHardActions(board);
    return board;
  }

  /**
   * Creates the specific board layout for the Astro Rally game.
   * This board is a circuit/loop of 40 tiles.
   *
   * @return A new Board instance configured for Astro Rally.
   * @throws IllegalStateException if the board cannot be initialized (e.g., start/end tiles missing)
   */
  public Board createAstroRallyBoard() {
    int totalTiles = 40;
    Board board = new Board(1, totalTiles);

    Tile lastTile = board.getTileById(totalTiles);
    Tile firstTile = board.getTileById(1);

    if (lastTile != null && firstTile != null) {
      lastTile.setNextTile(firstTile);
      lastTile.setNextTileId(firstTile.getTileId());
    } else {
      String errorMsg =
          "Could not create Astro Rally loop: Tile 1 or Tile "
              + totalTiles
              + " not found. Board initialization failed.";
      throw new IllegalStateException(errorMsg);
    }

    assignAstroRallyActions(board);

    return board;
  }


  private void assignEasyActions(Board board) {
    Tile startTile;
    Tile destinationTile;
    TileAction action;

    startTile = board.getTileById(3);
    destinationTile = board.getTileById(13);
    action = TileActionFactory.createLadderAction(
        destinationTile, "climbs a short ladder to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(15);
    destinationTile = board.getTileById(37);
    action = TileActionFactory.createLadderAction(
        destinationTile, "climbs a long ladder to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(32);
    destinationTile = board.getTileById(45);
    action = TileActionFactory.createLadderAction(
        destinationTile, "climbs a medium ladder to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(28);
    destinationTile = board.getTileById(20);
    action = TileActionFactory.createSnakeAction(
        destinationTile, "slides down a pesky snake to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);
  }

  private void assignNormalActions(Board board) {
    Tile startTile;
    Tile destinationTile;
    TileAction action;

    startTile = board.getTileById(8);
    destinationTile = board.getTileById(28);
    action = TileActionFactory.createLadderAction(
        destinationTile, "climbs a ladder to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(33);
    destinationTile = board.getTileById(58);
    action = TileActionFactory.createLadderAction(
        destinationTile, "climbs a ladder to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(63);
    destinationTile = board.getTileById(86);
    action = TileActionFactory.createLadderAction(
        destinationTile, "climbs a ladder to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(15);
    destinationTile = board.getTileById(2);
    action = TileActionFactory.createSnakeAction(
        destinationTile, "slides down a snake to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(42);
    destinationTile = board.getTileById(16);
    action = TileActionFactory.createSnakeAction(
        destinationTile, "slides down a snake to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(70);
    destinationTile = board.getTileById(55);
    action = TileActionFactory.createSnakeAction(
        destinationTile, "slides down a snake to tile " + destinationTile.getTileId());
    startTile.setLandAction(action);

    action = TileActionFactory.createSkipTurnAction();
    board.getTileById(22).setLandAction(action);
    board.getTileById(47).setLandAction(action);
    board.getTileById(77).setLandAction(action);
  }

  private void assignHardActions(Board board) {
    Tile startTile;
    Tile destinationTile;
    TileAction action;
    Tile firstTile = board.getTileById(1);

    startTile = board.getTileById(7);
    destinationTile = board.getTileById(25);
    action = TileActionFactory.createLadderAction(
        destinationTile, "finds a small rickety ladder to " + destinationTile.getTileId());
    startTile.setLandAction(action);

    startTile = board.getTileById(50);
    destinationTile = board.getTileById(75);
    action = TileActionFactory.createLadderAction(
        destinationTile, "struggles up a weathered ladder to " + destinationTile.getTileId());
    startTile.setLandAction(action);

    int[][] snakePositions = {
        {20, 5}, {35, 12}, {53, 30}, {40, 18}, {69, 48}, {78, 59},
        {82, 61}, {92, 51}, {96, 72}, {105, 85}, {110, 70}, {118, 90}
    };

    for (int[] pos : snakePositions) {
      startTile = board.getTileById(pos[0]);
      destinationTile = board.getTileById(pos[1]);
      action = TileActionFactory.createSnakeAction(
          destinationTile, "slithers down a treacherous snake to " + destinationTile.getTileId());
      startTile.setLandAction(action);
    }

    int[] skipTurnTiles = {28, 58, 88, 108};
    action = TileActionFactory.createSkipTurnAction();
    for (int tileId : skipTurnTiles) {
      board.getTileById(tileId).setLandAction(action);
    }

    if (firstTile != null) {
      action = TileActionFactory.createReturnToStartAction(firstTile);
      board.getTileById(65).setLandAction(action);
      board.getTileById(99).setLandAction(action);
    }


    startTile = board.getTileById(115);
    destinationTile = board.getTileById(40);
    action = TileActionFactory.createSnakeAction(
        destinationTile, "tumbles all the way down a giant snake to " + destinationTile.getTileId());
    startTile.setLandAction(action);
  }

  /**
   * Assigns specific actions (Boost Pads, Asteroid Fields, etc.) to tiles for an Astro Rally
   * board. The board is assumed to have 40 tiles.
   *
   * @param board The Astro Rally board to configure.
   */
  private void assignAstroRallyActions(Board board) {
    Tile boostTile1 = board.getTileById(7);
    if (boostTile1 != null) {
      boostTile1.setLandAction(
          TileActionFactory.createBoostPadAction("Engage hyperdrive! +2 speed next turn."));
    }

    Tile boostTile2 = board.getTileById(23);
    if (boostTile2 != null) {
      boostTile2.setLandAction(
          TileActionFactory.createBoostPadAction("Caught a solar wind! +2 speed next turn."));
    }

    Tile asteroidTile1 = board.getTileById(15);
    if (asteroidTile1 != null) {
      asteroidTile1.setLandAction(
          TileActionFactory.createAsteroidFieldAction("Dense asteroid cluster ahead!"));
    }

    Tile asteroidTile2 = board.getTileById(35);
    if (asteroidTile2 != null) {
      asteroidTile2.setLandAction(
          TileActionFactory.createAsteroidFieldAction("Watch out for space debris!"));
    }
  }

  /**
   * Creates a Board instance from a Data Transfer Object.
   *
   * @param dto The BoardDto containing board data.
   * @return A new Board instance.
   * @throws InvalidBoardFormatException if the DTO data is invalid.
   */
  public Board createBoardFromDto(BoardDto dto) throws InvalidBoardFormatException {
    return BoardConverter.fromDto(dto);
  }
}
