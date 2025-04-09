package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.converter.BoardConverter;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.TileAction;


public class BoardFactory {

  private TileFactory tileFactory;

  public BoardFactory() {
    // Instantiate the TileFactory for creating tiles (if needed)
    this.tileFactory = new TileFactory();
  }

  /**
   * Creates an easy board: a small board (50 tiles) with many ladders.
   *
   * @return a new Board instance configured as an easy board.
   */
  public Board createEasyBoard() {
    Board board = new Board(10, 5);
    assignEasyActions(board);
    return board;
  }

  /**
   * Creates a normal board: a default board (90 tiles) with an equal number of ladders and snakes.
   *
   * @return a new Board instance configured as a normal board.
   */
  public Board createNormalBoard() {
    Board board = new Board(10, 10);
    assignNormalActions(board);
    return board;
  }

  /**
   * Creates a hard board: a larger board (120 tiles) with more snakes than ladders.
   *
   * @return a new Board instance configured as a hard board.
   */
  public Board createHardBoard() {
    Board board = new Board(12, 10);
    assignHardActions(board);
    return board;
  }

  public Board createBoardFromFile() {
    return null;
  }

  // Assigns ladder actions to an easy board (small board with many ladders)
  private void assignEasyActions(Board board) {
    Tile start = board.getTileById(3);
    Tile destination = board.getTileById(13);
    TileAction ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(15);
    destination = board.getTileById(37);
    ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(32);
    destination = board.getTileById(45);
    ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(28);
    destination = board.getTileById(20);
    TileAction snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);
  }

  // Assigns an equal number of ladder and snake actions for a normal board (90 tiles)
  private void assignNormalActions(Board board) {
    Tile start = board.getTileById(8);
    Tile destination = board.getTileById(18);
    TileAction ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(33);
    destination = board.getTileById(58);
    ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(63);
    destination = board.getTileById(86);
    ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(15);
    destination = board.getTileById(2);
    TileAction snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(42);
    destination = board.getTileById(16);
    snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(70);
    destination = board.getTileById(55);
    snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(22);
    TileAction skipTurnAction = TileActionFactory.createSkipTurnAction();
    start.setLandAction(skipTurnAction);

    start = board.getTileById(47);
    skipTurnAction = TileActionFactory.createSkipTurnAction();
    start.setLandAction(skipTurnAction);

    start = board.getTileById(66);
    skipTurnAction = TileActionFactory.createSkipTurnAction();
    start.setLandAction(skipTurnAction);
  }

  // Assigns fewer ladder actions and more snake actions for a hard board (120 tiles)
  private void assignHardActions(Board board) {
    Tile start = board.getTileById(14);
    Tile destination = board.getTileById(68);
    TileAction ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(60);
    destination = board.getTileById(100);
    ladder = TileActionFactory.createLadderAction(
        destination, "climbs up to tile " + destination.getTileId());
    start.setLandAction(ladder);

    start = board.getTileById(15);
    destination = board.getTileById(2);
    TileAction snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(13);
    destination = board.getTileById(1);
    snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(45);
    destination = board.getTileById(22);
    snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(74);
    destination = board.getTileById(55);
    snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(98);
    destination = board.getTileById(40);
    snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);

    start = board.getTileById(115);
    destination = board.getTileById(95);
    snake = TileActionFactory.createSnakeAction(
        destination, "slides down to tile " + destination.getTileId());
    start.setLandAction(snake);
  }

  public Board createBoardFromDto(BoardDto dto) {
    Board board = BoardConverter.fromDto(dto);

    return board;
  }
}
