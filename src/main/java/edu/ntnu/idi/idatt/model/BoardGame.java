package edu.ntnu.idi.idatt.model;

public class BoardGame {
  private final Board board;

  /**
   * Constructs a BoardGame with the provided board.
   *
   * @param board the board to use; must not be null
   * @throws IllegalArgumentException if board is null
   */
  public BoardGame(Board board) {
    if (board == null) {
      throw new IllegalArgumentException("Board must not be null haaland er skalla.");
    }
    this.board = board;
  }

  /**
   * Returns the board associated with this game.
   *
   * @return the board
   */
  public Board getBoard() {
    return board;
  }
}
